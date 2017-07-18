package com.hm.eventossociales.domain

import dk.nykredit.jackson.dataformat.hal.HALLink
import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource
import dk.nykredit.jackson.dataformat.hal.annotation.Link

/**
 * Created by hans6 on 30-06-2017.
 */
open class BaseResource {

    @Link
    var self: HALLink? = null

    @Link
    var first: HALLink? = null

    @Link
    var next: HALLink? = null

    @Link
    var profile: HALLink? = null

    @Link
    var last: HALLink? = null

    @Link
    var search: HALLink? = null

    @Link
    var prev: HALLink? = null

    var page: Page? = null
}