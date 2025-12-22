package com.controlefinanceiro.api.domain.port

import com.controlefinanceiro.api.domain.entity.LancamentoEntity
import java.time.LocalDate

interface LancamentoRepositoryPort {
    fun create(valor: String, data: LocalDate?, idSubcategoria: Long, comentario: String?): LancamentoEntity
    fun update(idLancamento: Long, valor: String, data: LocalDate?, idSubcategoria: Long, comentario: String?): LancamentoEntity
    fun delete(idLancamento: Long)
    fun findById(idLancamento: Long): LancamentoEntity?
    fun search(data: LocalDate?): List<LancamentoEntity>

    fun existsBySubcategoria(idSubcategoria: Long): Boolean
    fun hasLancamentosByCategoria(idCategoria: Long): Boolean

    fun sumReceitaDespesa(dataInicio: LocalDate, dataFim: LocalDate, idCategoria: Long?): ReceitaDespesaAggregate
}

data class ReceitaDespesaAggregate(
    val receita: String,
    val despesa: String
)
