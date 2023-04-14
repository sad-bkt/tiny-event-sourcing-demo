package ru.quipy.service

import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import ru.quipy.entity.ProductMongo
import ru.quipy.entity.TimeslotMongo
import java.util.*

@Repository
class ProductRepository(val mongoTemplate: MongoTemplate) {

    fun findOneByProductId(id: UUID): ProductMongo? {
        val query = Query()
        query.addCriteria(Criteria.where("id").isEqualTo(id))
        return mongoTemplate.findOne(query)
    }

    fun create(productMongo: ProductMongo) {
        mongoTemplate.insert<ProductMongo>(productMongo)
    }

    fun findAll(): List<ProductMongo> {
        return mongoTemplate.findAll(ProductMongo::class.java)
    }
}