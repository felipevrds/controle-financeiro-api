package com.controlefinanceiro.api.exception

open class ApiException(
    val codigo: String,
    override val message: String
) : RuntimeException(message)
