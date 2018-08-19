package slip.dev.weathertestapp.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import slip.dev.weathertestapp.api.WeatherAPI

class WeatherRepository {
    private val weatherAPI = WeatherAPI.create()

    private val weatherData = MutableLiveData<List<WeatherRecord>>()
    fun getWeatherData(): LiveData<List<WeatherRecord>> = weatherData

    private val statusData = MutableLiveData<String>().apply { value = "OK" }
    fun getLoadingStatus(): LiveData<String> = statusData

    fun loadForecast(geopoint: Geopoint) {
        statusData.value = "Loading"
        weatherAPI.getForecast(
                geopoint.latitude,
                geopoint.longitude
        ).enqueue(object : Callback<ForecastResponse> {
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Log.e("LOADING", "LOADING FAILED", t)
                statusData.value = "Failed to load forecast (${t.message})"
            }

            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                val forecast = response.body()?.forecast
                if (response.isSuccessful && forecast != null) {
                    weatherData.value = forecast
                    statusData.value = "OK"
                } else {
                    statusData.value = "Invalid Response"
                }
            }
        })
    }
}