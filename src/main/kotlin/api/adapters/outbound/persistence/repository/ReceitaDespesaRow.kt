package com.controlefinanceiro.api.adapters.outbound.persistence.repository

import java.math.BigDecimal

interface ReceitaDespesaRow {
    val receita: BigDecimal?
    val despesa: BigDecimal?
}
