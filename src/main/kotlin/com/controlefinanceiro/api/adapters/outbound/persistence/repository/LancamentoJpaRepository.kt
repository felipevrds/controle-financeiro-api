package com.controlefinanceiro.api.adapters.outbound.persistence.repository

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.LancamentoPersistenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface LancamentoJpaRepository : JpaRepository<LancamentoPersistenceEntity, Long> {
    fun existsBySubcategoria_IdSubcategoria(idSubcategoria: Long): Boolean
    fun findAllByData(data: LocalDate): List<LancamentoPersistenceEntity>

    @Query("""
        select case when count(l) > 0 then true else false end
        from LancamentoPersistenceEntity l
        join l.subcategoria s
        join s.categoria c
        where c.idCategoria = :idCategoria
    """)
    fun existsByCategoria(@Param("idCategoria") idCategoria: Long): Boolean

    @Query("""
        select
        coalesce(sum(case when l.valor > 0 then l.valor else 0 end), 0) as receita,
        coalesce(sum(case when l.valor < 0 then -l.valor else 0 end), 0) as despesa
        from LancamentoPersistenceEntity l
        where l.data >= :dataInicio and l.data <= :dataFim
        and (:idCategoria is null or l.subcategoria.categoria.idCategoria = :idCategoria)
    """)
    fun sumReceitaDespesa(
        @Param("dataInicio") dataInicio: LocalDate,
        @Param("dataFim") dataFim: LocalDate,
        @Param("idCategoria") idCategoria: Long?
    ): ReceitaDespesaProjection

}
