package com.doctorx

import android.app.Application
import com.doctorx.db.AppDatabase

class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    // initialize Rudder SDK here
    AppDatabase.invoke(this)
  }
}