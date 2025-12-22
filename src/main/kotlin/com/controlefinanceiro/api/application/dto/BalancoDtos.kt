package com.controlefinanceiro.api.application.dto

data class BalancoResponseDto(
    val categoria: CategoriaResponseDto? = null,
    val receita: String,
    val despesa: String,
    val saldo: String
)
