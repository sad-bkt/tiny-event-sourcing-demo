package ru.quipy.dto

import java.beans.ConstructorProperties
import java.util.*

data class DeliveryTimeslotChangedDTO
@ConstructorProperties("deliveryId", "timeslotId")
constructor(val deliveryId: String, val timeslotId: String)