package org.terserah.ngaber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.terserah.ngaber.databinding.ActivityOnboardingBinding

class Onboarding : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val contentview = binding.root
        setContentView(contentview)
    }
}