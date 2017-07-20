package com.hm.eventossociales.domain.views;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.view.View;
import android.widget.Spinner;

import com.hm.eventossociales.BR;
import com.hm.eventossociales.R;
import com.hm.eventossociales.domain.Categoria;
import com.hm.eventossociales.domain.Evento;
import com.hm.eventossociales.util.adapter.RecyclerViewScrollAdapter;
import com.hm.eventossociales.util.adapter.SpinnerAdapter;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

/**
 * Created by hans6 on 19-07-2017.
 */

public class SpinnerViewModel {

    private View view;

    public ObservableList<Categoria> items = new ObservableArrayList<>();
    public final OnItemBind<Categoria> itemBinding = new OnItemBind<Categoria>()

    {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, Categoria item) {
            itemBinding.set(BR.item, R.layout.spinner_item);
            if (view != null) {
                Spinner spinner = view.findViewById(R.id.categoria);
                if (position == 0) {
                    spinner.setEnabled(false);

                } else {
                    spinner.setEnabled(true);
                }
            }
        }
    };


    public void addItems(List<Categoria> categorias) {
        this.items.addAll(categorias);
        Categoria categoria = new Categoria();
        categoria.setNombre("¿Qué estás buscando?");
        this.items.add(0, categoria);
    }

    public void setContext(View view) {
        this.view = view;
    }
}
