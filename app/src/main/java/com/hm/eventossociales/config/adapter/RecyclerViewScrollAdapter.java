package com.hm.eventossociales.config.adapter;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.hm.eventossociales.activities.EventoActivity;
import com.hm.eventossociales.domain.Evento;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

/**
 * Created by hans6 on 07-06-2017.
 */

public class RecyclerViewScrollAdapter<T> extends BindingRecyclerViewAdapter<T> {


    @Override
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup viewGroup) {
        ViewDataBinding binding = super.onCreateBinding(inflater, layoutId, viewGroup);
        Log.d("", "created binding: " + binding);
        return binding;
    }

    @Override
    public void onBindBinding(ViewDataBinding binding, int bindingVariable, @LayoutRes int layoutId, final int position, final T item) {
        super.onBindBinding(binding, bindingVariable, layoutId, position, item);
        Log.d("", "bound binding: " + binding + " at position: " + position);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Evento evento = (Evento) item;
                Intent intent = new Intent(view.getContext(), EventoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("nombre", evento.getNombre());
                intent.putExtra("eventoUrl", evento.getSelf().getHref());
                intent.putExtra("fotosUrl", evento.getFotos().getHref());
                intent.putExtra("LatLng", new LatLng(evento.getpLat(), evento.getpLng()));
                view.getContext().startActivity(intent);
            }
        });
    }
}
