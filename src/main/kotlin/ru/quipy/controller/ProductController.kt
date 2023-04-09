package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProductAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.ProductDTO
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/product")
class ProductController(
    val productEsService: EventSourcingService<UUID, ProductAggregate, ProductAggregateState>
) {

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: UUID) : ProductAggregateState? {
        return productEsService.getState(productId)
    }

    @PostMapping("/createProduct")
    fun createProduct(@RequestBody productDto: ProductDTO) : Any {
        return productEsService.create { it.create(UUID.randomUUID(), productDto.name, productDto.count) }
    }

    @PostMapping("/updateProductCount")
    fun updateProduct(@RequestParam id: UUID, @RequestParam name: String, @RequestParam count: Int) : Any {
        return productEsService.create { it.update(id, name, count) }
    }

}