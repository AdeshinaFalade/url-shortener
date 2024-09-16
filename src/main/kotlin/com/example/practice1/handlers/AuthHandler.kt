package com.example.practice1.handlers

import com.example.practice1.CreateUserDto
import com.example.practice1.LoginRequest
import com.example.practice1.services.AuthService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

class AuthHandler(private val authService: AuthService) {
    suspend fun signUp(request: ServerRequest): ServerResponse {
        val userDto: CreateUserDto
        try {
            userDto = request.awaitBody<CreateUserDto>()
        } catch (e: Exception) {
            return ServerResponse.badRequest().bodyValueAndAwait("Invalid request body")
        }

        // Validate the userDto object
        userDto.validate()?.let { errorMessage ->
            return ServerResponse.badRequest().bodyValueAndAwait(errorMessage)
        }
        if (authService.userExists(userDto.email!!)) {
            return ServerResponse.badRequest().bodyValueAndAwait("User with the provided email already exists.")
        }
        
        try {
            authService.signup(email = userDto.email, name = userDto.name!!, password = userDto.password!!)
            return ServerResponse.ok().bodyValueAndAwait("Signup completed, proceed to login.")
        } catch (e: Exception) {
            e.printStackTrace()
            return ServerResponse.badRequest().bodyValueAndAwait(e.message ?: "An error occurred")
        }
    }

    suspend fun login(request: ServerRequest): ServerResponse {
        val loginRequest = request.awaitBody<LoginRequest>()
        val response = authService.login(email = loginRequest.email, password = loginRequest.password)

        return if (response == null) {
            ServerResponse.badRequest().bodyValueAndAwait("Invalid email or password")
        } else {
            ServerResponse.ok().bodyValueAndAwait(response)
        }
    }
}