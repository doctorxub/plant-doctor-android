package com.doctorx.ui.disease_details

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.doctorx.databinding.FragmentDiseaseDetailsBinding
import com.doctorx.db.data.Disease
import java.io.File
import java.lang.Exception


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DiseaseDetailsFragment : Fragment() {

  private var binding: FragmentDiseaseDetailsBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  val args: DiseaseDetailsFragmentArgs by navArgs()


  private val model: DiseaseDetailsViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    binding = FragmentDiseaseDetailsBinding.inflate(inflater, container, false)
    return binding?.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (activity as? AppCompatActivity)?.let{ activity ->
      binding?.toolbar?.let{
      activity.setSupportActionBar(it)
    }
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
      activity.supportActionBar?.setDisplayShowHomeEnabled(true)
      setHasOptionsMenu(false)
    }

    (args.diseaseId as? Int)?.let{ id ->
      activity?.let { activity ->
        model.getDiseaseLiveData(activity,id).observe(activity, { disease ->
          Log.d("awslog", "disease details are: $disease")
            updateUI(disease)
        })
      }
    }

    binding?.expandedImage?.apply{
      visibility = (args.image as? String)?.let{
        val file = File(it)
        if(file.exists()) {
          try {
            val myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
            setImageBitmap(myBitmap)
          }
          catch (e: Exception){
            Log.e("awslog", "error loading image from disk  error message: ${e.message}")
          }
        }
      View.VISIBLE
    }?:View.GONE
    }
  }

  private fun updateUI(disease: Disease){
    binding?.apply{
      collapsingToolbar.title = getSpannableExpandedTitle(disease)
      hosts.text = disease.conditions
      controls.text = listToText(disease.chemical_control)
      symptoms.text = listToText(disease.symptoms)
      confidence.apply {
        visibility = disease.confidence?.takeIf { it > -1 }?.let {
          text = "Confidence: ".plus(it).plus("%")
          View.VISIBLE
        }?: View.GONE
      }
    }
  }


  private fun getSpannableExpandedTitle(disease: Disease): SpannableString{
    var title = disease.type_fr?.plus("\n")?:""
    title = title.plus(disease.name_fr?:"").plus("\n")
    title = title.plus(disease.pathogen_fr?:"")
    val ss1 = SpannableString(title)
    val titleSize = disease.type_fr?.length?.plus(1)?:0
    val diseaseSize = disease.name_fr?.length?.plus(1)?:0
    ss1.setSpan(RelativeSizeSpan(0.7f), titleSize, titleSize + diseaseSize, 0) // set size
    ss1.setSpan(RelativeSizeSpan(0.5f), (titleSize + diseaseSize -1), title.length, 0) // set size
    return ss1
  }

  private fun listToText(list: List<String>?): String{
    var value = ""
    list?.forEach {
      if(value.isNotBlank()){
        value = value.plus("\n\n")
      }
      value = value.plus("• ").plus(it)
    }
    return value
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }



}