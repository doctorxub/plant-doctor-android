package com.example.doctorxub.server

import android.content.Context
import android.util.Log
import com.example.doctorxub.db.AppDatabase
import com.example.doctorxub.server.api_responses.DiseasesResponse
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.lang.Exception


interface ApiInterface {

    companion object {
        val BaseUrl = "https://doctorxub.com/api/"
        var gson = GsonBuilder()
            .setLenient()
            .create()

        var okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(ApiInterceptor())
            .build()

        fun create() : ApiInterface {

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
                    retrofit.getCurrentWeatherData().diseases?.let {
                        val db = AppDatabase(context)
                        db.DiseasesDao().nukeTable()
                        db.DiseasesDao().insertAll(*it.toTypedArray())
                    }
                }
                catch (exception: Exception){
                    Log.e("awslogE", "error loading Diseases error message: ${exception.message}")
                }
            }
        }
    }

    @GET("diseases")
    suspend fun getCurrentWeatherData(): DiseasesResponse
}