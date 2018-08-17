package slip.dev.weathertestapp.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import slip.dev.weathertestapp.model.WeatherRecord
import slip.dev.weathertestapp.model.WeatherRepository
import java.util.*

class ForecastViewModel : ViewModel() {
    private val repository = WeatherRepository()
    private val weatherData: LiveData<List<WeatherRecord>> = repository.getWeatherData()

    val dailyForecast: LiveData<List<WeatherRecord>> = Transformations.map(weatherData) { forecast ->
        val weatherByDays = forecast.groupBy { record ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = record.datetime
            calendar.get(Calendar.DAY_OF_YEAR) - 366 //don't brake order on years' edge
        }.toSortedMap().values.toList()
        weatherByDays.map { oneDayForecast ->
            val weatherGroup = oneDayForecast
                    .groupBy(WeatherRecord::weatherGroup)
                    .mapValues { it.value.size }
                    .toList()
                    .sortedByDescending { it.second }[0]
                    .first
            val tempMin = oneDayForecast.minBy(WeatherRecord::tempMin)?.tempMin ?: 0.0
            val tempMax = oneDayForecast.maxBy(WeatherRecord::tempMax)?.tempMax ?: 0.0
            val humidity = oneDayForecast.sumBy(WeatherRecord::humidity) / oneDayForecast.size
            val windSpeed = oneDayForecast.sumByDouble(WeatherRecord::windSpeed) / oneDayForecast.size
            val windDegree = oneDayForecast
                    .groupBy(WeatherRecord::windDegree)
                    .mapValues { it.value.size }
                    .toList()
                    .sortedByDescending { it.second }[0]
                    .first
            WeatherRecord(
                    weatherGroup,
                    tempMin,
                    tempMax,
                    humidity,
                    windSpeed,
                    windDegree,
                    oneDayForecast[0].latitude,
                    oneDayForecast[0].longitude,
                    oneDayForecast[0].datetime,
                    oneDayForecast[0].city
            )
        }
    }

    fun getLoadingStatus() = repository.getLoadingStatus()
    fun loadForecast() = repository.loadForecast()
}