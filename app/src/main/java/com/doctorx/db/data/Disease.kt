package com.doctorx.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Disease(
  @PrimaryKey val id: Int,

  val type: String?,
  val pathogen: String?,
  val name: String?,
  val sci_name: String?,
  val local_name: String?,
  val symptoms: List<String>?,
  val conditions: String?,
  val chemical_control: List<String>?,
  val pest_management: List<String>?,
  val bio_control: List<String>?,
  val geo: String?,

  val type_ar: String?,
  val pathogen_ar: String?,
  val name_ar: String?,
  val sci_name_ar: String?,
  val local_name_ar: String?,
  val symptoms_ar: List<String>?,
  val conditions_ar: String?,
  val chemical_control_ar: List<String>?,
  val pest_management_ar: List<String>?,
  val bio_control_ar: List<String>?,
  val geo_ar: String?,

  val type_fr: String?,
  val pathogen_fr: String?,
  val name_fr: String?,
  val sci_name_fr: String?,
  val local_name_fr: String?,
  val symptoms_fr: List<String>?,
  val conditions_fr: String?,
  val chemical_control_fr: List<String>?,
  val pest_management_fr: List<String>?,
  val bio_control_fr: List<String>?,
  val geo_fr: String?,

  val image: String?,

  val predicted: Boolean = false,
  val confidence: Int? = -1,
)
