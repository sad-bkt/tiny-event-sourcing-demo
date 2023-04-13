package ru.quipy.dto

import java.beans.ConstructorProperties

data class ProductDTO
@ConstructorProperties("name", "count")
constructor(val name: String, val count: Int)
