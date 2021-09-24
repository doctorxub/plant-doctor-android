package com.example.doctorxub.ui.models

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.doctorxub.R
import com.example.doctorxub.db.data.Disease
import com.example.doctorxub.ui.DiseasesController


@EpoxyModelClass(layout = R.layout.view_holder_disease_row)
abstract class DiseaseRowModel : EpoxyModelWithHolder<DiseaseRowModel.Holder>() {

  @EpoxyAttribute
  var disease: Disease? = null

  @EpoxyAttribute
  var clickListener : DiseasesController.DiseasesClickListener? = null

  override fun bind(holder: Holder) {
    holder.titleTextView.text = disease?.disease?:""
    holder.itemView.setOnClickListener {
      clickListener?.onDiseaseClick()
    }
  }

  class Holder : EpoxyHolder() {
    lateinit var titleTextView: TextView
    lateinit var itemView: View
    override fun bindView(itemView: View) {
      this.itemView = itemView
      titleTextView = itemView.findViewById(R.id.tv_title)
    }
  }
}