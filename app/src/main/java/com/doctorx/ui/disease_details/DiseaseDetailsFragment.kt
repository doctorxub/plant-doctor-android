package com.doctorx.ui.disease_details

import android.content.Context
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
import com.doctorx.R
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

    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
    val defaultValue = resources.getString(R.string.EN)
    val lang = sharedPref.getString(getString(R.string.pref_lang), defaultValue)

    (activity as? AppCompatActivity)?.let{ activity ->
      binding?.toolbar?.let{
        activity.setSupportActionBar(it)
      }
      binding?.conditionsAndGeo?.text = "Conditions and Geo"
      binding?.symptomsLabel?.text = "Symptoms"
      binding?.control?.text = "Control"
      if (lang == "FR") {
        binding?.conditionsAndGeo?.text = "Conditions et Geo"
        binding?.symptomsLabel?.text = "Symptômes"
        binding?.control?.text = "Lutte"
      }
      else if (lang == "AR") {
        binding?.conditionsAndGeo?.text = "Conditions and Geo"
        binding?.symptomsLabel?.text = "Symptoms"
        binding?.control?.text = "Control"
      }

      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
      activity.supportActionBar?.setDisplayShowHomeEnabled(true)
      setHasOptionsMenu(false)
    }

    (args.diseaseId as? Int)?.let{ id ->
      activity?.let { activity ->
        model.getDiseaseLiveData(activity,id).observe(activity, { disease ->
          Log.d("awslog", "disease details are: $disease")
          if (lang != null) {
            updateUI(disease, lang)
          }
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

  private fun getControlsText(disease: Disease, lang: String): String {
    var cclabel = "Chemical Controls:"
    var pmlabel = "Pest Management:"
    var biolabel = "Bio Controls:"
    var dcc = disease.chemical_control
    var dpm = disease.pest_management
    var dbc = disease.bio_control
    if (lang == "FR") {
      cclabel = "Lutte chimique:"
      pmlabel = "Lutte intégrée:"
      biolabel = "Lutte biologique:"
      dcc = disease.chemical_control_fr
      dpm = disease.pest_management_fr
      dbc = disease.bio_control_fr
    }
    else if (lang == "AR") {
      cclabel = "Chemical Controls:"
      pmlabel = "Pest Management:"
      biolabel = "Bio Controls:"
      dcc = disease.chemical_control_ar
      dpm = disease.pest_management_ar
      dbc = disease.bio_control_ar
    }

    return cclabel.plus("\n")
      .plus(listToText(dcc)).plus("\n\n\n")
      .plus(pmlabel).plus("\n")
      .plus(listToText(dpm)).plus("\n\n\n")
      .plus(biolabel).plus("\n")
      .plus(listToText(dbc))
  }

  private fun updateUI(disease: Disease, lang: String){
    binding?.apply{
      collapsingToolbar.title = getSpannableExpandedTitle(disease, lang)
      controls.text = getControlsText(disease, lang)
      hosts.text = disease.conditions.plus("\n\n").plus(disease.geo)
      symptoms.text = listToText(disease.symptoms)
      when (lang) {
        "FR" -> {
          hosts.text = disease.conditions_fr.plus("\n\n").plus(disease.geo_fr)
          symptoms.text = listToText(disease.symptoms_fr)
        }
        "AR" -> {
          hosts.text = disease.conditions_ar.plus("\n\n").plus(disease.geo_ar)
          symptoms.text = listToText(disease.symptoms_ar)
        }
      }
      confidence.apply {
        visibility = disease.confidence?.takeIf { it > -1 }?.let {
          text = "Confidence: ".plus(it).plus("%")
          View.VISIBLE
        }?: View.GONE
      }
    }
  }

  private fun getSpannableExpandedTitle(disease: Disease, lang: String): SpannableString{
    var dtype = disease.type
    var dname = disease.name
    var dpathogen = disease.pathogen
    if (lang == "FR") {
      dtype = disease.type_fr
      dname = disease.name_fr
      dpathogen = disease.pathogen_fr
    }
    else if (lang == "AR") {
      dtype = disease.type_ar
      dname = disease.name_ar
      dpathogen = disease.pathogen_ar
    }

    var title = dtype?.plus("\n")?:""
    title = title.plus(dname?:"").plus("\n")
    title = title.plus(dpathogen?:"")
    val ss1 = SpannableString(title)
    val titleSize = dtype?.length?.plus(1)?:0
    val diseaseSize = dname?.length?.plus(1)?:0
    ss1.setSpan(RelativeSizeSpan(0.5f), titleSize, titleSize + diseaseSize, 0) // set size
    ss1.setSpan(RelativeSizeSpan(0.4f), (titleSize + diseaseSize -1), title.length, 0) // set size
    return ss1
  }

  private fun listToText(list: List<String>?): String{
    var value = ""
    list?.forEach {
      if(value.isNotBlank()){
        value = value.plus("\n")
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