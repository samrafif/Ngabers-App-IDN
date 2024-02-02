package org.terserah.ngaber.firebase_ops

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class Auth {
    fun validateInput(ctx: Context, email: String, passwd: String): Boolean {
        val valid = email.isNotEmpty() and passwd.isNotEmpty()
        if (!valid) {
            Toast.makeText(
                ctx,
                "Password or Email is empty",
                Toast.LENGTH_SHORT,
            ).show()
        }

        return valid
    }
    fun createAccount(auth: FirebaseAuth, email: String, passwd: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, passwd)
    }

    fun logInAccount(auth: FirebaseAuth, email: String, passwd: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, passwd)
    }
}