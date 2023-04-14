package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.Delivery
import java.util.*

const val DELIVERY_CREATED = "DELIVERY_CREATED"
const val DELIVERY_CANCELED = "DELIVERY_CANCELED"
const val DELIVERY_COMPLETED = "DELIVERY_COMPLETED"
const val DELIVERY_TIMESLOT_CHANGED = "DELIVERY_TIMESLOT_CHANGED"
const val DELIVERY_ADDRESS_CHANGED = "DELIVERY_ADDRESS_CHANGED"
const val DELIVERY_STATE_CHANGED = "DELIVERY_STATE_CHANGED"

@DomainEvent(name = DELIVERY_CREATED)
data class DeliveryCreatedEvent(
    val deliveryId: UUID,
    val timeslotId: UUID,
    val basketId: UUID,
    //val addressId: String,
) : Event<DeliveryAggregate>(
    name = DELIVERY_CREATED,
)

@DomainEvent(name = DELIVERY_CANCELED)
data class DeliveryCanceledEvent(
    val deliveryId: UUID,
) : Event<DeliveryAggregate>(
    name = DELIVERY_CANCELED,
)

@DomainEvent(name = DELIVERY_COMPLETED)
data class DeliveryCompletedEvent(
    val deliveryId: UUID,
) : Event<DeliveryAggregate>(
    name = DELIVERY_COMPLETED,
)

@DomainEvent(name = DELIVERY_TIMESLOT_CHANGED)
data class DeliveryTimeslotChangedEvent(
    val deliveryId: UUID,
    val timeslotId: UUID
) : Event<DeliveryAggregate>(
    name = DELIVERY_TIMESLOT_CHANGED,
)

//@DomainEvent(name = DELIVERY_ADDRESS_CHANGED)
//data class DeliveryAddressChangedEvent(
//    val deliveryId: UUID,
//    //val addressId: String
//) : Event<DeliveryAggregate>(
//    name = DELIVERY_ADDRESS_CHANGED,
//)

@DomainEvent(name = DELIVERY_STATE_CHANGED)
data class DeliveryStatusChangedEvent(
    val deliveryId: UUID,
    val status: Delivery.DeliveryStatus
) : Event<DeliveryAggregate>(
    name = DELIVERY_STATE_CHANGED,
)

