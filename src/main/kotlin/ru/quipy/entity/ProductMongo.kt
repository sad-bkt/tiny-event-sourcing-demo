package ru.quipy.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

@Document
data class ProductMongo(
    @MongoId
    val productId: UUID,
    val productName: String,
    val productCount: Int,
)