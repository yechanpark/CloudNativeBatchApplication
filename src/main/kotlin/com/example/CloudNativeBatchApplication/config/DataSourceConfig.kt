package com.example.CloudNativeBatchApplication.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    @Value("\${spring.datasource.url}") private var dbUrl: String,
    @Value("\${spring.datasource.username}") private var dbUsername: String,
    @Value("\${spring.datasource.password}") private var dbPassword: String
) {

    @Bean
    fun dataSource(): DataSource {
        return DataSourceBuilder.create()
            .url(dbUrl)
            .username(dbUsername)
            .password(dbPassword)
            .build()
    }
}