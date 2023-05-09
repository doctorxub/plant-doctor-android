package com.doctorx.server

import android.content.Context
import android.util.Log
import com.doctorx.db.AppDatabase
import com.doctorx.server.api_responses.DiseasesResponse
import com.doctorx.server.api_responses.PredictionResponse
import com.doctorx.server.api_responses.UploadImageResponse
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface ApiInterface {

  companion object {
    var BaseUrl = "https://doctorxub.com/api/"
    //val BaseUrl = "https://470c-181-55-68-32.ngrok-free.app/api/"

    var gson = GsonBuilder()
      .setLenient()
      .create()

    var okHttpClient = OkHttpClient()
      .newBuilder()
      .connectTimeout(500000,TimeUnit.MILLISECONDS)
      .addInterceptor(ApiInterceptor())
      .build()

    fun create(): ApiInterface {

      val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BaseUrl)
        .client(okHttpClient)
        .build()
      return retrofit.create(ApiInterface::class.java)

    }

    val retrofit = create()


    fun getAndStoreDiseases(context: Context) {
      GlobalScope.launch {
        try {
          retrofit.getDiseasesList().diseases?.let {
            val db = AppDatabase(context)
            db.DiseasesDao().nukeTable()
            db.DiseasesDao().insertAll(*it.toTypedArray())
          }
        } catch (exception: Exception) {
          Log.e("awslogE", "error loading Diseases error message: ${exception.message}")
        }
      }
    }

    suspend fun UploadImageAndSavePrediction(
      context: Context,
      part: MultipartBody.Part,
      ptype: Int
    ): Pair<String?, Int> {
      Log.d("awslogUp", "UploadImageAndSavePrediction called")
      var diseaseId: Int  = -1
      var ErrorMessage: String? = null

      try {
        Log.d("awslogUp", "UploadImageAndSavePrediction starting upload")
        val uploadImageResponse = retrofit.updateImage(part)
        if (uploadImageResponse.success == 1) {
          Log.d(
            "awslogUp",
            "UploadImageAndSavePrediction upload success filename: ${uploadImageResponse.filename}"
          )
          uploadImageResponse.filename?.let { fileName ->
            try {
              Log.d("awslogUp", "UploadImageAndSavePrediction getting prediction for plant type just cucumber")
              val response = retrofit.getPredictionPlantsType(fileName, ptype)
              if (response.success == 1) {
                Log.d("awslogUp", "UploadImageAndSavePrediction getting prediction succeeded")
                response.getDiseaseWithConfidence()?.let {
                  val db = AppDatabase(context)
                  db.DiseasesDao().insert(it)
                }
                diseaseId = response.disease?.id?:-1
              } else {
                Log.d(
                  "awslogUp",
                  "UploadImageAndSavePrediction getting prediction failed with error"
                )
                Log.e(
                  "awslogE",
                  "error loading prediction from api error message: ${response.message}"
                )
                ErrorMessage = response.message
              }
            } catch (exception: Exception) {
              Log.d(
                "awslogUp",
                "UploadImageAndSavePrediction getting prediction failed with exception"
              )
              Log.e(
                "awslogE",
                "error while loading prediction from api error message: ${exception.message}"
              )
            }
          }
        } else {
          Log.d("awslogUp", "UploadImageAndSavePrediction Uploading image failed with error")
          Log.e(
            "awslogE",
            "error Uploading image to api error message:: ${uploadImageResponse.message}"
          )
          ErrorMessage = uploadImageResponse.message
        }
      } catch (exception: Exception) {
        Log.d("awslogUp", "UploadImageAndSavePrediction Uploading image failed with exception")
        Log.e("awslogE", "error while Uploading image to api error message:: ${exception.message}")
      }
      return Pair(ErrorMessage, diseaseId)
    }

  }

  @GET("diseases")
  suspend fun getDiseasesList(): DiseasesResponse

  @GET("predict/{fileName}")
  suspend fun getPrediction(@Path("fileName") fileName: String): PredictionResponse
  @GET("predict/{fileName}/{ptype}")
  suspend fun getPredictionPlantsType(@Path("fileName") fileName: String,
                                      @Path("ptype") ptype: Int): PredictionResponse

  @Multipart
  @POST("upload")
  suspend fun updateImage(
    @Part part: MultipartBody.Part
  ): UploadImageResponse
}