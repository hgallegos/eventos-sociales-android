package com.hm.eventossociales.fragments

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.*

import com.hm.eventossociales.R
import org.json.JSONException
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.hm.eventossociales.MainActivity
import com.hm.eventossociales.databinding.FragmentPerfilBinding
import com.hm.eventossociales.util.FbConnectHelper
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status


class PerfilFragment : BaseFragment(), FbConnectHelper.OnFbSignInListener {

    private val TAG: String = "PERFIL"
    private val RC_SIGN_IN = 100
    private val GOOGLE_CLIENT_ID = "860133987317-2e022bv6m71hmg2nqjtvbtgpshdffetm.apps.googleusercontent.com"
    var mFacebookCallbackManager: CallbackManager? = null
    var fbConnectHelper: FbConnectHelper? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var binding: FragmentPerfilBinding? = null;
    private var mToolbar: Toolbar? = null
    private var mAppBarLayout: AppBarLayout? = null


    override fun onStart() {
        super.onStart()

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = null
        FacebookSdk.sdkInitialize(this.context)
        //mFacebookCallbackManager = CallbackManager.Factory.create()
        fbConnectHelper = FbConnectHelper(this, this)

        val gso = getGoogleOptionsInstance()

        mGoogleApiClient = GoogleApiClient.Builder(context)
                .enableAutoManage(activity /* FragmentActivity */, 1, /* OnConnectionFailedListener */ GoogleApiClient.OnConnectionFailedListener { })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        if (isTheUserLoginIn()) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_perfil, container, false)
            val logoutBtn = binding?.logout
            mToolbar = binding?.perfilToolbar


            getPerfilInfo()

            logoutBtn?.setOnClickListener { onLogoutClicked() }
            view = binding!!.root
        } else {
            view = inflater!!.inflate(R.layout.fragment_login, container, false);
            mToolbar = view?.findViewById<Toolbar>(R.id.login_toolbar)
            mToolbar?.title = "Iniciar sesión"

            val btnFace = view?.findViewById<AppCompatButton>(R.id.facebook)
            val btnGoogle = view?.findViewById<SignInButton>(R.id.google)
            btnGoogle?.setOnClickListener { onGoogleClicked() }
            btnFace?.setOnClickListener { onFacebookClicked(view!!) }
            // onFacebookClicked(view)
        }

        (activity as AppCompatActivity).setSupportActionBar(mToolbar)

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

            fragmentManager.beginTransaction().detach(this).attach(this).commit()


        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun OnFbError(errorMessage: String?) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
    }

    companion object {

        fun newInstance(instance: Int): PerfilFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = PerfilFragment()
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

    private fun onLogoutClicked() {
        fbConnectHelper?.disconnect()
        val sharedPref = activity.getSharedPreferences(MainActivity::getPackageName.name, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        if (mGoogleApiClient!!.isConnected) {
            googleSignOut()
        }

        if (binding == null) {
            switchView()
        }

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
                    .enableAutoManage(activity /* FragmentActivity */, 1, /* OnConnectionFailedListener */ GoogleApiClient.OnConnectionFailedListener { })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleOptionsInstance())
                    .build();
        }
    }


    private fun getPerfilInfo() {
        val sharedPref = activity.getSharedPreferences(MainActivity::getPackageName.name, Context.MODE_PRIVATE)

        val imageUrl = sharedPref.getString(USER_PROFILE_IMAGE, null)

        if (imageUrl != null) {
            Glide.with(activity).load(imageUrl).into(binding?.perfilImage)
        }

        binding?.perfilToolbar?.title = sharedPref.getString(USER_NAME, "")
    }
}
