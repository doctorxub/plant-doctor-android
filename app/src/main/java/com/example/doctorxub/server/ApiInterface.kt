package com.example.doctorxub.server

import android.util.Log
import com.example.doctorxub.server.api_responses.DiseasesResponse
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface ApiInterface {

    companion object {
        val BaseUrl = "https://doctorxub.com/api/"
        var gson = GsonBuilder()
            .setLenient()
            .create()

        var okHttpClient = OkHttpClient()
            .newBuilder() //httpLogging interceptor for logging network requests
//            .addInterceptor(httpLoggingInterceptor) //Encryption interceptor for encryption of request data
//            .addInterceptor(encryptionInterceptor) // interceptor for decryption of request data
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


        fun getDiseases() {
            retrofit.getCurrentWeatherData().enqueue( object : Callback<DiseasesResponse> {
                override fun onResponse(call: Call<DiseasesResponse>?, response: Response<DiseasesResponse>?) {
                    Log.d("awslog", "response : ${response?.body()?:"null body"}")
                }

                override fun onFailure(call: Call<DiseasesResponse>?, t: Throwable?) {
                    Log.e("awslog", "t : $t")
                }
            })

        }
    }

    @GET("diseases")
    fun getCurrentWeatherData(): Call<DiseasesResponse>
}