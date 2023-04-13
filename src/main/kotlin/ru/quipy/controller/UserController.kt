package ru.quipy.controller

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserDeletedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.service.UserRepository
import ru.quipy.service.UserService
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val userService: UserService
) {
    @PostMapping
    @Operation(
        summary = "Register new user",
        responses = [
            ApiResponse(description = "OK", responseCode = "201", content = [Content()]),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ]
    )
    fun register(@RequestBody request: RegistrationRequest): ResponseEntity<Any> {
        var resp: AppUserModel? = null
        if (request.role != "user")
            return ResponseEntity<Any>("You can create only regular user", HttpStatus.BAD_REQUEST)
        runCatching {
            resp = userService.register(request)
        }.onSuccess {
            return ResponseEntity<Any>(resp, HttpStatus.CREATED)
        }.onFailure { e ->
            return ResponseEntity<Any>(e.message, HttpStatus.CONFLICT)
        }
        return ResponseEntity<Any>(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Register user with any role",
        responses = [
            ApiResponse(description = "OK", responseCode = "201", content = [Content()]),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun registerAdmin(@RequestBody request: RegistrationRequest): ResponseEntity<Any> {
        var resp: AppUserModel? = null
        runCatching {
            resp = userService.register(request)
        }.onSuccess {
            return ResponseEntity<Any>(resp, HttpStatus.CREATED)
        }
        return ResponseEntity<Any>(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current user info",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getAccountData(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails): ResponseEntity<Any> {
        val userData = userService.getAccountData(user) ?: return ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        return ResponseEntity<Any>(userData, HttpStatus.OK)
    }

    @DeleteMapping("/me")
    @Operation(
        summary = "Delete current user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteCurrentUser(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails): ResponseEntity<Any> {
        var res: UserDeletedEvent? = null
        runCatching {
            res = userService.deleteUser(user.username) ?: return ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }.onSuccess {
            return ResponseEntity<Any>(res, HttpStatus.OK)
        }
        return ResponseEntity<Any>(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @DeleteMapping("/admin/{email}")
    @Operation(
        summary = "Delete any user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteAnyUser(@PathVariable email: String): ResponseEntity<Any> {
        var res: UserDeletedEvent? = null
        runCatching {
            res = userService.deleteUser(email) ?: return ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }.onSuccess {
            return ResponseEntity<Any>(res, HttpStatus.OK)
        }.onFailure { e -> return ResponseEntity<Any>(e.message, HttpStatus.INTERNAL_SERVER_ERROR) }
        return ResponseEntity<Any>(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}