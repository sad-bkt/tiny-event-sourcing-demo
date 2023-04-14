package ru.quipy.dto

import ru.quipy.logic.Delivery
import java.beans.ConstructorProperties

data class DeliveryListDTO
@ConstructorProperties("deliveryId", "status", "time")
constructor(val deliveryId: String, val status: Delivery.DeliveryStatus, val time: String)