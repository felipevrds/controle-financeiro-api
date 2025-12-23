package com.controlefinanceiro.api.adapters.outbound.persistence.repository

import java.math.BigDecimal

interface ReceitaDespesaProjection {
    val receita: BigDecimal?
    val despesa: BigDecimal?
}
