package slip.dev.weathertestapp.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.item_weather_selected.*
import slip.dev.weathertestapp.R
import slip.dev.weathertestapp.model.Geopoint
import slip.dev.weathertestapp.view.adapter.DailyWeatherAdapter
import slip.dev.weathertestapp.view.adapter.HourlyWeatherAdapter
import slip.dev.weathertestapp.viewmodel.ForecastViewModel
import slip.dev.weathertestapp.viewmodel.LocationViewModel
import java.util.*

private const val LOCATION_REQUEST = 100

class MainActivity : AppCompatActivity() {
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var forecastViewModel: ForecastViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        title = "UNKNOWN LOCATION"
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        forecastViewModel = ViewModelProviders.of(this).get(ForecastViewModel::class.java)
        initDailyForecast()
        initHourlyForecast()
        initCurrentWeather()

        locationViewModel.location.observe(this, Observer { geopoint ->
            if (geopoint != null) {
                title = geopoint.city
                forecastViewModel.loadForecast(geopoint)
            }
        })
    }

    private fun initDailyForecast() {
        daily_weather_list.layoutManager = LinearLayoutManager(this)
        val weatherAdapter = DailyWeatherAdapter(this, emptyList())
        weatherAdapter.onClickListener = View.OnClickListener {
            val i = daily_weather_list.indexOfChild(it)
            weatherAdapter.selectItem(i)
            forecastViewModel.updateHourlyForecast(weatherAdapter.getItem(i))
        }
        daily_weather_list.adapter = weatherAdapter
        forecastViewModel.dailyForecast.observe(this, Observer { list ->
            if (list != null) {
                weatherAdapter.updateWeather(list)
                forecastViewModel.updateHourlyForecast(weatherAdapter.getItem(0))
            }
        })
        forecastViewModel.getLoadingStatus().observe(this, Observer { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun initHourlyForecast() {
        hourly_weather_list.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val weatherAdapter = HourlyWeatherAdapter(this, emptyList())
        hourly_weather_list.adapter = weatherAdapter
        forecastViewModel.hourlyForecast.observe(this, Observer { list ->
            weatherAdapter.updateWeather(list ?: emptyList())
        })
    }

    private fun initCurrentWeather() {
        forecastViewModel.currentWeather.observe(this, Observer { weather ->
            if (weather != null) {
                val calendar = Calendar.getInstance()
                calendar.timeZone = TimeZone.getDefault()
                calendar.timeInMillis = weather.datetime
                date_view.text = String.format("%1\$ta, %1\$td %1\$tB", calendar)
                ic_weather.setImageResource(weatherGroupIcon(weather.weatherGroup))
                temperature.text = "${weather.tempMax}\u00B0 / ${weather.tempMin}\u00B0"
                humidity.text = "${weather.humidity}%"
                wind.text = "${weather.windSpeed} м/с"
                ic_wind_direction.setImageResource(windDirectionIcon(weather.windDegree))
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.location_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, MapsActivity::class.java)
        startActivityForResult(intent, LOCATION_REQUEST)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK || data == null) return
        val city = data.getStringExtra("city")
        val lat = data.getDoubleExtra("lat", 0.0)
        val lng = data.getDoubleExtra("lng", 0.0)
        locationViewModel.location.value = Geopoint(lat, lng, city)
        locationViewModel.saveLocation()
    }
}

fun weatherGroupIcon(weatherGroup: String) = when (weatherGroup) {
    "Clear" -> R.drawable.ic_white_day_bright
    "Rain" -> R.drawable.ic_white_day_rain
    "Drizzle" -> R.drawable.ic_white_day_shower
    "Thunderstorm" -> R.drawable.ic_white_day_thunder
    else -> R.drawable.ic_white_day_cloudy
}

fun windDirectionIcon(degree: Int) = when (degree) {
    in 23..67 -> R.drawable.ic_icon_wind_ne
    in 68..112 -> R.drawable.ic_icon_wind_n
    in 113..157 -> R.drawable.ic_icon_wind_wn
    in 158..202 -> R.drawable.ic_icon_wind_w
    in 203..247 -> R.drawable.ic_icon_wind_ws
    in 248..292 -> R.drawable.ic_icon_wind_s
    in 293..337 -> R.drawable.ic_icon_wind_se
    else -> R.drawable.ic_icon_wind_e
}