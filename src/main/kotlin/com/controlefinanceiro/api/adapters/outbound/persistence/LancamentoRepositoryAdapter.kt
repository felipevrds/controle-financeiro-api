package com.controlefinanceiro.api.adapters.outbound.persistence

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.LancamentoPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.LancamentoJpaRepository
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.SubcategoriaJpaRepository
import com.controlefinanceiro.api.domain.entity.LancamentoEntity
import com.controlefinanceiro.api.domain.port.LancamentoRepositoryPort
import com.controlefinanceiro.api.domain.port.ReceitaDespesaAggregate
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.exception.ValidationException
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.collections.map
import kotlin.text.replace

@Component
class LancamentoRepositoryAdapter(
    private val repo: LancamentoJpaRepository,
    private val subRepo: SubcategoriaJpaRepository
) : LancamentoRepositoryPort {

    override fun create(valor: String, data: LocalDate?, idSubcategoria: Long, comentario: String?): LancamentoEntity {
        val big = parseValor(valor)
        if (big.compareTo(BigDecimal.ZERO) == 0) throw ValidationException("Valor deve ser diferente de zero")

        val sub = subRepo.findById(idSubcategoria).orElseThrow { NotFoundException("Subcategoria não encontrada") }
        val saved = repo.save(
            LancamentoPersistenceEntity(
                valor = big,
                data = data ?: LocalDate.now(),
                subcategoria = sub,
                comentario = comentario
            )
        )
        return toDomain(saved)
    }

    override fun update(idLancamento: Long, valor: String, data: LocalDate?, idSubcategoria: Long, comentario: String?): LancamentoEntity {
        val entity = repo.findById(idLancamento).orElseThrow { NotFoundException("Lançamento não encontrado") }
        val big = parseValor(valor)
        if (big.compareTo(BigDecimal.ZERO) == 0) throw ValidationException("Valor deve ser diferente de zero")
        val sub = subRepo.findById(idSubcategoria).orElseThrow { NotFoundException("Subcategoria não encontrada") }

        entity.valor = big
        entity.data = data ?: entity.data
        entity.subcategoria = sub
        entity.comentario = comentario

        return toDomain(repo.save(entity))
    }

    override fun delete(idLancamento: Long) {
        repo.deleteById(idLancamento)
    }

    override fun findById(idLancamento: Long): LancamentoEntity? =
        repo.findById(idLancamento).map { toDomain(it) }.orElse(null)

    override fun search(data: LocalDate?): List<LancamentoEntity> {
        val list = if (data == null) repo.findAll() else repo.findAllByData(data)
        return list.map { toDomain(it) }
    }

    override fun existsBySubcategoria(idSubcategoria: Long): Boolean =
        repo.existsBySubcategoria_IdSubcategoria(idSubcategoria)

    override fun hasLancamentosByCategoria(idCategoria: Long): Boolean =
        repo.existsByCategoria(idCategoria)

    override fun sumReceitaDespesa(
        dataInicio: LocalDate,
        dataFim: LocalDate,
        idCategoria: Long?
    ): ReceitaDespesaAggregate {

        val row = repo.sumReceitaDespesa(dataInicio, dataFim, idCategoria)

        val receita = (row.receita ?: BigDecimal.ZERO)
            .setScale(2)
            .toPlainString()

        val despesa = (row.despesa ?: BigDecimal.ZERO)
            .setScale(2)
            .toPlainString()

        return ReceitaDespesaAggregate(
            receita = receita,
            despesa = despesa
        )
    }

    private fun parseValor(valor: String): BigDecimal {
        return try {
            BigDecimal(valor.replace(",", "."))
        } catch (_: Exception) {
            throw ValidationException("Valor inválido")
        }
    }

    private fun toDomain(e: LancamentoPersistenceEntity): LancamentoEntity =
        LancamentoEntity(
            idLancamento = e.idLancamento!!,
            valor = e.valor,
            data = e.data,
            idSubcategoria = e.subcategoria!!.idSubcategoria!!,
            comentario = e.comentario
        )
}
