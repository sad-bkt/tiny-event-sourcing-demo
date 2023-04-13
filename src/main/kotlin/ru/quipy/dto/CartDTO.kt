package ru.quipy.dto

import ru.quipy.api.ProductAggregate
import ru.quipy.entity.AppUser
import java.beans.ConstructorProperties
import java.util.UUID

data class CartDTO
@ConstructorProperties("userId", "cartList")
constructor(val userId: AppUser, val cartList: List<ProductAggregate>)
