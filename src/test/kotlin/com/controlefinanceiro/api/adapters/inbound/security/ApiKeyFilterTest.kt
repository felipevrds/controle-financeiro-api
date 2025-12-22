package com.controlefinanceiro.api.adapters.inbound.security

import com.controlefinanceiro.api.exception.UnauthorizedException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import kotlin.jvm.java

class ApiKeyFilterTest {

    private val filter = ApiKeyFilter()

    @Test
    fun `deve ignorar requests fora de v1`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val chain = mock(FilterChain::class.java)

        `when`(request.requestURI).thenReturn("/actuator/health")

        filter.doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
        verifyNoInteractions(response)
    }

    @Test
    fun `deve permitir request com api-key valida`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val chain = mock(FilterChain::class.java)

        `when`(request.requestURI).thenReturn("/v1/categorias")
        `when`(request.getHeader("api-key")).thenReturn("aXRhw7o=") // ajuste se o valor real for outro

        filter.doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
        verifyNoInteractions(response)
    }

    @Test
    fun `deve bloquear request sem api-key`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val chain = mock(FilterChain::class.java)

        `when`(request.requestURI).thenReturn("/v1/categorias")
        `when`(request.getHeader("api-key")).thenReturn(null)

        assertThrows(UnauthorizedException::class.java) {
            filter.doFilter(request, response, chain)
        }

        verify(chain, never()).doFilter(any(), any())
        verifyNoInteractions(response)
    }

    @Test
    fun `deve bloquear request com api-key invalida`() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val chain = mock(FilterChain::class.java)

        `when`(request.requestURI).thenReturn("/v1/categorias")
        `when`(request.getHeader("api-key")).thenReturn("errada")

        assertThrows(UnauthorizedException::class.java) {
            filter.doFilter(request, response, chain)
        }

        verify(chain, never()).doFilter(any(), any())
        verifyNoInteractions(response)
    }
}
