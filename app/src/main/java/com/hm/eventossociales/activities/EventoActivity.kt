package com.hm.eventossociales.activities

import `in`.myinnos.imagesliderwithswipeslibrary.Animations.DescriptionAnimation
import `in`.myinnos.imagesliderwithswipeslibrary.SliderLayout
import `in`.myinnos.imagesliderwithswipeslibrary.SliderTypes.BaseSliderView
import `in`.myinnos.imagesliderwithswipeslibrary.SliderTypes.TextSliderView
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hm.eventossociales.R
import com.hm.eventossociales.util.converter.JacksonConverterFactory
import com.hm.eventossociales.databinding.ActivityEventoBinding
import com.hm.eventossociales.fragments.BaseFragment
import com.hm.eventossociales.services.EventoService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import android.view.MenuItem
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hm.eventossociales.services.CategoriaService
import com.hm.eventossociales.services.FotoService


/**
 * Created by hans6 on 02-07-2017.
 */
class EventoActivity() : AppCompatActivity(), OnMapReadyCallback {


    private val TAG = "EventoActivity"
    internal lateinit var binding: ActivityEventoBinding
    internal lateinit var mSlider: SliderLayout;
    internal lateinit var mMapView: MapView
    private var googleMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_evento)
        mMapView = binding.mapLite
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)
        if (intent.hasExtra("nombre")) {
            binding.toolbar.title = intent.getStringExtra("nombre")
        } else {
            binding.toolbar.title = "Evento"
        }
        val mToolbar = binding.toolbar
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mSlider = binding.slider



        loadImages();

        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top)
        mSlider.setPresetTransformer(SliderLayout.Transformer.Stack)
        mSlider.setCustomAnimation(DescriptionAnimation())
        mSlider.setDuration(4000)

        getEvento();



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mSlider.stopAutoCycle()
        super.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        MapsInitializer.initialize(getApplicationContext());
        googleMap= map;
            if (intent.hasExtra("LatLng")) {
                val position = intent.extras.get("LatLng") as LatLng
                setMapLocation(map, position);
            }

    }

    private fun  setMapLocation(map: GoogleMap, latLng: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        map.addMarker(MarkerOptions().position(latLng));

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    private fun loadImages() {
        if(intent.hasExtra("fotosUrl")) {
            val retro = getRetrofitInstance()
            val fotoService: FotoService = retro.create(FotoService::class.java)

            fotoService.getFotosByUrl(intent.getStringExtra("fotosUrl"))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->
                                response.eventoFotos?.forEach { f ->
                                    val textSlider: TextSliderView = TextSliderView(this);
                                    textSlider.description(f.titulo)
                                            .descriptionSize(20)
                                            .image(f.url)
                                            .setScaleType(BaseSliderView.ScaleType.Fit);

                                    mSlider.addSlider(textSlider)
                                }

                            }
                    )
        }
    }

    private fun getEvento() {
        if(intent.hasExtra("eventoUrl")) {
            val retro = getRetrofitInstance()
            val eventoService = retro.create(EventoService::class.java)

            eventoService.getEventoByUrl(intent.getStringExtra("eventoUrl"))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { response ->
                                binding.evento = response
                                if(response.asignaCategorias.isNotEmpty()) {
                                    getCategoria(response.asignaCategorias[0]?.categoriaRel?.href!!)
                                } else {
                                    binding.categoriaText.text = "Misceláneos";
                                }
                            }
                    )
        }
    }

    private fun getCategoria(url: String) {
        val retro = getRetrofitInstance()
        val categoriaService = retro.create(CategoriaService::class.java);

        categoriaService.getCategoriaByUrl(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            binding.categoria = response
                        }
                )
    }

    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BaseFragment.BASE_URL)
                .build()
    }



}