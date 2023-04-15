package ru.quipy.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import ru.quipy.api.BasketAggregate
import ru.quipy.api.DeliveryAggregate
import ru.quipy.api.ProductAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.*
import ru.quipy.entity.TimeslotMongo
import ru.quipy.logic.*
import ru.quipy.service.ProductRepository
import ru.quipy.service.TimeslotRepository
import ru.quipy.service.UserRepository
import java.util.*

@RestController
@RequestMapping("/delivery")
class DeliveryController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val productEsService: EventSourcingService<UUID, ProductAggregate, ProductAggregateState>,
    val deliveryEsService: EventSourcingService<UUID, DeliveryAggregate, Delivery>,
    val basketEsService: EventSourcingService<UUID, BasketAggregate, BasketAggregateState>,
    val userRepository: UserRepository,
    val timeslotRepository: TimeslotRepository,
    val productRepository: ProductRepository
) {
    // private val mutexChangeSlot = ReentrantLock()
    @GetMapping("/list")
    @Operation(
        summary = "List of deliveries",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
        ]
    )
    fun deliveryList(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails): ResponseEntity<Any> {
        val result = ArrayList<DeliveryListDTO>()
        val userLogged = userRepository.findOneByEmail(user.username)
            ?: return ResponseEntity<Any>("User not found", HttpStatus.NOT_FOUND)

        for (deliveryId in userEsService.getState(userLogged.aggregateId)!!.getDeliveries()) {
            val delivery = deliveryEsService.getState(deliveryId)!!
            result.add(
                DeliveryListDTO(
                    deliveryId = deliveryId.toString(),
                    status = delivery.getDeliveryStatus(),
                    time = timeslotRepository.findOneById(delivery.getTimeslotId())!!.time
                )
            )
        }
        return ResponseEntity<Any>(result, HttpStatus.OK)
    }

    @GetMapping("/list_timeslots")
    @Operation(
        summary = "List of timeslots",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ApiResponse(description = "Not found", responseCode = "404", content = [Content()])
        ]
    )
    fun deliveryTimeslotList(): ResponseEntity<Any> {
        return ResponseEntity<Any>(timeslotRepository.findAll(), HttpStatus.OK)
    }

    @PostMapping("/create_timeslot")
    @Operation(
        summary = "Create new timeslot",
        responses = [
            ApiResponse(description = "Created", responseCode = "201", content = [Content()])
        ]
    )
    fun createDeliveryTimeslot(@RequestBody time: String): ResponseEntity<Any> {
        val id = UUID.randomUUID()
        timeslotRepository.create(
            TimeslotMongo(
                id = id,
                time = time,
                busy = false,
            )
        )

        return ResponseEntity<Any>("Delivery timeslot with id = $id created", HttpStatus.CREATED)

    }

    @PostMapping("/create")
    @Operation(
        summary = "Create new delivery",
        responses = [
            ApiResponse(description = "Created", responseCode = "201", content = [Content()]),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ]
    )
    fun createDelivery(
        @Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails,
        @RequestBody dto: DeliveryDTO
    ): ResponseEntity<Any> {
        val userLogged = userRepository.findOneByEmail(user.username)
            ?: return ResponseEntity<Any>("User not found", HttpStatus.NOT_FOUND)


        if (!userEsService.getState(userLogged.aggregateId)!!.existBasket()) {
            return ResponseEntity<Any>("Basket doesn't exist", HttpStatus.NOT_FOUND)
        }

        val basketId = userEsService.getState(userLogged.aggregateId)!!.getBasketId()
        val basket = basketEsService.getState(basketId!!)?.getBasket() ?: return ResponseEntity<Any>(
            "Basket doesn't exist",
            HttpStatus.NOT_FOUND
        )

        if (timeslotRepository.changeBusy(UUID.fromString(dto.timeslotId), true) == null)
            return ResponseEntity<Any>("This timeslot is busy or doesn't exist. Please, choose another slot", HttpStatus.CONFLICT)
        for (item in basket.entries.iterator()) {
            var product = productRepository.findOneByProductId(item.key)
            if (product == null) {
                basket.remove(item.key)
                continue
            }
            while (item.value <= product!!.productCount) {
                 if (productRepository.changeCount(item.key, product.productCount, product.productCount - item.value) == null) {
                    product = productRepository.findOneByProductId(item.key)
                    if (product == null) {
                        basket.remove(item.key)
                        timeslotRepository.changeBusy(UUID.fromString(dto.timeslotId), false)
                        return ResponseEntity<Any>("No product with such id $item.key", HttpStatus.NOT_FOUND)
                    }
                    continue
                }
                productEsService.update(item.key) { it.updateCount(item.value) }
//                item.setValue(item.value)

                val delivery = deliveryEsService.create {
                    it.createNewDelivery(
                        timeslotId = UUID.fromString(dto.timeslotId),
                        basketId = basketId
                    )
                }
                userEsService.update(userLogged.aggregateId) { it.addDelivery(delivery.deliveryId) }
                userEsService.update(userLogged.aggregateId) { it.deleteUserBasket() }
                break
            }
            if (item.value > product.productCount){
                timeslotRepository.changeBusy(UUID.fromString(dto.timeslotId), false)
                return ResponseEntity<Any>("The number of items in stock is less than what you want to buy", HttpStatus.BAD_REQUEST)
            }
        }
        return ResponseEntity<Any>("Delivery created", HttpStatus.CREATED)
    }

    @PostMapping("/cancel")
    @Operation(
        summary = "Cancel delivery",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun cancelDelivery(@RequestBody dto: DeliveryIdDTO): ResponseEntity<Any> {
        val basketId = deliveryEsService.getState(UUID.fromString(dto.deliveryId))?.getBasketId()
            ?: return ResponseEntity<Any>("Delivery doesn't exist or doesn't have basket", HttpStatus.NOT_FOUND)
        var basket = basketEsService.getState(basketId)?.getBasket()
            ?: return ResponseEntity<Any>("Delivery doesn't have basket", HttpStatus.NOT_FOUND)

        for (item in basket.entries.iterator()) {
            while (true) {
                val product = productRepository.findOneByProductId(item.key)
                if (product == null) {
                    basket.remove(item.key)
                    break
                }

                val itemCount = item.value + product.productCount
                productRepository.changeCount(item.key, product.productCount, itemCount) ?: continue
                productEsService.update(item.key) { it.updateCount(itemCount) }
                break
            }
        }

        return changeStatusDelivery(DeliveryStatusChangedDTO(dto.deliveryId, Delivery.DeliveryStatus.CANCELED))
    }

    @PostMapping("/complete")
    @Operation(
        summary = "Complete delivery",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun completeDelivery(@RequestBody dto: DeliveryIdDTO): Any {
        return changeStatusDelivery(DeliveryStatusChangedDTO(dto.deliveryId, Delivery.DeliveryStatus.COMPLETED))
    }

    @PostMapping("/change_timeslot")
    @Operation(
        summary = "Change delivery timeslot",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ApiResponse(description = "User delivery not found", responseCode = "404", content = [Content()]),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ]
    )
    fun changeDeliveryTimeslot(
        @Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails,
        @RequestBody dto: DeliveryTimeslotChangedDTO
    ): ResponseEntity<Any> {
        val userLogged = userRepository.findOneByEmail(user.username)
            ?: return ResponseEntity<Any>("User not found", HttpStatus.NOT_FOUND)

        var isUserHasDelivery = false
        val deliveryId = UUID.fromString(dto.deliveryId)
        val deliveryTimeslotId : UUID = deliveryEsService.getState(deliveryId)?.getTimeslotId()
            ?: return ResponseEntity<Any>("No such delivery id: $deliveryId", HttpStatus.NOT_FOUND)
        if (userLogged.role != "admin") {
            for (userDeliveryId in userEsService.getState(userLogged.aggregateId)!!.getDeliveries()) {
                if (userDeliveryId == deliveryId) {
                    if (deliveryEsService.getState(userDeliveryId)!!
                            .getDeliveryStatus() != Delivery.DeliveryStatus.IN_DELIVERY
                    ) {
                        return ResponseEntity<Any>(
                            "Can't change timeslot for order with the status other than IN_DELIVERY",
                            HttpStatus.BAD_REQUEST
                        )
                    }
                    isUserHasDelivery = true
                    break
                }
            }
            if (!isUserHasDelivery) {
                return ResponseEntity<Any>("User doesn't have delivery", HttpStatus.NOT_FOUND)
            }
        }
        timeslotRepository.changeBusy(UUID.fromString(dto.timeslotId), true) ?: return ResponseEntity<Any>(
            null,
            HttpStatus.CONFLICT
        )
        timeslotRepository.changeBusy(deliveryTimeslotId, false)
        deliveryEsService.update(deliveryId) {
            it.changeDeliveryTimeslot(
                id = deliveryId,
                timeslotId = UUID.fromString(dto.timeslotId)
            )
        }
        return ResponseEntity<Any>("Delivery timeslot updated", HttpStatus.OK)
    }

    @PostMapping("/change_status")
    @Operation(
        summary = "Change delivery timeslot",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ApiResponse(description = "User delivery not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun changeStatusDelivery(@RequestBody dto: DeliveryStatusChangedDTO): ResponseEntity<Any> {
        val deliveryId = UUID.fromString(dto.deliveryId)
        val deliveryStatus = deliveryEsService.getState(deliveryId)?.getDeliveryStatus()
            ?: return ResponseEntity<Any>("No delivery with such id: ${dto.deliveryId}", HttpStatus.NOT_FOUND)
        if (deliveryStatus != dto.status) {
            when (dto.status) {
                Delivery.DeliveryStatus.CANCELED -> {
                    deliveryEsService.update(deliveryId) { it.cancelDelivery(deliveryId) }
                    val timeslotId = deliveryEsService.getState(deliveryId)!!.getTimeslotId()
//                    timeslot?.busy = false
                    timeslotRepository.changeBusy(timeslotId, false)
                }

                else -> deliveryEsService.update(deliveryId) { it.changeDeliveryStatus(deliveryId, dto.status) }
            }
            return ResponseEntity<Any>("Delivery status updated", HttpStatus.OK)
        }
        return ResponseEntity<Any>("Delivery has already have status $deliveryStatus", HttpStatus.OK)
    }
}