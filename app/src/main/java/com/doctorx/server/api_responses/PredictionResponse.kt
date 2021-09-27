package com.doctorx.server.api_responses

import com.doctorx.db.data.Disease

class
PredictionResponse(val success: Int?, val disease: Disease?, val confidence: Int?, val message: String?){

  fun getDiseaseWithConfidence(): Disease? = disease?.copy(confidence = confidence, predicted = true)
}