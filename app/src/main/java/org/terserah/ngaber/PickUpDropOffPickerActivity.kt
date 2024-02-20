package org.terserah.ngaber

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPolylineAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import fuel.Fuel
import fuel.HttpResponse
import fuel.get
import fuel.post
import kotlinx.coroutines.runBlocking
import org.terserah.ngaber.model.DirectionsResponse
import org.terserah.ngaber.model.LatLng
import org.terserah.ngaber.utils.LocationPermissionHelper
import java.lang.ref.WeakReference
import java.util.Locale

class PickUpDropOffPickerActivity : AppCompatActivity() {
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private var locationProvider: DeviceLocationProvider? = null
    private var currSelected = true

    private lateinit var destLoc: Place
    private lateinit var destCoords: LatLng
    private lateinit var startCoords: LatLng

    private val LAYER_ID = "road-street-navigation"
    private val SOURCE_ID = "nav-lines"
    private val PITCH_OUTLINE = "pitch-outline"

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
        setContentView(R.layout.activity_pick_up_drop_off_picker)

        mapView = findViewById(R.id.mapView)

        val closeBtn: ImageView = findViewById(R.id.close_btn)
        closeBtn.setOnClickListener {
            finish()
        }

        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }


    }

    private fun onMapReady() {

        val tvPickup: TextView = findViewById(R.id.tv_pickup_loc)
        val tvDropOff: TextView = findViewById(R.id.tv_drop_off_loc)
        val fabRecenter: FloatingActionButton = findViewById(R.id.fab_recenter)

        val font = this.let { ResourcesCompat.getFont(it, R.font.inter_light_beta) };
        val fontBold = this.let { ResourcesCompat.getFont(it, R.font.inter_bold) };

        fabRecenter.setOnClickListener {
            changeCamLoc()
        }

        tvPickup.setOnClickListener {
            tvPickup.typeface = fontBold
            tvDropOff.typeface = font

            if (currSelected) {
                val editText = EditText(this)
                editText.setText(tvPickup.text.toString())
                val alert = setupDialog(tvPickup, editText)

                alert.show()
            }

            currSelected = true
            changeCamLoc()
        }

        tvDropOff.setOnClickListener {
            tvPickup.typeface = font
            tvDropOff.typeface = fontBold

            if (!currSelected) {
                val editText = EditText(this)
                editText.setText(tvDropOff.text.toString())
                val alert = setupDialog(tvDropOff, editText)

                alert.show()
            }

            currSelected = false
            changeCamLoc()
        }

        destLoc = intent.getParcelableExtra("place_info")!!

        val hoveringMarker = ImageView(this)
        hoveringMarker.setImageResource(R.drawable.baseline_location_on_24)
        val params = FrameLayout.LayoutParams(
            120,
            120, Gravity.CENTER
        )
        hoveringMarker.layoutParams = params
        mapView.addView(hoveringMarker)

        destCoords = LatLng(
            destLoc.latLng.latitude,
            destLoc.latLng.longitude
        )
        startCoords = getCurrLocation()

        mapView.mapboxMap.loadStyle(
            Style.STANDARD
        ) {
            initLocationComponent()
            setupGesturesListener()

//            getRouteAndShow()

            val pickRouteButton: Button = findViewById(R.id.button2)

            pickRouteButton.setOnClickListener {
                val route = getRouteAndShow()
                val distance = route.routes[0].legs[0].distance.value
                val intent = Intent(this, ActivityOrderDetail::class.java)

                intent.putExtra("place_info", destLoc)
                intent.putExtra("origin_loc_lat", startCoords.latitude)
                intent.putExtra("origin_loc_long", startCoords.longitude)
                intent.putExtra("distance", distance)


                startActivity(intent)
            }
        }

        tvPickup.text = "Your Location"
        tvDropOff.text = destLoc.address
    }

    private fun setupDialog(addressOld: TextView, editText: EditText): AlertDialog.Builder {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setMessage("Change the address")
        alert.setTitle("Enter a more detailed address, i.e apartment #, house # etc ")
        alert.setView(editText)

        alert.setPositiveButton("Set",
            DialogInterface.OnClickListener { _, _ -> //What ever you want to do with the value
                val addressNew = editText.text.toString()
                addressOld.text = addressNew
            })

        return alert
    }

    private fun changeCamLoc() {
        if (currSelected) {
            reCenterCamera()
        } else {
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .zoom(15.0)
                    .center(Point.fromLngLat(destCoords.longitude, destCoords.latitude))
                    .build()
            )
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
        val location = getCurrLocation()

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(location.longitude, location.latitude))
                .build()
        )
    }

    private fun getCurrLocation(): LatLng {
        var location = LatLng(
            -6.525277,
            107.0355607
        )

        val lastLocationCancelable = locationProvider?.getLastLocation { result ->
            result?.let {
                println("LOCF")
                println(result.latitude)
                println(result.longitude)

                location.latitude = result.latitude
                location.longitude = result.longitude
            }
        }

        println(location)
        return location
    }

    private fun onCameraMovementStop() {
        val mapTargetLatLng = mapView.mapboxMap.cameraState.center

        println(mapTargetLatLng.latitude())
        println(mapTargetLatLng.longitude())

        val lat = mapTargetLatLng.latitude()
        val long = mapTargetLatLng.longitude()

//        runBlocking {
//            val string = Fuel.get("https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${long}&result_type=street_address&key=${BuildConfig.PLACES_API_KEY}").body.toString()
//
//            var map: Map<String, Any> = HashMap()
//            map = Gson().fromJson(string, map.javaClass)
//            println(map["results"])
//        }

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, long, 1)
//        val address = addresses?.get(0)?.getAddressLine(0)
//
//        println(address)
//        val latLong = LatLng(
//            lat,
//            long
//        )
//
//        if (currSelected) {
//            val tvPickUp: TextView = findViewById(R.id.tv_pickup_loc)
//            tvPickUp.text = address
//            startCoords = latLong
//        } else {
//            val tvDropOff: TextView = findViewById(R.id.tv_drop_off_loc)
//            tvDropOff.text = address
//            destCoords = latLong
//        }

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

    private fun getRouteAndShow(): DirectionsResponse {

        val gson = Gson()
        val parameters = listOf(
            "key" to BuildConfig.PLACES_API_KEY,
            "origin" to "${startCoords.latitude},${startCoords.longitude}",
            "destination" to "${destCoords.latitude},${destCoords.longitude}",
            "mode" to "driving"
        )
        var response: HttpResponse

        runBlocking {
             response = Fuel.get("https://maps.googleapis.com/maps/api/directions/json", parameters)
            println(response.statusCode)
        }

        val route: DirectionsResponse = gson.fromJson(response.body, DirectionsResponse::class.java)

//        println(response.body)
//        println(parameters)
//        val points = route.routes[0].overview_polyline.decodedPolyline
//
//        mapView.mapboxMap.getStyle() {
//
//            val geoJsonSource = geoJsonSource("nav-lines") {
//                feature(Feature.fromGeometry(LineString.fromLngLats(listOf())))
//            }
//            it.addSource(geoJsonSource)
//            val layer = LineLayer(LAYER_ID, SOURCE_ID)
//            it.addLayer(layer)
//
//            println(it.getSource("nav-lines"))
//        }
//
//        val polylineAnnotationOptions = PolylineAnnotationOptions()
//            .withPoints(points)
//
//        annotationsManager.create(polylineAnnotationOptions)

        return route
    }
}
