package com.hm.eventossociales.util.adapter

import android.content.Intent
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import com.google.android.gms.maps.model.LatLng
import com.hm.eventossociales.activities.EventoActivity
import com.hm.eventossociales.domain.Evento
import com.hm.eventossociales.domain.Foto
import com.hm.eventossociales.fragments.BaseFragment
import com.hm.eventossociales.services.FotoService
import com.hm.eventossociales.util.converter.JacksonConverterFactory
import kotlinx.android.synthetic.main.item.view.*

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by hans6 on 07-06-2017.
 */

class RecyclerViewScrollAdapter<T> : BindingRecyclerViewAdapter<T>() {


    override fun onCreateBinding(inflater: LayoutInflater, @LayoutRes layoutId: Int, viewGroup: ViewGroup): ViewDataBinding {
        val binding = super.onCreateBinding(inflater, layoutId, viewGroup)
        Log.d("", "created binding: " + binding)
        return binding
    }

    override fun onBindBinding(binding: ViewDataBinding, bindingVariable: Int, @LayoutRes layoutId: Int, position: Int, item: T) {

        var imageUrl: String? = null;

        super.onBindBinding(binding, bindingVariable, layoutId, position, item)
        Log.d("", "bound binding: $binding at position: $position")

        val evento = item as Evento
        binding.root.setOnClickListener { view ->
            val evento = item as Evento
            val intent = Intent(view.context, EventoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("nombre", evento.nombre)
            intent.putExtra("eventoUrl", evento.self.href)
            intent.putExtra("fotosUrl", evento.fotos.href)
            intent.putExtra("LatLng", LatLng(evento.getpLat(), evento.getpLng()))
            view.context.startActivity(intent)
        }

        if (evento.fotos != null) {
            val retrofit = retrofitInstance

            val fotoService = retrofit.create(FotoService::class.java)

            fotoService.getFotosByUrl(evento.fotos.href)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->
                                if (response.eventoFotos?.isNotEmpty()!!) {
                                    imageUrl = response.eventoFotos?.get(0)?.url;

                                    Glide.with(binding.root.context).load(imageUrl).into(binding.root.imagen);
                                }
                            },
                            { error ->
                                Log.e("RecyclerView", error.message)
                            },
                            {

                            }
                    )
        }
    }

    private val retrofitInstance: Retrofit
        get() = Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BaseFragment.BASE_URL)
                .build()

}
