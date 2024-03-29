package org.terserah.ngaber

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import org.terserah.ngaber.main_menu.MainMenu


class AccountDetailsActivity : AppCompatActivity() {

//    private var pfpFileString: String = ""
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        val users = db.collection("users")

        val currentUser = auth.currentUser

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        val submitBtn: Button = findViewById(R.id.btn_submit)

        val etName: EditText = findViewById(R.id.et_name)
        val etDob: EditText = findViewById(R.id.et_bday)
        val etPlateNum: EditText = findViewById(R.id.et_name2)
        val etVehicleName: EditText = findViewById(R.id.et_name3)
//        val ivPfp: ImageView = findViewById(R.id.iv_pfp)

//        ivPfp.setOnClickListener {
//            chooseMedia()
//        }

        submitBtn.setOnClickListener {
            if (currentUser?.uid == null) {
                return@setOnClickListener
            }

            val data = hashMapOf(
                "user_date_of_birth" to etDob.text.toString(),
                "user_name" to etName.text.toString(),
                "user_plate_number" to etPlateNum.text.toString(),
                "user_vehicle" to etVehicleName.text.toString(),
                "user_rating" to 0.0f,
                "balance" to 500_000,
                "user_role" to "CUSTOMER"
            )

            users.document(currentUser.uid).set(data)
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(etName.text.toString()).build()

            currentUser.updateProfile(profileUpdates)

            startActivity(Intent(this, MainMenu::class.java))

        }

    }
//
//    private val REQUEST_CODE = 50
//    private fun chooseMedia() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        //launch picker screen
//        startActivityForResult(intent, REQUEST_CODE)
//    }
//
//    private fun getFileType(uri: Uri): String? {
//        val r = contentResolver
//        val mimeTypeMap = MimeTypeMap.getSingleton()
//        return mimeTypeMap.getExtensionFromMimeType(r.getType(uri))
//    }


//    @SuppressLint("Recycle")
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
//            //Get URI pointing to the file that was selected from the user
//            data.data?.let { uri ->
//
//                if (auth.currentUser?.uid == null) {
//                    return
//                }
//
//                println(uri.toString())
//                println(contentResolver.query(uri, null, null, null, null))
//                pfpFileString = auth.currentUser!!.uid + ".png"
//
//                // Create a storage reference from our app
//                val storageRef = storage.reference
//                val mountainsRef = storageRef.child("pfps/" + auth.currentUser!!.uid + ".png")
//
//                val stream = contentResolver.openInputStream(uri)!!
//
//                val uploadTask = mountainsRef.putStream(stream)
//                uploadTask.addOnFailureListener {
//                    // Handle unsuccessful uploads
//                }.addOnSuccessListener { taskSnapshot ->
//                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                    // ...
//                }
//
//                //At this point the Temporary file object is ready, you can upload or use as needed
//
//            }
//        }
//    }
}