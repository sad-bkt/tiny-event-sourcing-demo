package ru.quipy.controller

import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.quipy.api.DeliveryAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.*
import ru.quipy.entity.Timeslot
import ru.quipy.logic.Delivery
import ru.quipy.logic.UserAggregateState
import ru.quipy.service.TimeslotRepository
import ru.quipy.service.UserRepository
import java.util.*

@RestController
@RequestMapping("/delivery")
class DeliveryController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val deliveryEsService: EventSourcingService<UUID, DeliveryAggregate, Delivery>,
    val userRepository: UserRepository,
    val timeslotRepository: TimeslotRepository
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

        for (item in userEsService.getState(userLogged.aggregateId)!!.getDeliveries()) {
                val delivery = deliveryEsService.getState(item)!!
                result.add(
                    DeliveryListDTO(
                        id = item.toString(),
                        status = delivery.getDeliveryStatus(),
                        time = timeslotRepository.findOneById(ObjectId(delivery.getTimeslotId())).time
                    ))
            }
        return ResponseEntity<Any>(result, HttpStatus.OK)
    }

    @PostMapping("/create")
    @Operation(
        summary = "Create new delivery",
        responses = [
            ApiResponse(description = "Created", responseCode = "201", content = [Content()]),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ]
    )
    fun createDelivery(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails,
                       @RequestBody dto: DeliveryDTO
    ): ResponseEntity<Any> {
        val result = ArrayList<DeliveryListDTO>()
        val userLogged = userRepository.findOneByEmail(user.username)
            ?: return ResponseEntity<Any>("User not found", HttpStatus.NOT_FOUND)

        val delivery = deliveryEsService.create() {  it.createNewDelivery(timeslotId = dto.time) }
        userEsService.getState(userLogged.aggregateId)!!.addDelivery(delivery.deliveryId)
        return ResponseEntity<Any>("Delivery created", HttpStatus.CREATED)

    }

    @PostMapping("/cancel")
    @Operation(
        summary = "Cancel delivery",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ]
    )
    fun cancelDelivery(@RequestBody dto: DeliveryIdDTO): Any {
        return changeStatusDelivery(DeliveryStatusChangedDTO(dto.id, Delivery.DeliveryStatus.CANCELED))
    }

    @PostMapping("/complete")
    @Operation(
        summary = "Complete delivery",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ]
    )
    fun completeDelivery(@RequestBody dto: DeliveryIdDTO): Any {
        return changeStatusDelivery(DeliveryStatusChangedDTO(dto.id, Delivery.DeliveryStatus.COMPLETED))
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
    fun changeDeliveryTimeslot(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails,
                               @RequestBody dto: DeliveryTimeslotChangedDTO): ResponseEntity<Any> {
        val result = ArrayList<DeliveryListDTO>()
        val userLogged = userRepository.findOneByEmail(user.username)
            ?: return ResponseEntity<Any>("User not found", HttpStatus.NOT_FOUND)

        for (item in userEsService.getState(userLogged.aggregateId)!!.getDeliveries()) {
            if (item == UUID.fromString(dto.id)) {
                if (deliveryEsService.getState(item)!!.getDeliveryStatus() == Delivery.DeliveryStatus.IN_DELIVERY) {
                    //mutexChangeSlot.lock()
                    val newTimeslot = timeslotRepository.findOneById(ObjectId(dto.timeslotId))
                    if (newTimeslot.busy) {
                        //mutexChangeSlot.unlock()
                        return ResponseEntity<Any>(null, HttpStatus.CONFLICT)
                    }
                    val timeslot = timeslotRepository.findOneById(ObjectId(deliveryEsService.getState(item)!!.getTimeslotId()))
                    timeslot.busy = false
                    deliveryEsService.update(item) { it.changeDeliveryTimeslot(id = item, timeslotId = dto.timeslotId) }

                    newTimeslot.busy = true
                    timeslotRepository.save(newTimeslot)
                    //mutexChangeSlot.unlock()
                    return ResponseEntity<Any>("Can't change timeslot for order with the status not IN_DELIVERY", HttpStatus.OK)
                }
                return ResponseEntity<Any>("Can't change timeslot for order with the status not IN_DELIVERY", HttpStatus.BAD_REQUEST)
            }
        }
        return ResponseEntity<Any>("Delivery not found", HttpStatus.NOT_FOUND)
    }

    @PostMapping("/change_status")
    @Operation(
        summary = "Change delivery timeslot",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ApiResponse(description = "User delivery not found", responseCode = "404", content = [Content()])
        ]
    )
    fun changeStatusDelivery(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails,
                             @RequestBody dto: DeliveryStatusChangedDTO)
    : ResponseEntity<Any> {
        val result = ArrayList<DeliveryListDTO>()
        val userLogged = userRepository.findOneByEmail(user.username)
            ?: return ResponseEntity<Any>("User not found", HttpStatus.NOT_FOUND)

        for (item in userEsService.getState(userLogged.aggregateId)!!.getDeliveries()) {
            if (item == UUID.fromString(dto.id)) {
                val status = deliveryEsService.getState(item)!!.getDeliveryStatus()
                if (status != dto.status) {
                    when (dto.status) {
                        Delivery.DeliveryStatus.CANCELED -> {
                            deliveryEsService.update(item) { it.cancelDelivery(item) }

                            // Timeslot.findAndModify()
                            val timeslot = timeslotRepository.findOneById(ObjectId(deliveryEsService.getState(item)!!.getTimeslotId()))
                            timeslot.busy = false
                            timeslotRepository.save(timeslot)
                        }
                        else -> deliveryEsService.update(item) { it.changeDeliveryStatus(item, dto.status) }
                    }
                    ResponseEntity<Any>("Delivery status updated", HttpStatus.OK)
                }
                break
            }
        }
        return ResponseEntity<Any>("Delivery not found", HttpStatus.NOT_FOUND)
    }
}