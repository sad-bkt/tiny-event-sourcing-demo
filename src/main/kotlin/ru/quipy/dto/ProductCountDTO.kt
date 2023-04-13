package ru.quipy.dto

import java.beans.ConstructorProperties

data class ProductCountDTO
@ConstructorProperties("count")
constructor(val count: Int)
