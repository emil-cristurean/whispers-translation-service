package com.rr.whispers.translationservice

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint

@Configuration
@Order(90)
@EnableWebSecurity(debug = true)
class TranslationSecurityConfiguration {
    @Autowired
    lateinit var basicCredentials: BasicCredentials

    @Bean
    @Order(2)
    @Throws(Exception::class)
    fun basicAuthSecurityTechnicalEndpointsFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
        http.sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        http.securityMatcher("/api/app/**")

        http.httpBasic { basic: HttpBasicConfigurer<HttpSecurity?> ->
            basic.authenticationEntryPoint(basicAuthSecurity)
        }
        http.authorizeHttpRequests { request ->
            request.requestMatchers("/api/app/**").hasAnyRole("USER")
        }
        return http.build()
    }

    @Autowired
    @Throws(Exception::class)
    fun basicAuthenticationInMemoryUserDetailsManager(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication().withUser(basicCredentials.userDetails)
    }

    @get:Bean
    val basicAuthSecurity: CustomBasicAuthenticationEntryPoint
        get() = CustomBasicAuthenticationEntryPoint()

    class CustomBasicAuthenticationEntryPoint : BasicAuthenticationEntryPoint() {
        private val logger = LoggerFactory.getLogger(this::class.java)

        override fun commence(
            request: HttpServletRequest?,
            response: HttpServletResponse,
            authException: AuthenticationException?
        ) {
            logger.info("request: $request")
            response.status = HttpStatus.UNAUTHORIZED.value()
        }

        override fun afterPropertiesSet() {
            realmName = "PRIVATE"
            super.afterPropertiesSet()
        }
    }

    @Configuration
    @ConfigurationProperties("whsprs.user.security.basic-authentication.credentials")
    class BasicCredentials {
        var username: String? = null
        var password: String? = null
        val userDetails: UserDetails
            get() =
                User(
                    username,
                    "{noop}$password",
                    listOf(SimpleGrantedAuthority("ROLE_USER"))
                )
    }
}
