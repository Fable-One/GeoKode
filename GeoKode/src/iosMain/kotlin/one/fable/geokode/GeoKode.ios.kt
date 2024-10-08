package one.fable.geokode

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CompletableDeferred
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLPlacemark

/**
 * Actual implementation of GeoKode for the iOS platform.
 */
actual class GeoKode {

    /**
     * Geocodes the given address components into a list of Location objects.
     * The address components are concatenated into a single string for the geocoding query.
     *
     * @param address A list of address components (e.g., street, city, state).
     * @return A list of Location objects representing the geocoded results, or null if the geocoding fails.
     */
    actual suspend fun getLocation(address: List<String>): List<Location>? {
        val searchAddress = address.joinToString(" ")
        return getLocation(searchAddress)
    }

    /**
     * Geocodes the given address string into a list of Location objects.
     *
     * @param address A single address string.
     * @return A list of Location objects representing the geocoded results, or null if the geocoding fails.
     */
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getLocation(address: String): List<Location>? {
        if (address.isBlank()) return null

        val deferred = CompletableDeferred<List<Location>?>()
        val geocoder = CLGeocoder()

        geocoder.geocodeAddressString(address) { placemarks, error ->
            if (error != null) {
                print("Failed to retrieve location $error")
                deferred.complete(null)
            } else {
                val results = placemarks?.mapNotNull { clPlacemark ->
                    val placemark = clPlacemark as? CLPlacemark
                    val location = placemark?.location

                    if (location != null) {
                        Location(
                            latitude = location.coordinate.useContents { latitude },
                            longitude = location.coordinate.useContents { longitude },
                            hasLatitude = location.coordinate.useContents { latitude != 0.0 },
                            hasLongitude = location.coordinate.useContents { longitude != 0.0 },
                            featureName = placemark.name,
                            locality = placemark.locality,
                            thoroughfare = placemark.thoroughfare,
                            subThoroughfare = placemark.subThoroughfare,
                            postalCode = placemark.postalCode,
                            countryCode = placemark.ISOcountryCode,
                            countryName = placemark.country,
                            adminArea = placemark.administrativeArea,
                            subAdminArea = placemark.subAdministrativeArea,
                            addressLines = placemark.addressDictionary?.get("FormattedAddressLines") as? List<String> ?: emptyList()
                        )
                    } else {
                        null
                    }
                } ?: emptyList()
                deferred.complete(results)
            }
        }

        return deferred.await()
    }
}
