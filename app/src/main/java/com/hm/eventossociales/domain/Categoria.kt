package com.hm.eventossociales.domain

import dk.nykredit.jackson.dataformat.hal.HALLink
import dk.nykredit.jackson.dataformat.hal.annotation.Link
import dk.nykredit.jackson.dataformat.hal.annotation.Resource

/**
 * Created by hans6 on 16-07-2017.
 */
@Resource
class Categoria {

    var nombre: String? = null

    var descripcion: String? = null

    var asignaCategorias: Set<AsignaCategorias>? = null

    @Link
    var self: HALLink? = null

    @Link("eventoCategoria")
    var eventoCategoria: HALLink? = null
}