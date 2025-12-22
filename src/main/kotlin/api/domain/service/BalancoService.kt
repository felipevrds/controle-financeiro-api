package com.controlefinanceiro.api.domain.service

import com.controlefinanceiro.api.domain.port.CategoriaRepositoryPort
import com.controlefinanceiro.api.domain.port.LancamentoRepositoryPort
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.exception.ValidationException
import java.math.BigDecimal
import java.time.LocalDate

data class BalancoResult(
    val idCategoria: Long?,
    val nomeCategoria: String?,
    val receita: BigDecimal,
    val despesa: BigDecimal,
    val saldo: BigDecimal
)

class BalancoService(
    private val categoriaRepo: CategoriaRepositoryPort,
    private val lancamentoRepo: LancamentoRepositoryPort
) {
    fun calcular(dataInicio: LocalDate, dataFim: LocalDate, idCategoria: Long?): BalancoResult {
        if (dataFim.isBefore(dataInicio)) throw ValidationException("data_fim deve ser maior ou igual a data_inicio")

        val categoria = if (idCategoria != null) {
            categoriaRepo.findById(idCategoria) ?: throw NotFoundException("Categoria não encontrada")
        } else null

        val agg = lancamentoRepo.sumReceitaDespesa(dataInicio, dataFim, idCategoria)
        val receita = BigDecimal(agg.receita)
        val despesa = BigDecimal(agg.despesa)
        val saldo = receita.subtract(despesa)

        return BalancoResult(
            idCategoria = categoria?.idCategoria,
            nomeCategoria = categoria?.nome,
            receita = receita,
            despesa = despesa,
            saldo = saldo
        )
    }
}
