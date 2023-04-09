package ru.quipy.service

import com.itmo.microservices.demo.users.api.model.AppUserModel
import org.springframework.data.mongodb.repository.MongoRepository
//import org.springframework.stereotype.Repository
import ru.quipy.entity.AppUser

interface UserRepository: MongoRepository<AppUser, String> {
    @org.springframework.lang.Nullable
    fun findOneByEmail(email: String): AppUser?

//    fun deleteByEmail(email: String): AppUser
}