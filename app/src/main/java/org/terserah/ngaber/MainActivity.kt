package org.terserah.ngaber

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import kotlinx.coroutines.delay
import org.terserah.ngaber.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        with(binding.logo){
            alpha = 0.1f
            startAnimation(fade_in)
        }
        suspend { delay(5000) }
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val isFirstTime = sharedPref.getBoolean("is_first_time", true)
        if (isFirstTime) {
            startActivity(Intent(this, Onboarding::class.java))
            with (sharedPref.edit()) {
                putBoolean("is_first_time", false)
                apply()
            }
        } else {
            startActivity(Intent(this, MainMenu::class.java))
         }

    }
}