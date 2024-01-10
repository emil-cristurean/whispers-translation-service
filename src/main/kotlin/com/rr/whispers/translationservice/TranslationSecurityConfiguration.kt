package com.rr.whispers.translationservice

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Order(93)
@EnableWebSecurity
class TranslationSecurityConfiguration {
    @Bean
    @Throws(Exception::class)
    fun openApiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
        http.sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        http.securityMatcher("/api/app/**")
        http.authorizeHttpRequests {
            it.requestMatchers("/api/app/**").permitAll()
        }
        return http.build()
    }
}
