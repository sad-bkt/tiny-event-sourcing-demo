package ru.quipy.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.api.DeliveryAggregate
import ru.quipy.logic.Delivery
import java.util.*

@Configuration
public class DeliveryBoundedContextConfig {
    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun trackEsService(): EventSourcingService<UUID, DeliveryAggregate, Delivery> =
        eventSourcingServiceFactory.create()
}