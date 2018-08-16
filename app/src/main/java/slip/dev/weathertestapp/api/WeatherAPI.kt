package slip.dev.weathertestapp.api

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import slip.dev.weathertestapp.model.ForecastResponse

interface WeatherAPI {
    @GET("forecast?$DEFAULT_PARAMS")
    fun getForecast(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<ForecastResponse>

    companion object {
        private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
        private const val DEFAULT_PARAMS = "units=metric&lang=ru&APPID=9e56cdd894013de6a160a5bc63d9ae8b"
        fun create(): WeatherAPI {
            val gson = GsonBuilder()
                    .registerTypeAdapter(
                            ForecastResponse::class.java,
                            ForecastResponse.Deserializer())
                    .create()
            val retrofit = Retrofit.Builder()
                    .baseUrl(WeatherAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            return retrofit.create(WeatherAPI::class.java)
        }
    }
}