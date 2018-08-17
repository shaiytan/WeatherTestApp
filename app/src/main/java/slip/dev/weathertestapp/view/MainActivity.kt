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
import slip.dev.weathertestapp.viewmodel.ForecastViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDailyForecast()
    }

    private fun initDailyForecast() {
        daily_weather_list.layoutManager = LinearLayoutManager(this)
        val weatherAdapter = WeatherAdapter(this, emptyList())
        weatherAdapter.onClickListener = View.OnClickListener {
            val i = daily_weather_list.indexOfChild(it)
            weatherAdapter.selectItem(i)
        }
        daily_weather_list.adapter = weatherAdapter
        val viewModel = ViewModelProviders.of(this).get(ForecastViewModel::class.java)
        viewModel.dailyForecast.observe(this, Observer { list ->
            weatherAdapter.updateWeather(list ?: emptyList())
        })
        viewModel.loadForecast()
        viewModel.getLoadingStatus().observe(this, Observer { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })
    }
}
