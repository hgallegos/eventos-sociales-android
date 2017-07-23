package com.hm.eventossociales.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hm.eventossociales.R
import org.json.JSONException
import org.json.JSONObject
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookSdk
import com.facebook.login.widget.LoginButton
import java.util.*
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.hm.eventossociales.MainActivity


class PerfilFragment : BaseFragment() {

    private val  TAG: String = "PERFIL"
    var mFacebookCallbackManager: CallbackManager? = null


    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = null
        FacebookSdk.sdkInitialize(this.context)
        mFacebookCallbackManager = CallbackManager.Factory.create()
        if (isTheUserLoginIn()) {
            view = inflater!!.inflate(R.layout.fragment_perfil, container, false);
        } else {
            view = inflater!!.inflate(R.layout.fragment_login, container, false);
            onFacebookClicked(view)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
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
        val loginFacebook = view.findViewById<LoginButton>(R.id.facebook_sign_in_button)
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
        });
    }

    private fun isTheUserLoginIn(): Boolean {
        val sharedPref = activity.getSharedPreferences(MainActivity::getPackageName.name, Context.MODE_PRIVATE)

        return sharedPref.contains("user");
    }
}
