package com.doctorx.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.doctorx.db.converters.StringsToStringConverter
import com.doctorx.db.data.Disease

@Database(entities = arrayOf(Disease::class), version = 1)
@TypeConverters(StringsToStringConverter::class)
abstract class AppDatabase: RoomDatabase() {
  abstract fun DiseasesDao(): DiseasesDao

  companion object {
    @Volatile private var instance: AppDatabase? = null
    private val LOCK = Any()

    operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
      instance ?: buildDatabase(context).also { instance = it}
    }

    private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
      AppDatabase::class.java, "diseases.db")
      .build()
  }

}