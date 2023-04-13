package ru.quipy.service

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate

import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

import ru.quipy.entity.Timeslot

@Repository
public class TimeslotRepository(val mongoTemplate: MongoTemplate) {

    fun findOneById(id: ObjectId): Timeslot? {
        val query = Query();
        query.addCriteria(Criteria.where("id").isEqualTo(id));
        return mongoTemplate.findOne(query)
    }

    fun changeBusy(id: ObjectId, busy: Boolean): Timeslot? {
        val query = Query()
        query.addCriteria(Criteria.where("id").isEqualTo(id).and("busy").isEqualTo(!busy))
        val update: Update = Update().set("busy", busy)
        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), Timeslot::class.java)
    }
}