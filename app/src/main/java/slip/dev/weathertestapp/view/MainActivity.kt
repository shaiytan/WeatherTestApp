package slip.dev.weathertestapp.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import slip.dev.weathertestapp.R
import slip.dev.weathertestapp.view.adapter.DailyWeatherAdapter
import slip.dev.weathertestapp.view.adapter.HourlyWeatherAdapter
import slip.dev.weathertestapp.viewmodel.ForecastViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(ForecastViewModel::class.java)
        initDailyForecast(viewModel)
        initHourlyForecast(viewModel)
    }

    private fun initDailyForecast(viewModel: ForecastViewModel) {
        daily_weather_list.layoutManager = LinearLayoutManager(this)
        val weatherAdapter = DailyWeatherAdapter(this, emptyList())
        weatherAdapter.onClickListener = View.OnClickListener {
            val i = daily_weather_list.indexOfChild(it)
            weatherAdapter.selectItem(i)
            viewModel.updateHourlyForecast(weatherAdapter.getItem(i))
        }
        daily_weather_list.adapter = weatherAdapter
        viewModel.dailyForecast.observe(this, Observer { list ->
            if (list != null) {
                weatherAdapter.updateWeather(list)
                viewModel.updateHourlyForecast(weatherAdapter.getItem(0))
            }
        })
        viewModel.loadForecast()
        viewModel.getLoadingStatus().observe(this, Observer { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun initHourlyForecast(viewModel: ForecastViewModel) {
        hourly_weather_list.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val weatherAdapter = HourlyWeatherAdapter(this, emptyList())
        hourly_weather_list.adapter = weatherAdapter
        viewModel.hourlyForecast.observe(this, Observer { list ->
            weatherAdapter.updateWeather(list ?: emptyList())
        })
    }
}
