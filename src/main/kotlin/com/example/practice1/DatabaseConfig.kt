package com.example.practice1

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
@EnableR2dbcRepositories
class DatabaseConfig(
    @Value("\${db.host}") private val host: String,
    @Value("\${db.port}") private val port: Int,
    @Value("\${db.name}") private val database: String,
    @Value("\${db.username}") private val username: String,
    @Value("\${db.password}") private val password: String
) : AbstractR2dbcConfiguration() {
    @Bean
    override fun connectionFactory(): ConnectionFactory =
        PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(host)
                .database(database)
                .port(port)
                .username(username)
                .password(password)
                .build()
        )

    @Bean
    fun databaseInitializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer =
        ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            setDatabasePopulator(
                CompositeDatabasePopulator().apply {
                    addPopulators(
                        ResourceDatabasePopulator(
                            ClassPathResource("db/migration/schema.sql"),
                            ClassPathResource("db/migration/data.sql")
                        )
                    )
                }
            )
        }


}