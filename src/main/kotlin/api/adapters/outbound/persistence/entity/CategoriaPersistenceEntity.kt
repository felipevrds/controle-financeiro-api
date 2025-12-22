package com.controlefinanceiro.api.adapters.outbound.persistence.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "categorias",
    uniqueConstraints = [UniqueConstraint(name = "uk_categoria_nome", columnNames = ["nome"])]
)
class CategoriaPersistenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    var idCategoria: Long? = null,

    @Column(name = "nome", nullable = false)
    var nome: String = ""
)
