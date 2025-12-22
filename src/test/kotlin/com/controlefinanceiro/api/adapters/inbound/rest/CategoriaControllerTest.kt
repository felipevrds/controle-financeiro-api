package com.controlefinanceiro.api.adapters.inbound.rest

import com.controlefinanceiro.api.application.dto.CategoriaCreateRequestDto
import com.controlefinanceiro.api.application.dto.CategoriaUpdateRequestDto
import com.controlefinanceiro.api.application.usecase.*
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.support.inmemory.InMemoryCategoriaRepository
import com.controlefinanceiro.api.support.inmemory.InMemoryLancamentoRepository
import com.controlefinanceiro.api.support.inmemory.InMemorySubcategoriaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CategoriaControllerTest {

    @Test
    fun `deve criar, buscar, atualizar e excluir categoria`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val controller = CategoriaController(
            createUseCase = CreateCategoriaUseCase(catRepo),
            updateUseCase = UpdateCategoriaUseCase(catRepo),
            deleteUseCase = DeleteCategoriaUseCase(catRepo, lancRepo),
            getUseCase = GetCategoriaUseCase(catRepo),
            searchUseCase = SearchCategoriaUseCase(catRepo)
        )

        // create
        val created = controller.create(CategoriaCreateRequestDto(nome = "Alimentação"))
        assertEquals("Alimentação", created.nome)
        assertTrue(created.id_categoria > 0)

        // get
        val got = controller.get(created.id_categoria)
        assertEquals(created.id_categoria, got.id_categoria)
        assertEquals("Alimentação", got.nome)

        // search
        val list = controller.search(nome = "ali")
        assertEquals(1, list.size)
        assertEquals(created.id_categoria, list.first().id_categoria)

        // update
        val updated = controller.update(created.id_categoria, CategoriaUpdateRequestDto(nome = "Mercado"))
        assertEquals(created.id_categoria, updated.id_categoria)
        assertEquals("Mercado", updated.nome)

        // delete
        controller.delete(created.id_categoria)

        // after delete -> NotFound
        assertThrows(NotFoundException::class.java) {
            controller.get(created.id_categoria)
        }
    }
}
