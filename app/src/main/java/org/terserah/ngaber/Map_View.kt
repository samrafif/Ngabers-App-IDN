package org.terserah.ngaber

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import java.util.UUID

class Map_View : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var mapView: MapView
    private lateinit var auth: FirebaseAuth
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)

        firestore = Firebase.firestore
        auth = Firebase.auth

        val extra1: Place = intent.getParcelableExtra("place_info")!!
        val originLat: Double = intent.getDoubleExtra("origin_loc_lat", 0.0)
        val originLong: Double = intent.getDoubleExtra("origin_loc_long", 0.0)
        val distance = intent.getIntExtra("distance", 0)


        mapView = findViewById(R.id.mapViewInner)
        val tvPlaceName: TextView = findViewById(R.id.destination)

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

        val tvTotal: TextView = findViewById(R.id.total)

        val myUuid = UUID.randomUUID()
        val myUuidAsString = myUuid.toString()

        val ordersRef = firestore.collection("orders")
        val usersRef = firestore.collection("users")
        val newOrder = ordersRef.document(myUuidAsString)
        var bal = 0

        val orderData = mapOf(
            "origin" to "$originLat,$originLong",
            "destination" to "${extra1.latLng.latitude},${extra1.latLng.longitude}",
            "places" to "${extra1.name}",
            "price" to "${distance * 0.01}",
            "taken" to false,
            "done" to false,
            "driver_location" to false,
            "customer" to auth.currentUser?.uid.toString(),
            "driver" to ""
        )
        newOrder.set(orderData)

        usersRef.document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
            if (it.data != null) {
                bal = (it.data!!["balance"] as Long).toInt()
                tvTotal.text = "${distance * 0.01}"

                if (bal < (((distance / 1000)) * 1000) ) {
                    tvTotal.text = "BRO YOU BROKE"
                }
            }
        }

        val btnBuy: Button = findViewById(R.id.buy)

        btnBuy.setOnClickListener {
            val intent = Intent(this, ActivityOrderDetail::class.java)

            intent.putExtra("order_id", myUuidAsString)
            intent.putExtra("place_info", extra1)

            startActivity(intent)
        }
    }
}