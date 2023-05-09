package com.doctorx.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.doctorx.R
import com.doctorx.databinding.ActivityMainBinding
import com.doctorx.server.ApiInterface
import com.doctorx.ui.diseases.DiseasesFragmentDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
/*
  companion object {
    const val REQUEST_CODE_PICK_IMAGE = 101
    const val REQUEST_IMAGE_CAPTURE = 102
    const val REQUEST_LEGACY_STORAGE_PERMISSION = 112
  }
*/
  private var tutorialCounter = 1
  private var selectedImageUri: Uri? = null
  private lateinit var binding: ActivityMainBinding

  private lateinit var controller: NavController
  private lateinit var listener: NavController.OnDestinationChangedListener

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    MenuCompat.setGroupDividerEnabled(menu, true)
    return true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //ApiInterface.getAndStoreDiseases(this)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.fab.setOnClickListener { view ->
      showMenu(view)
    }

    controller = findNavController( binding.root.findViewById(R.id.nav_host_fragment_content_main))
    listener =  NavController.OnDestinationChangedListener{ controller, destination, arguments ->
      // react on change
      // you can check destination.id or destination.label and act based on that
      if( (destination.id == R.id.SecondFragment) || (destination.id == R.id.ClassSelectionFragment))
         {
        binding.fab.visibility = View.GONE
      } else{
        binding.fab.visibility = View.VISIBLE
      }
    }

    binding.nextButton.setOnClickListener {
      binding.backButton.visibility = View.VISIBLE
      if (tutorialCounter < 4) {
        tutorialCounter++
      }

      when (tutorialCounter) {
          2 -> {
            binding.tutorialImageView.setImageResource(R.drawable.tutorial_2)
          }
          3 -> {
            binding.tutorialImageView.setImageResource(R.drawable.tutorial_3)
            binding.nextButton.text = "Done"
          }
          4 -> {
            tutorialCounter = 1
            binding.fab.visibility = View.VISIBLE
            binding.tutorialView.visibility = View.GONE
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
              putBoolean(getString(com.doctorx.R.string.pref_tutorial), true)
              apply()
            }
          }
      }
    }

    binding.backButton.setOnClickListener {
      binding.nextButton.text = "Next"
      if (tutorialCounter > 1) {
        tutorialCounter--
      }

      binding.backButton.visibility = View.VISIBLE
      when (tutorialCounter) {
        1 -> {
          binding.backButton.visibility = View.GONE
          binding.tutorialImageView.setImageResource(R.drawable.tutorial_1)
        }
        2 -> {
          binding.tutorialImageView.setImageResource(R.drawable.tutorial_2)
        }
      }
    }
  }

  fun showTutorial() {
    tutorialCounter = 1
    binding.nextButton.text = "Next"
    binding.backButton.visibility = View.GONE
    binding.tutorialImageView.setImageResource(R.drawable.tutorial_1)
    binding.fab.visibility = View.GONE
    binding.tutorialView.visibility = View.VISIBLE
  }

  fun showMenu(v: View) {
    val popup = PopupMenu(this, v)

    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
    val defaultValue = resources.getString(R.string.EN)
    val lang = sharedPref.getString(getString(R.string.pref_lang), defaultValue)

    when {
      lang.equals(resources.getString(R.string.FR)) -> {
        popup.inflate(R.menu.actions_fr)
      }
      lang.equals(resources.getString(R.string.AR)) -> {
        popup.inflate(R.menu.actions_ar)
      }
      else -> {
        popup.inflate(R.menu.actions)
      }
    }
    popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

      when (item!!.itemId) {
        R.id.takePicture -> {
          controller.navigate(
            DiseasesFragmentDirections.actionFirstFragmentToClassSelectionFragment()
          )
        }
        R.id.showTutorial -> {
          showTutorial()
        }
      }

      true
    })

    popup.show()
  }

  override fun onResume() {
    super.onResume()
    controller.addOnDestinationChangedListener(listener)

    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
    val tutorial = sharedPref.getBoolean(getString(R.string.pref_tutorial), false)
    if (!tutorial) {
      binding.fab.visibility = View.GONE
    } else {
      binding.tutorialView.visibility = View.GONE
    }
  }

  override fun onPause() {
    controller.removeOnDestinationChangedListener(listener)
    super.onPause()
  }



  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

  private fun showErrorMessage(view: View, message: String){
    Snackbar.make(view, message, Snackbar.LENGTH_LONG)
      .setAction("Action", null).show()
  }

}