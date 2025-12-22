package com.controlefinanceiro.api.domain.entity

import java.math.BigDecimal
import java.time.LocalDate

data class LancamentoEntity(
    val idLancamento: Long,
    val valor: BigDecimal,
    val data: LocalDate,
    val idSubcategoria: Long,
    val comentario: String?
)
