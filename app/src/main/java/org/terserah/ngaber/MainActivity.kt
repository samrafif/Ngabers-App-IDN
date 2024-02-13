package org.terserah.ngaber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import android.widget.ImageView
import org.terserah.ngaber.BuildConfig
import com.google.android.libraries.places.api.Places
import org.terserah.ngaber.main_menu.MainMenu

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiKey = BuildConfig.PLACES_API_KEY

        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No api key")
            finish()
            return
        }

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        val fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        with(findViewById<ImageView>(R.id.logo)){
            alpha = 0f
            startAnimation(fadein)
        }

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            println("Not Logged in")
            startActivity(Intent(this, Onboarding::class.java))
        } else {
            startActivity(Intent(this, MainMenu::class.java))
        }
    }
}