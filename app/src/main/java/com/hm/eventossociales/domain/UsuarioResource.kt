package com.hm.eventossociales.domain

import dk.nykredit.jackson.dataformat.hal.HALLink
import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource
import dk.nykredit.jackson.dataformat.hal.annotation.Link
import dk.nykredit.jackson.dataformat.hal.annotation.Resource

/**
 * Created by hans6 on 02-06-2017.
 */
@Resource
class UsuarioResource: BaseResource() {

    @EmbeddedResource
    var usuarios: List<Usuario>? = null

}
