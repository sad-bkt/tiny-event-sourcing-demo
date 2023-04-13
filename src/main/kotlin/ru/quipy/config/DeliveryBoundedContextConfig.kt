package ru.quipy.config

import com.mongodb.client.MongoClientFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.api.DeliveryAggregate
import ru.quipy.logic.Delivery
import java.util.*

@Configuration
public class DeliveryBoundedContextConfig {
//    @Autowired
//    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

//    @Bean
//    fun deliveryEsService(): EventSourcingService<UUID, DeliveryAggregate, Delivery> =
//        eventSourcingServiceFactory.create()
//
//    @Autowired
//    private lateinit var mongoTemplate: MongoTemplate
//
//    @Bean
//    fun mongoTemplate(): MongoTemplate = mongoTemplate
}