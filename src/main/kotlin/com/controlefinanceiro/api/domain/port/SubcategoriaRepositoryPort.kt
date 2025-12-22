package com.controlefinanceiro.api.domain.port

import com.controlefinanceiro.api.domain.entity.SubcategoriaEntity

interface SubcategoriaRepositoryPort {
    fun create(nome: String, idCategoria: Long): SubcategoriaEntity
    fun update(idSubcategoria: Long, nome: String, idCategoria: Long): SubcategoriaEntity
    fun delete(idSubcategoria: Long)
    fun findById(idSubcategoria: Long): SubcategoriaEntity?
    fun search(nome: String?): List<SubcategoriaEntity>
    fun existsByNomeInCategoria(nome: String, idCategoria: Long): Boolean
    fun findAllByCategoria(idCategoria: Long): List<SubcategoriaEntity>
}
