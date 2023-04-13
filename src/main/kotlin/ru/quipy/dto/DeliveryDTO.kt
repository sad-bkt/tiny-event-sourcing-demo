package ru.quipy.dto

import ru.quipy.logic.Delivery
import java.beans.ConstructorProperties

data class DeliveryDTO
@ConstructorProperties("status", "time")
constructor(val status: Delivery.DeliveryStatus, val time: String)