package com.controlefinanceiro.api.application.usecase

import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.support.inmemory.InMemoryLancamentoRepository
import com.controlefinanceiro.api.support.inmemory.InMemorySubcategoriaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LancamentoUseCasesTest {

    @Test
    fun `create deve falhar quando subcategoria nao existir`() {
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)
        val uc = CreateLancamentoUseCase(lancRepo, subRepo)

        assertThrows(NotFoundException::class.java) {
            uc.execute("10.00", LocalDate.parse("2025-12-20"), 999L, null)
        }
    }

    @Test
    fun `update deve falhar quando lancamento nao existir`() {
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)
        val uc = UpdateLancamentoUseCase(lancRepo, subRepo)

        val sub = subRepo.create("Mercado", 1L)

        assertThrows(NotFoundException::class.java) {
            uc.execute(123L, "10.00", LocalDate.parse("2025-12-20"), sub.idSubcategoria, null)
        }
    }

    @Test
    fun `delete deve remover quando existir`() {
        val subRepo = InMemorySubcategoriaRepository()
        val lancRepo = InMemoryLancamentoRepository(subRepo)
        val sub = subRepo.create("Mercado", 1L)
        val created = lancRepo.create("10.00", LocalDate.parse("2025-12-20"), sub.idSubcategoria, "x")

        val uc = DeleteLancamentoUseCase(lancRepo)
        uc.execute(created.idLancamento)

        assertEquals(null, lancRepo.findById(created.idLancamento))
    }
}
