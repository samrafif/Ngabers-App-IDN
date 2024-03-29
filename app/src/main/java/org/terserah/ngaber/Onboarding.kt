package org.terserah.ngaber

import android.content.Intent
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
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.terserah.ngaber.OnboardingAdapter
import org.terserah.ngaber.firebase_ops.Auth
import org.terserah.ngaber.main_menu.MainMenu

class Onboarding : AppCompatActivity() {
    private fun onSignupComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("VERBOSE", "createUserWithEmail:success")

            startActivity(Intent(this, AccountDetailsActivity::class.java))
        } else {
            // If sign in fails, display a message to the user.
            Log.w("VERBOSE", "createUserWithEmail:failure", task.exception)
            task.exception
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        auth = Firebase.auth
        auth_ops = Auth()

        viewPager = findViewById(R.id.slideViewPager)

        val layouts = intArrayOf(
            R.layout.activity_onboard_1,
            R.layout.activity_onboard_2,
            R.layout.activity_onboard_3,
            R.layout.activity_onboard_4
        )

        adapter = OnboardingAdapter(this, layouts)
        viewPager.adapter = adapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                println(position)
                if (position == 3) {
                    setupLoginSignup()
                }
            }

        })


//        nextBtn.setOnClickListener {
//            val current = viewPager.currentItem + 1
//            println(current)
//            if (current < layouts.size) {
//                viewPager.currentItem = current
//            } else {
//                setupLoginSignup()
//                finish()
//            }
//        }
//
//        backBtn.setOnClickListener{
//                viewPager.currentItem = viewPager.currentItem - 1
//            }
        }

    private fun setupLoginSignup() {
        println("NO SIMPING FOR A CHILD")
        val buttonSignup: Button = findViewById(R.id.buttonSignup)
        val loginText: TextView = findViewById(R.id.tv_login)
        val emailInputSignup: EditText = findViewById(R.id.editTextTextEmailAddressSignup)
        val passwdInputSignup: EditText = findViewById(R.id.editTextTextPasswordSignup)

//            val buttonLogin: Button = findViewById(R.id.buttonLogin)
//            val emailInputLogin: EditText = findViewById(R.id.editTextTextEmailAddressLogin)
//            val passwdInputLogin: EditText = findViewById(R.id.editTextTextPasswordLogin)
//            val emailTextLogin = emailInputLogin.text.toString()
//            val passwdTextLogin = passwdInputLogin.text.toString()


        buttonSignup.setOnClickListener {
            auth_ops.createAccount(auth, emailInputSignup.text.toString(), passwdInputSignup.text.toString())
                .addOnCompleteListener(this) { task -> onSignupComplete(task)} // WOW! Makasih kur kur!
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginMenu::class.java))
        }
        }
    }