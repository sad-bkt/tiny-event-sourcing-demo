package ru.quipy.dto

import java.beans.ConstructorProperties
import java.util.UUID

data class ProductDTO
@ConstructorProperties("name", "count")
constructor(val name: String, val count: Int)
