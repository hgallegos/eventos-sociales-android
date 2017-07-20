package com.hm.eventossociales.domain

import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource
import dk.nykredit.jackson.dataformat.hal.annotation.Resource

/**
 * Created by hans6 on 16-07-2017.
 */
@Resource
class CategoriaResource: BaseResource() {

    @EmbeddedResource("evento_categorias")
    var categorias: List<Categoria>? = null

}