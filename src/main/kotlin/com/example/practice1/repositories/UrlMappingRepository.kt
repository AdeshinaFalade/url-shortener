package com.example.practice1.repositories

import com.example.practice1.UrlMapping
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UrlMappingRepository : CoroutineCrudRepository<UrlMapping, String> {
    @Query(
        """
        SELECT * FROM url_mappings WHERE original_url ILIKE :originalUrl AND user_id = :userId
        LIMIT 1
    """
    )
    suspend fun findByOriginalUrl(@Param("originalUrl") originalUrl: String, @Param("userId") userId: Long): UrlMapping?

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
        INSERT INTO url_mappings (id, original_url, short_url, expiry_date, date_created, user_id)
        VALUES (:id, :originalUrl, :shortUrl, :expiryDate, :dateCreated, :userId)
        """
    )
    suspend fun insertUrlMapping(
        @Param("id") id: String,
        @Param("originalUrl") originalUrl: String,
        @Param("shortUrl") shortUrl: String,
        @Param("expiryDate") expiryDate: LocalDateTime?,
        @Param("dateCreated") dateCreated: LocalDateTime = LocalDateTime.now(),
        @Param("userId") userId: Long
    )

    @Query(
        """
        SELECT * FROM url_mappings WHERE user_id = :userId
    """
    )
    suspend fun getUrlsByUserId(@Param("userId") userId: Int): List<UrlMapping>?
}