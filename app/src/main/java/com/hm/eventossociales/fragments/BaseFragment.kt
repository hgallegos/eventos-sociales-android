package com.hm.eventossociales.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.hm.eventossociales.MainActivity
import com.hm.eventossociales.util.converter.JacksonConverterFactory

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory


open class BaseFragment : Fragment() {

    internal lateinit var mFragmentNavigation: FragmentNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is FragmentNavigation) {
            mFragmentNavigation = context
        }
    }

    val retrofitInstance: Retrofit
        get() = Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

    interface FragmentNavigation {
        fun pushFragment(fragment: Fragment)
    }

    companion object {
        val ARGS_INSTANCE = "com.hm.eventossociales"
        val BASE_URL = "http://192.95.15.158:9090/"
        val USER_NAME = "name"
        val USER_EMAIL = "email"
        val USER_PROFILE_IMAGE = "profileImage"
        val SOCIAL = "social"
        val FACEBOOK = "Facebook"
        val GOOGLE = "Google"
    }

    fun isTheUserLoginIn(): Boolean {
        val sharedPref = activity.getSharedPreferences(MainActivity::getPackageName.name, Context.MODE_PRIVATE)

        return sharedPref.contains(USER_NAME);
    }

    fun getGoogleOptionsInstance(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
    }
}
