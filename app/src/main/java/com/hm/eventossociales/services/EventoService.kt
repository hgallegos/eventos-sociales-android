package com.hm.eventossociales.services

import com.hm.eventossociales.domain.Evento
import com.hm.eventossociales.domain.EventoResource
import com.hm.eventossociales.domain.UsuarioResource
import retrofit2.Call
import retrofit2.http.*
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

    @GET("eventos/search/filterBy")
    fun searchBy(@Query("nombre") nombre: String, @Query("direccion") direccion: String, @Query("categoria") categoria: Int): Observable<EventoResource>

    @GET
    fun getEventoByUrl(@Url url: String?): Observable<Evento>

    @POST("/eventos")
    fun saveEvento(@Body evento: Evento): Observable<Evento>
}