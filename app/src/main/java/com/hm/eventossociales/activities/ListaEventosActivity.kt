package com.hm.eventossociales.activities

import android.content.Intent
import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hm.eventossociales.R
import com.hm.eventossociales.config.adapter.EndlessRecyclerOnScrollListener
import com.hm.eventossociales.config.adapter.RecyclerViewScrollAdapter
import com.hm.eventossociales.config.converter.JacksonConverterFactory
import com.hm.eventossociales.databinding.ActivityListaEventosBinding
import com.hm.eventossociales.domain.Evento
import com.hm.eventossociales.domain.views.ItemViewModel
import com.hm.eventossociales.fragments.BaseFragment
import com.hm.eventossociales.services.EventoService
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by hans6 on 02-07-2017.
 */
class ListaEventosActivity : AppCompatActivity() {

    private val TAG = "ListaEventos"
    private val LOCATION = "location"
    private val NOMBRE = "nombre"
    private val LUGAR = "lugar"
    internal lateinit var binding: ActivityListaEventosBinding
    var next: String? = null
    var location: Location? = null
    var nombre: String? = null
    var lugar: String? = null
    var categoria: Int? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()


    }

    override fun onResume() {
        super.onResume()
        initActivity()
        Log.d(TAG, "onResume...................")
    }

    fun initActivity() {
        binding = DataBindingUtil.setContentView<ActivityListaEventosBinding>(this, R.layout.activity_lista_eventos)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.title = "Lista de eventos cercanos"
        val viewModel = ItemViewModel()
        binding.itemViewModel = viewModel

        getParamsForSearch()
        getLastLocation()

        if (location != null) {
            getEventsNearby(location)
        } else if (nombre != null) {
            searchEventos(nombre, lugar, categoria)
        }
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        location = savedInstanceState?.getParcelable(LOCATION)
        nombre = savedInstanceState?.getString(NOMBRE)
        lugar = savedInstanceState?.getString(LUGAR)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        if (location != null) {
            outState.putParcelable(LOCATION, location)
        } else if (nombre != null) {
            outState.putString(NOMBRE, nombre)
            outState.putString(LUGAR, lugar)
        }
        super.onSaveInstanceState(outState)
    }


    private fun getParamsForSearch() {
        if( intent.hasExtra(NOMBRE)) {
            nombre = intent.getStringExtra(NOMBRE)
            lugar = intent.getStringExtra(LUGAR)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun getLastLocation() {
        if (intent.hasExtra(LOCATION)) {
            location = intent.extras.getParcelable<Location>("location")
        }
    }

    private fun getEventsNearby(location: Location?) {
        val retro: Retrofit = getRetrofitInstance()
        val eventoService: EventoService = retro.create(EventoService::class.java)

        if (location != null) {
            eventoService.getNearEventos(location.latitude, location.longitude)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->
                                Log.d(TAG, response.eventos?.size.toString())
                                val viewModel = ItemViewModel()
                                viewModel.addItems(response.eventos)
                                binding.itemViewModel = viewModel
                            },
                            { error ->
                                Log.e(TAG, error.message)
                            },
                            {
                                binding.listaEventos.addOnScrollListener(object : EndlessRecyclerOnScrollListener(binding.listaEventos.layoutManager as LinearLayoutManager) {
                                    override fun onLoadMore() {
                                        if (next != null) {
                                            addDataToList(eventoService)
                                        } else {
                                            binding.itemProgressBar.visibility = View.GONE
                                            Toast.makeText(applicationContext, "No hay más eventos", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                })
                            }
                    )
        }
    }

    private fun searchEventos(nombre: String?, lugar: String?, categoria: Int?) {
        val retro = getRetrofitInstance()
        val eventoService: EventoService = retro.create(EventoService::class.java)
        if (nombre != null && lugar != null && categoria == null) {
            eventoService.searchByNombreAndLugar(nombre, lugar)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->
                                binding.toolbar.title = response.page?.totalElements.toString() + " resultados"
                                val viewModel = ItemViewModel()
                                viewModel.addItems(response.eventos)
                                binding.itemViewModel = viewModel
                                next = response.next?.href
                            },
                            {
                                error ->
                                Log.e(TAG, error.message)
                            },
                            {

                                binding.listaEventos.addOnScrollListener(object : EndlessRecyclerOnScrollListener(binding.listaEventos.layoutManager as LinearLayoutManager) {
                                    override fun onLoadMore() {
                                        if (next != null) {
                                            addDataToList(eventoService)
                                        } else {
                                            binding.itemProgressBar.visibility = View.GONE
                                            Toast.makeText(applicationContext, "No hay más eventos", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                })
                            }

                    )
        }


    }

    private fun addDataToList(service: EventoService) {
        binding.itemProgressBar.visibility = View.VISIBLE

        if (next != null) {
            service.getNextEventos(next)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->
                                next = response.next?.href
                                binding.itemViewModel.addItems(response.eventos)
                            },
                            { error ->
                                Log.e(TAG, error.message)
                            },
                            {
                                binding.itemProgressBar.visibility = View.GONE
                            }
                    )
        }
    }

    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BaseFragment.BASE_URL)
                .build()
    }
}