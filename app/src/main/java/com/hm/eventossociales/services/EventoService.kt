package com.hm.eventossociales.services

import com.hm.eventossociales.domain.Evento
import com.hm.eventossociales.domain.EventoResource
import com.hm.eventossociales.domain.UsuarioResource
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import rx.Observable

/**
 * Created by hans6 on 30-06-2017.
 */
interface EventoService {

    @GET("eventos")
    fun getEventos(): Observable<EventoResource>

    @GET("eventos/search/near_eventos")
    fun getNearEventos(@Query("lat") lat: Double, @Query("lng") lng: Double ): Observable<EventoResource>

    @GET
    fun getNextEventos(@Url url: String?): Observable<EventoResource>

    @GET("eventos/search/nombreODireccion")
    fun searchByNombreAndLugar(@Query("nombre") nombre: String, @Query("direccion") direccion: String): Observable<EventoResource>

    @GET
    fun getEventoByUrl(@Url url: String?): Observable<Evento>
}