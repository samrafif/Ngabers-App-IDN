package org.terserah.ngaber.model

import com.mapbox.geojson.utils.PolylineUtils
import kotlinx.serialization.Serializable
import java.util.HashMap

@Serializable
data class RouteRequestModel(
    val origin: Location,
    val destination: Location,
    val travelMode: String,
    val routingPreference: String,
    val computeAlternativeRoutes: Boolean,
    val languageCode: String,
    val units: String,
    val routeModifiers: RouteModifiers
)

@Serializable
data class Location(val location: Location_sub)

@Serializable
data class Location_sub(val latLng: LatLng)
@Serializable
data class LatLng(var latitude: Double, var longitude: Double)
@Serializable
data class RouteModifiers(
    val avoidFerries: Boolean,
    val avoidHighways: Boolean,
    val avoidTolls: Boolean
)
//abstract class RouteRequestModel {
//    abstract val origin: Location
//}
//
//abstract class Location {
//    abstract val latitiude: Double
//    abstract val longitude: Double
//}