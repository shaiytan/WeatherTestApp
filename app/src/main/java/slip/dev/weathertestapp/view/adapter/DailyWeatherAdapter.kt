package slip.dev.weathertestapp.view.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import slip.dev.weathertestapp.R
import slip.dev.weathertestapp.model.WeatherRecord
import java.util.*

fun weatherGroupIcon(weatherGroup: String) = when (weatherGroup) {
    "Clear" -> R.drawable.ic_white_day_bright
    "Rain" -> R.drawable.ic_white_day_rain
    "Drizzle" -> R.drawable.ic_white_day_shower
    "Thunderstorm" -> R.drawable.ic_white_day_thunder
    else -> R.drawable.ic_white_day_cloudy
}

class DailyWeatherAdapter(
        private val context: Context,
        private var data: List<WeatherRecord>
) : RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder>() {

    private var selectedPosition = 0
    var onClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.item_weather_daily, parent, false)
        view.setOnClickListener { onClickListener?.onClick(it) }
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = data[position]
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.timeInMillis = weather.datetime
        holder.datetime.text = String.format("%1\$ta", calendar)
        holder.temperature.text = "${weather.tempMax}\u00B0 / ${weather.tempMin}\u00B0"
        holder.weatherIcon.setImageResource(weatherGroupIcon(weather.weatherGroup))
        val (textColor, backgroundColor) = if (position == selectedPosition) {
            context.resources.getColor(R.color.colorPrimary) to context.resources.getColor(R.color.colorAccent)
        } else {
            context.resources.getColor(R.color.colorText) to context.resources.getColor(R.color.colorBackground)
        }
        holder.weatherIcon.setColorFilter(textColor, PorterDuff.Mode.MULTIPLY)
        holder.datetime.setTextColor(textColor)
        holder.temperature.setTextColor(textColor)
        holder.itemView.setBackgroundColor(backgroundColor)
    }

    fun updateWeather(data: List<WeatherRecord>) {
        this.data = data
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun selectItem(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data[position]

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val temperature: TextView = item.findViewById(R.id.temperature)
        val datetime: TextView = item.findViewById(R.id.datetime)
        val weatherIcon: ImageView = item.findViewById(R.id.ic_weather)
    }
}