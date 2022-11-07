package com.doctorx.ui.diseases

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctorx.R
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
      val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
      val defaultValue = resources.getString(R.string.EN)
      val lang = sharedPref.getString(getString(R.string.pref_lang), defaultValue)

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
        it.value?.let { it1 ->
          if (lang != null) {
            updateController(it1, lang)
          }
        }
        it.observe(activity, { data ->
          // Update the UI, in this case, a TextView.
          Log.d("awslog", "diseases : $data")
          if (lang != null) {
            updateController(data, lang)
          }
        })
      }

      binding.apply {
        langButton.text = lang

        when {
            lang.equals(resources.getString(R.string.FR)) -> {
              toolbarTitle.text = resources.getString(R.string.supported_diseases_fr)
            }
            lang.equals(resources.getString(R.string.AR)) -> {
              toolbarTitle.text = resources.getString(R.string.supported_diseases_ar)
            }
            else -> {
              toolbarTitle.text = resources.getString(R.string.supported_diseases)
            }
        }

        langButton.setOnClickListener {
          when {
              langButton.text.equals(resources.getString(R.string.EN)) -> {
                  langButton.text = resources.getString(R.string.FR)
                  with (sharedPref.edit()) {
                    putString(getString(R.string.pref_lang), resources.getString(R.string.FR))
                    apply()
                  }
              }
              langButton.text.equals(resources.getString(R.string.FR)) -> {
                  langButton.text = resources.getString(R.string.AR)
                  with (sharedPref.edit()) {
                    putString(getString(R.string.pref_lang), resources.getString(R.string.AR))
                    apply()
                  }
              }
              langButton.text.equals(resources.getString(R.string.AR)) -> {
                  langButton.text = resources.getString(R.string.EN)
                  with (sharedPref.edit()) {
                    putString(getString(R.string.pref_lang), resources.getString(R.string.EN))
                    apply()
                  }
              }
          }

          updateControllerLang(langButton.text.toString())
        }
      }
    }
  }

  fun updateController(dataList: List<Disease>, pref_lang: String) {
    dataController?.apply {
      data = dataList
      lang = pref_lang
      requestModelBuild()
    }
  }

  fun updateControllerLang(pref_lang: String) {
    dataController?.apply {
      lang = pref_lang
      requestModelBuild()
    }
  }

  override fun onDestroyView() {

    super.onDestroyView()
    _binding = null
  }
}