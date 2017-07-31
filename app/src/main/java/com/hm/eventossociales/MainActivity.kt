package com.hm.eventossociales

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.net.Uri
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.hm.eventossociales.databinding.ActivityMainBinding

import com.hm.eventossociales.fragments.BaseFragment
import com.hm.eventossociales.fragments.PerfilFragment
import com.hm.eventossociales.fragments.AddFragment
import com.hm.eventossociales.fragments.NearbyFragment
import com.hm.eventossociales.fragments.ExplorarFragment
import com.ncapdevi.fragnav.FragNavController
import com.roughike.bottombar.BottomBar
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.hm.eventossociales.activities.ListaEventosActivity
import com.hm.eventossociales.util.converter.JacksonConverterFactory
import com.hm.eventossociales.services.EventoService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.DateFormat
import java.util.*


class MainActivity : AppCompatActivity(),
        BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private val INDEX_BUSCAR = FragNavController.TAB1
    private val INDEX_NEARBY = FragNavController.TAB2
    private val INDEX_FRIENDS = FragNavController.TAB3

    private val PERMISSION_CALLBACK_CONSTANT = 101
    private val REQUEST_PERMISSION_SETTING = 102

    private val TAG = "MainActivity"
    private val INTERVAL = (60000 * 1).toLong()
    private val FASTEST_INTERVAL = (60000 * 0.5).toLong()

    var mBottomBar: BottomBar? = null
    var mNavController: FragNavController? = null
    internal lateinit var mGoogleApiClient: GoogleApiClient
    private var mCurrentLocation: Location? = null
    internal lateinit var mLocationRequest: LocationRequest
    private var mLastUpdateTime: String? = null
    private var sentToSettings = false
    private var canUpdateLocation = false
    private var permissionStatus: SharedPreferences? = null
    private var first = true;
    private var lastIndex = INDEX_NEARBY

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(INTERVAL)
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        Log.d(TAG, "onCreate ...............................");



        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build()

        checkPermissions()
        createLocationRequest()

        mBottomBar = binding.bottomBar;
        mBottomBar!!.selectTabAtPosition(INDEX_NEARBY)
        mNavController = FragNavController.newBuilder(savedInstanceState, supportFragmentManager, R.id.container)
                .transactionListener(this)
                .rootFragmentListener(this, 5)
                .build()

        mBottomBar!!.setOnTabSelectListener { tabId ->
            when (tabId) {
                R.id.bb_menu_search -> {
                    lastIndex = mNavController!!.currentStackIndex
                    mNavController!!.switchTab(INDEX_BUSCAR)
                }
                R.id.bb_menu_nearby -> {
                    lastIndex = mNavController!!.currentStackIndex
                    mNavController!!.switchTab(INDEX_NEARBY)
                }
                R.id.bb_menu_friends -> {
                    lastIndex = mNavController!!.currentStackIndex
                    mNavController!!.switchTab(INDEX_FRIENDS)
                }
                R.id.bb_menu_profile -> {
                    lastIndex = mNavController!!.currentStackIndex
                    mNavController!!.switchTab(INDEX_PROFILE)
                }
            }
            first = false

        }

        mBottomBar!!.setOnTabReselectListener { mNavController!!.clearStack() }

    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionStatus = this.getSharedPreferences("permissionStatus", Context.MODE_PRIVATE)

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Show Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Se necesitan permisos")
                builder.setMessage("Está aplicacion necesita acceder a su ubicación.")
                builder.setPositiveButton("Conceder") { dialog, which ->
                    dialog.cancel()
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CALLBACK_CONSTANT)
                }
                builder.setNegativeButton("No conceder") { dialog, which ->
                    dialog.cancel()
                    canUpdateLocation = false
                    mGoogleApiClient.disconnect()
                }
                builder.show()
            } else if (permissionStatus!!.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Se necesitan permisos")
                builder.setMessage("Está aplicacion necesita acceder a su ubicación.")
                builder.setPositiveButton("Conceder") { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    Toast.makeText(this, "Ve ajustes y concede los permisos", Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton("Cancelar") { dialog, which ->
                    dialog.cancel()
                    canUpdateLocation = false
                    mGoogleApiClient.disconnect()
                }
                builder.show()
            } else {
                //just request the permission
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CALLBACK_CONSTANT)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart fired ..............");
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop fired ..............")
        //mGoogleApiClient.disconnect()
        mGoogleApiClient.connect()
        startLocationUpdates()
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected)
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected)
        //startLocationUpdates()
    }

    protected fun startLocationUpdates() {
        if (canUpdateLocation) {
            if (mGoogleApiClient.isConnected) {
                val pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this)
                Log.d(TAG, "Location update started ..............: ")
            }
        } else {
            stopLocationUpdates();
        }


    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "Firing onLocationChanged..............................................")
        if (mCurrentLocation == null) {
            Log.d(TAG, "FirstAttemp")
            mCurrentLocation = location
        } else if (isDistanceOverThan10(location)) {
            Log.d(TAG, "The distance is over than 10 km")
            areEventsNearby(location)
            mCurrentLocation = location
        }
        mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
        Log.d(TAG, location.latitude.toString() + "" + location.longitude.toString())
        Log.d(TAG, mLastUpdateTime)
    }

    private fun areEventsNearby(location: Location) {
        val retro: Retrofit = getRetrofitInstance()
        val eventoService: EventoService = retro.create(EventoService::class.java)

        eventoService.getNearEventos(location.latitude, location.longitude)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            if (response.page?.size!! > 0) {
                                createNotification()
                            }
                            Log.d(TAG, response.eventos?.size.toString())
                        }
                )
    }

    override fun onPause() {
        super.onPause()
        mGoogleApiClient.connect()
        startLocationUpdates()
    }

    protected fun stopLocationUpdates() {
        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this)
            Log.d(TAG, "Location update stopped .......................")
        }

    }


    public override fun onResume() {
        super.onResume()
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                canUpdateLocation = true
                mGoogleApiClient.connect()
            }
        }
        if (mGoogleApiClient.isConnected) {
            stopLocationUpdates()
            Log.d(TAG, "App resume .....................")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mNavController != null) {
            mNavController!!.onSaveInstanceState(outState)
        }
    }

    override fun pushFragment(fragment: Fragment) {
        if (mNavController != null) {
            mNavController!!.pushFragment(fragment)
        }
    }

    override fun onTabTransaction(fragment: Fragment, index: Int) {
        // If we have a backstack, show the back button
        if (supportActionBar != null && mNavController != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(!mNavController!!.isRootFragment)
        }
    }


    override fun onFragmentTransaction(fragment: Fragment, transactionType: FragNavController.TransactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (supportActionBar != null && mNavController != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(!mNavController!!.isRootFragment)
        }
    }

    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            INDEX_BUSCAR -> return ExplorarFragment.newInstance(0)
            INDEX_NEARBY -> return NearbyFragment.newInstance(0)
            INDEX_FRIENDS -> return AddFragment.newInstance(0)
            INDEX_PROFILE -> return PerfilFragment.newInstance(0)
        }
        throw IllegalStateException("Need to send an index that we know")
    }

    private fun isDistanceOverThan10(location: Location): Boolean {
        Log.d(TAG, mCurrentLocation?.distanceTo(location).toString());
        return mCurrentLocation!!.distanceTo(location) > 10000
    }

    private fun createNotification() {
        val intent: Intent = Intent(this, ListaEventosActivity::class.java)
        intent.putExtra("location", mCurrentLocation)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_nearby)
                .setContentTitle("Eventos cercanos")
                .setContentText("Se han encontrado eventos cercanos. Pulse aquí para verlos")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build()

        val mNotificationId = 1
        //notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        val mNotifyMgr: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(mNotificationId, notification)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
                //check if all permissions are granted
                var allgranted = false
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        allgranted = true
                    } else {
                        allgranted = false
                        break
                    }
                }
                if (allgranted) {
                    canUpdateLocation = true
                    mGoogleApiClient.connect()
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Se necesitan permisos")
                    builder.setMessage("Está aplicación necesita acceder a la ubicación para ubicar eventos.")
                    builder.setPositiveButton("Conceder") { dialog, which ->
                        dialog.cancel()

                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CALLBACK_CONSTANT)

                    }
                    builder.setNegativeButton("No Conceder") { dialog, which ->
                        dialog.cancel()
                        canUpdateLocation = false
                        mGoogleApiClient.disconnect()
                    }
                    builder.show()
                } else {
                    Toast.makeText(this, "Permisos no concedidos", Toast.LENGTH_LONG).show()
                    canUpdateLocation = false
                    mGoogleApiClient.disconnect();
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                canUpdateLocation = true
                mGoogleApiClient.connect()
            }
        }
    }

    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BaseFragment.BASE_URL)
                .build()
    }

    fun getApiClient(): GoogleApiClient {
        return mGoogleApiClient
    }

    fun setLastIndex(index: Int?) {
        if(index != null) {
            lastIndex = index
        }
    }

    companion object {

        val INDEX_PROFILE = FragNavController.TAB4
    }



}
