package com.example.practice1.routes

import com.example.practice1.handlers.UserRouteHandler
import com.example.practice1.repositories.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter


@Configuration
class UserRoute {

    @Bean
    fun userHandler(userRepository: UserRepository) = UserRouteHandler(userRepository)

    @Bean
    fun getAllUsersRoute(userRouteHandler: UserRouteHandler) = coRouter {

        GET("/users") {
            userRouteHandler.getUsers(it)
        }
    }

    @Bean
    fun getUserById(userRouteHandler: UserRouteHandler) = coRouter {

        GET("/users/{id}") {
            userRouteHandler.getUserById(it)
        }
    }

    @Bean
    fun createUser(userRouteHandler: UserRouteHandler) = coRouter {

        POST("/users") {
            userRouteHandler.createUser(it)
        }
    }

    @Bean
    fun updateUser(userRouteHandler: UserRouteHandler) = coRouter {

        PUT("/users") {
            userRouteHandler.updateUser(it)
        }
    }

    @Bean
    fun deleteUser(userRouteHandler: UserRouteHandler) = coRouter {

        DELETE("/users/{id}") {
            userRouteHandler.deleteUser(it)
        }
    }

}