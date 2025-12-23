package com.controlefinanceiro.api.adapters.inbound.rest

import com.controlefinanceiro.api.application.dto.BalancoResponseDto
import com.controlefinanceiro.api.domain.service.BalancoService
import com.controlefinanceiro.api.exception.ValidationException
import com.controlefinanceiro.api.support.inmemory.InMemoryCategoriaRepository
import com.controlefinanceiro.api.support.inmemory.InMemoryLancamentoRepository
import com.controlefinanceiro.api.support.inmemory.InMemorySubcategoriaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BalancoControllerTest {

    @Test
    fun `deve validar datas invalidas`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val service = BalancoService(catRepo, lancRepo)
        val controller = BalancoController(service)

        assertThrows(ValidationException::class.java) {
            controller.get(dataInicio = "2025-12-01", dataFim = "31/12/2025", idCategoria = null)
        }
        assertThrows(ValidationException::class.java) {
            controller.get(dataInicio = "01/12/2025", dataFim = "2025-12-31", idCategoria = null)
        }
    }

    @Test
    fun `deve validar datas vazias`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val service = BalancoService(catRepo, lancRepo)
        val controller = BalancoController(service)

        assertThrows(ValidationException::class.java) {
            controller.get(dataInicio = "", dataFim = "31/12/2025", idCategoria = null)
        }
        assertThrows(ValidationException::class.java) {
            controller.get(dataInicio = "01/12/2025", dataFim = "   ", idCategoria = null)
        }
    }

    @Test
    fun `deve retornar receita despesa e saldo formatados`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        // cria categoria/subcategoria e 2 lançamentos
        val cat = catRepo.create("Geral")
        val sub = subRepo.create("Base", cat.idCategoria)
        lancRepo.create(valor = "40.00", data = LocalDate.of(2025, 12, 20), idSubcategoria = sub.idSubcategoria, comentario = null)
        lancRepo.create(valor = "-15.00", data = LocalDate.of(2025, 12, 21), idSubcategoria = sub.idSubcategoria, comentario = null)

        val service = BalancoService(catRepo, lancRepo)
        val controller = BalancoController(service)

        val res: BalancoResponseDto = controller.get(
            dataInicio = "01/12/2025",
            dataFim = "31/12/2025",
            idCategoria = null
        )

        assertEquals("40.00", res.receita)
        assertEquals("15.00", res.despesa)
        assertEquals("25.00", res.saldo)
        assertNull(res.categoria) // sem filtro de categoria
    }

    @Test
    fun `deve retornar categoria no response quando filtra por idCategoria`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val cat1 = catRepo.create("Transporte")
        val cat2 = catRepo.create("Alimentação")

        val sub1 = subRepo.create("Uber", cat1.idCategoria)
        val sub2 = subRepo.create("Restaurante", cat2.idCategoria)

        lancRepo.create(valor = "40.00", data = LocalDate.of(2025, 12, 20), idSubcategoria = sub1.idSubcategoria, comentario = null)
        lancRepo.create(valor = "-10.00", data = LocalDate.of(2025, 12, 21), idSubcategoria = sub1.idSubcategoria, comentario = null)

        // fora do filtro, não pode entrar
        lancRepo.create(valor = "999.00", data = LocalDate.of(2025, 12, 22), idSubcategoria = sub2.idSubcategoria, comentario = null)

        val service = BalancoService(catRepo, lancRepo)
        val controller = BalancoController(service)

        val res = controller.get(
            dataInicio = "01/12/2025",
            dataFim = "31/12/2025",
            idCategoria = cat1.idCategoria
        )

        assertNotNull(res.categoria)
        assertEquals(cat1.idCategoria, res.categoria!!.id_categoria)
        assertEquals("Transporte", res.categoria!!.nome)

        assertEquals("40.00", res.receita)
        assertEquals("10.00", res.despesa)
        assertEquals("30.00", res.saldo)
    }

    @Test
    fun `deve retornar zeros quando nao ha lancamentos no periodo`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val service = BalancoService(catRepo, lancRepo)
        val controller = BalancoController(service)

        val res = controller.get(
            dataInicio = "01/12/2025",
            dataFim = "31/12/2025",
            idCategoria = null
        )

        assertEquals("0.00", res.receita)
        assertEquals("0.00", res.despesa)
        assertEquals("0.00", res.saldo)
        assertNull(res.categoria)
    }

    @Test
    fun `deve calcular corretamente quando so tem despesa`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val cat = catRepo.create("Geral")
        val sub = subRepo.create("Base", cat.idCategoria)
        lancRepo.create(valor = "-15.00", data = LocalDate.of(2025, 12, 21), idSubcategoria = sub.idSubcategoria, comentario = null)

        val service = BalancoService(catRepo, lancRepo)
        val controller = BalancoController(service)

        val res = controller.get(
            dataInicio = "01/12/2025",
            dataFim = "31/12/2025",
            idCategoria = null
        )

        assertEquals("0.00", res.receita)
        assertEquals("15.00", res.despesa)
        assertEquals("-15.00", res.saldo)
    }
}
