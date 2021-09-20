package com.example.doctorxub.diseases

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.doctorxub.databinding.FragmentFirstBinding
import com.example.doctorxub.db.AppDatabase
import com.example.doctorxub.db.data.Disease
import com.example.doctorxub.server.ApiInterface

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DiseasesFragment : Fragment() {

  private var _binding: FragmentFirstBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  private val model: DiseasesViewModel by viewModels()

  private var diseasesObserver = Observer<List<Disease>> { diseases ->
    // Update the UI, in this case, a TextView.
    Log.d("awslog", "diseases : $diseases")
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    _binding = FragmentFirstBinding.inflate(inflater, container, false)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    activity?.let{
      model.getDiseasesLiveData(it).observe(it, diseasesObserver)
    }
    binding.buttonFirst.setOnClickListener {
//      findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
      activity?.let{
        ApiInterface.getAndStoreDiseases(it)
      }

    }
  }

  override fun onDestroyView() {

    super.onDestroyView()
    _binding = null
  }
}