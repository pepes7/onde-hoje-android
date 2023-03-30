package com.example.ondehoje

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.ondehoje.dao.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)
        val email = view.findViewById<EditText>(R.id.edit_email_perfil)
        val name = view.findViewById<EditText>(R.id.edit_name_perfil)

        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser

        if(user != null){
            var query: Query = database.child("usuarios").child(user.uid)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Passar os dados para a interface grafica
                   val user = dataSnapshot.getValue(User::class.java)
                    email.setText(user!!.email)
                    name.setText(user!!.nome)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //Se ocorrer um erro
                }
            })

        }
        val btn_perfil = view.findViewById<Button>(R.id.btn_criar_evento)
        btn_perfil.setOnClickListener {
            startActivity( Intent( context, CreateEventActivity::class.java))
        }
        val btn_preferencias = view.findViewById<Button>(R.id.btn_edit_preferences)
        btn_preferencias.setOnClickListener {
            startActivity( Intent( context, PreferencesActivity::class.java))
        }

        val btn_disconect = view.findViewById<Button>(R.id.btn_disconect)
        btn_disconect.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity( Intent( context, SplashActivity::class.java))
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}