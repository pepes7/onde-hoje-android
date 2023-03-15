package com.example.ondehoje

import MapFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

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
                    // Ação do item "Home"
                    supportFragmentManager.beginTransaction().replace(R.id.viewPage, HomeFragment()).commit()
                    true
                }
                R.id.navigation_compass -> {
                    // Ação do item "Dashboard"
                    supportFragmentManager.beginTransaction().replace(R.id.viewPage, MapFragment()).commit()
                    true
                }
                R.id.navigation_profile -> {
                    // Ação do item "Notifications"
                    true
                }
                else -> false
            }
        }
    }
}