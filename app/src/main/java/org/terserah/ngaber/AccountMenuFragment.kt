package org.terserah.ngaber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.terserah.ngaber.main_menu.WalletActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "ACC_INFO"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore


//        auth.signOut()

//        val btnSignOut: ConstraintLayout = view.findViewById(R.id.btn_signout)
        val tvUserName: TextView = view.findViewById(R.id.username)
        val tvRole: TextView = view.findViewById(R.id.role)

        if (auth.currentUser?.uid != null) {
            val docRef = firestore.collection("users").document(auth.currentUser!!.uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val docData = document.data

                    tvRole.text = (docData?.get("user_role") ?: "NO DATA").toString()

                } else {
                    Log.d(TAG, "No such document")
                }
            }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

        tvUserName.text = auth.currentUser?.displayName
        val btnPayment: ConstraintLayout = view.findViewById(R.id.payment)
        val btnSecurity: ConstraintLayout = view.findViewById(R.id.security)
        val btnInformation: ConstraintLayout = view.findViewById(R.id.information)

        btnPayment.setOnClickListener {
            startActivity(Intent(activity, WalletActivity::class.java))
        }

        btnSecurity.setOnClickListener {
            startActivity(Intent(activity, ActivitySecurity::class.java))
        }

        btnInformation.setOnClickListener {
            startActivity(Intent(activity, PersonalInformation::class.java))
        }

//        btnSignOut.setOnClickListener {
//            auth.signOut()
//            startActivity(Intent(activity, MainActivity::class.java))
//        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}