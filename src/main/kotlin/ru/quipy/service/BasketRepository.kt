package ru.quipy.service

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.entity.BasketMongo

interface BasketRepository : MongoRepository<BasketMongo, String>