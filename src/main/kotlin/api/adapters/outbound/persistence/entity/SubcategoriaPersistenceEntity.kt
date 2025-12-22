package com.controlefinanceiro.api.adapters.outbound.persistence.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "subcategorias",
    uniqueConstraints = [UniqueConstraint(name = "uk_subcategoria_nome_categoria", columnNames = ["nome", "id_categoria"])]
)
class SubcategoriaPersistenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subcategoria")
    var idSubcategoria: Long? = null,

    @Column(name = "nome", nullable = false)
    var nome: String = "",

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    var categoria: CategoriaPersistenceEntity? = null
)
