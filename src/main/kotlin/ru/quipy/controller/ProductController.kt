package ru.quipy.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProductAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.ProductCountDTO
import ru.quipy.dto.ProductDTO
import ru.quipy.entity.ProductMongo
import ru.quipy.logic.*
import ru.quipy.service.ProductRepository
import java.util.*

@RestController
@RequestMapping("/product")
class ProductController(
    val productEsService: EventSourcingService<UUID, ProductAggregate, ProductAggregateState>,
    val productRepository: ProductRepository,
) {

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all products",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getAllProduct(): ResponseEntity<Any> {
        return ResponseEntity<Any>(productRepository.findAll(), HttpStatus.OK)
    }

    @GetMapping("/{productId}")
    @Operation(
        summary = "Get product by productId",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getProduct(@PathVariable productId: UUID): ResponseEntity<Any> {
        productRepository.findOneByProductId(productId)
            ?: return ResponseEntity<Any>("Product with id $productId was not found", HttpStatus.NOT_FOUND)
        return ResponseEntity<Any>(productRepository.findOneByProductId(productId), HttpStatus.OK)
    }

    @PostMapping("/create")
    @Operation(
        summary = "Create product",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun createProduct(@RequestBody productDto: ProductDTO): ResponseEntity<Any> {
        val product = productEsService.create { it.create(UUID.randomUUID(), productDto.name, productDto.count) }
        return ResponseEntity<Any>(
            productRepository.create(
                ProductMongo(
                    productId = product.productId,
                    productName = product.productName,
                    productCount = product.count
                )
            ), HttpStatus.OK
        )
    }

    @PostMapping("/updateCount")
    @Operation(
        summary = "Update product count",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun updateProduct(@RequestBody productCountDto: ProductCountDTO): ResponseEntity<Any> {
        if (productCountDto.count < 0) {
            return ResponseEntity<Any>("Count of the product cannot be negative", HttpStatus.BAD_REQUEST)
        }
        val id = productCountDto.productId
        var tryChangeCount: ProductMongo? = null
        while (tryChangeCount == null) {
            val product = productRepository.findOneByProductId(id)
                ?: return ResponseEntity<Any>("Product with id $id was not found", HttpStatus.NOT_FOUND)
            tryChangeCount = productRepository.changeCount(id, product.productCount, productCountDto.count)
        }
        return ResponseEntity<Any>(productEsService.update(id) { it.updateCount(productCountDto.count) }, HttpStatus.OK)
    }

    @PostMapping("/delete")
    @Operation(
        summary = "Delete product",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteProduct(@RequestParam id: UUID): ResponseEntity<Any> {
        productRepository.findOneByProductId(id)
            ?: return ResponseEntity<Any>("Product with id $id was not found", HttpStatus.NOT_FOUND)
        productRepository.delete(id)
        return ResponseEntity<Any>(productEsService.update(id) { it.delete(id) }, HttpStatus.OK)
    }

}