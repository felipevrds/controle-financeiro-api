package com.controlefinanceiro.api.application.dto

import jakarta.validation.constraints.NotBlank

data class CategoriaCreateRequestDto(
    @field:NotBlank val nome: String
)

data class CategoriaUpdateRequestDto(
    @field:NotBlank val nome: String
)

data class CategoriaResponseDto(
    val id_categoria: Long,
    val nome: String
)
