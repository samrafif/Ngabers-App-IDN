package org.terserah.ngaber

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.terserah.ngaber.model.LatLng

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderOngoingDriverFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderOngoingDriverFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        print(result.resultCode)
        println(" result")
        if (result.resultCode == Activity.RESULT_OK) {
            print(result)
            println(" result")
        }

         val id = this.arguments?.getString("order_id")

        if (id != null) {
            firestore.collection("orders").document(id).update("done", true)
        }
    }


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
        return inflater.inflate(R.layout.activity_order_request_driver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = Firebase.firestore
        auth = Firebase.auth

        val place = this.arguments?.getString("place")
        val name = this.arguments?.getString("name")
        val distance = this.arguments?.getString("distance")
        val price = this.arguments?.getString("price")
        val id = this.arguments?.getString("order_id")

        val tvDropOff: TextView = view.findViewById(R.id.dropoff_point)
        val tvName: TextView = view.findViewById(R.id.customer_name)
        val tvDistance: TextView = view.findViewById(R.id.distance)
        val tvPrice: TextView = view.findViewById(R.id.price)

        if (name != null) {
            firestore.collection("users").document(name).get().addOnSuccessListener {
                tvName.text = it.data?.get("user_name")?.toString()
            }
        }

        tvDropOff.text = place
        tvDistance.text = distance + " KM"
        tvPrice.text = "Rp" + price

        val btnDecline: ConstraintLayout = view.findViewById(R.id.btn_decline)
        val btnAccept: ConstraintLayout = view.findViewById(R.id.btn_accept)

        btnDecline.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();

            println("all the witnesses")
        }

        btnAccept.setOnClickListener {
            val userOriginString = this.arguments?.getString("customer_origin").toString().split(",")
            val userOrigin = LatLng(
                userOriginString[0].toDouble(),
                userOriginString[1].toDouble()
            )

            val userDestString = this.arguments?.getString("customer_dest").toString().split(",")
            val userDest = LatLng(
                userDestString[0].toDouble(),
                userDestString[1].toDouble()
            )

            println(userDest)

            val updateData = mapOf(
                "driver" to auth.currentUser?.uid.toString(),
                "taken" to true
            )

            if (id != null) {
                firestore.collection("orders").document(id).update(updateData)
            }

            val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$userDestString&waypoints=$userOriginString")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            startForResult.launch(mapIntent)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderOngoingDriverFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderOngoingDriverFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}