package com.example.practice1.user

import com.example.practice1.User
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param


interface UserRepository : CoroutineCrudRepository<User, Long> {
    // language=SQL
    @Query(
        """
        SELECT * FROM users WHERE name ILIKE :name
        LIMIT 1
    """
    )
    suspend fun findByName(@Param("name") name: String): User?

    @Query(
        """
        SELECT * FROM users
        OFFSET :startIndex LIMIT :pageSize
    """
    )
    suspend fun getAllUsers(@Param("startIndex") startIndex: Int, @Param("pageSize") pageSize: Int): List<User>
}
