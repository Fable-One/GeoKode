package one.fable.geokode

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class GeoKode(private val context: Context, private val maxResults: Int = 5) {
    /**
     * Note: The GeoApiContext is designed to be a Singleton in your application.
     * Please instantiate one on application startup, and continue to use it for the life of your application.
     * This will enable proper QPS enforcement across all of your requests.
     * https://github.com/googlemaps/google-maps-services-java?tab=readme-ov-file#usage
     *
     * By instantiating in a global object, we ensure that the GeoApiContext is a singleton.
     */

    actual suspend fun getLocation(address: List<String>): List<Location>? {
        //Concatenate the address fields into a single string to be used for geocoding query
        val searchAddress = address.joinToString(" ")
        return getLocation(searchAddress)
    }

    actual suspend fun getLocation(address: String): List<Location>? {
        return try {
            //Check if we are online before attempting to get a geocode
            //if (!ConnectivityCheckHelper.isOnline()) return null



            //If the address is blank, no use in trying to get a geocode
            if (address.isBlank()) return null

            val geocoder = Geocoder(context)


            return withContext(Dispatchers.IO) {
                //First attempt to get the geocode from the Geocoder class
                getGeocodeFromGeocoder(geocoder, address)?.let {
                    return@let it
                }
            }

            //TODO - Fall back to using the Google Places Geocode API

        } catch (e: Exception) {
            println(e)
            null
        }
    }

    private suspend fun getGeocodeFromGeocoder(geocoder: Geocoder, address: String): List<Location>? {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) { //32 or lower
                getGeocodeFromGeocoderApi32OrLower(geocoder, address)
            } else {
                getGeocodeFromGeocoderApi33OrHigher(geocoder, address)
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    @Suppress("DEPRECATION")
    private fun getGeocodeFromGeocoderApi32OrLower(geocoder: Geocoder, invoiceAddress: String): List<Location>? {
        var locations: List<Location>? = null

        return try {
            val results = geocoder.getFromLocationName(invoiceAddress , maxResults)
            results?.let {
                //Map results to Location
                locations = results.map {
                    it.toLocation()
                }
            }
            locations
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getGeocodeFromGeocoderApi33OrHigher(geocoder: Geocoder, invoiceAddress: String): List<Location>? {
        return try {
            val deferred = CompletableDeferred<List<Location>?>()
            var locations: List<Location>
            geocoder.getFromLocationName(invoiceAddress, maxResults) { results ->
                //Map results to Location
                locations = results.map {
                    it.toLocation()
                }
                deferred.complete(locations)
            }
            deferred.await()
        } catch (e: Exception) {
            println(e)
            null
        }
    }
}