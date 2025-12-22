package com.controlefinanceiro.api.config

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.security.web.SecurityFilterChain
import kotlin.jvm.java

class SecurityConfigTest {

    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                // garante contexto web (servlet)
                DispatcherServletAutoConfiguration::class.java,
                // garante o stack de security web
                SecurityAutoConfiguration::class.java,
                UserDetailsServiceAutoConfiguration::class.java
            )
        )
        .withUserConfiguration(SecurityConfig::class.java)

    @Test
    fun `deve registrar SecurityFilterChain no contexto`() {
        val contextRunner = WebApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                SecurityAutoConfiguration::class.java,
                UserDetailsServiceAutoConfiguration::class.java
            )
        )
        .withUserConfiguration(SecurityConfig::class.java)
    }
}
