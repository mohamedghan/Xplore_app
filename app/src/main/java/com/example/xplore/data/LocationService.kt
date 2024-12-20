package com.example.xplore.data

import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class LocationService @Inject constructor(@ApplicationContext private val context: Context) {
    /**
     * Get country name from latitude and longitude using Android Geocoder
     *
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @return Country name or null if not found
     */
    suspend fun getCountryString(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Check if Geocoder is available on the device
                if (!Geocoder.isPresent()) {
                    return@withContext null
                }

                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                val address = addresses?.firstOrNull()
                val name = address?.countryName;
                val wilaya = address?.subAdminArea;

                "$wilaya / $name"
            } catch (e: Exception) {
                // Log the exception in a real-world app
                e.printStackTrace()
                null
            }
        }
    }
}