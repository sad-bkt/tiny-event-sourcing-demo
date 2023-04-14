package ru.quipy.dto

import java.beans.ConstructorProperties

data class DeliveryIdDTO
@ConstructorProperties("deliveryId")
constructor(val deliveryId: String)