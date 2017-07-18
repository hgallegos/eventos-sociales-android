package com.hm.eventossociales.domain

import android.databinding.BaseObservable
import android.databinding.Bindable
import dk.nykredit.jackson.dataformat.hal.HALLink
import dk.nykredit.jackson.dataformat.hal.annotation.Link
import dk.nykredit.jackson.dataformat.hal.annotation.Resource

/**
 * Created by hans6 on 04-06-2017.
 */
@Resource
class Usuario : BaseObservable() {

    @Bindable
    var usuario: String? = null
    var edad: Int? = null

    @Bindable
    var email: String? = null

    var token: String? = null
    var nivel: String? = null

    @Link
    var self: HALLink? = null

    @Link("usuario")
    var usuarioRel: HALLink? = null

    @Link("actividades")
    var actividades: HALLink? = null

    @Link("eventos")
    var eventos: HALLink? = null
}
