package com.example.doctorxub.ui.models

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.doctorxub.R
import com.example.doctorxub.db.data.Disease


@EpoxyModelClass(layout = R.layout.view_holder_disease_row)
abstract class DiseaseRowModel : EpoxyModelWithHolder<DiseaseRowModel.Holder>() {

  @EpoxyAttribute
  var disease: Disease? = null

  override fun bind(holder: Holder) {
    holder.titleTextView.text = disease?.disease?:""
  }

  class Holder : EpoxyHolder() {
    lateinit var titleTextView: TextView
    override fun bindView(itemView: View) {
      titleTextView = itemView.findViewById(R.id.tv_title)
    }
  }
}