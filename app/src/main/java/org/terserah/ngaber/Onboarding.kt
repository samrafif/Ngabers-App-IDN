package org.terserah.ngaber

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import org.terserah.ngaber.databinding.ActivityOnboardingBinding

class Onboarding : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var adapter: OnboardingAdapter
    private lateinit var binding: ActivityOnboardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_onboarding)

        if (viewPager.currentItem < 0){
            binding.backBtn.visibility = View.INVISIBLE
        }
        if (viewPager.currentItem == 4){
            binding.nextBtn.text = "Get started"
        }

        val layouts = intArrayOf(
            R.layout.activity_onboard_1,
            R.layout.activity_onboard_2,
            R.layout.activity_onboard_3,
            R.layout.activity_onboard_4
        )

        viewPager = findViewById(R.id.slideViewPager)
        adapter = OnboardingAdapter(this, layouts)
        viewPager.adapter = adapter

        val btnNext = findViewById<Button>(R.id.next_btn)
        btnNext.setOnClickListener {
            val current = viewPager.currentItem + 1
            if (current < layouts.size) {
                viewPager.currentItem = current
            } else {
                startActivity(Intent(this, MainMenu::class.java))
                finish()

            }
        }
        val btnBack = findViewById<Button>(R.id.back_btn)
        btnBack.setOnClickListener{
            val current = viewPager.currentItem - 1
        }
    }
}