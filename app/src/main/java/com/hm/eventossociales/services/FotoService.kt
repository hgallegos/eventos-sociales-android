package com.hm.eventossociales.services

import com.hm.eventossociales.domain.FotoResource
import retrofit2.http.GET
import retrofit2.http.Url
import rx.Observable

/**
 * Created by hans6 on 13-07-2017.
 */
interface FotoService {

    @GET
    fun getFotosByUrl(@Url url: String): Observable<FotoResource>
}