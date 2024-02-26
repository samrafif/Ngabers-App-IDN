package org.terserah.ngaber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class PersonalInformation : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)

        auth = Firebase.auth
        firestore = Firebase.firestore

        val etFullName: EditText = findViewById(R.id.et_full_name)
        val etDOB: EditText = findViewById(R.id.et_birth_date)
        val etPhoneNum: EditText = findViewById(R.id.et_phone_numb)
        val userRole: TextView = findViewById(R.id.user_role)
        val etVehicle: EditText = findViewById(R.id.et_vehicle_type)

        firestore.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
            if (it.data == null) return@addOnSuccessListener

            userRole.text = it.data!!["user_role"].toString().capitalize()
            etFullName.setText(it.data!!["user_name"].toString())
            etDOB.setText(it.data!!["user_date_of_birth"].toString())
            etPhoneNum.setText(it.data!!["user_plate_number"].toString())
            etVehicle.setText(it.data!!["user_vehicle"].toString())

        }

        val btnBack: ImageView = findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            finish()
        }

        val btnSave: ConstraintLayout = findViewById(R.id.btn_save)

        btnSave.setOnClickListener {
            val edittedData = hashMapOf(
                "user_date_of_birth" to etDOB.text.toString(),
                "user_name" to etFullName.text.toString(),
                "user_plate_number" to etPhoneNum.text.toString(),
                "user_vehicle" to etVehicle.text.toString(),
            )

            firestore.collection("users").document(auth.currentUser?.uid.toString()).update(
                edittedData as Map<String, Any>
            )
        }

    }
}