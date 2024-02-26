package org.terserah.ngaber

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import org.terserah.ngaber.main_menu.MainMenu
import org.terserah.ngaber.main_menu.StartGameDialogFragment

class StartGameDialogFragment(activity: ActivityOrderDetail) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val TAG = "AA"

        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder.setMessage("DRIVER HAS ARRIVED")
                .setPositiveButton("OK") { dialog, id ->
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class StartGameDialogFragmenta(activity: ActivityOrderDetail) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val TAG = "AA"

        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder.setMessage("THANK YOU FOR USING NGABERS")
                .setPositiveButton("OK") { dialog, id ->
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class ActivityOrderDetail : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        firestore = Firebase.firestore
        val extra1: Place = intent.getParcelableExtra("place_info")!!
        val extraUUID: String? = intent.getStringExtra("order_id")


        mapView = findViewById(R.id.mapViewInner)
        val tvPlaceName: TextView = findViewById(R.id.dropoff_location)

        val driverName: TextView = findViewById(R.id.driver_username)
        val driverVehicle: TextView = findViewById(R.id.vehicle)
        val driverRate: TextView = findViewById(R.id.driver_rate)

        val hoveringMarker = ImageView(this)
        hoveringMarker.setImageResource(R.drawable.baseline_location_on_24)
        val params = FrameLayout.LayoutParams(
            120,
            120, Gravity.CENTER
        )
        hoveringMarker.layoutParams = params
        mapView.addView(hoveringMarker)

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .zoom(15.0)
                .center(Point.fromLngLat(extra1.latLng.longitude, extra1.latLng.latitude))
                .build()
        )

        tvPlaceName.text = extra1.name

        val ordersRef = firestore.collection("orders")
        val usersRef = firestore.collection("users")

        val TAG = "TAG"

        extraUUID?.let { ordersRef.document(it) }?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")

                if (!(snapshot.data?.get("taken") as Boolean)) return@addSnapshotListener

                if (driverName.text == "-") {

                    usersRef.document(snapshot.data?.get("driver").toString()).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d(TAG, "DocumentSnapshot data: ${document.data}")

                                driverName.text = document.data?.get("user_name").toString()
                                driverRate.text = document.data?.get("user_rating").toString()
                                driverVehicle.text = document.data?.get("user_vehicle").toString()

                            } else {
                                Log.d(TAG, "No such document")
                            }
                        }.addOnFailureListener { exception ->
                            Log.d(TAG, "get failed with ", exception)
                        }
                }

                if (snapshot.data!!["driver_location"] as Boolean) {
                    StartGameDialogFragment(this@ActivityOrderDetail).show(supportFragmentManager, "GAME_DIALOG")
                }

                if (snapshot.data!!["done"] as Boolean) {
                    StartGameDialogFragmenta(this@ActivityOrderDetail).show(supportFragmentManager, "GAME_DIALOG")
                }


            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }
}