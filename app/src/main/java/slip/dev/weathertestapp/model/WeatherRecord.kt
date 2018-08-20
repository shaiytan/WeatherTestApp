package slip.dev.weathertestapp.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

@Entity(tableName = "weather")
data class WeatherRecord(
        @field:ColumnInfo(name = "weather_group") val weatherGroup: String,
        @field:ColumnInfo(name = "temp_min") val tempMin: Int,
        @field:ColumnInfo(name = "temp_max") val tempMax: Int,
        val humidity: Int,
        @field:ColumnInfo(name = "wind_speed") val windSpeed: Int,
        @field:ColumnInfo(name = "wind_degree") val windDegree: Int,
        val latitude: Double,
        val longitude: Double,
        val datetime: Long,
        var city: String? = null,
        @field:PrimaryKey(autoGenerate = true) var id: Long? = null
)

//gson expects json array if I use JsonDeserializer<List<*>>, but response is json object
class ForecastResponse(val forecast: List<WeatherRecord>) {
    class Deserializer : JsonDeserializer<ForecastResponse> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): ForecastResponse {
            val forecast = mutableListOf<WeatherRecord>()
            val root = json.asJsonObject
            val coord = root["coord"]?.asJsonObject
            val longitude: Double = coord?.get("lon")?.asDouble ?: 0.0
            val latitude: Double = coord?.get("lat")?.asDouble ?: 0.0
            val weatherItems = root["list"]?.asJsonArray
            weatherItems?.let {
                val parsedItems = it.mapNotNull { element -> element?.asJsonObject }
                        .map { element -> parseWeatherRecord(element, longitude, latitude) }
                forecast.addAll(parsedItems)
            }
            return ForecastResponse(forecast)
        }

        private fun parseWeatherRecord(root: JsonObject, longitude: Double, latitude: Double): WeatherRecord {
            val datetime = root["dt"]?.asLong ?: 0L
            val main = root["main"]?.asJsonObject
            val tempMin = main?.get("temp_min")?.asInt ?: 0
            val tempMax = main?.get("temp_max")?.asInt ?: 0
            val humidity = main?.get("humidity")?.asInt ?: 0
            val weather = root["weather"]?.asJsonArray?.get(0)?.asJsonObject
            val weatherGroup = weather?.get("main")?.asString ?: "Other"
            val wind = root["wind"]?.asJsonObject
            val windSpeed = wind?.get("speed")?.asInt ?: 0
            val windDegree = wind?.get("deg")?.asInt ?: 0
            return WeatherRecord(
                    weatherGroup,
                    tempMin,
                    tempMax,
                    humidity,
                    windSpeed,
                    windDegree,
                    latitude,
                    longitude,
                    datetime * 1000 //to milliseconds
            )
        }
    }
}