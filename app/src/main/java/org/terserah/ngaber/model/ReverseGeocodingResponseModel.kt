package org.terserah.ngaber.model

data class ReverseGeocodingResponseModel(
    val plus_code: PlusCode,
    val results: Array<RevGeocodingResult>,
    val status: String
)

data class RevGeocodingResult(
    val address_components: AddressComponent,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val plus_code: PlusCode,
    val types: Array<String>
)

data class PlusCode(
    val compound_code: String,
    val global_code: String
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: Array<String>
)

data class Geometry(
    val location: Location,
    val location_type: String,
    val viewport: ViewPort,
)

data class ViewPort(
    val northeast: LatLng,
    val southeast: LatLng
)
