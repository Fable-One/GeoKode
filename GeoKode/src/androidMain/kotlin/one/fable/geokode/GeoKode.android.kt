package one.fable.geokode

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Actual implementation of GeoKode for the Android platform.
 *
 * @param context The Android context.
 * @param maxResults The maximum number of results to return (default is 5).
 */
actual class GeoKode(private val context: Context, private val maxResults: Int = 5) {

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
    actual suspend fun getLocation(address: String): List<Location>? {
        return try {
            if (address.isBlank()) return null

            val geocoder = Geocoder(context)

            return withContext(Dispatchers.IO) {
                getGeocodeFromGeocoder(geocoder, address)?.let {
                    return@let it
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Attempts to retrieve the geocode (latitude and longitude) for the provided address using the Geocoder class.
     *
     * @param geocoder The Geocoder instance.
     * @param address The address string to geocode.
     * @return A list of Location objects representing the geocoded results, or null if the geocoding fails.
     */
    private suspend fun getGeocodeFromGeocoder(geocoder: Geocoder, address: String): List<Location>? {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                getGeocodeFromGeocoderApi32OrLower(geocoder, address)
            } else {
                getGeocodeFromGeocoderApi33OrHigher(geocoder, address)
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Geocodes the given address string using the Geocoder class for API level 32 or lower.
     *
     * @param geocoder The Geocoder instance.
     * @param address The address string to geocode.
     * @return A list of Location objects representing the geocoded results, or null if the geocoding fails.
     */
    @Suppress("DEPRECATION")
    private fun getGeocodeFromGeocoderApi32OrLower(geocoder: Geocoder, address: String): List<Location>? {
        return try {
            val results = geocoder.getFromLocationName(address, maxResults)
            results?.map { it.toLocation() }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    /**
     * Geocodes the given address string using the Geocoder class for API level 33 or higher.
     *
     * @param geocoder The Geocoder instance.
     * @param address The address string to geocode.
     * @return A list of Location objects representing the geocoded results, or null if the geocoding fails.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getGeocodeFromGeocoderApi33OrHigher(geocoder: Geocoder, address: String): List<Location>? {
        return try {
            val deferred = CompletableDeferred<List<Location>?>()
            geocoder.getFromLocationName(address, maxResults) { results ->
                val locations = results.map { it.toLocation() }
                deferred.complete(locations)
            }
            deferred.await()
        } catch (e: Exception) {
            println(e)
            null
        }
    }
}