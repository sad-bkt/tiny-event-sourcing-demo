package ru.quipy.dto

import java.beans.ConstructorProperties

data class DeliveryTimeslotChangedDTO
@ConstructorProperties("deliveryId", "timeslotId")
constructor(val deliveryId: String, val timeslotId: String)