package ru.quipy.controller

import com.itmo.microservices.demo.auth.api.model.AuthenticationRequest
import com.itmo.microservices.demo.auth.api.model.AuthenticationResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.service.AuthService

@RestController
@RequestMapping("/authentication")
class AuthController(private val authService: AuthService) {

    @PostMapping
    @Operation(
        summary = "Authenticate",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()]),
            ApiResponse(description = "Invalid password", responseCode = "403", content = [Content()])
        ]
    )
    fun authenticate(@RequestBody request: AuthenticationRequest): ResponseEntity<Any> {
        var resp : AuthenticationResult? = null
        kotlin.runCatching {
            resp = authService.authenticate(request)
        }.onSuccess {
            return ResponseEntity<Any>(resp, HttpStatus.OK)
        }.onFailure { e -> return ResponseEntity<Any>(e.message, HttpStatus.BAD_REQUEST) }
        return ResponseEntity<Any>(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh authentication",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Authentication error", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun refresh(authentication: Authentication): ResponseEntity<Any> {
        var resp : AuthenticationResult? = null
        kotlin.runCatching {
            resp = authService.refresh(authentication)
        }.onSuccess {
            return ResponseEntity<Any>(resp, HttpStatus.OK)
        }.onFailure { e -> return ResponseEntity<Any>(e.message, HttpStatus.BAD_REQUEST) }
        return ResponseEntity<Any>(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
