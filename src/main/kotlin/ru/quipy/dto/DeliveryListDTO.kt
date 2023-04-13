package ru.quipy.dto

import ru.quipy.logic.Delivery
import java.beans.ConstructorProperties

data class DeliveryListDTO
@ConstructorProperties("id", "status", "time")
constructor(val id: String, val status: Delivery.DeliveryStatus, val time: String)