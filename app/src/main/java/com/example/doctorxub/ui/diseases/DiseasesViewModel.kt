package com.example.doctorxub.ui.diseases

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.doctorxub.db.AppDatabase
import com.example.doctorxub.db.data.Disease

class DiseasesViewModel: ViewModel() {
  var diseasesLiveData: LiveData<List<Disease>>? = null
  fun getDiseasesLiveData(context: Context) = diseasesLiveData?: AppDatabase(context).DiseasesDao().getAll()?.also { diseasesLiveData = it }
}