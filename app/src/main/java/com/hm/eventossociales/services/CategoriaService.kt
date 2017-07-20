package com.hm.eventossociales.services

import com.hm.eventossociales.domain.Categoria
import com.hm.eventossociales.domain.CategoriaResource
import retrofit2.http.GET
import retrofit2.http.Url
import rx.Observable

/**
 * Created by hans6 on 16-07-2017.
 */
interface CategoriaService {

    @GET
    fun getCategoriaByUrl(@Url url: String): Observable<Categoria>

    @GET("/evento_categorias")
    fun getCategorias(): Observable<CategoriaResource>
}