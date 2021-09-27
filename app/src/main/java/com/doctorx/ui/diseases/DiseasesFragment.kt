package com.doctorx.ui.diseases

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctorx.databinding.FragmentDiseasesBinding
import com.doctorx.db.data.Disease
import com.doctorx.ui.DiseasesController

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

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentDiseasesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    activity?.let { activity ->
      dataController = DiseasesController().also {
        _binding?.recyclerView?.apply {
          adapter = it.adapter
          layoutManager = LinearLayoutManager(activity)
        }
        it.onItemClickListener = object : DiseasesController.DiseasesClickListener {
          override fun onDiseaseClick(id: Int) {
            findNavController().navigate(
              DiseasesFragmentDirections.actionFirstFragmentToSecondFragment(
                id
              )
            )
          }
        }
      }
      model.getDiseasesLiveData(activity).let {
        it.value?.let { it1 -> updateController(it1) }
        it.observe(activity, { data ->
          // Update the UI, in this case, a TextView.
          Log.d("awslog", "diseases : $data")
          updateController(data)
        })
      }
    }
  }

  fun updateController(dataList: List<Disease>) {
    dataController?.apply {
      data = dataList
      requestModelBuild()
    }
  }

  override fun onDestroyView() {

    super.onDestroyView()
    _binding = null
  }
}