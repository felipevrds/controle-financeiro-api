package com.controlefinanceiro.api.exception

class ValidationException(message: String) : ApiException("erro_validacao", message)
