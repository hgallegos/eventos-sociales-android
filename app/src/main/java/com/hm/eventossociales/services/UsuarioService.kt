package com.hm.eventossociales.services

import android.provider.ContactsContract
import com.hm.eventossociales.domain.Usuario
import com.hm.eventossociales.domain.UsuarioResource
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import rx.Observable

/**
 * Created by hans6 on 02-06-2017.
 */
interface UsuarioService {

    @GET("usuarios")
    fun getUsuarios(): Observable<UsuarioResource>

    @GET
    fun getNextUsuarios(@Url url: String?): Observable<UsuarioResource>

    @GET("usuarios/search/email")
    fun getUsuarioByEmail(@Query("email") email: String?): Observable<UsuarioResource>
}