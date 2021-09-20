package com.example.doctorxub

import android.app.Application
import com.example.doctorxub.db.AppDatabase

class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    // initialize Rudder SDK here
    AppDatabase.invoke(this)
  }
}