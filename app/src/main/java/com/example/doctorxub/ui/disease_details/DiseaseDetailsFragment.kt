package com.example.doctorxub.ui.disease_details

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
import com.example.doctorxub.databinding.FragmentDiseaseDetailsBinding
import com.example.doctorxub.db.data.Disease


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DiseaseDetailsFragment : Fragment() {

  private var _binding: FragmentDiseaseDetailsBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!
  val args: DiseaseDetailsFragmentArgs by navArgs()


  private val model: DiseaseDetailsViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    _binding = FragmentDiseaseDetailsBinding.inflate(inflater, container, false)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (activity as? AppCompatActivity)?.let{ activity ->
      activity.setSupportActionBar(binding.toolbar)
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
  }

  private fun updateUI(disease: Disease){
    binding.collapsingToolbar.title = getSpannableExpandedTitle(disease)
    binding.hosts.text = disease.hosts
    binding.controls.text = listToText(disease.control)
    binding.symptoms.text = listToText(disease.symptoms)
  }


  private fun getSpannableExpandedTitle(disease: Disease): SpannableString{
    var title = disease.title?.plus("\n")?:""
    title = title.plus(disease.disease?:"").plus("\n")
    title = title.plus(disease.type?:"")
    val ss1 = SpannableString(title)
    val titleSize = disease.title?.length?.plus(1)?:0
    val diseaseSize = disease.disease?.length?.plus(1)?:0
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
    _binding = null
  }



}