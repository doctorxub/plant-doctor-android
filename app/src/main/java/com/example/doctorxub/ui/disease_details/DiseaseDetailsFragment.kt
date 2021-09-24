package com.example.doctorxub.ui.disease_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.doctorxub.R
import com.example.doctorxub.databinding.FragmentDiseaseDetailsBinding

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

    binding.buttonSecond.setOnClickListener {
      findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

    (args.diseaseId as? Int)?.let{ id ->
      activity?.let { activity ->
        model.getDiseaseLiveData(activity,id).observe(activity, { disease ->
          Log.d("awslog", "disease details are: $disease")
        })
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}