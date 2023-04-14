package ru.quipy.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.api.*
import java.util.*

class Delivery : AggregateState<UUID, DeliveryAggregate> {
    private lateinit var deliveryId: UUID
    private lateinit var timeslotId: UUID
    private lateinit var addressId: String
    private lateinit var deliveryStatus: DeliveryStatus
    // private var basket: MutableMap<UUID, Int> = mutableMapOf()
    private lateinit var basketId: UUID
    enum class DeliveryStatus {
        IN_DELIVERY,
        COMPLETED,
        CANCELED
    }

    fun getBasketId() = basketId
    override fun getId() = deliveryId

    fun getDeliveryStatus() = deliveryStatus

    fun getTimeslotId() = timeslotId

    fun getAddressId() = addressId

    fun createNewDelivery(id: UUID = UUID.randomUUID(), timeslotId: UUID, basketId: UUID) : DeliveryCreatedEvent {
        return DeliveryCreatedEvent(deliveryId = id, timeslotId = timeslotId, basketId = basketId)
    }

    fun cancelDelivery(id: UUID) : DeliveryCanceledEvent {
        return DeliveryCanceledEvent(deliveryId = id)
    }

    fun completeDelivery(id: UUID) : DeliveryCompletedEvent {
        return DeliveryCompletedEvent(deliveryId = id)
    }

    fun changeDeliveryTimeslot(id: UUID, timeslotId: UUID) : DeliveryTimeslotChangedEvent {
        return DeliveryTimeslotChangedEvent(deliveryId = id, timeslotId = timeslotId)
    }

//    fun changeDeliveryAddress(id: UUID, timeslotId: String) : DeliveryAddressChangedEvent {
//        return DeliveryAddressChangedEvent(deliveryId = id, addressId = addressId)
//    }

    fun changeDeliveryStatus(id: UUID, status: DeliveryStatus) : DeliveryStatusChangedEvent {
        return DeliveryStatusChangedEvent(deliveryId = id, status = status)
    }

    @StateTransitionFunc
    fun createNewDelivery(event: DeliveryCreatedEvent) {
        deliveryId = event.deliveryId
        timeslotId = event.timeslotId
        deliveryStatus = DeliveryStatus.IN_DELIVERY
        basketId = event.basketId
    }

    @StateTransitionFunc
    fun cancelDelivery(event: DeliveryCanceledEvent) {
        deliveryId = event.deliveryId
        deliveryStatus = DeliveryStatus.CANCELED
    }

    @StateTransitionFunc
    fun completeDelivery(event: DeliveryCompletedEvent) {
        deliveryId = event.deliveryId
        deliveryStatus = DeliveryStatus.COMPLETED
    }

    @StateTransitionFunc
    fun changeDeliveryTimeslot(event: DeliveryTimeslotChangedEvent) {
        deliveryId = event.deliveryId
        timeslotId = event.timeslotId
    }

//    @StateTransitionFunc
//    fun changeDeliveryAddress(event: DeliveryAddressChangedEvent) {
//        deliveryId = event.deliveryId
//        addressId = event.addressId
//    }

    @StateTransitionFunc
    fun changeDeliveryStatus(event: DeliveryStatusChangedEvent) {
        deliveryId = event.deliveryId
        deliveryStatus = event.status
    }
}