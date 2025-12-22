package com.controlefinanceiro.api.config

import com.controlefinanceiro.api.adapters.inbound.security.ApiKeyFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration(proxyBeanMethods = false)
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { headers -> headers.frameOptions { it.sameOrigin() } } // <- ESSENCIAL p/ H2 Console
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/h2-console/**").permitAll()
                it.requestMatchers("/v1/**").permitAll()
                it.anyRequest().permitAll()
            }
            .addFilterBefore(ApiKeyFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
