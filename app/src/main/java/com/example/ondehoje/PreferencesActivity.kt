package com.example.ondehoje

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class PreferencesActivity : AppCompatActivity() {
    var category = ""
    var turno = ""
    private lateinit var userReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            userReference = FirebaseDatabase.getInstance().reference.child("usuarios").child(user.uid)
        }


        initial()
        val button = findViewById<Button>(R.id.btn_confirm)
        button.setOnClickListener {
            register()
        }
    }

    fun initial(){
        val itensCategories = arrayOf("", "Festa", "Reustaurante","Entretenimento", "Bar/Pub", "Café", "Museus", "Parques" )
        val spinner = findViewById<Spinner>(R.id.spinner_categoria)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, itensCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                category = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spinner.adapter = adapter

        val itensTurno = arrayOf("", "Noturno", "Diurno","Matutino" )

        val spinnerTurno = findViewById<Spinner>(R.id.spinner_turno)
        val adapterTurno = ArrayAdapter(this, R.layout.spinner_item, itensTurno)
        adapterTurno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinnerTurno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                turno = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spinnerTurno.adapter = adapterTurno
    }

    fun register(){
        val pd = ProgressDialog(this)
        pd.setMessage("Criando evento")
        pd.show()
        userReference.child("category").setValue(category)
        userReference.child("turno").setValue(turno).addOnCompleteListener {
            finish()
        }

    }
}