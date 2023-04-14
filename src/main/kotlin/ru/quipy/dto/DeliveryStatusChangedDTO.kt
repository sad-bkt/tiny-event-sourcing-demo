package ru.quipy.dto

import ru.quipy.logic.Delivery
import java.beans.ConstructorProperties

data class DeliveryStatusChangedDTO
@ConstructorProperties("deliveryId", "status")
constructor(val deliveryId: String, val status: Delivery.DeliveryStatus)