package ru.quipy.dto

import java.beans.ConstructorProperties

data class DeliveryTimeslotChangedDTO
@ConstructorProperties("id", "timeslotId")
constructor(val id: String, val timeslotId: String)