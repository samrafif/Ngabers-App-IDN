package org.terserah.ngaber

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
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
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import org.terserah.ngaber.model.LatLng
import org.terserah.ngaber.utils.LocationPermissionHelper
import java.lang.ref.WeakReference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DriverMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DriverMainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private var locationProvider: DeviceLocationProvider? = null

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

        locationPermissionHelper = LocationPermissionHelper(WeakReference(activity))
        locationPermissionHelper.checkPermissions {



        }

        return inflater.inflate(R.layout.fragment_driver_main, container, false)
    }


    private lateinit var mapView: MapView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var orders: ArrayList<QueryDocumentSnapshot> = ArrayList<QueryDocumentSnapshot>()
    private var added: ArrayList<Int> = ArrayList<Int>()

    private fun initLocationComponent() {

        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
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
    }

    private fun getCurrLocation(lambda: (result: LatLng) -> Unit): LatLng {
        initLocationComponent()
        var location = LatLng(
            0.0,
            0.0
        )

        val lastLocationCancelable = locationProvider?.getLastLocation { result ->
            println("LOCF")
            println(result)
            if (result != null) {
                println(result.latitude)
                println(result.longitude)

                location = LatLng(
                    result.latitude,
                    result.longitude
                )

                println(location)

                val currentLoc = LatLng(
                    location.latitude,
                    location.longitude
                )

                lambda(currentLoc)
            }
        }

        return location
    }

    private fun reCenterCamera() {
        getCurrLocation {
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(it.longitude, it.latitude))
                    .build()
            )
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = Firebase.firestore
        auth = Firebase.auth

        mapView = view.findViewById(R.id.mapViewInner)

        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
//            reCenterCamera()
        }


        val ref = firestore.collection("orders").whereEqualTo("done", false)

        ref.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w("TAG", "Listen failed.", error)
                return@addSnapshotListener
            }

            for (doc in value!!) {
                orders.add(doc)
            }

            orders.forEachIndexed { index, element ->

                if (!(element.data.get("taken") as Boolean) and (index !in added)) {
                    val customerLocationStr = element.data.get("origin").toString().split(",")
                    val customerLocation = LatLng(
                        customerLocationStr[0].toDouble(),
                        customerLocationStr[1].toDouble()
                    )

                    added.add(index)

                    addAnnotationToMap(index.toString(), Point.fromLngLat(customerLocation.longitude, customerLocation.latitude))

                }

            }
        }

        ref.get().addOnSuccessListener {
            for (doc in it!!) {
                orders.add(doc)
            }

            orders.forEachIndexed { index, element ->

                if (!(element.data.get("taken") as Boolean) and (index !in added)) {
                    val customerLocationStr = element.data.get("origin").toString().split(",")
                    val customerLocation = LatLng(
                        customerLocationStr[0].toDouble(),
                        customerLocationStr[1].toDouble()
                    )
                    val price = element.data.get("price")

                    added.add(index)
                    println(index)

                    addAnnotationToMap(index.toString(), Point.fromLngLat(customerLocation.longitude, customerLocation.latitude))

                    Toast.makeText(activity, "NEW ORDER !!!!!! $price", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun addAnnotationToMap(id: String, point: Point) {
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .zoom(10.0)
                .center(point)
                .build()
        )
// Create an instance of the Annotation API and get the PointAnnotationManager.
        this.context?.let { context ->
            bitmapFromDrawableRes(
                context,
                R.drawable.baseline_location_on_24
            )?.let {
                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                pointAnnotationManager.addClickListener {

                    println(id)

                    val order = orders[id.toInt()]

                    val bundle = Bundle()

                    println(order.data)

                    bundle.putString("name", order.data["customer"].toString())

                    bundle.putString("price", order.data["price"].toString())
                    bundle.putString("place", order.data["places"].toString())

                    bundle.putString("customer_origin", order.data["origin"].toString())
                    bundle.putString("customer_dest", order.data["destination"].toString())
                    bundle.putString("order_id", order.id)

                    //bundle.putString("distance", (order.data["distance"].toString().toDouble() / 1000).toString())

                    val frag = OrderOngoingDriverFragment()
                    frag.arguments = bundle

                    setCurrentFragment(frag)

                    true
                }
    // Set options for the resulting symbol layer.
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
    // Define a geographic coordinate.
                    .withPoint(point)
                    .withIconImage(it)
                    .withIconSize(2.0)
                    .withTextField(id)
                pointAnnotationManager.create(pointAnnotationOptions)
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.flFragment2, fragment)

            commit()
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DriverMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DriverMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}