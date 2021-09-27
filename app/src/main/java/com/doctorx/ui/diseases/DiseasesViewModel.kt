package com.doctorx.ui.diseases

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.doctorx.db.AppDatabase
import com.doctorx.db.data.Disease

class DiseasesViewModel: ViewModel() {
  var diseasesLiveData: LiveData<List<Disease>>? = null
  fun getDiseasesLiveData(context: Context) = diseasesLiveData?: AppDatabase(context).DiseasesDao().getAll()?.also { diseasesLiveData = it }
}