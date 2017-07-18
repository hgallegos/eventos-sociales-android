package com.hm.eventossociales.domain.views;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hm.eventossociales.BR;
import com.hm.eventossociales.R;
import com.hm.eventossociales.activities.ListaEventosActivity;
import com.hm.eventossociales.config.adapter.RecyclerViewScrollAdapter;
import com.hm.eventossociales.domain.Evento;
import com.hm.eventossociales.domain.Usuario;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * Created by hans6 on 06-06-2017.
 */

public class ItemViewModel {


    public ObservableList<Evento> items = new ObservableArrayList<>();
    public final ItemBinding<Evento> itemBinding = ItemBinding.<Evento>of(BR.item, R.layout.item);

    public RecyclerViewScrollAdapter adapter = new RecyclerViewScrollAdapter();


    public void addItems(List<Evento> eventos) {
        this.items.addAll(eventos);
    }
}
