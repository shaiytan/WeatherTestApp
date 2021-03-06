package slip.dev.weathertestapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import slip.dev.weathertestapp.model.Geopoint
import slip.dev.weathertestapp.model.WeatherRecord
import slip.dev.weathertestapp.model.WeatherRepository
import java.util.*

class ForecastViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = WeatherRepository(app)
    private val weatherData: LiveData<List<WeatherRecord>> = repository.weatherData

    val dailyForecast: LiveData<List<WeatherRecord>>
    val hourlyForecast: MutableLiveData<List<WeatherRecord>>
    val currentWeather: MutableLiveData<WeatherRecord>

    init { //summerize weather items for each day
        dailyForecast = Transformations.map(weatherData) { forecast ->
            val weatherByDays: List<List<WeatherRecord>> = forecast.groupBy { record ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = record.datetime
                calendar.get(Calendar.DAY_OF_YEAR) - 366 //don't brake order on years' edge
            }.toSortedMap().values.toList()
            //summerize each list to one common WeatherRecord
            weatherByDays.map { oneDayForecast: List<WeatherRecord> ->
                //take the day's most frequent weather group
                val weatherGroup = oneDayForecast
                        .groupBy(WeatherRecord::weatherGroup)
                        .mapValues { it.value.size }
                        .toList()
                        .sortedByDescending { it.second }[0]
                        .first
                val tempMin = oneDayForecast.minBy(WeatherRecord::tempMin)?.tempMin ?: 0
                val tempMax = oneDayForecast.maxBy(WeatherRecord::tempMax)?.tempMax ?: 0
                val humidity = oneDayForecast.sumBy(WeatherRecord::humidity) / oneDayForecast.size
                val windSpeed = oneDayForecast.sumBy(WeatherRecord::windSpeed) / oneDayForecast.size
                //take the day's most frequent wind degree
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
        hourlyForecast = MutableLiveData()
        hourlyForecast.value = hourlyForecast(System.currentTimeMillis())
        currentWeather = MutableLiveData()
    }

    //filter forecast by selected date
    private fun hourlyForecast(date: Long): List<WeatherRecord> {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.timeInMillis = date
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val todayForecast = weatherData.value?.filter {
            calendar.timeInMillis = it.datetime
            calendar.get(Calendar.DAY_OF_YEAR) == today
        }
        return todayForecast ?: emptyList()
    }

    fun getLoadingStatus() = repository.getLoadingStatus()
    fun loadForecast(geopoint: Geopoint, forced: Boolean = false) {
        repository.loadForecast(geopoint, forced)
    }

    fun updateHourlyForecast(dailyForecast: WeatherRecord) {
        hourlyForecast.value = hourlyForecast(dailyForecast.datetime)
        currentWeather.value = dailyForecast
    }
}