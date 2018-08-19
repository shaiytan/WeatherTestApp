package slip.dev.weathertestapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.LocationManager
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import slip.dev.weathertestapp.api.GeocodingAPI
import slip.dev.weathertestapp.model.Geopoint

class LocationViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val CITY_PREFERENCE = "city"
        private const val LATITUDE_PREFERENCE = "latitude"
        private const val LONGITUDE_PREFERENCE = "longitude"
    }

    private val preferences = app.getSharedPreferences("last_location", Context.MODE_PRIVATE)
    private val geocodingAPI = GeocodingAPI.create()
    val location = MutableLiveData<Geopoint>()

    init {
        if (preferences.contains(CITY_PREFERENCE)) {
            val lat = preferences.getFloat(LATITUDE_PREFERENCE, 0.0F).toDouble()
            val lng = preferences.getFloat(LONGITUDE_PREFERENCE, 0.0F).toDouble()
            val city = preferences.getString(CITY_PREFERENCE, "UNKNOWN LOCATION")
            location.value = Geopoint(lat, lng, city)
        } else {
            val manager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let { loc ->
                    updateLocation(loc.latitude, loc.longitude)
                }
            } catch (ignored: SecurityException) {
            }
        }
    }

    fun saveLocation() {
        location.value?.let { loc ->
            preferences.edit()
                    .putString(CITY_PREFERENCE, loc.city)
                    .putFloat(LATITUDE_PREFERENCE, loc.latitude.toFloat())
                    .putFloat(LONGITUDE_PREFERENCE, loc.longitude.toFloat())
                    .apply()
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        geocodingAPI.geocode("$lat,$lng").enqueue(object : Callback<Geopoint> {
            override fun onFailure(call: Call<Geopoint>, t: Throwable) {
                Log.e("GEOCODING", "failed to geocode $lat,$lng", t)
            }

            override fun onResponse(call: Call<Geopoint>, response: Response<Geopoint>) {
                if (response.isSuccessful) {
                    val point = response.body()
                    location.value = point
                }
            }
        })
    }
}