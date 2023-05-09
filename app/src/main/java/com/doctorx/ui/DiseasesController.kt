package com.doctorx.ui

import com.airbnb.epoxy.EpoxyController
import com.doctorx.db.data.Disease
import com.doctorx.ui.models.DiseaseRowModel_

class DiseasesController : EpoxyController() {

  var data : List<Disease> = emptyList()
  var lang: String = "EN"
  var onItemClickListener: DiseasesClickListener? = null
  override fun buildModels() {
    data.forEach {
      DiseaseRowModel_()
        .id(it.id)
        .disease(it)
        .lang(lang)
        .clickListener(onItemClickListener)
        .also {
          add(it)
        }
    }
  }


  interface DiseasesClickListener{
    fun onDiseaseClick(//id: Int
     )
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