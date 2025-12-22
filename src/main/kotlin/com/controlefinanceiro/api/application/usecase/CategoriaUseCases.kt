package com.controlefinanceiro.api.application.usecase

import com.controlefinanceiro.api.domain.entity.CategoriaEntity
import com.controlefinanceiro.api.domain.port.CategoriaRepositoryPort
import com.controlefinanceiro.api.domain.port.LancamentoRepositoryPort
import com.controlefinanceiro.api.exception.ConflictException
import com.controlefinanceiro.api.exception.NotFoundException
import com.controlefinanceiro.api.exception.ValidationException

class CreateCategoriaUseCase(private val repo: CategoriaRepositoryPort) {
    fun execute(nome: String): CategoriaEntity {
        if (repo.existsByNome(nome)) throw ConflictException("Categoria já existe")
        return repo.create(nome)
    }
}

class UpdateCategoriaUseCase(private val repo: CategoriaRepositoryPort) {
    fun execute(id: Long, nome: String): CategoriaEntity {
        val current = repo.findById(id) ?: throw NotFoundException("Categoria não encontrada")
        if (current.nome != nome && repo.existsByNome(nome)) throw ConflictException("Categoria já existe")
        return repo.update(id, nome)
    }
}

class DeleteCategoriaUseCase(
    private val categoriaRepo: CategoriaRepositoryPort,
    private val lancamentoRepo: LancamentoRepositoryPort
) {
    fun execute(id: Long) {
        categoriaRepo.findById(id) ?: throw NotFoundException("Categoria não encontrada")
        if (lancamentoRepo.hasLancamentosByCategoria(id)) {
            throw ValidationException("Não é permitido excluir categoria com lançamentos atrelados")
        }
        categoriaRepo.delete(id)
    }
}

class GetCategoriaUseCase(private val repo: CategoriaRepositoryPort) {
    fun execute(id: Long): CategoriaEntity =
        repo.findById(id) ?: throw NotFoundException("Categoria não encontrada")
}

class SearchCategoriaUseCase(private val repo: CategoriaRepositoryPort) {
    fun execute(nome: String?): List<CategoriaEntity> = repo.search(nome)
}
