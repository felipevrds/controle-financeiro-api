package com.controlefinanceiro.api.exception

import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError

class ApiExceptionHandlerTest {

    private val handler = ApiExceptionHandler()

    @Test
    fun `handleApi deve mapear UnauthorizedException para 401`() {
        val request = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.requestURI).thenReturn("/v1/teste")

        val response = handler.handleApi(UnauthorizedException("sem acesso"), request)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertNotNull(response.body)
        assertEquals("erro_nao_autorizado", response.body!!.codigo)
        assertEquals("sem acesso", response.body!!.mensagem)
    }

    @Test
    fun `handleApi deve mapear NotFoundException para 404`() {
        val request = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.requestURI).thenReturn("/v1/categorias/1")

        val response = handler.handleApi(NotFoundException("Categoria não encontrada"), request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("erro_nao_encontrado", response.body!!.codigo)
    }

    @Test
    fun `handleApi deve mapear ConflictException para 409`() {
        val request = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.requestURI).thenReturn("/v1/categorias")

        val response = handler.handleApi(ConflictException("Nome já existe"), request)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("erro_conflito", response.body!!.codigo)
    }

    @Test
    fun `handleApi deve mapear ValidationException para 400`() {
        val request = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.requestURI).thenReturn("/v1/lancamentos")

        val response = handler.handleApi(ValidationException("Valor inválido"), request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("erro_validacao", response.body!!.codigo)
        assertEquals("Valor inválido", response.body!!.mensagem)
    }

    @Test
    fun `handleValidation deve retornar msg amigavel do primeiro fieldError`() {
        val request = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.requestURI).thenReturn("/v1/categorias")

        val bindingResult = Mockito.mock(BindingResult::class.java)
        Mockito.`when`(bindingResult.fieldErrors).thenReturn(listOf(FieldError("req", "nome", "must not be blank")))

        val ex = Mockito.mock(MethodArgumentNotValidException::class.java)
        Mockito.`when`(ex.bindingResult).thenReturn(bindingResult)

        val response = handler.handleValidation(ex, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("erro_validacao", response.body!!.codigo)
        assertEquals("O campo 'nome' é obrigatório", response.body!!.mensagem)
    }

    @Test
    fun `handleValidation sem fieldError deve retornar mensagem generica`() {
        val request = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.requestURI).thenReturn("/v1/categorias")

        val bindingResult = Mockito.mock(BindingResult::class.java)
        Mockito.`when`(bindingResult.fieldErrors).thenReturn(emptyList())

        val ex = Mockito.mock(MethodArgumentNotValidException::class.java)
        Mockito.`when`(ex.bindingResult).thenReturn(bindingResult)

        val response = handler.handleValidation(ex, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("erro_validacao", response.body!!.codigo)
        assertEquals("Requisição inválida", response.body!!.mensagem)
    }

    @Test
    fun `handleUnexpected deve retornar 500 e codigo erro_interno`() {
        val request = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.requestURI).thenReturn("/v1/balanco")

        val response = handler.handleUnexpected(RuntimeException("boom"), request)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("erro_interno", response.body!!.codigo)
        assertEquals("Erro inesperado", response.body!!.mensagem)
    }
}
