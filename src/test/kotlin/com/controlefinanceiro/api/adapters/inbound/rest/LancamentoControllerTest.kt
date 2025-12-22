package com.controlefinanceiro.api.adapters.inbound.rest

import com.controlefinanceiro.api.application.dto.*
import com.controlefinanceiro.api.application.usecase.*
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.exception.ValidationException
import com.controlefinanceiro.api.support.inmemory.InMemoryCategoriaRepository
import com.controlefinanceiro.api.support.inmemory.InMemoryLancamentoRepository
import com.controlefinanceiro.api.support.inmemory.InMemorySubcategoriaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LancamentoControllerTest {

    @Test
    fun `deve validar data invalida no search`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val controller = LancamentoController(
            createUseCase = CreateLancamentoUseCase(lancRepo, subRepo),
            updateUseCase = UpdateLancamentoUseCase(lancRepo, subRepo),
            deleteUseCase = DeleteLancamentoUseCase(lancRepo),
            getUseCase = GetLancamentoUseCase(lancRepo),
            searchUseCase = SearchLancamentoUseCase(lancRepo)
        )

        assertThrows(ValidationException::class.java) {
            controller.search(data = "2025-12-01") // formato errado, esperado dd/MM/yyyy
        }
    }

    @Test
    fun `deve criar, buscar, atualizar e excluir lancamento`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        // cria categoria e subcategoria base
        val cat = catRepo.create("Receitas")
        val sub = subRepo.create("Salário", cat.idCategoria)

        val controller = LancamentoController(
            createUseCase = CreateLancamentoUseCase(lancRepo, subRepo),
            updateUseCase = UpdateLancamentoUseCase(lancRepo, subRepo),
            deleteUseCase = DeleteLancamentoUseCase(lancRepo),
            getUseCase = GetLancamentoUseCase(lancRepo),
            searchUseCase = SearchLancamentoUseCase(lancRepo)
        )

        val created = controller.create(
            LancamentoCreateRequestDto(
                valor = "40.00",
                data = "20/12/2025",
                id_subcategoria = sub.idSubcategoria,
                comentario = "teste"
            )
        )
        assertTrue(created.id_lancamento > 0)
        assertEquals("40.00", created.valor)
        assertEquals("20/12/2025", created.data)

        val got = controller.get(created.id_lancamento)
        assertEquals(created.id_lancamento, got.id_lancamento)

        val updated = controller.update(
            created.id_lancamento,
            LancamentoUpdateRequestDto(
                valor = "-15.00",
                data = "21/12/2025",
                id_subcategoria = sub.idSubcategoria,
                comentario = null
            )
        )
        assertEquals("-15.00", updated.valor)
        assertEquals("21/12/2025", updated.data)

        controller.delete(created.id_lancamento)
        assertThrows(NotFoundException::class.java) {
            controller.get(created.id_lancamento)
        }
    }
}
