package com.example.doctorxub.ui.diseases

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorxub.databinding.FragmentDiseasesBinding
import com.example.doctorxub.db.data.Disease
import com.example.doctorxub.ui.DiseasesController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DiseasesFragment : Fragment() {

  private var _binding: FragmentDiseasesBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  private val model: DiseasesViewModel by viewModels()

  private var dataController: DiseasesController? = null

  private var diseasesObserver = Observer<List<Disease>> { data ->
    // Update the UI, in this case, a TextView.
    Log.d("awslog", "diseases : $data")
    updateController(data)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentDiseasesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    activity?.let{ activity ->
      model.getDiseasesLiveData(activity).observe(activity, diseasesObserver)
      dataController = DiseasesController().also {
        _binding?.recyclerView?.apply{
          adapter = it.adapter
          layoutManager = LinearLayoutManager(activity)
        }
      }
    }


//    binding.buttonFirst.setOnClickListener {
////      findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//
//    }

  }

  fun updateController(dataList: List<Disease>){
    dataController?.apply{
      data = dataList
      requestModelBuild()
    }
  }

  override fun onDestroyView() {

    super.onDestroyView()
    _binding = null
  }
}