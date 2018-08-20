package slip.dev.weathertestapp.model.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import slip.dev.weathertestapp.model.WeatherRecord

@Dao
abstract class WeatherDAO {
    @Insert
    abstract fun insertForecast(forecast: List<WeatherRecord>)

    @Query("DELETE FROM weather")
    abstract fun clearForecast()

    @Transaction
    open fun updateCache(forecast: List<WeatherRecord>) {
        clearForecast()
        insertForecast(forecast)
    }

    @Query("SELECT * FROM weather")
    abstract fun getForecast(): LiveData<List<WeatherRecord>>
}