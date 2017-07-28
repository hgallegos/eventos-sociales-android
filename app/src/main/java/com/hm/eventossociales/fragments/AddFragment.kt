package com.hm.eventossociales.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.GraphResponse
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.hm.eventossociales.MainActivity

import com.hm.eventossociales.R
import com.hm.eventossociales.databinding.FragmentIngresarBinding
import com.hm.eventossociales.databinding.FragmentPerfilBinding
import com.hm.eventossociales.databinding.FragmentSearchBinding
import com.hm.eventossociales.util.FbConnectHelper
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : BaseFragment(), FbConnectHelper.OnFbSignInListener, DatePickerDialog.OnDateSetListener {

    internal lateinit var binding: FragmentIngresarBinding;
    private val TAG = "AddFragment"
    private val RC_SIGN_IN = 100
    private val GOOGLE_CLIENT_ID = "860133987317-2e022bv6m71hmg2nqjtvbtgpshdffetm.apps.googleusercontent.com"
    var mFacebookCallbackManager: CallbackManager? = null
    var fbConnectHelper: FbConnectHelper? = null
    var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mToolbar: Toolbar
    internal lateinit var myCalendar: Calendar
    private var isDesde = false;

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = null
        FacebookSdk.sdkInitialize(this.context)
        //mFacebookCallbackManager = CallbackManager.Factory.create()
        fbConnectHelper = FbConnectHelper(this, this)

        val gso = getGoogleOptionsInstance()

        mGoogleApiClient = GoogleApiClient.Builder(context)
                .enableAutoManage(activity /* FragmentActivity */, 2, /* OnConnectionFailedListener */ GoogleApiClient.OnConnectionFailedListener { })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        if (isTheUserLoginIn()) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ingresar, container, false)
            mToolbar = binding.ingresarToolbar

            mToolbar.title = "Añadir un evento"

            myCalendar = Calendar.getInstance()

            binding.desde.setOnClickListener {
                DatePickerDialog(activity, this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                isDesde = true
            }
            binding.hasta.setOnClickListener {
                DatePickerDialog(activity, this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                isDesde = false
            }

            view = binding.root
        } else {
            view = inflater!!.inflate(R.layout.fragment_login, container, false)
            mToolbar = view?.findViewById<Toolbar>(R.id.login_toolbar)!!

            mToolbar.title = "Iniciar sesión"

            val btnFace = view.findViewById<AppCompatButton>(R.id.facebook)
            val btnGoogle = view.findViewById<SignInButton>(R.id.google)

            btnGoogle?.setOnClickListener { onGoogleClicked() }
            btnFace?.setOnClickListener { onFacebookClicked(view!!) }

        }

        (activity as MainActivity).setSupportActionBar(mToolbar)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //mFacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
        fbConnectHelper?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    override fun OnFbSuccess(graphResponse: GraphResponse?) {
        try {
            val jsonObject = graphResponse?.jsonObject
            val name = jsonObject?.getString("name")
            val email = jsonObject?.getString("email")
            val id = jsonObject?.getString("id")
            val profileImg = "http://graph.facebook.com/$id/picture?type=large"

            val packageName = MainActivity::getPackageName.name
            val prefs = activity.getSharedPreferences(packageName, Context.MODE_PRIVATE)
            prefs.edit().putString(USER_NAME, name).apply()
            prefs.edit().putString(USER_EMAIL, email).apply()
            prefs.edit().putString(USER_PROFILE_IMAGE, profileImg).apply()

            switchView()


        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun OnFbError(errorMessage: String?) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
    }

    override fun onStart() {
        super.onStart()

    }

    companion object {

        fun newInstance(instance: Int): AddFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = AddFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun onFacebookClicked(view: View) {
        fbConnectHelper?.connect()
    }

    private fun onGoogleClicked() {
        googleSignIn()
    }


    private fun googleSignIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount

            val packageName = MainActivity::getPackageName.name
            val prefs = activity.getSharedPreferences(packageName, Context.MODE_PRIVATE)
            prefs.edit().putString(USER_NAME, acct?.displayName).apply()
            prefs.edit().putString(USER_EMAIL, acct?.email).apply()

            if (acct?.photoUrl != null) prefs.edit().putString(USER_PROFILE_IMAGE, acct.photoUrl?.toString()).apply()

            switchView()

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private fun switchView() {
        fragmentManager.beginTransaction().detach(this).attach(this).commit()
    }

    private fun googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                object : ResultCallback<Status> {
                    override fun onResult(status: Status) {
                        // [START_EXCLUDE]
                        switchView()
                        // [END_EXCLUDE]
                    }
                })
    }


    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient?.stopAutoManage(activity)
            mGoogleApiClient?.disconnect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient?.stopAutoManage(activity)
            mGoogleApiClient?.disconnect()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!mGoogleApiClient!!.isConnected) {
            mGoogleApiClient?.stopAutoManage(activity)
            mGoogleApiClient?.disconnect()
            mGoogleApiClient = GoogleApiClient.Builder(context)
                    .enableAutoManage(activity /* FragmentActivity */, 2, /* OnConnectionFailedListener */ GoogleApiClient.OnConnectionFailedListener { })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleOptionsInstance())
                    .build();
        }
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, monthYear: Int, dayMonth: Int) {
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayMonth)
        if (isDesde) {
            updateDesde()
        } else {
            updateHasta()
        }
    }

    private fun updateDesde() {
        val myFormat = "dd-MM-y" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        binding.desde.setText(sdf.format(myCalendar.time));
    }

    private fun updateHasta() {
        val myFormat = "dd-MM-Y" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        binding.hasta.setText(sdf.format(myCalendar.time));
    }
}
