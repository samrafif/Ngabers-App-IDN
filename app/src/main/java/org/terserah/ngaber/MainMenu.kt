package org.terserah.ngaber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainMenu : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener() {
            when (it.itemId) {
                R.id.home -> startActivity(Intent(this, MainMenu::class.java))
                R.id.chat -> println("lol")
                R.id.activity -> startActivity(Intent(this, MainMenu::class.java))
                R.id.account -> startActivity(Intent(this, Acccount_Detail::class.java))

            }
            true
        }

    }
}


