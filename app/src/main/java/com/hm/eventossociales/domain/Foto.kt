package com.hm.eventossociales.domain

import android.databinding.BaseObservable
import dk.nykredit.jackson.dataformat.hal.HALLink
import dk.nykredit.jackson.dataformat.hal.annotation.Link
import dk.nykredit.jackson.dataformat.hal.annotation.Resource

/**
 * Created by hans6 on 13-07-2017.
 */
@Resource
class Foto: BaseObservable() {

    var titulo: String? = null

    var descripcion: String? = null

    var url: String? = null

    @Link
    var self: HALLink? = null

    @Link("eventoFoto")
    var eventoFotoRel: HALLink? = null

    @Link("evento")
    var eventoRel: HALLink? = null
}