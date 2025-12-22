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

class CategoriaUseCasesTest {

    @Test
    fun `create deve criar quando nao existir`() {
        val repo = InMemoryCategoriaRepository()
        val uc = CreateCategoriaUseCase(repo)

        val created = uc.execute("Alimentacao")

        assertEquals("Alimentacao", created.nome)
        assertEquals(true, created.idCategoria > 0)
    }

    @Test
    fun `create deve falhar quando nome ja existir`() {
        val repo = InMemoryCategoriaRepository()
        repo.create("Alimentacao")
        val uc = CreateCategoriaUseCase(repo)

        assertThrows(ConflictException::class.java) {
            uc.execute("Alimentacao")
        }
    }

    @Test
    fun `update deve falhar quando categoria nao existir`() {
        val repo = InMemoryCategoriaRepository()
        val uc = UpdateCategoriaUseCase(repo)

        assertThrows(NotFoundException::class.java) {
            uc.execute(999, "Nova")
        }
    }

    @Test
    fun `delete deve impedir quando houver lancamentos na categoria`() {
        val catRepo = InMemoryCategoriaRepository()
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)

        val cat = catRepo.create("Alimentacao")
        val sub = subRepo.create("Mercado", cat.idCategoria)
        lancRepo.create("10.00", LocalDate.parse("2025-12-20"), sub.idSubcategoria, null)

        val uc = DeleteCategoriaUseCase(catRepo, lancRepo)

        assertThrows(ValidationException::class.java) {
            uc.execute(cat.idCategoria)
        }
    }
}
