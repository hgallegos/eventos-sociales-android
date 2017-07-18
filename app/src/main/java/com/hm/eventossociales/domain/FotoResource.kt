package com.hm.eventossociales.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource
import dk.nykredit.jackson.dataformat.hal.annotation.Resource

/**
 * Created by hans6 on 13-07-2017.
 */
@Resource
@JsonIgnoreProperties(ignoreUnknown = true)
class FotoResource: BaseResource() {

    @EmbeddedResource
    @JsonProperty("evento_fotos")
    var eventoFotos: List<Foto>? = null;
}