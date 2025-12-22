package com.controlefinanceiro.api.exception

class NotFoundException(message: String) : ApiException("erro_nao_encontrado", message)
