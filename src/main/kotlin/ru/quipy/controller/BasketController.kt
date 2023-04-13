package ru.quipy.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import ru.quipy.api.BasketAggregate
import ru.quipy.api.ProductAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.ListProductDTO
import ru.quipy.dto.ProductCountDTO
import ru.quipy.dto.ProductDTO
import ru.quipy.entity.BasketMongo
import ru.quipy.logic.*
import ru.quipy.entity.ProductMongo
import ru.quipy.service.BasketRepository
import ru.quipy.service.ProductRepository
import ru.quipy.service.UserRepository
import java.util.*

@RestController
@RequestMapping("/basket")
class BasketController(
    val productEsService: EventSourcingService<UUID, ProductAggregate, ProductAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val basketEsService: EventSourcingService<UUID, BasketAggregate, BasketAggregateState>,
    val productRepository: ProductRepository,
    val basketRepository: BasketRepository,
    val userRepository: UserRepository,
) {

    private fun basketCreateOrPass(id: UUID) {
        if(!userEsService.getState(id)!!.existBasket()){
            val basketId = basketEsService.create { it.create(UUID.randomUUID()) }.basketId
            userEsService.update(id){
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
    fun addProduct(@RequestBody listProductDTO: ListProductDTO, @AuthenticationPrincipal user: UserDetails): MutableMap<UUID, Int>? {
        val id = userRepository.findOneByEmail(user.username)!!.aggregateId
        basketCreateOrPass(id)
        val basketId = userEsService.getState(id)!!.getBasketId()
        basketEsService.update(basketId){ it.addProduct(listProductDTO.productId, listProductDTO.count) }
        return basketEsService.getState(basketId)?.getBasket()
    }

    @PostMapping("/updateCount")
    fun updateProductCount(@RequestBody listProductDTO: ListProductDTO, @AuthenticationPrincipal user: UserDetails): MutableMap<UUID, Int>? {
        val id = userRepository.findOneByEmail(user.username)!!.aggregateId
        basketCreateOrPass(id)
        val basketId = userEsService.getState(id)!!.getBasketId()
        basketEsService.update(basketId){ it.changeCount(listProductDTO.productId, listProductDTO.count) }
        return basketEsService.getState(basketId)?.getBasket()
    }

    @PostMapping("/deleteProduct")
    fun deleteProduct(@RequestBody productId: UUID, @AuthenticationPrincipal user: UserDetails): MutableMap<UUID, Int>? {
        val id = userRepository.findOneByEmail(user.username)!!.aggregateId
        basketCreateOrPass(id)
        val basketId = userEsService.getState(id)!!.getBasketId()
        basketEsService.update(basketId){ it.delete(productId) }
        return basketEsService.getState(basketId)?.getBasket()
    }

    @GetMapping("/getBasket")
    fun getBasket(@AuthenticationPrincipal user: UserDetails): MutableMap<UUID, Int>? {
        val id = userRepository.findOneByEmail(user.username)!!.aggregateId
        basketCreateOrPass(id)
        val basketId = userEsService.getState(id)!!.getBasketId()
        return basketEsService.getState(basketId)?.getBasket()
    }

}