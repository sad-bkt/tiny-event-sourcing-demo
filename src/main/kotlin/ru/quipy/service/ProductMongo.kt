package ru.quipy.service

import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

data class ProductMongo (
    @MongoId(value = FieldType.STRING)
    val productId: UUID,
    val productName: String,
    val productCount: Int,
)