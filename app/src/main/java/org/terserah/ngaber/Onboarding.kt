package org.terserah.ngaber

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.terserah.ngaber.databinding.ActivityOnboardingBinding
import org.terserah.ngaber.firebase_ops.Auth

class Onboarding : AppCompatActivity() {
    private fun onSignupComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("VERBOSE", "createUserWithEmail:success")

            startActivity(Intent(this, MainMenu::class.java))
        } else {
            // If sign in fails, display a message to the user.
            Log.w("VERBOSE", "createUserWithEmail:failure", task.exception)
            Toast.makeText(
                baseContext,
                "Authentication failed.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
    private fun onLoginComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("VERBOSE", "signInUserWithEmail:success")

            startActivity(Intent(this, MainMenu::class.java))
        } else {
            // If sign in fails, display a message to the user.
            Log.w("VERBOSE", "signInUserWithEmail:failure", task.exception)
            Toast.makeText(
                baseContext,
                "Authentication failed.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var auth_ops: Auth
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
        while (viewPager.currentItem == 4){
            val buttonSignup: Button = findViewById(R.id.buttonSignup)
            val signInText: TextView = findViewById(R.id.tv_signin)
            val emailInputSignup: EditText = findViewById(R.id.editTextTextEmailAddressSignup)
            val passwdInputSignup: EditText = findViewById(R.id.editTextTextPasswordSignup)

            val buttonLogin: Button = findViewById(R.id.buttonLogin)
            val emailInputLogin: EditText = findViewById(R.id.editTextTextEmailAddressLogin)
            val passwdInputLogin: EditText = findViewById(R.id.editTextTextPasswordLogin)
            val emailTextLogin = emailInputLogin.text.toString()
            val passwdTextLogin = passwdInputLogin.text.toString()


            buttonSignup.setOnClickListener {
                auth_ops.createAccount(auth, emailInputSignup.text.toString(), passwdInputSignup.text.toString())
                    .addOnCompleteListener(this) { task -> this.onSignupComplete(task)}
            }

            signInText.setOnClickListener {
                startActivity(Intent(this, LoginMenu::class.java))
            }
            buttonLogin.setOnClickListener {
                if (auth_ops.validateInput(baseContext, emailTextLogin, passwdTextLogin)) {
                    auth_ops.logInAccount(auth, emailTextLogin, passwdTextLogin)
                        .addOnCompleteListener(this) { task -> this.onLoginComplete(task)}
                }
            }
        }

        }
    }