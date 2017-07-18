package com.hm.eventossociales.domain

import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource
import dk.nykredit.jackson.dataformat.hal.annotation.Resource

/**
 * Created by hans6 on 30-06-2017.
 */
@Resource
class EventoResource: BaseResource() {

    @EmbeddedResource
    var eventos: List<Evento>? = null
}