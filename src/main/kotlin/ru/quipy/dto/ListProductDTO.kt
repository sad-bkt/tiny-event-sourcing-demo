package ru.quipy.dto

import java.beans.ConstructorProperties
import java.util.*

data class ListProductDTO
@ConstructorProperties("name", "productId", "count")
constructor(val name: String, val productId: UUID, val count: Int)
