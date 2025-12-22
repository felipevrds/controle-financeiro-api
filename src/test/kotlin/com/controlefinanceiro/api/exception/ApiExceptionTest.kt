package com.controlefinanceiro.api.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApiExceptionTest {

    @Test
    fun `ApiException deve carregar codigo e message`() {
        val ex = ApiException(codigo = "x", message = "msg")
        assertEquals("x", ex.codigo)
        assertEquals("msg", ex.message)
    }

    @Test
    fun `NotFoundException deve ter codigo correto`() {
        val ex = NotFoundException("nao achei")
        assertEquals("erro_nao_encontrado", ex.codigo)
        assertEquals("nao achei", ex.message)
    }

    @Test
    fun `UnauthorizedException deve ter codigo correto`() {
        val ex = UnauthorizedException("sem acesso")
        assertEquals("erro_nao_autorizado", ex.codigo)
        assertEquals("sem acesso", ex.message)
    }

    @Test
    fun `ConflictException deve ter codigo correto`() {
        val ex = ConflictException("conflito")
        assertEquals("erro_conflito", ex.codigo)
        assertEquals("conflito", ex.message)
    }

    @Test
    fun `ValidationException deve ter codigo correto`() {
        val ex = ValidationException("invalido")
        assertEquals("erro_validacao", ex.codigo)
        assertEquals("invalido", ex.message)
    }
}
