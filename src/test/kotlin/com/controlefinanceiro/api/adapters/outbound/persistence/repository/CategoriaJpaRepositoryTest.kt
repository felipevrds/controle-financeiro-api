package com.controlefinanceiro.api.adapters.outbound.persistence.repository

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.CategoriaPersistenceEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@TestPropertySource(properties = [
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
class CategoriaJpaRepositoryTest @Autowired constructor(
    private val repo: CategoriaJpaRepository
) {

    @Test
    fun `existsByNome deve retornar true quando existir`() {
        repo.save(CategoriaPersistenceEntity(nome = "Transporte"))
        assertTrue(repo.existsByNome("Transporte"))
        assertFalse(repo.existsByNome("Inexistente"))
    }

    @Test
    fun `findAllByNomeContainingIgnoreCase deve filtrar`() {
        repo.save(CategoriaPersistenceEntity(nome = "Transporte"))
        repo.save(CategoriaPersistenceEntity(nome = "Mercado"))

        val result = repo.findAllByNomeContainingIgnoreCase("tra")
        assertEquals(1, result.size)
        assertEquals("Transporte", result[0].nome)
    }
}