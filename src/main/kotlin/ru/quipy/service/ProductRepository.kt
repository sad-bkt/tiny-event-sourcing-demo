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
import java.util.*

@Repository
class ProductRepository(val mongoTemplate: MongoTemplate) {

    fun findOneByProductId(id: UUID): ProductMongo? {
        val query = Query()
        query.addCriteria(Criteria.where("productId").isEqualTo(id))
        return mongoTemplate.findOne(query)
    }

    fun create(productMongo: ProductMongo): ProductMongo {
        return mongoTemplate.insert<ProductMongo>(productMongo)
    }

    fun findAll(): List<ProductMongo> {
        return mongoTemplate.findAll(ProductMongo::class.java)
    }

    fun delete(id: UUID): ProductMongo? {
        val query = Query()
        query.addCriteria(Criteria.where("productId").isEqualTo(id))
        return mongoTemplate.findAndRemove(query, ProductMongo::class.java)
    }

    fun changeCount(id: UUID, expectedCount: Int, updatedCount: Int): ProductMongo? {
        val query = Query()
        query.addCriteria(Criteria.where("productId").isEqualTo(id).and("productCount").isEqualTo(expectedCount))
        val update: Update = Update().set("productCount", updatedCount)
        return mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            ProductMongo::class.java
        )
    }
}