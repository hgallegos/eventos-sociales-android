package com.hm.eventossociales.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.hm.eventossociales.R
import com.hm.eventossociales.config.converter.JacksonConverterFactory
import com.hm.eventossociales.domain.Evento
import com.hm.eventossociales.services.EventoService
import com.hm.eventossociales.services.UsuarioService

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class NearbyFragment : BaseFragment() {

    internal lateinit var mMapView: MapView
    private var googleMap: GoogleMap? = null
    private var permissionStatus: SharedPreferences? = null
    private var sentToSettings = false
    private var next: String? = null
    internal lateinit var mFusedLocationClient: FusedLocationProviderClient
    final val SANTIAGO = LatLng(-33.475643, -70.6653441)

    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_nearby, container, false)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        mMapView = view.findViewById<View>(R.id.mapView) as MapView
        mMapView.onCreate(savedInstanceState)

        checkPermissions()

        mMapView.onResume()
        try {
            MapsInitializer.initialize(activity.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionStatus = activity.getSharedPreferences("permissionStatus", Context.MODE_PRIVATE)

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission()
                return
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Show Information about why you need the permission
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Se necesitan permisos")
                builder.setMessage("Está aplicacion necesita acceder a su ubicación.")
                builder.setPositiveButton("Conceder") { dialog, which ->
                    dialog.cancel()
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CALLBACK_CONSTANT)
                }
                builder.setNegativeButton("No conceder") { dialog, which ->
                    dialog.cancel()
                    proceedAfterPermissionNotAllowed()
                }
                builder.show()
            } else if (permissionStatus!!.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Se necesitan permisos")
                builder.setMessage("Está aplicacion necesita acceder a su ubicación.")
                builder.setPositiveButton("Conceder") { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    Toast.makeText(activity, "Ve ajustes y concede los permisos", Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton("Cancelar") { dialog, which ->
                    dialog.cancel()
                    proceedAfterPermissionNotAllowed()
                }
                builder.show()
            } else {
                //just request the permission
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CALLBACK_CONSTANT)
            }
        }
    }

    private fun getEventos() {
        val retro: Retrofit = getRetrofitInstance()
        val eventoService: EventoService = retro.create(EventoService::class.java)

        eventoService.getEventos()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            Log.d("eventos", response.eventos?.size.toString())
                            /*viewModel.addItems(eventos.usuarios)
                            binding.viewModel = viewModel*/
                            next = response.next?.href
                            response.eventos?.forEach { evento: Evento -> setResultMarker(evento) }
                        },
                        {
                            error ->
                            Log.d("error", error.message)
                        },
                        {
                            if (next != null) {
                                getMoreEventos(eventoService, next as String);
                            }
                        }
                )
    }

    private fun proceedAfterPermission() {

        mMapView.getMapAsync { mMap ->
            googleMap = mMap

            googleMap!!.isMyLocationEnabled = true

            mFusedLocationClient.lastLocation.addOnSuccessListener(activity, { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    setMapCameraPosition(LatLng(location.latitude, location.longitude))
                    getEventos()
                } else {
                    setMapCameraPosition(SANTIAGO)
                    getEventos()
                }
            })

        }
    }

    private fun setMapCameraPosition(position: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(position).zoom(12f).build()
        googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun getMoreEventos(service: EventoService, nextUrl: String) {
        service.getNextEventos(nextUrl)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            next = response.next?.href
                            response.eventos?.forEach { evento: Evento -> setResultMarker(evento) }
                        },
                        { error ->
                            Log.d("error", error.message)
                        },
                        {
                            if (next != null) {
                                getMoreEventos(service, next as String);
                            }
                        }
                )
    }

    private fun setResultMarker(evento: Evento) {
        mMapView.getMapAsync { mMap ->
            googleMap = mMap
            googleMap!!.addMarker(MarkerOptions()
                    .position(LatLng(evento.getpLat(), evento.getpLng()))
                    .title(evento.nombre))
        }
    }

    private fun proceedAfterPermissionNotAllowed() {
        mMapView.getMapAsync { mMap ->
            googleMap = mMap

            // For dropping a marker at a point on the Map
            val santiago = LatLng(-33.475643, -70.6653441)
            googleMap!!.addMarker(MarkerOptions().position(santiago).title("Marker Title").snippet("Marker Description"))

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(santiago).zoom(12f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {

            when (requestCode) {
                PERMISSION_CALLBACK_CONSTANT -> {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {

                        proceedAfterPermission()

                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle("Se necesitan permisos")
                        builder.setMessage("Está aplicación necesita acceder a la ubicación para ubicar eventos.")
                        builder.setPositiveButton("Conceder") { dialog, which ->
                            dialog.cancel()
                            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CALLBACK_CONSTANT)
                        }
                        builder.setNegativeButton("No Conceder") { dialog, which ->
                            dialog.cancel()
                            proceedAfterPermissionNotAllowed()
                        }
                        builder.show()
                    } else {
                        Toast.makeText(activity, "Permisos no concedidos", Toast.LENGTH_LONG).show()
                        proceedAfterPermissionNotAllowed()
                    }
                    return
                }
            }// other 'case' lines to check for other
            // permissions this app might request
            //check if all permissions are granted
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    companion object {

        private val PERMISSION_CALLBACK_CONSTANT = 101
        private val REQUEST_PERMISSION_SETTING = 102
        private val DEFAULT_POSITION = LatLng(-33.475643, -70.6653441)


        fun newInstance(instance: Int): NearbyFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = NearbyFragment()
            fragment.arguments = args
            return fragment
        }
    }


}
