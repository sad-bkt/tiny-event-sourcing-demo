package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import ru.quipy.api.BasketAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.ProductCountDTO
import ru.quipy.entity.BasketMongo
import ru.quipy.logic.*
import ru.quipy.service.BasketRepository
import ru.quipy.service.UserRepository
import java.util.*

@RestController
@RequestMapping("/basket")
class BasketController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val basketEsService: EventSourcingService<UUID, BasketAggregate, BasketAggregateState>,
    val basketRepository: BasketRepository,
    val userRepository: UserRepository,
) {

    private fun basketCreateOrPass(id: UUID) {
        if (!userEsService.getState(id)!!.existBasket()) {
            val basketId = basketEsService.create { it.create(UUID.randomUUID()) }.basketId
            userEsService.update(id) {
                it.createBasket(basketId)
            }
            basketRepository.save(
                BasketMongo(
                    basketId = basketId,
                    basket = basketEsService.getState(basketId)?.getBasket()!!
                )
            )
        }
    }

    @PostMapping("/addProduct")
    fun addProduct(
        @RequestBody productCountDTO: ProductCountDTO,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Any> {
        val id = userRepository.findOneByEmail(user.username)?.aggregateId
            ?: return ResponseEntity<Any>("The user is not logged in", HttpStatus.BAD_REQUEST)
        basketCreateOrPass(id)
        val basketId = userEsService.getState(id)!!.getBasketId()
        basketEsService.update(basketId) {
            it.addProduct(
                productCountDTO.productId,
                productCountDTO.count
            )
        }
        return ResponseEntity<Any>(basketEsService.getState(basketId)?.getBasket(), HttpStatus.OK)
    }

    @PostMapping("/updateCount")
    fun updateProductCount(
        @RequestBody productCountDTO: ProductCountDTO,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Any> {
        val id = userRepository.findOneByEmail(user.username)?.aggregateId
            ?: return ResponseEntity<Any>("The user is not logged in", HttpStatus.BAD_REQUEST)
        basketCreateOrPass(id)
        val basketId = userEsService.getState(id)!!.getBasketId()
        if (!basketEsService.getState(basketId)?.existProduct(productCountDTO.productId)!!)
            return ResponseEntity<Any>("The product does not exist in the basket", HttpStatus.BAD_REQUEST)
        basketEsService.update(basketId) {
            it.changeCount(
                productCountDTO.productId,
                productCountDTO.count
            )
        }
        return ResponseEntity<Any>(basketEsService.getState(basketId)?.getBasket(), HttpStatus.OK)
    }

    @PostMapping("/deleteProduct")
    fun deleteProduct(
        @RequestParam productId: UUID,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Any> {
        val id = userRepository.findOneByEmail(user.username)?.aggregateId
            ?: return ResponseEntity<Any>("The user is not logged in", HttpStatus.BAD_REQUEST)
        val basketId = userEsService.getState(id)?.getBasketId()
            ?: return ResponseEntity<Any>("The shopping cart does not exist for the user", HttpStatus.BAD_REQUEST)
        if (!basketEsService.getState(basketId)?.existProduct(productId)!!)
            return ResponseEntity<Any>("The product does not exist in the basket", HttpStatus.BAD_REQUEST)

        basketEsService.update(basketId) { it.delete(productId) }
        return ResponseEntity<Any>(basketEsService.getState(basketId)?.getBasket(), HttpStatus.OK)
    }

    @GetMapping("/getBasket")
    fun getBasket(@AuthenticationPrincipal user: UserDetails): ResponseEntity<Any> {
        val id = userRepository.findOneByEmail(user.username)?.aggregateId
            ?: return ResponseEntity<Any>("The user is not logged in", HttpStatus.BAD_REQUEST)
        basketCreateOrPass(id)
        val basketId = userEsService.getState(id)!!.getBasketId()
        return ResponseEntity<Any>(basketEsService.getState(basketId)?.getBasket(), HttpStatus.OK)
    }

}