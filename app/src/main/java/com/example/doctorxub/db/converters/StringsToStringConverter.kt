package com.example.doctorxub.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object StringsToStringConverter {
  @TypeConverter
  fun fromString(value: String?): List<String>? = Gson().fromJson(value, object : TypeToken<List<String?>?>() {}.type)

  @TypeConverter
  fun fromArrayList(list: List<String?>?): String? = Gson().toJson(list)
}