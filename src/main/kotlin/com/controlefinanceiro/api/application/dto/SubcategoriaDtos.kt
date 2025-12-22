package com.controlefinanceiro.api.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SubCategoriaCreateRequestDto(
    @field:NotBlank val nome: String,
    @field:NotNull val id_categoria: Long
)

data class SubCategoriaUpdateRequestDto(
    @field:NotBlank val nome: String,
    @field:NotNull val id_categoria: Long
)

data class SubCategoriaResponseDto(
    val id_subcategoria: Long,
    val nome: String,
    val id_categoria: Long
)
