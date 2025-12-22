package com.controlefinanceiro.api.exception

class UnauthorizedException(message: String) : ApiException("erro_nao_autorizado", message)
