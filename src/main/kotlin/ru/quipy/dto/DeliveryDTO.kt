package ru.quipy.dto

import ru.quipy.logic.Delivery
import java.beans.ConstructorProperties

data class DeliveryDTO
@ConstructorProperties("timeslotId")
constructor(
    //val status: Delivery.DeliveryStatus = Delivery.DeliveryStatus.IN_DELIVERY,
    val timeslotId: String
)