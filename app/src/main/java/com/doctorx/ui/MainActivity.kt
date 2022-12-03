package com.doctorx.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
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

  companion object {
    const val REQUEST_CODE_PICK_IMAGE = 101
    const val REQUEST_IMAGE_CAPTURE = 102
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
      showMenu(view)
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
        R.id.pickImage -> {
          openImageChooser()
        }
        R.id.takePicture -> {
          takePicture()
        }
      }

      true
    })

    popup.show()
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
    Intent(Intent.ACTION_GET_CONTENT).also {
      it.type = "image/*"
      val mimeTypes = arrayOf("image/jpeg", "image/png")
      it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
      startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
    }
  }

  private fun takePicture() {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    try {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    } catch (e: ActivityNotFoundException) {
      // display error state to the user
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
                result.second, file.path
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
          uploadImage()
        }
        REQUEST_IMAGE_CAPTURE -> {
          val photo = data?.extras?.get("data") as Bitmap
          selectedImageUri = getImageUri(applicationContext, photo)
          uploadImage()
        }
      }
    }
  }

  fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path: String = MediaStore.Images.Media.insertImage(
      inContext.getContentResolver(),
      inImage, "Title", null
    )
    return Uri.parse(path)
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