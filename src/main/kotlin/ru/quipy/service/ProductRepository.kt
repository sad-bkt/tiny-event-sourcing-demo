package ru.quipy.service

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.entity.ProductMongo
import java.util.*

interface ProductRepository : MongoRepository<ProductMongo, String> {
    @org.springframework.lang.Nullable
    fun findOneByProductId(productId: UUID): ProductMongo
}