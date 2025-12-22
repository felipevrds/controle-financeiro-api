package com.controlefinanceiro.api.adapters.inbound.rest

import com.controlefinanceiro.api.application.dto.CategoriaCreateRequestDto
import com.controlefinanceiro.api.application.dto.CategoriaResponseDto
import com.controlefinanceiro.api.application.dto.CategoriaUpdateRequestDto
import com.controlefinanceiro.api.application.usecase.CreateCategoriaUseCase
import com.controlefinanceiro.api.application.usecase.DeleteCategoriaUseCase
import com.controlefinanceiro.api.application.usecase.GetCategoriaUseCase
import com.controlefinanceiro.api.application.usecase.SearchCategoriaUseCase
import com.controlefinanceiro.api.application.usecase.UpdateCategoriaUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/categorias")
class CategoriaController(
    private val createUseCase: CreateCategoriaUseCase,
    private val updateUseCase: UpdateCategoriaUseCase,
    private val deleteUseCase: DeleteCategoriaUseCase,
    private val getUseCase: GetCategoriaUseCase,
    private val searchUseCase: SearchCategoriaUseCase
) {
    @GetMapping
    fun search(@RequestParam(required = false) nome: String?): List<CategoriaResponseDto> =
        searchUseCase.execute(nome).map { CategoriaResponseDto(it.idCategoria, it.nome) }

    @GetMapping("/{id_categoria}")
    fun get(@PathVariable("id_categoria") id: Long): CategoriaResponseDto {
        val categoria = getUseCase.execute(id)
        return CategoriaResponseDto(categoria.idCategoria, categoria.nome)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid body: CategoriaCreateRequestDto): CategoriaResponseDto {
        val categoria = createUseCase.execute(body.nome)
        return CategoriaResponseDto(categoria.idCategoria, categoria.nome)
    }

    @PutMapping("/{id_categoria}")
    fun update(
        @PathVariable("id_categoria") id: Long,
        @RequestBody @Valid body: CategoriaUpdateRequestDto
    ): CategoriaResponseDto {
        val categoria = updateUseCase.execute(id, body.nome)
        return CategoriaResponseDto(categoria.idCategoria, categoria.nome)
    }

    @DeleteMapping("/{id_categoria}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("id_categoria") id: Long) {
        deleteUseCase.execute(id)
    }
}
