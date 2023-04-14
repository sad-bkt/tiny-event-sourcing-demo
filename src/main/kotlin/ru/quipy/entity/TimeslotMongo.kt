package ru.quipy.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import ru.quipy.model.TimeslotModel
import java.util.*

@Document
class TimeslotMongo(
    @MongoId
    val id: UUID,
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