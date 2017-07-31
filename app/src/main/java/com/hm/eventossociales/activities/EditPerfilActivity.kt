package com.hm.eventossociales.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hm.eventossociales.R
import com.hm.eventossociales.databinding.ActivityEditarPerfilBinding

/**
 * Created by hans6 on 30-07-2017.
 */
class EditPerfilActivity : AppCompatActivity() {

    private val TAG = "EditPerfilActivity"
    internal lateinit var binding: ActivityEditarPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_editar_perfil)

        binding.editarPerflToolbar.title = "Editar perfil"
        setSupportActionBar(binding.editarPerflToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}