package slip.dev.weathertestapp.api

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import slip.dev.weathertestapp.model.Geopoint

interface GeocodingAPI {
    @GET("geocode/json?$DEFAULT_PARAMS")
    fun geocode(@Query("latlng") latlng: String): Call<Geopoint>

    companion object {
        private const val DEFAULT_PARAMS =
                "language=ru&result_type=locality|administrative_area_level_1&" +
                        "key=AIzaSyCgu0Yep6Yuu6gsLWW-xDZwOnMTz4CIpYU"
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/"
        fun create(): GeocodingAPI {
            val gson = GsonBuilder()
                    .registerTypeAdapter(Geopoint::class.java, Geopoint.Deserializer())
                    .create()
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            return retrofit.create(GeocodingAPI::class.java)
        }
    }
}