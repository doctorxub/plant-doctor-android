package com.example.doctorxub.ui

import com.airbnb.epoxy.EpoxyController
import com.example.doctorxub.db.data.Disease

class DiseasesController : EpoxyController() {

  var data : List<Disease> = emptyList()
  override fun buildModels() {
    data.forEach {
      DiseaseRowModel_().id(it.id).disease(it).also { add(it) }
    }
  }


  interface DiseasesClickListener{
    fun onDiseaseClick()
    fun onTakePictureClick()
  }


//  @AutoModel
//  var headerModel: HeaderModel_? = null
//
//  @AutoModel
//  var loaderModel: LoaderModel_? = null
//  override fun buildModels(photos: List<Photo>, loadingMore: Boolean?) {
//    headerModel
//      .title("My Photos")
//      .description("My album description!")
//      .addTo(this)
//    for (photo in photos) {
//      PhotoModel()
//        .id(photo.id())
//        .url(photo.url())
//        .addTo(this)
//    }
//    loaderModel
//      .addIf(loadingMore, this)
//  }
}