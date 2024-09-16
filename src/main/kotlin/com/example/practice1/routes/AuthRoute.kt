package com.example.practice1.routes

import com.example.practice1.JwtAuthenticationFilter
import com.example.practice1.handlers.AuthHandler
import com.example.practice1.services.AuthService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AuthRoute(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {
    @Bean
    fun authHandler(authService: AuthService) = AuthHandler(authService)

    @Bean
    fun authRoutes(authHandler: AuthHandler) = coRouter {
//        POST("/authentication/login", authHandler::login)
//        POST("/authentication/signup", authHandler::signUp)
        "authentication".nest {
            POST("/login", authHandler::login)
            POST("/signup", authHandler::signUp)
        }
    }
}