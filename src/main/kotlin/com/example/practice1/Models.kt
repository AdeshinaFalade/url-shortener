package com.example.practice1

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table("users")
data class User(@Id val id: Long?, val name: String)

data class CreateUserDto(val name: String)

@Table("url_mappings")
data class UrlMapping(
    @Id
    val id: String,
    
    @Column("original_url")
    val originalUrl: String,

    @Column("short_url")
    val shortUrl: String,

    @Column("date_created")
    val dateCreated: LocalDateTime = LocalDateTime.now(),

    @Column("expiry_date")
    val expiryDate: LocalDateTime? = null

)