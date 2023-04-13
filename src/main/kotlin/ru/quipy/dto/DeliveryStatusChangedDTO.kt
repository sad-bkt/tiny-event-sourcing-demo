package ru.quipy.dto

import ru.quipy.logic.Delivery
import java.beans.ConstructorProperties

data class DeliveryStatusChangedDTO
@ConstructorProperties("id", "status")
constructor(val id: String, val status: Delivery.DeliveryStatus)