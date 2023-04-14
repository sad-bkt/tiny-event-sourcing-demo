package ru.quipy.service

import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import ru.quipy.entity.TimeslotMongo
import java.util.*

@Repository
class TimeslotRepository(val mongoTemplate: MongoTemplate) {

    fun findOneById(id: UUID): TimeslotMongo? {
        val query = Query()
        query.addCriteria(Criteria.where("id").isEqualTo(id))
        return mongoTemplate.findOne(query)
    }

    fun changeBusy(id: UUID, busy: Boolean): TimeslotMongo? {
        val query = Query()
        query.addCriteria(Criteria.where("id").isEqualTo(id).and("busy").isEqualTo(!busy))
        val update: Update = Update().set("busy", busy)
        return mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            TimeslotMongo::class.java
        )
    }

    fun create(timeslotMongo: TimeslotMongo) {
        mongoTemplate.insert<TimeslotMongo>(timeslotMongo)
    }

    fun findAll(): List<TimeslotMongo> {
        return mongoTemplate.findAll(TimeslotMongo::class.java)
    }
}