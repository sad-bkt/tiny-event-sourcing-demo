package ru.quipy.entity

import com.itmo.microservices.demo.users.api.model.AppUserModel
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import ru.quipy.model.TimeslotModel
import java.util.*

@Document
class Timeslot(
    @MongoId
    val id: ObjectId = ObjectId.get(),
    val time: String,
    var busy: Boolean
) {
    fun toModel(): TimeslotModel = kotlin.runCatching {
        TimeslotModel(
            time = this.time,
            busy = this.busy
        )
    }.getOrElse { exception -> throw IllegalStateException("Some of timeslot fields are incorrect", exception) }

}