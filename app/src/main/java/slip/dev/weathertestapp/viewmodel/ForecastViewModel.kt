package slip.dev.weathertestapp.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import slip.dev.weathertestapp.api.WeatherAPI
import slip.dev.weathertestapp.model.ForecastResponse
import slip.dev.weathertestapp.model.WeatherRecord

class ForecastViewModel : ViewModel() {
    private val weatherAPI = WeatherAPI.create()

    private val weatherData = MutableLiveData<List<WeatherRecord>>()
    fun getForecast(): LiveData<List<WeatherRecord>> = weatherData

    private val statusData = MutableLiveData<String>().apply { value = "OK" }
    fun getLoadingStatus(): LiveData<String> = statusData

    fun loadForecast() {
        statusData.value = "Loading"
        weatherAPI.getForecast(47.83, 35.19).enqueue(object : Callback<ForecastResponse> {
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Log.e("LOADING", "LOADING FAILED", t)
                statusData.value = "Failed to load forecast (${t.message})"
                throw t
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