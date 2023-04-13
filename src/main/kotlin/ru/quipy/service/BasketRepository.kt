package ru.quipy.service

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.entity.BasketMongo
import ru.quipy.entity.ProductMongo
import java.util.*

interface BasketRepository : MongoRepository<BasketMongo, String> {
}