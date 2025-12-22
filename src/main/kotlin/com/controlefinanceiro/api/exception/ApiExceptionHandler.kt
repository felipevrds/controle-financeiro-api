package com.controlefinanceiro.api.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    private val log = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApi(ex: ApiException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val status = when (ex) {
            is UnauthorizedException -> HttpStatus.UNAUTHORIZED
            is NotFoundException -> HttpStatus.NOT_FOUND
            is ConflictException -> HttpStatus.CONFLICT
            is ValidationException -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.BAD_REQUEST
        }
        log.warn("API error codigo={} status={} path={} msg={}", ex.codigo, status.value(), request.requestURI, ex.message)
        return ResponseEntity.status(status).body(ErrorResponse(ex.codigo, ex.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val fieldError = ex.bindingResult.fieldErrors.firstOrNull()
        val msg = if (fieldError != null) "O campo '${fieldError.field}' é obrigatório" else "Requisição inválida"
        log.warn("Validation error path={} msg={}", request.requestURI, msg)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse("erro_validacao", msg))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error path={}", request.requestURI, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("erro_interno", "Erro inesperado"))
    }
}
