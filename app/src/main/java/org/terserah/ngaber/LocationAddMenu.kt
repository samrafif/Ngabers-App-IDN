package org.terserah.ngaber

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import org.terserah.ngaber.utils.LocationPermissionHelper
import java.lang.ref.WeakReference
import kotlin.math.ceil


class LocationAddMenu : AppCompatActivity() {
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private var locationProvider: DeviceLocationProvider? = null

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
//            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
            onCameraMovementStop()
        }
    }


    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a map programmatically and set the initial camera
        setContentView(R.layout.activity_location_add_menu)
        mapView = findViewById(R.id.mapView)

        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
    }

    private fun onMapReady() {

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .zoom(15.0)
                .build()
        )

        // Add the map view to the activity (you can also add it to other views as a child)
        val hoveringMarker = ImageView(this)
        hoveringMarker.setImageResource(R.drawable.baseline_location_on_24)
        val params = FrameLayout.LayoutParams(
            120,
            120, Gravity.CENTER
        )
        hoveringMarker.setLayoutParams(params)
        mapView.addView(hoveringMarker)

        mapView.mapboxMap.loadStyle(
            Style.STANDARD
        ) {
            initLocationComponent()
            setupGesturesListener()
        }

        val reCenterBtn: FloatingActionButton = findViewById(R.id.fab_recenter)
        reCenterBtn.setOnClickListener {
            reCenterCamera()
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        val locationService : LocationService = LocationServiceFactory.getOrCreate()

        val request = LocationProviderRequest.Builder()
            .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
            .displacement(0F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build();

        val result = locationService.getDeviceLocationProvider(request)
        if (result.isValue) {
            locationProvider = result.value!!
        } else {
            Log.d("ERROR","Failed to get device location provider")
        }
        reCenterCamera()
    }

    private fun reCenterCamera() {
        val lastLocationCancelable = locationProvider?.getLastLocation { result ->
            result?.let {
                println(result.latitude)
                println(result.longitude)

                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(result.longitude, result.latitude))
                        .build()
                )
            }
        }
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
    }

    private fun onCameraMovementStop() {
        val mapTargetLatLng = mapView.mapboxMap.cameraState.center

        println(mapTargetLatLng.latitude())
        println(mapTargetLatLng.longitude())

        val lat = mapTargetLatLng.latitude()
        val long = mapTargetLatLng.longitude()

        val degsLat = lat.toInt()
        val minsLat = (lat - degsLat) * 60
        val secsLat = (minsLat - minsLat.toInt()) * 60

        val degsLong = long.toInt()
        val minsLong = (long - degsLong) * 60
        val secsLong = (minsLong - minsLong.toInt()) * 60

        val text: TextView = findViewById(R.id.textView)
        text.text = "lat: ${degsLat}'${ceil(minsLat).toInt()}''${ceil(secsLat).toInt()} \nlong: ${degsLong}'${ceil(minsLong).toInt()}''${ceil(secsLong).toInt()}"

//        Toast.makeText(
//            this,
//            "lat: ${degsLat}'${ceil(minsLat).toInt()}''${ceil(secsLat).toInt()} long: ${degsLong}'${ceil(minsLong).toInt()}''${ceil(secsLong).toInt()}",
//            Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}