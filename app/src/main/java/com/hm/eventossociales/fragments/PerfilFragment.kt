package com.hm.eventossociales.fragments

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.facebook.*

import com.hm.eventossociales.R
import org.json.JSONException
import org.json.JSONObject
import com.facebook.login.widget.LoginButton
import java.util.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.plus.model.people.Person
import com.hm.eventossociales.MainActivity
import com.hm.eventossociales.databinding.FragmentPerfilBinding
import com.hm.eventossociales.databinding.FragmentSearchBinding
import com.hm.eventossociales.util.FbConnectHelper
import com.hm.eventossociales.util.GooglePlusSignInHelper


class PerfilFragment : BaseFragment(), FbConnectHelper.OnFbSignInListener, GooglePlusSignInHelper.OnGoogleSignInListener {

    private val TAG: String = "PERFIL"
    private val GOOGLE_CLIENT_ID = "860133987317-2e022bv6m71hmg2nqjtvbtgpshdffetm.apps.googleusercontent.com"
    private val USER_NAME = "name"
    private val USER_EMAIL = "email"
    private val USER_PROFILE_IMAGE = "profileImage"
    var mFacebookCallbackManager: CallbackManager? = null
    var fbConnectHelper: FbConnectHelper? = null
    private var gSignInHelper: GooglePlusSignInHelper? = null
    internal lateinit var binding: FragmentPerfilBinding;


    override fun onStart() {
        super.onStart()

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = null
        FacebookSdk.sdkInitialize(this.context)
        //mFacebookCallbackManager = CallbackManager.Factory.create()
        fbConnectHelper = FbConnectHelper(this, this)
        GooglePlusSignInHelper.setClientID(GOOGLE_CLIENT_ID)
        gSignInHelper = GooglePlusSignInHelper.getInstance()
//        gSignInHelper?.initialize(activity, this)

        if (isTheUserLoginIn()) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_perfil, container, false)
            binding.logout.setOnClickListener { onLogoutClicked() }
            view = binding.root
        } else {
            view = inflater!!.inflate(R.layout.fragment_login, container, false);
            val btnFace = view?.findViewById<Button>(R.id.facebook)
            btnFace?.setOnClickListener { onFacebookClicked(view!!) }
            // onFacebookClicked(view)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //mFacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GooglePlusSignInHelper.RC_SIGN_IN) {
            gSignInHelper?.onActivityResult(requestCode, resultCode, data);
        }
        fbConnectHelper?.onActivityResult(requestCode, resultCode, data)
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

    override fun OnGSignSuccess(googleSignInAccount: GoogleSignInAccount?, person: Person?) {
        val name = if (googleSignInAccount?.displayName == null) "" else googleSignInAccount.displayName
        val email = googleSignInAccount?.email

        Log.i(TAG, "OnGSignSuccess: IdToken " + googleSignInAccount?.idToken)

        val photoUrl = googleSignInAccount?.photoUrl
        var profilePic = ""
        if (photoUrl != null)
            profilePic = photoUrl.toString()

        val packageName = MainActivity::getPackageName.name
        val prefs = activity.getSharedPreferences(packageName, Context.MODE_PRIVATE)
        prefs.edit().putString(USER_NAME, name).apply()
        prefs.edit().putString(USER_EMAIL, email).apply()
        prefs.edit().putString(USER_PROFILE_IMAGE, profilePic).apply()

        fragmentManager.beginTransaction().detach(this).attach(this).commit()

    }

    override fun OnGSignError(errorMessage: GoogleSignInResult?) {
        Toast.makeText(activity, errorMessage.toString(), Toast.LENGTH_SHORT).show();
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
        /*val loginFacebook = view.findViewById<LoginButton>(R.id.facebook_sign_in_button)
        loginFacebook.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email", "user_birthday"))
        loginFacebook.fragment = this

        loginFacebook.registerCallback(mFacebookCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { `object`, response ->
                    try {
                        // get profile information
                        var name = ""
                        var email = ""
                        var uriPicture = ""
                        if (`object`.getString("name") != null) {
                            name = `object`.getString("name")
                        }
                        if (`object`.getString("email") != null) {
                            email = `object`.getString("email")
                        }
                        if (`object`.getString("picture") != null) {
                            val imagen = JSONObject(`object`.getString("picture"))
                            val imagen2 = JSONObject(imagen.getString("data"))
                            uriPicture = imagen2.getString("url")
                        }
                        // save profile information to preferences
                        val packageName = MainActivity::getPackageName.name
                        val prefs = activity.getSharedPreferences(packageName, Context.MODE_PRIVATE)
                        prefs.edit().putString("user", name).apply()
                        prefs.edit().putString(packageName, email).apply()
                        prefs.edit().putString(packageName, uriPicture).apply()
                        // redirect to main screen
                        //startActivity(Intent(this@Registro, Main::class.java))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,link,gender,birthday,email,picture")
                request.parameters = parameters
                request.executeAsync()

            }

            override fun onCancel() {}

            override fun onError(error: FacebookException) {
                Log.d(PerfilFragment::class.java!!.canonicalName, error.message)
            }
        });*/
        fbConnectHelper?.connect()
    }

    private fun onGoogleClicked() {
        gSignInHelper?.signIn(activity)
    }

    private fun onLogoutClicked() {
        fbConnectHelper?.disconnect()
       // gSignInHelper?.signOut()
        val sharedPref = activity.getSharedPreferences(MainActivity::getPackageName.name, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        fragmentManager.beginTransaction().detach(this).attach(this).commit()

    }

    private fun isTheUserLoginIn(): Boolean {
        val sharedPref = activity.getSharedPreferences(MainActivity::getPackageName.name, Context.MODE_PRIVATE)

        return sharedPref.contains(USER_NAME);
    }
}
