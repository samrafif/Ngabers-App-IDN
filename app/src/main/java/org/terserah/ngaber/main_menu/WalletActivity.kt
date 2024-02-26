package org.terserah.ngaber.main_menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.terserah.ngaber.R

class WalletActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        firestore = Firebase.firestore
        auth = Firebase.auth

        val tvAccountBalance: TextView = findViewById(R.id.account_balance)
        val backBtn: ImageView = findViewById(R.id.back)

        backBtn.setOnClickListener {
            finish()
        }

        firestore.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
            if (it.data == null) return@addOnSuccessListener

            tvAccountBalance.text = it.data!!["balance"].toString()

        }
    }
}