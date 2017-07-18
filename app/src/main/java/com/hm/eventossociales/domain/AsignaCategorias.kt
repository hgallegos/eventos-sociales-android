package com.hm.eventossociales.domain

import android.databinding.BaseObservable
import dk.nykredit.jackson.dataformat.hal.HALLink
import dk.nykredit.jackson.dataformat.hal.annotation.Link
import dk.nykredit.jackson.dataformat.hal.annotation.Resource

/**
 * Created by hans6 on 16-07-2017.
 */
@Resource
class AsignaCategorias: BaseObservable() {

    @Link("evento")
    var eventoRel: HALLink? = null

    @Link("categoria")
    var categoriaRel: HALLink? = null
}