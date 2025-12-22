package com.controlefinanceiro.api.adapters.inbound.rest

import com.controlefinanceiro.api.application.dto.CategoriaCreateRequestDto
import com.controlefinanceiro.api.application.dto.SubCategoriaCreateRequestDto
import com.controlefinanceiro.api.application.dto.SubCategoriaUpdateRequestDto
import com.controlefinanceiro.api.application.usecase.*
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.support.inmemory.InMemoryCategoriaRepository
import com.controlefinanceiro.api.support.inmemory.InMemoryLancamentoRepository
import com.controlefinanceiro.api.support.inmemory.InMemorySubcategoriaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SubcategoriaControllerTest {

    @Test
    fun `deve criar, buscar, atualizar e excluir subcategoria`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        // cria uma categoria base
        val categoriaController = CategoriaController(
            createUseCase = CreateCategoriaUseCase(catRepo),
            updateUseCase = UpdateCategoriaUseCase(catRepo),
            deleteUseCase = DeleteCategoriaUseCase(catRepo, lancRepo),
            getUseCase = GetCategoriaUseCase(catRepo),
            searchUseCase = SearchCategoriaUseCase(catRepo)
        )
        val categoria = categoriaController.create(CategoriaCreateRequestDto("Transporte"))

        val controller = SubcategoriaController(
            createUseCase = CreateSubcategoriaUseCase(subRepo, catRepo),
            updateUseCase = UpdateSubcategoriaUseCase(subRepo, catRepo),
            deleteUseCase = DeleteSubcategoriaUseCase(subRepo, lancRepo),
            getUseCase = GetSubcategoriaUseCase(subRepo),
            searchUseCase = SearchSubcategoriaUseCase(subRepo)
        )

        val created = controller.create(SubCategoriaCreateRequestDto(nome = "Uber", id_categoria = categoria.id_categoria))
        assertTrue(created.id_subcategoria > 0)
        assertEquals("Uber", created.nome)
        assertEquals(categoria.id_categoria, created.id_categoria)

        val got = controller.get(created.id_subcategoria)
        assertEquals(created.id_subcategoria, got.id_subcategoria)

        val list = controller.search(nome = "ub")
        assertEquals(1, list.size)

        val updated = controller.update(
            created.id_subcategoria,
            SubCategoriaUpdateRequestDto(nome = "Taxi", id_categoria = categoria.id_categoria)
        )
        assertEquals("Taxi", updated.nome)

        controller.delete(created.id_subcategoria)

        assertThrows(NotFoundException::class.java) {
            controller.get(created.id_subcategoria)
        }
    }
}
