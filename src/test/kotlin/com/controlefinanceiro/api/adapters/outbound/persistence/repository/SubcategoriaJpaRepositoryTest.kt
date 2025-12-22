package com.controlefinanceiro.api.adapters.outbound.persistence.repository

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.CategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.entity.SubcategoriaPersistenceEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@TestPropertySource(properties = [
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
class SubcategoriaJpaRepositoryTest @Autowired constructor(
    private val catRepo: CategoriaJpaRepository,
    private val repo: SubcategoriaJpaRepository
) {

    @Test
    fun `existsByNomeAndCategoria_IdCategoria deve funcionar`() {
        val cat = catRepo.save(CategoriaPersistenceEntity(nome = "Transporte"))
        repo.save(SubcategoriaPersistenceEntity(nome = "Uber", categoria = cat))

        assertTrue(repo.existsByNomeAndCategoria_IdCategoria("Uber", cat.idCategoria!!))
        assertFalse(repo.existsByNomeAndCategoria_IdCategoria("Taxi", cat.idCategoria!!))
    }

    @Test
    fun `findAllByCategoria_IdCategoria deve retornar apenas da categoria`() {
        val cat1 = catRepo.save(CategoriaPersistenceEntity(nome = "Transporte"))
        val cat2 = catRepo.save(CategoriaPersistenceEntity(nome = "Lazer"))

        repo.save(SubcategoriaPersistenceEntity(nome = "Uber", categoria = cat1))
        repo.save(SubcategoriaPersistenceEntity(nome = "Cinema", categoria = cat2))

        val list1 = repo.findAllByCategoria_IdCategoria(cat1.idCategoria!!)
        assertEquals(1, list1.size)
        assertEquals("Uber", list1[0].nome)
    }
}