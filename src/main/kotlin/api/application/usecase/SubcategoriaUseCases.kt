package com.controlefinanceiro.api.application.usecase

import com.controlefinanceiro.api.domain.entity.SubcategoriaEntity
import com.controlefinanceiro.api.domain.port.CategoriaRepositoryPort
import com.controlefinanceiro.api.domain.port.SubcategoriaRepositoryPort
import com.controlefinanceiro.api.domain.port.LancamentoRepositoryPort
import com.controlefinanceiro.api.exception.ConflictException
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.exception.ValidationException

class CreateSubcategoriaUseCase(
    private val subRepo: SubcategoriaRepositoryPort,
    private val catRepo: CategoriaRepositoryPort
) {
    fun execute(nome: String, idCategoria: Long): SubcategoriaEntity {
        catRepo.findById(idCategoria) ?: throw NotFoundException("Categoria não encontrada")
        if (subRepo.existsByNomeInCategoria(nome, idCategoria)) throw ConflictException("Subcategoria já existe na categoria")
        return subRepo.create(nome, idCategoria)
    }
}

class UpdateSubcategoriaUseCase(
    private val subRepo: SubcategoriaRepositoryPort,
    private val catRepo: CategoriaRepositoryPort
) {
    fun execute(id: Long, nome: String, idCategoria: Long): SubcategoriaEntity {
        subRepo.findById(id) ?: throw NotFoundException("Subcategoria não encontrada")
        catRepo.findById(idCategoria) ?: throw NotFoundException("Categoria não encontrada")
        if (subRepo.existsByNomeInCategoria(nome, idCategoria)) throw ConflictException("Subcategoria já existe na categoria")
        return subRepo.update(id, nome, idCategoria)
    }
}

class DeleteSubcategoriaUseCase(
    private val subRepo: SubcategoriaRepositoryPort,
    private val lancRepo: LancamentoRepositoryPort
) {
    fun execute(id: Long) {
        subRepo.findById(id) ?: throw NotFoundException("Subcategoria não encontrada")
        if (lancRepo.existsBySubcategoria(id)) {
            throw ValidationException("Não é permitido excluir subcategoria com lançamentos atrelados")
        }
        subRepo.delete(id)
    }
}

class GetSubcategoriaUseCase(private val repo: SubcategoriaRepositoryPort) {
    fun execute(id: Long): SubcategoriaEntity =
        repo.findById(id) ?: throw NotFoundException("Subcategoria não encontrada")
}

class SearchSubcategoriaUseCase(private val repo: SubcategoriaRepositoryPort) {
    fun execute(nome: String?): List<SubcategoriaEntity> = repo.search(nome)
}
