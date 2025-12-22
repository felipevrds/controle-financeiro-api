package com.controlefinanceiro.api.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class SwaggerConfigTest {

    @Test
    fun `openApi deve criar OpenAPI com infos`() {
        val config = SwaggerConfig()
        val api = config.openApi()

        assertNotNull(api)
        assertNotNull(api.info)
        assertEquals("Controle Financeiro API", api.info.title)
        assertEquals("API REST para controle de gastos e ganhos (desafio)", api.info.description)
        assertEquals("v1", api.info.version)
    }
}
