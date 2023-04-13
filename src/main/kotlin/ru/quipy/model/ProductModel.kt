package com.itmo.microservices.demo.users.api.model

import java.util.*

data class ProductModel(val productId: UUID = UUID.randomUUID(), val name: String, val count: Int) {
    fun addProduct() {

    }
}

