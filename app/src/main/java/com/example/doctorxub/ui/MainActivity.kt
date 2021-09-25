package com.example.doctorxub.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.doctorxub.R
import com.example.doctorxub.databinding.ActivityMainBinding
import com.example.doctorxub.server.ApiInterface
import com.example.doctorxub.ui.diseases.DiseasesFragmentDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

  companion object {
    const val REQUEST_CODE_PICK_IMAGE = 101
  }

  private var selectedImageUri: Uri? = null
  private lateinit var binding: ActivityMainBinding

  private lateinit var controller: NavController
  private lateinit var listener: NavController.OnDestinationChangedListener

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ApiInterface.getAndStoreDiseases(this)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.fab.setOnClickListener { view ->
//      showErrorMessage(view,"Replace with your own action")
      openImageChooser()
    }

    controller = findNavController( binding.root.findViewById(R.id.nav_host_fragment_content_main))
    listener =  NavController.OnDestinationChangedListener{ controller, destination, arguments ->
      // react on change
      // you can check destination.id or destination.label and act based on that
      if(destination.id == R.id.SecondFragment){
        binding.fab.visibility = View.GONE
      } else{
        binding.fab.visibility = View.VISIBLE
      }
    }
  }

  override fun onResume() {
    super.onResume()
    controller.addOnDestinationChangedListener(listener)
  }

  override fun onPause() {
    controller.removeOnDestinationChangedListener(listener)
    super.onPause()
  }

  private fun openImageChooser() {
    Intent(Intent.ACTION_PICK).also {
      it.type = "image/*"
      val mimeTypes = arrayOf("image/jpeg", "image/png")
      it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
      startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }


  private val job = Job()
  private val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
  private val scopeIO = CoroutineScope(job + Dispatchers.IO)

  private fun uploadImage(){
    if (selectedImageUri == null) {
      return
    }

    scopeIO.launch {
      contentResolver.openFileDescriptor(selectedImageUri!!, "r", null)?.let{parcelFileDescriptor ->
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val part = getFileMultiPart("file", file)

        val result = ApiInterface.UploadImageAndSavePrediction(this@MainActivity, part)
        scopeMainThread.launch {
          if(result.second != -1){
            controller.navigate(
              DiseasesFragmentDirections.actionToSecondFragment(
                result.second
              )
            )
          }
        }
      }
    }
  }

  private fun showErrorMessage(view: View, message: String){
    Snackbar.make(view, message, Snackbar.LENGTH_LONG)
      .setAction("Action", null).show()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      when (requestCode) {
        REQUEST_CODE_PICK_IMAGE -> {
          selectedImageUri = data?.data
//          image_view.setImageURI(selectedImageUri)
          uploadImage()
        }
      }
    }
  }

  fun getFileMultiPart( partName: String?,
    file: File
  ): MultipartBody.Part {
    // create RequestBody instance from file
    val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
    // MultipartBody.Part is used to send also the actual file name
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
  }

  fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
      val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      returnCursor.moveToFirst()
      name = returnCursor.getString(nameIndex)
      returnCursor.close()
    }
    return name
  }
}