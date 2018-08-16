package slip.dev.weathertestapp.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import slip.dev.weathertestapp.R
import slip.dev.weathertestapp.model.WeatherRecord
import java.util.*

class WeatherAdapter(
        private val context: Context,
        private var data: List<WeatherRecord>
) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.item_weather, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = data[position]
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.timeInMillis = weather.datetime * 1000
        holder.datetime.text = String.format("%1\$td.%1\$tm\n%1\$tH:%1\$tM", calendar)
        holder.temperature.text = "${weather.tempMax}/${weather.tempMin} C"
        holder.weatherGroup.text = weather.weatherGroup
    }

    fun updateWeather(data: List<WeatherRecord>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val temperature: TextView = item.findViewById(R.id.temperature)
        val datetime: TextView = item.findViewById(R.id.datetime)
        val weatherGroup: TextView = item.findViewById(R.id.weather_group)
    }
}