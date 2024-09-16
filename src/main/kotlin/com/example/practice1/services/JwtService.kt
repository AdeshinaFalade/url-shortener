package com.example.practice1.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JWTService(
//    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expirationTime: Long
) {
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
//    private val base64Key = Base64.getEncoder().encodeToString(secretKey.encoded)

    fun generateToken(email: String, userId: Long): String {
        val claims: MutableMap<String, Any> = HashMap()
        claims["email"] = email
        claims["userId"] = userId
        return createToken(claims, email)
    }

    fun generateRefreshToken(email: String): String {
        return createToken(emptyMap(), email, refreshToken = true)
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = extractAllClaims(token)
            println("Token claims: $claims")

            // Check if the token is expired
            if (claims.expiration.before(Date())) {
                println("Token is expired")
                return false
            }
//
//            // Verify that the email matches the subject in the token
//            if (claims.subject != email) {
//                println("Email does not match. Expected: $email, Found: ${claims.subject}")
//                return false
//            }

            // Token is valid
            true
        } catch (e: Exception) {
            println("Token validation failed: ${e.message}")
            false
        }
    }

    private fun createToken(claims: Map<String, Any>, subject: String, refreshToken: Boolean = false): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuer("PracticeApp")
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + if (refreshToken) expirationTime * 10 else expirationTime))
            .signWith(secretKey)
            .compact()
    }

    fun extractAllClaims(token: String): Claims {
        val parser = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
        return parser.parseClaimsJws(token).body
    }

    private fun isTokenExpired(token: String): Boolean {
        val claims = extractAllClaims(token)
        return claims.expiration.before(Date())
    }
}