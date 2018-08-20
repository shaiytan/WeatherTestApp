package slip.dev.weathertestapp.model

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import slip.dev.weathertestapp.api.WeatherAPI
import slip.dev.weathertestapp.model.db.WeatherDatabase

class WeatherRepository(app: Application) {
    private val db = WeatherDatabase.create(app)
    private val dao = db.getDAO()
    private val weatherAPI = WeatherAPI.create()

    val weatherData: LiveData<List<WeatherRecord>> = dao.getForecast()

    private val statusData = MutableLiveData<String>()
    fun getLoadingStatus(): LiveData<String> = statusData

    init {
        weatherData.observeForever { forecast ->
            if (forecast == null || forecast.isEmpty()) {
                statusData.value = "No cache saved"
            } else {
                val forecastItem = forecast[0]
                val latitude = forecastItem.latitude
                val longitude = forecastItem.longitude
                val city = forecastItem.city ?: "UNKNOWN LOCATION"
                loadForecast(Geopoint(latitude, longitude, city))
            }
        }
    }

    fun loadForecast(geopoint: Geopoint) {
        if (checkCache(weatherData.value?.get(0), geopoint)) return
        statusData.value = "Loading"
        weatherAPI.getForecast(
                geopoint.latitude,
                geopoint.longitude
        ).enqueue(object : Callback<ForecastResponse> {
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                statusData.value = "No Internet"
            }

            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                val forecast = response.body()?.forecast
                if (response.isSuccessful && forecast != null) {
                    forecast.onEach { it.city = geopoint.city }
                    dao.updateCache(forecast)
                    statusData.value = "OK"
                } else {
                    statusData.value = "Invalid Response"
                }
            }
        })
    }

    private fun checkCache(forecast: WeatherRecord?, geopoint: Geopoint) = when {
        forecast == null -> false
        forecast.datetime < System.currentTimeMillis() -> false
        else -> forecast.city == geopoint.city
    }
}