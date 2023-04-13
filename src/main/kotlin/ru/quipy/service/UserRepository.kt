package ru.quipy.service

//import org.springframework.stereotype.Repository
import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.entity.AppUser

interface UserRepository : MongoRepository<AppUser, String> {
    @org.springframework.lang.Nullable
    fun findOneByEmail(email: String): AppUser?

//    fun deleteByEmail(email: String): AppUser
}