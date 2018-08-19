package slip.dev.weathertestapp.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


data class Geopoint(
        val latitude: Double,
        val longitude: Double,
        val city: String
) {
    class Deserializer : JsonDeserializer<Geopoint> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Geopoint? {
            val result = json.asJsonObject["results"].asJsonArray[0]?.asJsonObject ?: return null
            val longName = result["address_components"]
                    .asJsonArray[0]?.asJsonObject?.get("long_name")?.asString ?: "UNKNOWN LOCATION"
            val location = result["geometry"].asJsonObject?.get("location")?.asJsonObject
            val lat = location?.get("lat")?.asDouble ?: 0.0
            val lng = location?.get("lng")?.asDouble ?: 0.0
            return Geopoint(lat, lng, longName)
        }
    }
}