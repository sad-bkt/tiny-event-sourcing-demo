package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProductAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.ProductCountDTO
import ru.quipy.dto.ProductDTO
import ru.quipy.logic.*
import ru.quipy.entity.ProductMongo
import ru.quipy.service.ProductRepository
import java.util.*

@RestController
@RequestMapping("/product")
class ProductController(
    val productEsService: EventSourcingService<UUID, ProductAggregate, ProductAggregateState>,
    val productRepository: ProductRepository,
) {

    @GetMapping("/getAll")
    fun getAllProduct(): Any {
        return productRepository.findAll()
    }
    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: UUID): Any {
        return productRepository.findOneByProductId(productId)
    }

    @PostMapping("/create")
    fun createProduct(@RequestBody productDto: ProductDTO): Any {
        val product = productEsService.create { it.create(UUID.randomUUID(), productDto.name, productDto.count) }
        return productRepository.save(
            ProductMongo(
                productId = product.productId,
                productName = product.productName,
                productCount = product.count
            )
        )
    }

    @PostMapping("/updateCount")
    fun updateProduct(@RequestParam id: UUID, @RequestBody productCountDto: ProductCountDTO): Any {
        return productEsService.update(id){it.updateCount(productCountDto.count) }
    }

    @PostMapping("/delete")
    fun deleteProduct(@RequestParam id: UUID): Any {
        return productEsService.update(id){it.delete(id) }
    }

}