package com.example.ondehoje

import MapFragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.viewPage, HomeFragment()).commit()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.viewPage, HomeFragment()).commit()
                    true
                }
                R.id.navigation_compass -> {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.viewPage)
                    if (currentFragment is MapFragment) {
                        true // Retorna true imediatamente se já estiver no MapFragment
                    } else {
                        supportFragmentManager.beginTransaction().replace(R.id.viewPage, MapFragment()).commit()
                        true
                    }
                }
                R.id.navigation_profile -> {
                    val user = FirebaseAuth.getInstance().currentUser
                    if(user != null){
                        supportFragmentManager.beginTransaction().replace(R.id.viewPage, PerfilFragment()).commit()
                    }else{
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }
    }
}