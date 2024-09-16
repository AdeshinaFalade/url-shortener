package com.example.practice1.services

import com.example.practice1.User
import com.example.practice1.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JWTService
) {

    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    //check if user exists
    suspend fun userExists(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }

    suspend fun signup(email: String, password: String, name: String): User {
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(email = email, password = encodedPassword, name = name)
        return userRepository.save(user)
    }

    suspend fun login(email: String, password: String): Map<String, String>? {
        return userRepository.findByEmail(email)
            ?.let { user ->
                if (passwordEncoder.matches(password, user.password) && user.id != null) {
                    val token = jwtService.generateToken(email, user.id)
                    val refreshToken = jwtService.generateRefreshToken(email)
                    val updatedUser = user.copy(refreshToken = refreshToken)
                    userRepository.save(updatedUser).run {
                        mapOf(
                            "name" to user.name,
                            "accessToken" to token,
                            "refreshToken" to refreshToken
                        )
                    }
                } else {
                    null
                }
            }
    }

    suspend fun refreshToken(email: String, refreshToken: String): Map<String, String>? {
        return userRepository.findByEmail(email)
            ?.let { user ->
                if (user.refreshToken == refreshToken && jwtService.validateToken(refreshToken) && user.id != null) {
                    val token = jwtService.generateToken(email, user.id)
                    val newRefreshToken = jwtService.generateRefreshToken(email)
                    userRepository.save(user.copy(refreshToken = newRefreshToken)).run {
                        mapOf(
                            "accessToken" to token,
                            "refreshToken" to newRefreshToken
                        )
                    }
                } else {
                    null
                }
            }
    }
}