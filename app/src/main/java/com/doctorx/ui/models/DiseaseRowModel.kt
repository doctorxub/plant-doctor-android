package com.doctorx.ui.models

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.doctorx.R
import com.doctorx.db.data.Disease
import com.doctorx.ui.DiseasesController


@EpoxyModelClass(layout = R.layout.view_holder_disease_row)
abstract class DiseaseRowModel : EpoxyModelWithHolder<DiseaseRowModel.Holder>() {

  @EpoxyAttribute
  var disease: Disease? = null
  @EpoxyAttribute
  var lang: String? = null

  @EpoxyAttribute
  var clickListener : DiseasesController.DiseasesClickListener? = null

  override fun bind(holder: Holder) {
    when {
        lang.equals("FR") -> {
          holder.disease.text = disease?.name_fr?:""
          holder.title.text = disease?.type_fr?:""
          holder.type.text = disease?.pathogen_fr?:""
        }
        lang.equals("AR") -> {
          holder.disease.text = disease?.name_ar?:""
          holder.title.text = disease?.type_ar?:""
          holder.type.text = disease?.pathogen_ar?:""
        }
        else -> {
          holder.disease.text = disease?.name?:""
          holder.title.text = disease?.type?:""
          holder.type.text = disease?.pathogen?:""
        }
    }
    disease?.id?.let{ id ->
      holder.itemView.setOnClickListener {
        clickListener?.onDiseaseClick()
      }
    }
  }

  class Holder : EpoxyHolder() {
    lateinit var title: TextView
    lateinit var disease: TextView
    lateinit var type: TextView
    lateinit var itemView: View
    override fun bindView(itemView: View) {
      this.itemView = itemView
      title = itemView.findViewById(R.id.title)
      disease = itemView.findViewById(R.id.disease)
      type = itemView.findViewById(R.id.type)
    }
  }
}