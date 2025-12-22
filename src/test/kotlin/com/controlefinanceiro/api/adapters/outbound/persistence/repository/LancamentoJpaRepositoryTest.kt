package com.controlefinanceiro.api.adapters.outbound.persistence.repository

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.CategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.entity.LancamentoPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.entity.SubcategoriaPersistenceEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
@TestPropertySource(properties = [
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
class LancamentoJpaRepositoryTest @Autowired constructor(
    private val catRepo: CategoriaJpaRepository,
    private val subRepo: SubcategoriaJpaRepository,
    private val repo: LancamentoJpaRepository
) {

    @Test
    fun `existsByCategoria deve retornar true quando houver lancamento`() {
        val cat = catRepo.save(CategoriaPersistenceEntity(nome = "Transporte"))
        val sub = subRepo.save(SubcategoriaPersistenceEntity(nome = "Uber", categoria = cat))

        repo.save(
            LancamentoPersistenceEntity(
                valor = BigDecimal("10.00"),
                data = LocalDate.parse("2025-12-10"),
                subcategoria = sub
            )
        )

        assertTrue(repo.existsByCategoria(cat.idCategoria!!))
        assertFalse(repo.existsByCategoria(999L))
    }

    @Test
    fun `sumReceitaDespesa deve somar positivos e negativos como despesa positiva`() {
        val cat = catRepo.save(CategoriaPersistenceEntity(nome = "Transporte"))
        val sub = subRepo.save(SubcategoriaPersistenceEntity(nome = "Uber", categoria = cat))

        // receita +40
        repo.save(
            LancamentoPersistenceEntity(
                valor = BigDecimal("40.00"),
                data = LocalDate.parse("2025-12-05"),
                subcategoria = sub
            )
        )
        // despesa -15
        repo.save(
            LancamentoPersistenceEntity(
                valor = BigDecimal("-15.00"),
                data = LocalDate.parse("2025-12-06"),
                subcategoria = sub
            )
        )

        val row = repo.sumReceitaDespesa(
            LocalDate.parse("2025-12-01"),
            LocalDate.parse("2025-12-31"),
            null
        )

        assertEquals(BigDecimal("40.00"), row.receita!!.setScale(2))
        assertEquals(BigDecimal("15.00"), row.despesa!!.setScale(2))

        // filtrando por categoria
        val row2 = repo.sumReceitaDespesa(
            LocalDate.parse("2025-12-01"),
            LocalDate.parse("2025-12-31"),
            cat.idCategoria!!
        )
        assertEquals(BigDecimal("40.00"), row2.receita!!.setScale(2))
        assertEquals(BigDecimal("15.00"), row2.despesa!!.setScale(2))
    }
}