package com.hm.eventossociales.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.hm.eventossociales.R
import com.hm.eventossociales.activities.ListaEventosActivity

class ExplorarFragment : BaseFragment() {

    private val TAG = "ExplorarFragment"

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
        val view = inflater!!.inflate(R.layout.fragment_search, container, false)

        val button = view.findViewById<Button>(R.id.search)
        button.setOnClickListener { searchForEvents() }
        return view
    }

    public fun getCategories() {
    }

    fun searchForEvents() {

        val intent: Intent = Intent(activity, ListaEventosActivity::class.java)
        val nombreEdit = view?.findViewById<EditText>(R.id.nombre)
        val lugarEdit = view?.findViewById<EditText>(R.id.lugar)
        intent.putExtra("nombre", nombreEdit?.text.toString())
        intent.putExtra("lugar", lugarEdit?.text.toString())
        /*intent.putExtra("categoria", categoriaText?.text.toString())*/

        startActivity(intent)

    }
}
