package com.example.practice1.url

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.POST
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UrlRoute {

    @Bean
    fun urlHandler(urlShortenerService: UrlShortenerService) = UrlHandler(urlShortenerService)

    @Bean
    fun routes(urlHandler: UrlHandler) = coRouter {
        POST("url/shorten", urlHandler::shortenUrl)

        GET("url/{shortUrl}") {
            urlHandler.redirectToOriginal(it)
        }
        
        GET("url"){
            urlHandler.getAllUrls()
        }
    }

}
