package com.controlefinanceiro.api.config

import com.controlefinanceiro.api.application.usecase.*
import com.controlefinanceiro.api.exception.ValidationException
import com.controlefinanceiro.api.support.inmemory.InMemoryCategoriaRepository
import com.controlefinanceiro.api.support.inmemory.InMemoryLancamentoRepository
import com.controlefinanceiro.api.support.inmemory.InMemorySubcategoriaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.jvm.java

class UseCaseConfigTest {

    @Test
    fun `deve criar instancias tipadas de todos os use cases e do BalancoService`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val config = UseCaseConfig()

        // Categoria
        val createCategoria = config.createCategoriaUseCase(catRepo)
        val updateCategoria = config.updateCategoriaUseCase(catRepo)
        val deleteCategoria = config.deleteCategoriaUseCase(catRepo, lancRepo)
        val getCategoria = config.getCategoriaUseCase(catRepo)
        val searchCategoria = config.searchCategoriaUseCase(catRepo)

        assertTrue(createCategoria is CreateCategoriaUseCase)
        assertTrue(updateCategoria is UpdateCategoriaUseCase)
        assertTrue(deleteCategoria is DeleteCategoriaUseCase)
        assertTrue(getCategoria is GetCategoriaUseCase)
        assertTrue(searchCategoria is SearchCategoriaUseCase)

        // Subcategoria
        val createSub = config.createSubcategoriaUseCase(subRepo, catRepo)
        val updateSub = config.updateSubcategoriaUseCase(subRepo, catRepo)
        val deleteSub = config.deleteSubcategoriaUseCase(subRepo, lancRepo)
        val getSub = config.getSubcategoriaUseCase(subRepo)
        val searchSub = config.searchSubcategoriaUseCase(subRepo)

        assertTrue(createSub is CreateSubcategoriaUseCase)
        assertTrue(updateSub is UpdateSubcategoriaUseCase)
        assertTrue(deleteSub is DeleteSubcategoriaUseCase)
        assertTrue(getSub is GetSubcategoriaUseCase)
        assertTrue(searchSub is SearchSubcategoriaUseCase)

        // Lancamento
        val createLanc = config.createLancamentoUseCase(lancRepo, subRepo)
        val updateLanc = config.updateLancamentoUseCase(lancRepo, subRepo)
        val deleteLanc = config.deleteLancamentoUseCase(lancRepo)
        val getLanc = config.getLancamentoUseCase(lancRepo)
        val searchLanc = config.searchLancamentoUseCase(lancRepo)

        assertTrue(createLanc is CreateLancamentoUseCase)
        assertTrue(updateLanc is UpdateLancamentoUseCase)
        assertTrue(deleteLanc is DeleteLancamentoUseCase)
        assertTrue(getLanc is GetLancamentoUseCase)
        assertTrue(searchLanc is SearchLancamentoUseCase)

        // Service
        val balancoService = config.balancoService(catRepo, lancRepo)
        assertNotNull(balancoService)
    }

    @Test
    fun `BalancoService criado pelo config deve calcular receita despesa e saldo`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val config = UseCaseConfig()

        val createCategoria = config.createCategoriaUseCase(catRepo)
        val createSub = config.createSubcategoriaUseCase(subRepo, catRepo)
        val createLanc = config.createLancamentoUseCase(lancRepo, subRepo)
        val balancoService = config.balancoService(catRepo, lancRepo)

        val categoria = createCategoria.execute("Transporte")
        val subcategoria = createSub.execute("Uber", categoria.idCategoria)

        // Receita (+) e despesa (-) no mesmo intervalo
        createLanc.execute("40.00", LocalDate.of(2025, 12, 10), subcategoria.idSubcategoria, "cashback")
        createLanc.execute("-15.00", LocalDate.of(2025, 12, 11), subcategoria.idSubcategoria, "corrida")

        val result = balancoService.calcular(
            dataInicio = LocalDate.of(2025, 12, 1),
            dataFim = LocalDate.of(2025, 12, 31),
            idCategoria = null
        )

        assertEquals("40.00", result.receita.setScale(2).toPlainString())
        assertEquals("15.00", result.despesa.setScale(2).toPlainString())
        assertEquals("25.00", result.saldo.setScale(2).toPlainString())
    }

    @Test
    fun `BalancoService deve validar data_fim maior ou igual a data_inicio`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val config = UseCaseConfig()
        val balancoService = config.balancoService(catRepo, lancRepo)

        assertThrows(ValidationException::class.java) {
            balancoService.calcular(
                dataInicio = LocalDate.of(2025, 12, 31),
                dataFim = LocalDate.of(2025, 12, 1),
                idCategoria = null
            )
        }
    }
}
