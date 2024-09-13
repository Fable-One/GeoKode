package one.fable.geokode

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CompletableDeferred
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLPlacemark
import platform.Foundation.NSError
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKLocalSearch
import platform.MapKit.MKLocalSearchCompleter
import platform.MapKit.MKLocalSearchCompleterDelegateProtocol
import platform.MapKit.MKLocalSearchCompleterResultTypeAddress
import platform.MapKit.MKLocalSearchCompleterResultTypePointOfInterest
import platform.MapKit.MKLocalSearchCompleterResultTypeQuery
import platform.MapKit.MKLocalSearchCompletion
import platform.MapKit.MKLocalSearchRequest
import platform.MapKit.MKMapItem
import platform.darwin.NSObject

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

    /**
     * Performs an auto search for the given address string using MKLocalSearch.
     *
     * @param address A single address string.
     * @return A list of Location objects representing the search results, or null if the search fails.
     */
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun autoSearchAddress(address: String, userLatitude: Double?, userLongitude: Double?): List<Location>? {
        if (address.isBlank()) return null

        val region = if (userLatitude != null && userLongitude != null) {
            val userLocation = CLLocationCoordinate2DMake(
                latitude = userLatitude,
                longitude = userLongitude
            )

            MKCoordinateRegionMake(
                centerCoordinate = userLocation,
                span = MKCoordinateSpanMake(latitudeDelta = 0.1, longitudeDelta = 0.1)
            )
        } else {
            null
        }

        val deferred = CompletableDeferred<List<Location>?>()
        val request = MKLocalSearchRequest().apply {
            this.naturalLanguageQuery = address
            region?.let { this.region = it }
        }

        val search = MKLocalSearch(request)

        search.startWithCompletionHandler { response, error ->
            if (error != null) {
                print("Failed to search address $error")
                deferred.complete(null)
            } else {
                println(response)
                val results = response?.mapItems?.mapNotNull { mapItem ->
                    println(mapItem)
                    val mkMapItem = mapItem as? MKMapItem

                    val placemark = mkMapItem?.placemark
                    val location = placemark?.location

                    if (placemark != null) {

                        Location(
                            latitude = location?.coordinate?.useContents { latitude } ?: 0.0,
                            longitude = location?.coordinate?.useContents { longitude } ?: 0.0,
                            hasLatitude = location?.coordinate?.useContents { latitude != 0.0 } ?: false,
                            hasLongitude = location?.coordinate?.useContents { longitude != 0.0 } ?: false,
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

    @Deprecated("This function is not ready for use yet. Setting the queryFragment should trigger the delegate, but it doesn't seem to be working.")
    @OptIn(ExperimentalForeignApi::class)
    suspend fun getSearchCompletions(query: String, region: CValue<MKCoordinateRegion>?): List<MKLocalSearchCompletion>? {
        val deferred = CompletableDeferred<List<MKLocalSearchCompletion>?>()
        val completer = MKLocalSearchCompleter().apply {
            resultTypes = MKLocalSearchCompleterResultTypeAddress or MKLocalSearchCompleterResultTypePointOfInterest or MKLocalSearchCompleterResultTypeQuery
            delegate = CompleterDelegate(deferred)
            region?.let { this.region = it }
        }

        // Setting the queryFragment for MKLocalSearchCompleter should trigger the delegate, but it doesn't seem to be working yet
        completer.queryFragment = query

        return deferred.await()
    }

    class CompleterDelegate(
        private val deferred: CompletableDeferred<List<MKLocalSearchCompletion>?>
    ) : NSObject(), MKLocalSearchCompleterDelegateProtocol {

        override fun completerDidUpdateResults(completer: MKLocalSearchCompleter) {
            val completions = completer.results.mapNotNull { it as? MKLocalSearchCompletion }
            println(completions)
            deferred.complete(completions)
        }

        override fun completer(completer: MKLocalSearchCompleter, didFailWithError: NSError) {
            deferred.complete(null)
        }
    }
}


