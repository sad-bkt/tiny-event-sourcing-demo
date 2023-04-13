package ru.quipy.service

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.entity.Timeslot

interface TimeslotRepository: MongoRepository<Timeslot, String> {
    @org.springframework.lang.Nullable
    fun findOneById(id: ObjectId): Timeslot
}