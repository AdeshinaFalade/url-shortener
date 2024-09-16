package com.example.practice1.routes

import com.example.practice1.handlers.UrlHandler
import com.example.practice1.services.UrlShortenerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UrlRoute {

    @Bean
    fun urlHandler(urlShortenerService: UrlShortenerService) = UrlHandler(urlShortenerService)

    @Bean
    fun urlRoutes(urlHandler: UrlHandler) = coRouter {
        POST("url/shorten", urlHandler::shortenUrl)

        GET("url/{shortUrl}") {
            urlHandler.redirectToOriginal(it)
        }

        GET("url") {
            urlHandler.getAllUrls(it)
        }
    }

}
