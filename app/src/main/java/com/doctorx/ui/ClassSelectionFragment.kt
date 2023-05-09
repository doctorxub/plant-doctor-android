package com.doctorx.ui

import android.Manifest
import android.annotation.SuppressLint
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
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.doctorx.R
import com.doctorx.databinding.FragmentClassSelectionBinding
import com.doctorx.server.ApiInterface
import com.doctorx.ui.diseases.DiseasesFragmentDirections
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

class ClassSelectionFragment : Fragment() {

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 100
        const val REQUEST_CODE_PICK_IMAGE = 99
        const val REQUEST_CODE_PICK_IMAGE_CUCUMBER = 101
        const val REQUEST_IMAGE_CAPTURE_CUCUMBER = 102
        const val REQUEST_CODE_PICK_IMAGE_PEPPER = 103
        const val REQUEST_IMAGE_CAPTURE_PEPPER = 104
        const val REQUEST_CODE_PICK_IMAGE_TOMATO = 105
        const val REQUEST_IMAGE_CAPTURE_TOMATO = 106
        const val REQUEST_LEGACY_STORAGE_PERMISSION = 112
    }
    private enum class Model{
        CUCUMBER, PEPPER, TOMATO, OLD
    }

    private var selectedImageUri: Uri? = null
    private var _binding: FragmentClassSelectionBinding? = null
    private lateinit var controller: NavController
    //private lateinit var listener: NavController.OnDestinationChangedListener
    private lateinit var ptypeModel : Model

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        ApiInterface.getAndStoreDiseases(requireContext().applicationContext)
        _binding = FragmentClassSelectionBinding.inflate(inflater, container, false)

        controller = requireActivity().findNavController(R.id.nav_host_fragment_content_main)
        //listener =  NavController.OnDestinationChangedListener{ controller, destination, arguments ->
            // react on change
            // you can check destination.id or destination.label and act based on that
           /* if(destination.id == R.id.SecondFragment){
                _binding.fab.visibility = View.GONE
            } else{
                _binding.fab.visibility = View.VISIBLE
            }
            */
        //}



        ptypeModel = Model.OLD

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        /*
        inflater.inflate(R.menu.menu_main, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        val menuItem = menu.findItem(R.id.takePicture)
        menuItem.title = "Diseases List"
        */
        menu.clear()
    }

    private fun dispatchTakePictureIntent(model: Model) {
        ptypeModel = model
        try {
            takePicture()
        } catch (e: Exception) {
            val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (!hasPermissions(requireContext().applicationContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, REQUEST_LEGACY_STORAGE_PERMISSION)
            } else {
                Toast.makeText(
                    requireContext().applicationContext,
                    "Please check if you have full storage",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        /*
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }*/
    }

    private fun dispatchPickPictureIntent(model: Model) {
        ptypeModel = model
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            when (model){
                Model.CUCUMBER -> startActivityForResult(it, REQUEST_CODE_PICK_IMAGE_CUCUMBER)
                Model.PEPPER -> startActivityForResult(it, REQUEST_CODE_PICK_IMAGE_PEPPER)
                Model.TOMATO -> startActivityForResult(it, REQUEST_CODE_PICK_IMAGE_TOMATO)
                else -> startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    //val imageBitmap = data?.extras?.get("data") as Bitmap
                    // Aquí puedes hacer lo que quieras con la imagen capturada
                    uploadImage()
                }
                REQUEST_IMAGE_CAPTURE_CUCUMBER -> {
                    //val imageBitmap = data?.extras?.get("data") as Bitmap
                    // Aquí puedes hacer lo que quieras con la imagen capturada
                    uploadImage()
                }
                REQUEST_IMAGE_CAPTURE_PEPPER -> {
                    //val imageBitmap = data?.extras?.get("data") as Bitmap
                    // Aquí puedes hacer lo que quieras con la imagen capturada
                    uploadImage()
                }
                REQUEST_IMAGE_CAPTURE_TOMATO -> {
                    //val imageBitmap = data?.extras?.get("data") as Bitmap
                    // Aquí puedes hacer lo que quieras con la imagen capturada
                    uploadImage()
                }
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    // Aquí puedes hacer lo que quieras con la imagen seleccionada
                    uploadImage()
                }
                REQUEST_CODE_PICK_IMAGE_CUCUMBER -> {
                    selectedImageUri = data?.data
                    // Aquí puedes hacer lo que quieras con la imagen seleccionada
                    uploadImage()
                }
                REQUEST_CODE_PICK_IMAGE_PEPPER -> {
                    selectedImageUri = data?.data
                    // Aquí puedes hacer lo que quieras con la imagen seleccionada
                    uploadImage()
                }
                REQUEST_CODE_PICK_IMAGE_TOMATO -> {
                    selectedImageUri = data?.data
                    // Aquí puedes hacer lo que quieras con la imagen seleccionada
                    uploadImage()
                }
            }

        }
    }


    fun showMenu(v: View) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (activity as? AppCompatActivity)?.let { activity ->
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
            val defaultValue = resources.getString(R.string.EN)
            val lang = sharedPref.getString(getString(R.string.pref_lang), defaultValue)


            binding.apply {
                langButton.text = lang

                when {
                    lang.equals(resources.getString(R.string.FR)) -> {
                        toolbarTitle.text = resources.getString(R.string.plants_selection_fr)
                    }
                    lang.equals(resources.getString(R.string.AR)) -> {
                        toolbarTitle.text = resources.getString(R.string.plants_selection_ar)
                    }
                    else -> {
                        toolbarTitle.text = resources.getString(R.string.plants_selection)
                    }
                }

                langButton.setOnClickListener {
                    when {
                        langButton.text.equals(resources.getString(R.string.EN)) -> {
                            langButton.text = resources.getString(R.string.FR)
                            toolbarTitle.text = resources.getString(R.string.plants_selection_fr)
                            with (sharedPref.edit()) {
                                putString(getString(R.string.pref_lang), resources.getString(R.string.FR))
                                apply()
                            }
                        }
                        langButton.text.equals(resources.getString(R.string.FR)) -> {
                            langButton.text = resources.getString(R.string.AR)
                            toolbarTitle.text = resources.getString(R.string.plants_selection_ar)
                            with (sharedPref.edit()) {
                                putString(getString(R.string.pref_lang), resources.getString(R.string.AR))
                                apply()
                            }
                        }
                        langButton.text.equals(resources.getString(R.string.AR)) -> {
                            langButton.text = resources.getString(R.string.EN)
                            toolbarTitle.text = resources.getString(R.string.plants_selection)
                            with (sharedPref.edit()) {
                                putString(getString(R.string.pref_lang), resources.getString(R.string.EN))
                                apply()
                            }
                        }
                    }
                }
            }
        }


        binding.buttonCamCucumber.setOnClickListener {
            dispatchTakePictureIntent(Model.CUCUMBER)
        }
        binding.buttonPhotoCucumber.setOnClickListener {
            dispatchPickPictureIntent(Model.CUCUMBER)
        }

        binding.buttonCamTomatoes.setOnClickListener {
           // findNavController().navigate(R.id.action_ClassSelectionFragment_to_DiseaseDetailsFragment)
            dispatchTakePictureIntent(Model.TOMATO)
        }
        binding.buttonPhotoTomatoes.setOnClickListener {
            //findNavController().navigate(R.id.action_ClassSelectionFragment_to_DiseaseDetailsFragment)
            dispatchPickPictureIntent(Model.TOMATO)
        }
        binding.buttonCamPeppers.setOnClickListener {
            //findNavController().navigate(R.id.action_ClassSelectionFragment_to_DiseaseDetailsFragment)
            dispatchTakePictureIntent(Model.PEPPER)
        }
        binding.buttonPhotoPeppers.setOnClickListener {
        //    findNavController().navigate(R.id.action_ClassSelectionFragment_to_DiseaseDetailsFragment)
            dispatchPickPictureIntent(Model.PEPPER)
        }
    }
/*
    fun updateControllerLang(pref_lang: String) {
        dataController?.apply {
            lang = pref_lang
            requestModelBuild()
        }
    }
    */
    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        selectedImageUri = getImageUri(requireContext().applicationContext, bmp)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        try {
            when (ptypeModel){
                Model.CUCUMBER -> startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CUCUMBER)
                Model.PEPPER -> startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_PEPPER)
                Model.TOMATO -> startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_TOMATO)
                else -> startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }

        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
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

    private val job = Job()
    private val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
    private val scopeIO = CoroutineScope(job + Dispatchers.IO)

    private fun uploadImage(){
        if (selectedImageUri == null) {
            return
        }
        var ptype = 0
        when (ptypeModel){
            Model.CUCUMBER -> {ptype = 1}
            Model.PEPPER -> {ptype = 2}
            Model.TOMATO -> {ptype = 3}
        }
        Log.d("UPLOAD", "uploadImage() function called")
        scopeIO.launch {
            Log.d("UPLOAD", "antes del requireContext")
                requireContext().contentResolver.openFileDescriptor(selectedImageUri!!, "r", null)
                    ?.let { parcelFileDescriptor ->

                        Log.d("UPLOAD", "antes del try")
                        try {
                            Log.d("UPLOAD", "antes del inputStream")

                            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

                            //requireContext().contentResolver.getFileName(selectedImageUri!!)

                            val file = File(
                                requireContext().cacheDir,
                                requireContext().contentResolver.getFileName(selectedImageUri!!)
                            )
                            Log.d("UPLOAD", "antes del FileOutputStream")

                            val outputStream = FileOutputStream(file)
                            inputStream.copyTo(outputStream)

                            val part = getFileMultiPart("file", file)
                            Log.d("UPLOAD", "antes del API")

                            val result = ApiInterface.UploadImageAndSavePrediction(requireActivity(), part, ptype)
                            Log.d("UPLOAD", "antes del scopeMainThread")

                            scopeMainThread.launch {
                                Log.d("UPLOAD", "antes del result -1")

                                if (result.second != -1) {
                                    Log.d("UPLOAD", "antes del secondFragment que es el tercero")

                                    controller.navigate(
                                        DiseasesFragmentDirections.actionToSecondFragment(
                                            result.second, file.path
                                        )
                                    )
                                }
                            }
                        }
                        catch (e: Exception) {
                            Log.e("TAG", "Error al abrir el archivo: $e")
                        }
                    }
        }
    }

  //  @SuppressLint("Range")
  @SuppressLint("Range")
  fun ContentResolver.getFileName(uri: Uri): String {
        var name = ""
        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return name
    }

    fun getFileMultiPart( partName: String?,
                          file: File
    ): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LEGACY_STORAGE_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    Toast.makeText(
                        requireContext().applicationContext,
                        "The app was not allowed to write in your storage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        //controller.addOnDestinationChangedListener(listener)

        //val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        //val tutorial = sharedPref.getBoolean(getString(R.string.pref_tutorial), false)
        //if (!tutorial) {
            //binding.fab.visibility = View.GONE
        //} else {
            //binding.tutorialView.visibility = View.GONE
        //}
    }

    override fun onPause() {
       // controller.removeOnDestinationChangedListener(listener)
        super.onPause()
    }


}