package com.hm.eventossociales.util.adapter

import android.content.Intent
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.hm.eventossociales.activities.EventoActivity
import com.hm.eventossociales.domain.Categoria
import com.hm.eventossociales.domain.Evento
import com.hm.eventossociales.fragments.BaseFragment
import com.hm.eventossociales.services.FotoService
import com.hm.eventossociales.util.converter.JacksonConverterFactory
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.spinner_item.view.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by hans6 on 19-07-2017.
 */
class SpinnerAdapter<T> : BindingRecyclerViewAdapter<T>() {

    override fun onCreateBinding(inflater: LayoutInflater, @LayoutRes layoutId: Int, viewGroup: ViewGroup): ViewDataBinding {
        val binding = super.onCreateBinding(inflater, layoutId, viewGroup)
        Log.d("", "created binding: " + binding)
        return binding
    }

    override fun onBindBinding(binding: ViewDataBinding, bindingVariable: Int, @LayoutRes layoutId: Int, position: Int, item: T) {

        super.onBindBinding(binding, bindingVariable, layoutId, position, item)

        binding.root.categoria.isEnabled = false;

        if ( position == 0) {
            binding.root.spinnerText.setTextColor(Color.GRAY)
        } else {
            binding.root.spinnerText.setTextColor(Color.BLACK)
        }

        Log.d("", "bound binding: $binding at position: $position")


    }
}