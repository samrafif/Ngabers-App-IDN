package org.terserah.ngaber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import android.widget.ImageView
class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        val fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        setContentView(R.layout.activity_main)
        with(findViewById<ImageView>(R.id.logo)){
            alpha = 0f
            startAnimation(fadein)
        }
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        val currentUser = auth.currentUser
        if (currentUser == null) {
            println("Not Logged in")
            startActivity(Intent(this, Onboarding::class.java))
        } else {
            startActivity(Intent(this, MainMenu::class.java))
        }
    }
}