package com.rr.whispers.translationservice.translations.adapter

import feign.Logger
import feign.RequestInterceptor
import feign.RequestTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class LocalizeClientConfiguration(
    @Value("\${whsprs.third-party-systems.localize.api.token}") val apiToken: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun localizeClientRequestInterceptor(): RequestInterceptor {
        logger.info("apiToken: $apiToken")

        return RequestInterceptor { requestTemplate: RequestTemplate ->
            requestTemplate.header("X-Api-Token", apiToken)
        }
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }
}
