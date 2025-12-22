package com.controlefinanceiro.api.domain.service

import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.exception.ValidationException
import com.controlefinanceiro.api.support.inmemory.InMemoryCategoriaRepository
import com.controlefinanceiro.api.support.inmemory.InMemoryLancamentoRepository
import com.controlefinanceiro.api.support.inmemory.InMemorySubcategoriaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BalancoServiceTest {

    @Test
    fun `deve validar que data_fim nao pode ser anterior a data_inicio`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)
        val service = BalancoService(catRepo, lancRepo)

        assertThrows(ValidationException::class.java) {
            service.calcular(LocalDate.parse("2025-12-31"), LocalDate.parse("2025-12-01"), null)
        }
    }

    @Test
    fun `deve calcular balanco sem filtro de categoria`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)
        val service = BalancoService(catRepo, lancRepo)

        val cat = catRepo.create("Alimentacao")
        val sub = subRepo.create("Mercado", cat.idCategoria)

        lancRepo.create("40.00", LocalDate.parse("2025-12-10"), sub.idSubcategoria, null)
        lancRepo.create("-15.00", LocalDate.parse("2025-12-11"), sub.idSubcategoria, null)

        val r = service.calcular(LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-31"), null)

        assertEquals("40.00", r.receita.setScale(2).toPlainString())
        assertEquals("15.00", r.despesa.setScale(2).toPlainString())
        assertEquals("25.00", r.saldo.setScale(2).toPlainString())
        assertEquals(null, r.idCategoria)
        assertEquals(null, r.nomeCategoria)
    }

    @Test
    fun `deve calcular balanco com filtro de categoria e validar existencia`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)
        val service = BalancoService(catRepo, lancRepo)

        val cat = catRepo.create("Alimentacao")
        val sub = subRepo.create("Mercado", cat.idCategoria)
        lancRepo.create("10.00", LocalDate.parse("2025-12-10"), sub.idSubcategoria, null)

        val r = service.calcular(LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-31"), cat.idCategoria)

        assertEquals(cat.idCategoria, r.idCategoria)
        assertEquals("Alimentacao", r.nomeCategoria)
        assertEquals("10.00", r.saldo.setScale(2).toPlainString())

        assertThrows(NotFoundException::class.java) {
            service.calcular(LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-31"), 999L)
        }
    }
}
