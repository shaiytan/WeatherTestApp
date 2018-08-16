package slip.dev.weathertestapp.viewmodel

import android.arch.lifecycle.ViewModel
import slip.dev.weathertestapp.model.WeatherRepository

class ForecastViewModel : ViewModel() {
    private val repository = WeatherRepository()

    fun getForecast() = repository.getWeatherData()
    fun getLoadingStatus() = repository.getLoadingStatus()
    fun loadForecast() = repository.loadForecast()
}