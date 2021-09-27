package com.doctorx.ui.disease_details

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.doctorx.db.AppDatabase
import com.doctorx.db.data.Disease

class DiseaseDetailsViewModel: ViewModel() {
  var diseasesLiveData: LiveData<Disease>? = null
  fun getDiseaseLiveData(context: Context, diseaseId: Int) = diseasesLiveData?: AppDatabase(context).DiseasesDao().findById(diseaseId)?.also { diseasesLiveData = it }
}