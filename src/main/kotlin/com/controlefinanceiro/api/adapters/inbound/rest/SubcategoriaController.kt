package com.controlefinanceiro.api.adapters.inbound.rest

import com.controlefinanceiro.api.application.dto.SubCategoriaCreateRequestDto
import com.controlefinanceiro.api.application.dto.SubCategoriaResponseDto
import com.controlefinanceiro.api.application.dto.SubCategoriaUpdateRequestDto
import com.controlefinanceiro.api.application.usecase.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/subcategorias")
class SubcategoriaController(
    private val createUseCase: CreateSubcategoriaUseCase,
    private val updateUseCase: UpdateSubcategoriaUseCase,
    private val deleteUseCase: DeleteSubcategoriaUseCase,
    private val getUseCase: GetSubcategoriaUseCase,
    private val searchUseCase: SearchSubcategoriaUseCase
) {
    @GetMapping
    fun search(@RequestParam(required = false) nome: String?): List<SubCategoriaResponseDto> =
        searchUseCase.execute(nome).map { SubCategoriaResponseDto(it.idSubcategoria, it.nome, it.idCategoria) }

    @GetMapping("/{id_subcategoria}")
    fun get(@PathVariable("id_subcategoria") id: Long): SubCategoriaResponseDto {
        val subCategoria = getUseCase.execute(id)
        return SubCategoriaResponseDto(subCategoria.idSubcategoria, subCategoria.nome, subCategoria.idCategoria)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid body: SubCategoriaCreateRequestDto): SubCategoriaResponseDto {
        val subCategoria = createUseCase.execute(body.nome, body.id_categoria)
        return SubCategoriaResponseDto(subCategoria.idSubcategoria, subCategoria.nome, subCategoria.idCategoria)
    }

    @PutMapping("/{id_subcategoria}")
    fun update(
        @PathVariable("id_subcategoria") id: Long,
        @RequestBody @Valid body: SubCategoriaUpdateRequestDto
    ): SubCategoriaResponseDto {
        val subCategoria = updateUseCase.execute(id, body.nome, body.id_categoria)
        return SubCategoriaResponseDto(subCategoria.idSubcategoria, subCategoria.nome, subCategoria.idCategoria)
    }

    @DeleteMapping("/{id_subcategoria}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("id_subcategoria") id: Long) {
        deleteUseCase.execute(id)
    }
}
