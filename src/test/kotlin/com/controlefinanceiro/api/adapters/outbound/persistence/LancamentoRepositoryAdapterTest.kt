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

    @Test
    fun `create deve lançar NotFound quando subcategoria não existe`() {
        Mockito.`when`(subRepo.findById(999L)).thenReturn(Optional.empty())

        assertThrows(NotFoundException::class.java) {
            adapter.create("10.00", LocalDate.parse("2025-12-20"), 999L, "x")
        }

        Mockito.verify(subRepo).findById(999L)
        Mockito.verify(repo, Mockito.never()).save(Mockito.any(LancamentoPersistenceEntity::class.java))
    }

    @Test
    fun `create deve lançar ValidationException quando valor é invalido`() {
        Mockito.`when`(subRepo.findById(1L)).thenReturn(Optional.of(
            SubcategoriaPersistenceEntity(
                idSubcategoria = 1L,
                nome = "Uber",
                categoria = CategoriaPersistenceEntity(1L, "Transporte")
            )
        ))

        assertThrows(ValidationException::class.java) {
            adapter.create("abc", LocalDate.parse("2025-12-20"), 1L, null)
        }

        Mockito.verify(repo, Mockito.never()).save(Mockito.any(LancamentoPersistenceEntity::class.java))
    }

    @Test
    fun `update deve atualizar campos e salvar`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val subOld = SubcategoriaPersistenceEntity(idSubcategoria = 2L, nome = "Uber", categoria = cat)
        val subNew = SubcategoriaPersistenceEntity(idSubcategoria = 3L, nome = "Taxi", categoria = cat)

        val existente = LancamentoPersistenceEntity(
            idLancamento = 5L,
            valor = BigDecimal("10.00"),
            data = LocalDate.parse("2025-12-20"),
            subcategoria = subOld,
            comentario = "old"
        )

        Mockito.`when`(repo.findById(5L)).thenReturn(Optional.of(existente))
        Mockito.`when`(subRepo.findById(3L)).thenReturn(Optional.of(subNew))
        Mockito.`when`(repo.save(Mockito.any(LancamentoPersistenceEntity::class.java))).thenAnswer { it.arguments[0] }

        val result = adapter.update(
            5L,
            "40.00",
            LocalDate.parse("2025-12-21"),
            3L,
            "novo"
        )

        assertEquals(5L, result.idLancamento)
        assertEquals(BigDecimal("40.00"), result.valor)
        assertEquals(LocalDate.parse("2025-12-21"), result.data)
        assertEquals(3L, result.idSubcategoria)

        // garante que objeto foi mutado corretamente
        assertEquals(BigDecimal("40.00"), existente.valor)
        assertEquals(LocalDate.parse("2025-12-21"), existente.data)
        assertEquals(subNew, existente.subcategoria)
        assertEquals("novo", existente.comentario)

        Mockito.verify(repo).findById(5L)
        Mockito.verify(subRepo).findById(3L)
        Mockito.verify(repo).save(Mockito.any(LancamentoPersistenceEntity::class.java))
    }

    @Test
    fun `update deve lançar ValidationException quando valor é zero`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val sub = SubcategoriaPersistenceEntity(idSubcategoria = 2L, nome = "Uber", categoria = cat)

        val existente = LancamentoPersistenceEntity(
            idLancamento = 5L,
            valor = BigDecimal("10.00"),
            data = LocalDate.parse("2025-12-20"),
            subcategoria = sub,
            comentario = null
        )
        Mockito.`when`(repo.findById(5L)).thenReturn(Optional.of(existente))

        assertThrows(ValidationException::class.java) {
            adapter.update(5L, "0", LocalDate.parse("2025-12-20"), 2L, null)
        }

        Mockito.verify(repo, Mockito.never()).save(Mockito.any(LancamentoPersistenceEntity::class.java))
    }

    @Test
    fun `update deve lançar NotFound quando subcategoria do update não existe`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val subOld = SubcategoriaPersistenceEntity(idSubcategoria = 2L, nome = "Uber", categoria = cat)

        val existente = LancamentoPersistenceEntity(
            idLancamento = 5L,
            valor = BigDecimal("10.00"),
            data = LocalDate.parse("2025-12-20"),
            subcategoria = subOld,
            comentario = null
        )

        Mockito.`when`(repo.findById(5L)).thenReturn(Optional.of(existente))
        Mockito.`when`(subRepo.findById(999L)).thenReturn(Optional.empty())

        assertThrows(NotFoundException::class.java) {
            adapter.update(5L, "10.00", LocalDate.parse("2025-12-20"), 999L, null)
        }

        Mockito.verify(repo, Mockito.never()).save(Mockito.any(LancamentoPersistenceEntity::class.java))
    }

    @Test
    fun `delete deve delegar para repo`() {
        adapter.delete(10L)
        Mockito.verify(repo).deleteById(10L)
    }

    @Test
    fun `findById deve retornar null quando não existe`() {
        Mockito.`when`(repo.findById(123L)).thenReturn(Optional.empty())
        val result = adapter.findById(123L)
        assertNull(result)
    }

    @Test
    fun `sumReceitaDespesa deve tratar null como zero`() {
        val row = object : ReceitaDespesaRow {
            override val receita: BigDecimal? = null
            override val despesa: BigDecimal? = null
        }
        Mockito.`when`(repo.sumReceitaDespesa(LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-31"), null))
            .thenReturn(row)

        val agg = adapter.sumReceitaDespesa(LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-31"), null)

        assertEquals("0.00", agg.receita)
        assertEquals("0.00", agg.despesa)
    }

}
