package slip.dev.weathertestapp.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import slip.dev.weathertestapp.R
import slip.dev.weathertestapp.model.WeatherRecord
import java.util.*

private val nightIcons = mapOf(
        R.drawable.ic_white_day_cloudy to R.drawable.ic_white_night_cloudy,
        R.drawable.ic_white_day_bright to R.drawable.ic_white_night_bright,
        R.drawable.ic_white_day_rain to R.drawable.ic_white_night_rain,
        R.drawable.ic_white_day_shower to R.drawable.ic_white_night_shower,
        R.drawable.ic_white_day_thunder to R.drawable.ic_white_night_thunder
)

class HourlyWeatherAdapter(
        private val context: Context,
        private var data: List<WeatherRecord>
) : RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.item_weather_hourly, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = data[position]
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.timeInMillis = weather.datetime
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        holder.datetime.text = Html.fromHtml("$hour<sup>00</sup>")
        holder.temperature.text = "${weather.tempMax}\u00B0"
        var icon = weatherGroupIcon(weather.weatherGroup)
        if (hour < 4 || hour > 20) {
            icon = nightIcons[icon]!!
        }
        holder.weatherIcon.setImageResource(icon)
        val textColor = context.resources.getColor(R.color.colorBackground)
        holder.datetime.setTextColor(textColor)
        holder.temperature.setTextColor(textColor)
    }

    fun updateWeather(data: List<WeatherRecord>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val temperature: TextView = item.findViewById(R.id.temperature)
        val datetime: TextView = item.findViewById(R.id.datetime)
        val weatherIcon: ImageView = item.findViewById(R.id.ic_weather)
    }
}