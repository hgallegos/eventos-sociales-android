package com.hm.eventossociales.fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.hm.eventossociales.R
import com.hm.eventossociales.activities.ListaEventosActivity
import com.hm.eventossociales.databinding.ActivityEventoBinding
import com.hm.eventossociales.databinding.FragmentSearchBinding
import com.hm.eventossociales.domain.Categoria
import com.hm.eventossociales.domain.views.ItemViewModel
import com.hm.eventossociales.domain.views.SpinnerViewModel
import com.hm.eventossociales.services.CategoriaService
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ExplorarFragment : BaseFragment() {

    private val TAG = "ExplorarFragment"
    internal lateinit var binding: FragmentSearchBinding;


    override fun onStart() {
        super.onStart()
    }

    companion object {

        fun newInstance(instance: Int): ExplorarFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = ExplorarFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        val button = binding.search;

        val viewModel = SpinnerViewModel()
        binding.spinnerViewModel = viewModel

        binding.searchToolbar.title = "Busca eventos"

        (activity as AppCompatActivity).setSupportActionBar(binding.searchToolbar)


        getCategories();

        button.setOnClickListener { searchForEvents() }
        return binding.root
    }

    fun getCategories() {
        val categoriaService = retrofitInstance.create(CategoriaService::class.java);

        categoriaService.getCategorias()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            Log.d(TAG, response.categorias?.size.toString())
                            val viewModel = SpinnerViewModel()
                            viewModel.addItems(response.categorias)
                            viewModel.setContext(this.view)
                            binding.spinnerViewModel = viewModel
                        },
                        { error ->
                            Log.e(TAG, error.message)
                        }
                )
    }

    fun searchForEvents() {

        val intent: Intent = Intent(activity, ListaEventosActivity::class.java)
        val nombreEdit = binding.nombre
        val lugarEdit = binding.lugar
        val categoriaSprinner = binding.categoria
        intent.putExtra("nombre", nombreEdit?.text.toString())
        intent.putExtra("lugar", lugarEdit?.text.toString())
        intent.putExtra("categoria", (categoriaSprinner?.selectedItem as Categoria).self?.href)

        startActivity(intent)

    }
}
