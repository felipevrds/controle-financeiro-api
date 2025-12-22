package com.controlefinanceiro.api.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class LancamentoCreateRequestDto(
    @field:NotBlank val valor: String,
    val data: String?,
    @field:NotNull val id_subcategoria: Long,
    val comentario: String?
)

data class LancamentoUpdateRequestDto(
    @field:NotBlank val valor: String,
    val data: String?,
    @field:NotNull val id_subcategoria: Long,
    val comentario: String?
)

data class LancamentoResponseDto(
    val id_lancamento: Long,
    val valor: String,
    val data: String,
    val id_subcategoria: Long,
    val comentario: String?
)
