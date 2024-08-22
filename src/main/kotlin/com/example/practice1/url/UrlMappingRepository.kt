package com.example.practice1.url

import com.example.practice1.UrlMapping
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UrlMappingRepository : CoroutineCrudRepository<UrlMapping, String> {
    @Query(
        """
        SELECT * FROM url_mappings WHERE original_url ILIKE :originalUrl
        LIMIT 1
    """
    )
    suspend fun findByOriginalUrl(@Param("originalUrl") originalUrl: String): UrlMapping?

    @Query(
        """
        SELECT * FROM url_mappings WHERE short_url ILIKE :shortUrl
        LIMIT 1
    """
    )
    suspend fun findByShortUrl(@Param("shortUrl") shortUrl: String): UrlMapping?
    
    @Modifying
    @Query(
        """
        INSERT INTO url_mappings (id, original_url, short_url, expiry_date, date_created) 
        VALUES (:id, :originalUrl, :shortUrl, :expiryDate, :dateCreated)
        """
    )
    suspend fun insertUrlMapping(
        @Param("id") id: String,
        @Param("originalUrl") originalUrl: String,
        @Param("shortUrl") shortUrl: String,
        @Param("expiryDate") expiryDate: LocalDateTime?,
        @Param("dateCreated") dateCreated: LocalDateTime = LocalDateTime.now()
    )
}