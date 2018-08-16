package slip.dev.weathertestapp.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import slip.dev.weathertestapp.R
import slip.dev.weathertestapp.viewmodel.ForecastViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val weatherAdapter = WeatherAdapter(this, emptyList())
        weather_list.layoutManager = LinearLayoutManager(this)
        weather_list.adapter = weatherAdapter
        val viewModel = ViewModelProviders.of(this).get(ForecastViewModel::class.java)
        val forecast = viewModel.getForecast()
        forecast.observe(this, Observer { list ->
            if (list == null) {
                viewModel.loadForecast()
            } else {
                weatherAdapter.updateWeather(list)
            }
        })
        viewModel.loadForecast()
        viewModel.getLoadingStatus().observe(this, Observer { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
