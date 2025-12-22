package com.controlefinanceiro.api.application.usecase

import com.controlefinanceiro.api.exception.ConflictException
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.exception.ValidationException
import com.controlefinanceiro.api.support.inmemory.InMemoryCategoriaRepository
import com.controlefinanceiro.api.support.inmemory.InMemoryLancamentoRepository
import com.controlefinanceiro.api.support.inmemory.InMemorySubcategoriaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SubcategoriaUseCasesTest {

    @Test
    fun `create deve falhar quando categoria nao existir`() {
        val subRepo = InMemorySubcategoriaRepository()
        val catRepo = InMemoryCategoriaRepository()
        val uc = CreateSubcategoriaUseCase(subRepo, catRepo)

        assertThrows(NotFoundException::class.java) {
            uc.execute("Mercado", 123)
        }
    }

    @Test
    fun `create deve falhar quando subcategoria ja existir na categoria`() {
        val subRepo = InMemorySubcategoriaRepository()
        val catRepo = InMemoryCategoriaRepository()
        val cat = catRepo.create("Alimentacao")
        subRepo.create("Mercado", cat.idCategoria)

        val uc = CreateSubcategoriaUseCase(subRepo, catRepo)

        assertThrows(ConflictException::class.java) {
            uc.execute("Mercado", cat.idCategoria)
        }
    }

    @Test
    fun `delete deve impedir quando houver lancamentos na subcategoria`() {
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val sub = subRepo.create("Mercado", 1L)
        lancRepo.create("10.00", LocalDate.parse("2025-12-20"), sub.idSubcategoria, null)

        val uc = DeleteSubcategoriaUseCase(subRepo, lancRepo)

        assertThrows(ValidationException::class.java) {
            uc.execute(sub.idSubcategoria)
        }
    }

    @Test
    fun `search deve retornar por nome`() {
        val subRepo = InMemorySubcategoriaRepository()
        subRepo.create("Mercado", 1L)
        subRepo.create("Restaurante", 1L)

        val uc = SearchSubcategoriaUseCase(subRepo)
        val result = uc.execute("rest")

        assertEquals(1, result.size)
        assertEquals("Restaurante", result.first().nome)
    }
}
