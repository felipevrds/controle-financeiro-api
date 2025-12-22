package com.controlefinanceiro.api.adapters.inbound.security

import com.controlefinanceiro.api.exception.UnauthorizedException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

private const val API_KEY_HEADER = "api-key"
private const val EXPECTED_API_KEY = "aXRhw7o="

class ApiKeyFilter : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        !request.requestURI.startsWith("/v1")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val apiKey = request.getHeader(API_KEY_HEADER)
        if (apiKey != EXPECTED_API_KEY) {
            throw UnauthorizedException("Não autorizado")
        }
        filterChain.doFilter(request, response)
    }
}
