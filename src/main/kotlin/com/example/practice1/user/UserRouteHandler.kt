package com.example.practice1

import com.example.practice1.user.UserRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class UserRouteHandler(private val userRepository: UserRepository) {
    suspend fun getUsers(request: ServerRequest): ServerResponse {
        val name = request.queryParam("name").orElse(null)?.toString()

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
        return userRepository.findById(id)?.let { user ->
            ServerResponse.ok().bodyValueAndAwait(user)
        } ?: ServerResponse.notFound().buildAndAwait()
    }

    suspend fun createUser(request: ServerRequest): ServerResponse {
        val userDto = request.awaitBody<CreateUserDto>()
        val user = User(null, userDto.name)
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