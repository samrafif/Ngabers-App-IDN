package org.terserah.ngaber.model

import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils

data class Bounds(
    val northeast: Location,
    val southwest: Location
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: Location,
    val html_instructions: String,
    val maneuver: String?,
    val polyline: Polyline,
    val start_location: Location,
    val travel_mode: String
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String,
    val end_location: Location,
    val start_address: String,
    val start_location: Location,
    val steps: List<Step>,
    val traffic_speed_entry: List<Any>,
    val via_waypoint: List<Any>
)

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>,
    val overview_polyline: Polyline,
    val summary: String,
    val warnings: List<String>, // Changed to List<String> for warnings
    val waypoint_order: List<Int> // Changed to List<Int> for waypoint_order
)

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)

data class TrafficSpeedEntry(
    val speed_category: String,
    val offset_meters: Int
)


data class DirectionsResponse(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String,
)

class Polyline(val points: String) {

    val decodedPolyline: List<Point>
        get() {
            return PolylineUtils.decode(points, 5)
        }
}