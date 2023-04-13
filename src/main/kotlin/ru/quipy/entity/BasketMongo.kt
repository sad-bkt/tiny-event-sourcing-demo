package ru.quipy.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

@Document
data class BasketMongo(
    @MongoId
    val basketId: UUID,
    val basket: MutableMap<UUID, Int>
)