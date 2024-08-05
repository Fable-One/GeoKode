package one.fable.geokode

/**
 * Location class representing a geocoded location.
 * This class is an adaptation of the Android Address class.
 * https://developer.android.com/reference/android/location/Address
 *
 * @property featureName The name of the feature (e.g., building, landmark).
 * @property addressLines The address lines of the location.
 * @property maxAddressLineIndex The maximum index of the address lines.
 * @property adminArea The administrative area (e.g., state, province).
 * @property subAdminArea The sub-administrative area (e.g., county, district).
 * @property locality The locality (e.g., city, town).
 * @property subLocality The sub-locality (e.g., neighborhood).
 * @property thoroughfare The thoroughfare (e.g., street).
 * @property subThoroughfare The sub-thoroughfare (e.g., street number).
 * @property premises The premises (e.g., building name).
 * @property postalCode The postal code.
 * @property countryCode The country code (ISO 3166-1 alpha-2).
 * @property countryName The country name.
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property hasLatitude Whether the location has a valid latitude.
 * @property hasLongitude Whether the location has a valid longitude.
 * @property phone The phone number associated with the location.
 * @property url The URL associated with the location.
 * @property extras Additional key-value pairs associated with the location.
 */
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