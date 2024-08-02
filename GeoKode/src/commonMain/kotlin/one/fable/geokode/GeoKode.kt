package one.fable.geokode

expect class GeoKode {

    fun setPlacesApiKey(apiKey: String)

    suspend fun getLocation(address: List<String>): List<Location>?
    //TODO - Get location from coordinates
}