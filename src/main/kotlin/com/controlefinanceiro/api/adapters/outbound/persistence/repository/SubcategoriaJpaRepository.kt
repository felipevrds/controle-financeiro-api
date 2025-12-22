package com.controlefinanceiro.api.adapters.outbound.persistence.repository

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.SubcategoriaPersistenceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SubcategoriaJpaRepository : JpaRepository<SubcategoriaPersistenceEntity, Long> {
    fun existsByNomeAndCategoria_IdCategoria(nome: String, idCategoria: Long): Boolean
    fun findAllByNomeContainingIgnoreCase(nome: String): List<SubcategoriaPersistenceEntity>
    fun findAllByCategoria_IdCategoria(idCategoria: Long): List<SubcategoriaPersistenceEntity>
}
