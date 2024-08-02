package one.fable.geokode

expect class GeoKode {

    //TODO - Implement Google Places API as either fallback or primary
    //fun setPlacesApiKey(apiKey: String)

    suspend fun getLocation(address: List<String>): List<Location>?
    suspend fun getLocation(address: String): List<Location>?
}