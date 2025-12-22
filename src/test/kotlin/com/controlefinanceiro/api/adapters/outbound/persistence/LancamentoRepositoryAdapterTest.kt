package com.controlefinanceiro.api.adapters.outbound.persistence

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.CategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.entity.LancamentoPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.entity.SubcategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.LancamentoJpaRepository
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.ReceitaDespesaRow
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.SubcategoriaJpaRepository
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.exception.ValidationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class LancamentoRepositoryAdapterTest {

    private val repo: LancamentoJpaRepository = Mockito.mock(LancamentoJpaRepository::class.java)
    private val subRepo: SubcategoriaJpaRepository = Mockito.mock(SubcategoriaJpaRepository::class.java)
    private val adapter = LancamentoRepositoryAdapter(repo, subRepo)

    @Test
    fun `create deve validar valor diferente de zero e salvar`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val sub = SubcategoriaPersistenceEntity(idSubcategoria = 2L, nome = "Uber", categoria = cat)
        Mockito.`when`(subRepo.findById(2L)).thenReturn(Optional.of(sub))

        val saved = LancamentoPersistenceEntity(
            idLancamento = 10L,
            valor = BigDecimal("40.00"),
            data = LocalDate.parse("2025-12-20"),
            subcategoria = sub,
            comentario = "teste"
        )
        Mockito.`when`(repo.save(Mockito.any(LancamentoPersistenceEntity::class.java))).thenReturn(saved)

        val result = adapter.create("40.00", LocalDate.parse("2025-12-20"), 2L, "teste")

        assertEquals(10L, result.idLancamento)
        assertEquals(BigDecimal("40.00"), result.valor)
        assertEquals(LocalDate.parse("2025-12-20"), result.data)
        assertEquals(2L, result.idSubcategoria)
    }

    @Test
    fun `create deve lançar ValidationException quando valor é zero`() {
        assertThrows(ValidationException::class.java) {
            adapter.create("0", LocalDate.parse("2025-12-20"), 1L, null)
        }
    }

    @Test
    fun `update deve lançar NotFound quando não existe`() {
        Mockito.`when`(repo.findById(999L)).thenReturn(Optional.empty())

        assertThrows(NotFoundException::class.java) {
            adapter.update(999L, "10.00", LocalDate.parse("2025-12-20"), 1L, null)
        }
    }

    @Test
    fun `findById deve mapear`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val sub = SubcategoriaPersistenceEntity(idSubcategoria = 2L, nome = "Uber", categoria = cat)
        val e = LancamentoPersistenceEntity(
            idLancamento = 5L,
            valor = BigDecimal("-15.00"),
            data = LocalDate.parse("2025-12-20"),
            subcategoria = sub,
            comentario = null
        )
        Mockito.`when`(repo.findById(5L)).thenReturn(Optional.of(e))

        val result = adapter.findById(5L)

        assertNotNull(result)
        assertEquals(5L, result!!.idLancamento)
        assertEquals(BigDecimal("-15.00"), result.valor)
        assertEquals(2L, result.idSubcategoria)
    }

    @Test
    fun `search deve delegar por data`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val sub = SubcategoriaPersistenceEntity(idSubcategoria = 2L, nome = "Uber", categoria = cat)
        val e = LancamentoPersistenceEntity(
            idLancamento = 5L,
            valor = BigDecimal("10.00"),
            data = LocalDate.parse("2025-12-20"),
            subcategoria = sub,
            comentario = null
        )
        Mockito.`when`(repo.findAllByData(LocalDate.parse("2025-12-20"))).thenReturn(listOf(e))

        val result = adapter.search(LocalDate.parse("2025-12-20"))

        assertEquals(1, result.size)
        Mockito.verify(repo).findAllByData(LocalDate.parse("2025-12-20"))
    }

    @Test
    fun `existsBySubcategoria deve delegar`() {
        Mockito.`when`(repo.existsBySubcategoria_IdSubcategoria(2L)).thenReturn(true)

        assertTrue(adapter.existsBySubcategoria(2L))
        Mockito.verify(repo).existsBySubcategoria_IdSubcategoria(2L)
    }

    @Test
    fun `hasLancamentosByCategoria deve delegar`() {
        Mockito.`when`(repo.existsByCategoria(1L)).thenReturn(true)

        assertTrue(adapter.hasLancamentosByCategoria(1L))
        Mockito.verify(repo).existsByCategoria(1L)
    }

    @Test
    fun `sumReceitaDespesa deve mapear receita e despesa com scale 2`() {
        val row = object : ReceitaDespesaRow {
            override val receita: BigDecimal? = BigDecimal("40.0")
            override val despesa: BigDecimal? = BigDecimal("15.0")
        }
        Mockito.`when`(repo.sumReceitaDespesa(LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-31"), null))
            .thenReturn(row)

        val agg = adapter.sumReceitaDespesa(LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-31"), null)

        assertEquals("40.00", agg.receita)
        assertEquals("15.00", agg.despesa)
    }
}
