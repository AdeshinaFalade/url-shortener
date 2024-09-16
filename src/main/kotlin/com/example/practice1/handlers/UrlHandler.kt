package com.example.practice1.handlers

import com.example.practice1.services.UrlShortenerService
import io.jsonwebtoken.Claims
import org.springframework.web.reactive.function.server.*
import java.net.URI

class UrlHandler(private val urlShortenerService: UrlShortenerService) {

    suspend fun shortenUrl(request: ServerRequest): ServerResponse {
        val claims = request.attribute("claims")
            .orElseThrow { IllegalStateException("No claims found in request") } as Claims

        val email = claims.subject
        val userId = claims["userId"].toString().toLong()

        val originalUrl = request.awaitBody<String>()
        val expiryDays = request.queryParamOrNull("expiryDays")?.toInt() ?: 30
        val shortUrl = urlShortenerService.shortenUrl(userId, originalUrl, expiryDays)
        return ServerResponse.ok().bodyValueAndAwait(shortUrl)
    }

    suspend fun redirectToOriginal(request: ServerRequest): ServerResponse {
        val shortUrl = request.pathVariable("shortUrl")
        val originalUrl = urlShortenerService.getOriginalUrl(shortUrl)?.replace("\\", "")?.replace("\"", "")

        return if (originalUrl != null) {
            val formattedUrl = if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                "https://$originalUrl"
            } else {
                originalUrl
            }
            ServerResponse.temporaryRedirect(URI.create(formattedUrl)).buildAndAwait()
        } else {
            ServerResponse.notFound().buildAndAwait()
        }
    }

    suspend fun getAllUrls(request: ServerRequest): ServerResponse {
        val claims = request.attribute("claims")
            .orElseThrow { IllegalStateException("No claims found in request") } as Claims

        val email = claims.subject
        val userId = claims["userId"].toString().toInt()

        val urls = urlShortenerService.getUrlMappingsByUser(userId) ?: emptyList()
        return ServerResponse.ok().bodyValueAndAwait(urls)
    }
}
