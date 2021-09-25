package com.example.doctorxub.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Disease(
  @PrimaryKey val id: Int,
  val disease: String?,
  val title: String?,
  val type: String?,
  val image: String?,
  val hosts: String?,
  val sci: String?,
  val symptoms: List<String>?,
  val control: List<String>?,
  val predicted: Boolean = false,
  val confidence: Int? = -1
)
