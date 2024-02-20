package org.terserah.ngaber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import java.util.UUID

class ActivityOrderDetail : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        firestore = Firebase.firestore
        val extra1: Place = intent.getParcelableExtra("place_info")!!
        val originLat: Double = intent.getDoubleExtra("origin_loc_lat", 0.0)
        val originLong: Double = intent.getDoubleExtra("origin_loc_long", 0.0)
        val distance = intent.getIntExtra("distance", 0)


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
                .center(Point.fromLngLat(extra1.latLng.longitude, extra1.latLng.latitude ))
                .build()
        )

        tvPlaceName.text = extra1.name

        val myUuid = UUID.randomUUID()
        val myUuidAsString = myUuid.toString()

        val ordersRef = firestore.collection("orders")
        val usersRef = firestore.collection("users")
        val newOrder = ordersRef.document(myUuidAsString)

        val orderData = mapOf(
            "origin" to "$originLat,$originLong",
            "destination" to "${extra1.latLng.latitude},${extra1.latLng.longitude}",
            "price" to "${distance * 0.01}",
            "taken" to false,
            "driver_location" to "$originLat,$originLong",
            "driver" to ""
        )
        newOrder.set(orderData)

        val TAG = "TAG"

        newOrder.addSnapshotListener { snapshot, e ->
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

                var location = snapshot.data?.get("driver_location")
                location = location.toString().split(",")

                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .zoom(15.0)
                        .center(Point.fromLngLat(location[0].toDouble(), location[1].toDouble() ))
                        .build()
                )

            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }
}