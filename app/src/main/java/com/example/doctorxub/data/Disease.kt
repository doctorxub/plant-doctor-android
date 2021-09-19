package com.example.doctorxub.data

data class Disease(
  val disease: String?,
  val title: String?,
  val type: String?,
  val image: String?,
  val hosts: String?,
  val sci: String?,
  val symptoms: List<String?>,
  val control: List<String>?
)
