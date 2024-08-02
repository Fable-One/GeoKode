package one.fable.geokode

import android.location.Address
import android.os.Bundle

//https://developer.android.com/reference/android/location/Address
fun Address.toLocation(): Location {
    val addressLinesList = mutableListOf<String>()
    for (i in 0..maxAddressLineIndex) {
        addressLinesList.add(getAddressLine(i))
    }

    return Location(
        featureName = featureName,
        addressLines = addressLinesList,
        maxAddressLineIndex = maxAddressLineIndex,
        adminArea = adminArea,
        subAdminArea = subAdminArea,
        locality = locality,
        subLocality = subLocality,
        thoroughfare = thoroughfare,
        subThoroughfare = subThoroughfare,
        premises = premises,
        postalCode = postalCode,
        countryCode = countryCode,
        countryName = countryName,
        latitude = latitude,
        longitude = longitude,
        hasLatitude = hasLatitude(),
        hasLongitude = hasLongitude(),
        phone = phone,
        url = url,
        extras = extras?.let { bundleToPrimitiveList(it) } ?: emptyList()
    )
}

fun bundleToPrimitiveList(bundle: Bundle): List<Pair<String, Any?>> {
    val list = mutableListOf<Pair<String, Any?>>()
    for (key in bundle.keySet()) {
        try {
            val value = when (val item = bundle.get(key)) {
                is Int -> item
                is Long -> item
                is Float -> item
                is Double -> item
                is Boolean -> item
                is String -> item
                else -> null
            }
            if (value != null) {
                list.add(Pair(key, value))
            }
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            println("Error retrieving value for key $key: ${e.message}")
        }
    }
    return list
}