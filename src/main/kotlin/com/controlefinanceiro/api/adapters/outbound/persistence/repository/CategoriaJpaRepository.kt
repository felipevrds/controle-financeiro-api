package com.controlefinanceiro.api.adapters.outbound.persistence.repository

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.CategoriaPersistenceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategoriaJpaRepository : JpaRepository<CategoriaPersistenceEntity, Long> {
    fun existsByNome(nome: String): Boolean
    fun findAllByNomeContainingIgnoreCase(nome: String): List<CategoriaPersistenceEntity>
}
