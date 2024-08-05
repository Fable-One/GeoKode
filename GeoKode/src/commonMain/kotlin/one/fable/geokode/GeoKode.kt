package one.fable.geokode

/**
 * GeoKode is a Kotlin Multiplatform library for geocoding and reverse geocoding.
 * It provides a unified API for both Android and iOS platforms.
 */
expect class GeoKode {
    /**
     * Geocodes the given address components into a list of Location objects.
     * The address components are concatenated into a single string for the geocoding query.
     *
     * @param address A list of address components (e.g., street, city, state).
     * @return A list of Location objects representing the geocoded results, or null if the geocoding fails.
     */
    suspend fun getLocation(address: List<String>): List<Location>?

    /**
     * Geocodes the given address string into a list of Location objects.
     *
     * @param address A single address string.
     * @return A list of Location objects representing the geocoded results, or null if the geocoding fails.
     */
    suspend fun getLocation(address: String): List<Location>?

    //TODO - Implement Google Places API as either fallback or primary
    //fun setPlacesApiKey(apiKey: String)
}