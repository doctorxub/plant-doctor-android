package com.doctorx.server

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody


class ApiInterceptor: Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {

    val response = chain.proceed(chain.request())
    if (response.isSuccessful) {
      val newResponse = response.newBuilder()
      var contentType = response.header("Content-Type")
      if (TextUtils.isEmpty(contentType)) contentType = "application/json"
      val responseString = response.body()!!.string()
//      Log.d("awslogreply", "responseString : $responseString")
      newResponse.body(ResponseBody.create(MediaType.parse(contentType), responseString))
      return newResponse.build()
    }
    return response
  }
}