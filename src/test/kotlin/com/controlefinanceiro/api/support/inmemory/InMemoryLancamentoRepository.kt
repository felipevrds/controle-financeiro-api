package com.controlefinanceiro.api.support.inmemory

import com.controlefinanceiro.api.domain.entity.LancamentoEntity
import com.controlefinanceiro.api.domain.port.LancamentoRepositoryPort
import com.controlefinanceiro.api.domain.port.ReceitaDespesaAggregate
import java.math.BigDecimal
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

class InMemoryLancamentoRepository(
    private val subRepo: InMemorySubcategoriaRepository
) : LancamentoRepositoryPort {

    private val seq = AtomicLong(1)
    private val data = linkedMapOf<Long, LancamentoEntity>()

    override fun create(valor: String, data: LocalDate?, idSubcategoria: Long, comentario: String?): LancamentoEntity {
        val id = seq.getAndIncrement()
        val dt = data ?: LocalDate.now()
        val entity = LancamentoEntity(
            idLancamento = id,
            valor = BigDecimal(valor),
            data = dt,
            idSubcategoria = idSubcategoria,
            comentario = comentario
        )
        this.data[id] = entity
        return entity
    }

    override fun update(idLancamento: Long, valor: String, data: LocalDate?, idSubcategoria: Long, comentario: String?): LancamentoEntity {
        val current = this.data[idLancamento] ?: create(valor, data, idSubcategoria, comentario).copy(idLancamento = idLancamento)
        val updated = current.copy(
            valor = BigDecimal(valor),
            data = data ?: current.data,
            idSubcategoria = idSubcategoria,
            comentario = comentario
        )
        this.data[idLancamento] = updated
        return updated
    }

    override fun delete(idLancamento: Long) {
        data.remove(idLancamento)
    }

    override fun findById(idLancamento: Long): LancamentoEntity? = data[idLancamento]

    override fun search(data: LocalDate?): List<LancamentoEntity> {
        if (data == null) return this.data.values.toList()
        return this.data.values.filter { it.data == data }
    }

    override fun existsBySubcategoria(idSubcategoria: Long): Boolean =
        data.values.any { it.idSubcategoria == idSubcategoria }

    override fun hasLancamentosByCategoria(idCategoria: Long): Boolean {
        val subIds = subRepo.findAllByCategoria(idCategoria).map { it.idSubcategoria }.toSet()
        return data.values.any { it.idSubcategoria in subIds }
    }

    override fun sumReceitaDespesa(dataInicio: LocalDate, dataFim: LocalDate, idCategoria: Long?): ReceitaDespesaAggregate {
        val filtered = data.values.filter { !it.data.isBefore(dataInicio) && !it.data.isAfter(dataFim) }
            .let { list ->
                if (idCategoria == null) list
                else {
                    val subIds = subRepo.findAllByCategoria(idCategoria).map { it.idSubcategoria }.toSet()
                    list.filter { it.idSubcategoria in subIds }
                }
            }

        val receita = filtered.filter { it.valor > BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { acc, l -> acc + l.valor }

        val despesa = filtered.filter { it.valor < BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { acc, l -> acc + l.valor.abs() }

        return ReceitaDespesaAggregate(
            receita = receita.setScale(2).toPlainString(),
            despesa = despesa.setScale(2).toPlainString()
        )
    }
}
