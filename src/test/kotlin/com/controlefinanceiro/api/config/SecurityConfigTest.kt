package com.controlefinanceiro.api.config

import com.controlefinanceiro.api.adapters.inbound.security.ApiKeyFilter
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

class SecurityConfigTest {

    @Test
    fun `filterChain deve construir SecurityFilterChain e registrar ApiKeyFilter antes do UsernamePasswordAuthenticationFilter`() {
        val http = mock(HttpSecurity::class.java, RETURNS_SELF)

        // build() retorna DefaultSecurityFilterChain
        val expectedChain = mock(DefaultSecurityFilterChain::class.java)
        `when`(http.build()).thenReturn(expectedChain)

        val config = SecurityConfig()

        val chain = config.filterChain(http)

        verify(http).build()
        assertSame(expectedChain, chain)

        val filterCaptor: ArgumentCaptor<OncePerRequestFilter> =
            ArgumentCaptor.forClass(OncePerRequestFilter::class.java)

        verify(http).addFilterBefore(
            filterCaptor.capture(),
            eq(UsernamePasswordAuthenticationFilter::class.java)
        )

        assertTrue(filterCaptor.value is ApiKeyFilter)
    }
}
