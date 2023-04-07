package ru.quipy.entity

import com.itmo.microservices.demo.users.api.model.AppUserModel
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

@Document
class AppUser(
    @MongoId
    val email: String,
    var name: String? = null,
    var surname: String? = null,
    var password: String? = null,
    val aggregateId: UUID
) {

//    constructor()
//
//    constructor(name: String?, surname: String?, email: String?, password: String?) {
//        this.name = name
//        this.surname = surname
//        this.email = email
//        this.password = password
//    }

    fun toModel(): AppUserModel = kotlin.runCatching {
        AppUserModel(
            name = this.name!!,
            surname = this.surname!!,
            email = this.email!!,
            password = this.password!!,
            aggregateId = this.aggregateId!!
        )
    }.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }
}