package one.fable.geokode

data class Location (
    val featureName: String? = null,
    val addressLines: List<String>? = null,
    val maxAddressLineIndex: Int? = -1,
    val adminArea: String? = null,
    val subAdminArea: String? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val thoroughfare: String? = null,
    val subThoroughfare: String? = null,
    val premises: String? = null,
    val postalCode: String? = null,
    val countryCode: String? = null,
    val countryName: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hasLatitude: Boolean = false,
    val hasLongitude: Boolean = false,
    val phone: String? = null,
    val url: String? = null,
    val extras: List<Pair<String, Any?>> = emptyList(),
)