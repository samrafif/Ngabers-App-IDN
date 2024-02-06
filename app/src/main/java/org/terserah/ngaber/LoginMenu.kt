package org.terserah.ngaber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.terserah.ngaber.firebase_ops.Auth

class LoginMenu : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var auth_ops: Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        auth_ops = Auth()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)

        val signupText: TextView = findViewById(R.id.tv_signup)
        val button: Button = findViewById(R.id.button)
        val emailInput: EditText = findViewById(R.id.editTextTextEmailAddress)
        val passwdInput: EditText = findViewById(R.id.editTextTextPassword)



        button.setOnClickListener {
            val emailText = emailInput.text.toString()
            val passwdText = passwdInput.text.toString()

            if (auth_ops.validateInput(baseContext, emailText, passwdText)) {
                auth_ops.logInAccount(auth, emailText, passwdText)
                    .addOnCompleteListener(this) { task -> onLoginComplete(task)}
            }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, Onboarding::class.java))
        }
    }

    private fun onLoginComplete(task: Task<AuthResult>) {
        println("JALAN")
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
}