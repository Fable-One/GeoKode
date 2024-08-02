package one.fable.geokode

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CompletableDeferred
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLPlacemark

actual class GeoKode {
    //TODO - Get location from coordinates
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getLocation(address: List<String>): List<Location>? {
        //Concatenate the address fields into a single string to be used for geocoding query
        val searchAddress = address.joinToString(" ")

        if (searchAddress.isBlank()) return null

        val deferred = CompletableDeferred<List<Location>?>()

        val geocoder = CLGeocoder()

        geocoder.geocodeAddressString(searchAddress) { placemarks , error ->
            if (error != null) {
                println("Failed to retrieve location $error")
                deferred.complete(null)
            } else {
//                val results = placemarks?.mapNotNull { placemark ->
//                    (placemark as? CLPlacemark)?.location
//                }

                val results = placemarks?.mapNotNull { clPlacemark ->
                    val placemark = clPlacemark as? CLPlacemark
                    val location = placemark?.location

                    if (location != null) {
                        Location(
                            latitude =  location.coordinate.useContents { latitude },
                            longitude = location.coordinate.useContents { longitude },
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

    actual fun setPlacesApiKey(apiKey: String) {
    }
}

//    actual fun setPlacesApiKey(apiKey: String) {
//    }