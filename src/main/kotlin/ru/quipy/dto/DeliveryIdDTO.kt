package ru.quipy.dto

import ru.quipy.logic.Delivery
import java.beans.ConstructorProperties

data class DeliveryIdDTO
@ConstructorProperties("deliveryId")
constructor(val deliveryId: String)