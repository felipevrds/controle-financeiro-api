package com.controlefinanceiro.api.domain.port

import com.controlefinanceiro.api.domain.entity.CategoriaEntity

interface CategoriaRepositoryPort {
    fun create(nome: String): CategoriaEntity
    fun update(idCategoria: Long, nome: String): CategoriaEntity
    fun delete(idCategoria: Long)
    fun findById(idCategoria: Long): CategoriaEntity?
    fun search(nome: String?): List<CategoriaEntity>
    fun existsByNome(nome: String): Boolean
}
