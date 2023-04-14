package ru.quipy.dto

import java.beans.ConstructorProperties
import java.util.*

data class ListProductDTO
@ConstructorProperties("productId", "count")
constructor(val productId: String, val count: Int)
