package com.example.practice1.handlers

import com.example.practice1.CreateUserDto
import com.example.practice1.User
import com.example.practice1.repositories.UserRepository
import io.jsonwebtoken.Claims
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class UserRouteHandler(private val userRepository: UserRepository) {
    suspend fun getUsers(request: ServerRequest): ServerResponse {
        val name = request.queryParam("name").orElse(null)?.toString()
        val claims = request.attribute("claims")
            .orElseThrow { IllegalStateException("No claims found in request") } as Claims

        val email = claims.subject

        return if (name != null) {
            userRepository.findByName(name)?.let { user ->
                ServerResponse.ok().bodyValueAndAwait(user)
            } ?: ServerResponse.notFound().buildAndAwait()
        } else {
            val page = request.queryParamOrNull("pageNumber")?.toInt() ?: 1
            val pageSize = request.queryParamOrNull("size")?.toInt() ?: 5
            val startIndex = (page - 1) * pageSize

            if (page < 1 || pageSize < 1) return ServerResponse.badRequest()
                .bodyValueAndAwait("Invalid page or page size")

            val users = userRepository.getAllUsers(startIndex, pageSize)
            ServerResponse.ok().bodyValueAndAwait(users)
        }
    }

    suspend fun getUserById(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val claims = request.attribute("claims")
            .orElseThrow { IllegalStateException("No claims found in request") } as Claims

        val email = claims.subject

        return userRepository.findById(id)?.let { user ->
            ServerResponse.ok().bodyValueAndAwait(user)
        } ?: ServerResponse.notFound().buildAndAwait()
    }

    suspend fun createUser(request: ServerRequest): ServerResponse {
        val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
        val userDto = request.awaitBody<CreateUserDto>()
        val encodedPassword = passwordEncoder.encode(userDto.password)
        // Validate the userDto object
        userDto.validate()?.let { errorMessage ->
            return ServerResponse.badRequest().bodyValueAndAwait(errorMessage)
        }
        val userExists = userRepository.findByEmail(userDto.email!!) != null
        if (userExists) {
            ServerResponse.badRequest().bodyValueAndAwait("User with the provided email already exists.")
        }
        val user = User(null, name = userDto.name!!, email = userDto.email!!, password = encodedPassword)
        val savedUser = userRepository.save(user)

        val locationUri = request.uriBuilder()
            .path("/${savedUser.id}").build()

        return ServerResponse.created(locationUri).bodyValueAndAwait(savedUser)
    }

    suspend fun updateUser(request: ServerRequest): ServerResponse {
        val userDto = request.awaitBody<User>()
        if (userDto.id == null) return ServerResponse.badRequest().buildAndAwait()

        val user = userRepository.findById(userDto.id) ?: return ServerResponse.notFound().buildAndAwait()

        val updatedUser = user.copy(
            name = userDto.name
        )
        userRepository.save(updatedUser)

        return ServerResponse.ok().bodyValueAndAwait(updatedUser)
    }

    suspend fun deleteUser(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        userRepository.findById(id) ?: return ServerResponse.notFound().buildAndAwait()
        userRepository.deleteById(id)
        return ServerResponse.noContent().buildAndAwait()
    }
}