package one.fable.geokode

expect class GeoKode {

    fun setPlacesApiKey(apiKey: String)

    suspend fun getLocation(address: List<String>): List<Location>?
    suspend fun getLocation(address: String): List<Location>?
}