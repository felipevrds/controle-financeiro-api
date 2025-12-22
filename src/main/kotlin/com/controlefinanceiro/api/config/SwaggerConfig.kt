package com.controlefinanceiro.api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class SwaggerConfig {
    @Bean
    fun openApi(): OpenAPI =
        OpenAPI().info(
            Info()
                .title("Controle Financeiro API")
                .description("API REST para controle de gastos e ganhos (desafio)")
                .version("v1")
        )
}
