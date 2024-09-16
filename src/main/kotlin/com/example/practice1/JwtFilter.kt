package com.example.practice1

import com.example.practice1.repositories.UserRepository
import com.example.practice1.services.JWTService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(private val jwtService: JWTService) : WebFilter {
    private val publicPaths = listOf(
        Regex("/authentication/.*"),
        Regex("/url/[^/]+")  // Matches /url/{shortUrl}
        // Add more public paths here as needed
    )

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestPath = exchange.request.uri.path

        val token = extractToken(exchange)

        // Check if the path is public; if so, skip the filter
        // Check if the current request is for a public path
        if (publicPaths.any { it.matches(requestPath) }) {
            if (token != null) {
                val claims = jwtService.extractAllClaims(token)
                exchange.attributes["claims"] = claims
            }
            return chain.filter(exchange)
        }


//        val email = extractEmailFromToken(token) ?: ""
        return if (token != null && jwtService.validateToken(token)) {
            val claims = jwtService.extractAllClaims(token)
            exchange.attributes["claims"] = claims
            chain.filter(exchange)
        } else {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            exchange.response.setComplete()
        }
    }

    private fun extractToken(exchange: ServerWebExchange): String? {
        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            null
        }
    }

    private fun extractEmailFromToken(token: String?): String? {
        return token?.let { jwtService.extractAllClaims(it).subject }
    }
}