package com.example.practice1.url

import com.example.practice1.UrlMapping
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.web.util.UriBuilder
import java.time.LocalDateTime
import java.util.*

@Service
class UrlShortenerService(private val urlMappingRepository: UrlMappingRepository) {

    private val baseUrl = "http://localhost:8080/url/"
    private val defaultExpiryDays = 30 // Default expiry in days

    suspend fun shortenUrl(originalUrl: String, expiryDays: Int? = defaultExpiryDays): String {
        val existingMapping = urlMappingRepository.findByOriginalUrl(originalUrl)
        if (existingMapping != null) {
            return existingMapping.shortUrl
        }

        val shortUrl = generateShortUrl()
        val expiryDate = expiryDays?.let { LocalDateTime.now().plusDays(it.toLong()) }
        val urlMapping = UrlMapping(
            id = UUID.randomUUID().toString(),
            originalUrl = originalUrl,
            shortUrl = baseUrl + shortUrl,
            expiryDate = expiryDate
        )
        urlMappingRepository.insertUrlMapping(
            urlMapping.id,
            urlMapping.originalUrl,
            urlMapping.shortUrl,
            urlMapping.expiryDate
        )
        return urlMapping.shortUrl
    }

    suspend fun getOriginalUrl(shortUrl: String): String? {
        val urlMapping = urlMappingRepository.findByShortUrl(baseUrl + shortUrl)
        return if (urlMapping != null && !isExpired(urlMapping)) {
            urlMapping.originalUrl
        } else {
            null
        }
    }

    private fun generateShortUrl(): String {
        // Simple short URL generation logic (e.g., base62 encoding)
        return UUID.randomUUID().toString().take(6)
    }

    private fun isExpired(urlMapping: UrlMapping): Boolean {
        return urlMapping.expiryDate?.isBefore(LocalDateTime.now()) == true
    }

    suspend fun getAllUrlMappings() = urlMappingRepository.findAll().toList()
}
