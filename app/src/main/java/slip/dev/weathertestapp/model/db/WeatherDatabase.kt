package slip.dev.weathertestapp.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import slip.dev.weathertestapp.model.WeatherRecord

@Database(entities = [WeatherRecord::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    companion object {
        private const val DB_NAME = "weathercache.db"
        fun create(context: Context) = Room
                .databaseBuilder(context.applicationContext, WeatherDatabase::class.java, DB_NAME)
                .allowMainThreadQueries()
                .build()
    }

    abstract fun getDAO(): WeatherDAO
}