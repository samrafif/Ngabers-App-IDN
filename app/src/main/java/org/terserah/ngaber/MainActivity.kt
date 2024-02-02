package org.terserah.ngaber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        val currentUser = auth.currentUser
        if (currentUser == null) {
            println("Not Logged in")
            startActivity(Intent(this, SignUpMenu::class.java))
        } else {
            startActivity(Intent(this, MainMenu::class.java))
        }
        setContentView(R.layout.activity_main)
    }
}