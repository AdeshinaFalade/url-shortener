package com.example.practice1.url

import org.springframework.web.reactive.function.server.*
import java.net.URI

class UrlHandler(private val urlShortenerService: UrlShortenerService) {

    suspend fun shortenUrl(request: ServerRequest): ServerResponse {
        val originalUrl = request.awaitBody<String>()
        val expiryDays = request.queryParamOrNull("expiryDays")?.toInt() ?: 30
        val shortUrl = urlShortenerService.shortenUrl(originalUrl, expiryDays)
        return ServerResponse.ok().bodyValueAndAwait(shortUrl)
    }

    suspend fun redirectToOriginal(request: ServerRequest): ServerResponse {
        val shortUrl = request.pathVariable("shortUrl")
        val originalUrl = urlShortenerService.getOriginalUrl(shortUrl)?.replace("\\", "")?.replace("\"", "")
        println(originalUrl)
        return if (originalUrl != null) {
            val formattedUrl = if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                "http://$originalUrl"
            } else {
                originalUrl
            }
            ServerResponse.temporaryRedirect(URI.create(formattedUrl)).buildAndAwait()
        } else {
            ServerResponse.notFound().buildAndAwait()
        }
    }

    suspend fun getAllUrls(): ServerResponse {
        val urls = urlShortenerService.getAllUrlMappings()
        return ServerResponse.ok().bodyValueAndAwait(urls)
    }
}
