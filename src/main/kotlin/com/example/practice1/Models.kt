package com.example.practice1

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table("users")
data class User(
    @Id val id: Long? = null,
    val name: String,
    val email: String,
    val password: String,
    @Column("refreshtoken")
    val refreshToken: String? = null
)

data class CreateUserDto(
    val name: String?,
    val email: String?,
    val password: String?
) {
    fun validate(): String? {
        return when {
            email.isNullOrBlank() -> "Email is required."
            name.isNullOrBlank() -> "Name is required."
            password.isNullOrBlank() -> "Password is required."
            password.length < 6 -> "Password should contain at least 6 characters."
            else -> null // No errors
        }
    }
}

data class LoginRequest(
    val email: String,
    val password: String,
)

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
    val expiryDate: LocalDateTime? = null,

    @Column("user_id")
    val userId: Long? = null

)