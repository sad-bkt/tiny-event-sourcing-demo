package ru.quipy.dto

import java.beans.ConstructorProperties

data class ListProductDTO
@ConstructorProperties("productId", "count")
constructor(val productId: String, val count: Int)
