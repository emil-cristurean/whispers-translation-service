package com.rr.whispers.translationservice

import com.rr.whispers.commons.authentication.WhsprsDefaultSecurityConfig
import feign.okhttp.OkHttpClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@EnableFeignClients
@WhsprsDefaultSecurityConfig.EnableWhsprsAuth
@SpringBootApplication
class TranslationServiceApplication {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun client(): OkHttpClient {
        return OkHttpClient()
    }
}

fun main(args: Array<String>) {
    runApplication<TranslationServiceApplication>(*args)
}
