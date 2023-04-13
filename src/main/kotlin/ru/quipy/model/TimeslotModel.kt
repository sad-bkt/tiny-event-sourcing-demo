package ru.quipy.model

data class TimeslotModel(
    val time: String,
    val busy: Boolean) {

    // fun timeslotDetails(): timeslotDetails = User(email, password, Collections.singleton(SimpleGrantedAuthority(role)))
}