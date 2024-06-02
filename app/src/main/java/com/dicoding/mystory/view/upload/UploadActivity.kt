package com.dicoding.mystory.view.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.mystory.R
import com.dicoding.mystory.data.pref.UserPreference
import com.dicoding.mystory.databinding.ActivityUploadBinding
import com.dicoding.mystory.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference
    private lateinit var binding: ActivityUploadBinding
    private lateinit var viewModel: UploadStoryViewModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()

        userPreference = UserPreference.getInstance(dataStore)
    }

    private val dataStore by preferencesDataStore(name = "settings")

    private suspend fun getUserToken(): String = withContext(Dispatchers.IO) {
        userPreference.getSession().firstOrNull()?.token ?: ""
    }

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()), ".jpg", storageDir)
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }
    }


    private fun showPermissionRationaleDialog(permission: String, onAccept: () -> Unit) {
        val message = when (permission) {
            Manifest.permission.CAMERA -> getString(R.string.camera_permission_rationale)
            Manifest.permission.READ_EXTERNAL_STORAGE -> getString(R.string.gallery_permission_rationale)
            else -> getString(R.string.permission_rationale)
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_needed))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ -> onAccept() }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            requestCode
        )
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[UploadStoryViewModel::class.java]

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.uploadResult.observe(this) {
            when (it) {
                is UploadResultState.Success -> {
                    Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                is UploadResultState.Error -> {
                    Toast.makeText(this, getString(R.string.upload_failed), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            createCustomTempFile(this).also { file ->
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@UploadActivity,
                    "com.dicoding.mystory.fileprovider",
                    file
                )
                currentPhotoPath = file.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            if (myFile.exists()) {
                getFile = myFile

                val result = rotateBitmap(
                    BitmapFactory.decodeFile(myFile.path),
                    true
                )

                binding.imagePreview.setImageBitmap(result)
            } else {
                Toast.makeText(this, "Failed to get the image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var currentPhotoPath: String = ""

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadActivity)
                getFile = myFile
                binding.imagePreview.setImageURI(uri)
            }
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            lifecycleScope.launch {
                val token = getUserToken()
                if (token.isNotBlank()) {
                    viewModel.uploadStory(imageMultipart, description, token)
                } else {
                    Toast.makeText(
                        this@UploadActivity,
                        "User token is empty. Please log in again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this@UploadActivity,
                "Please select an image first",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startTakePhoto()
                } else {
                    Toast.makeText(this, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_GALLERY_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startGallery()
                } else {
                    Toast.makeText(this, getString(R.string.gallery_permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
        private const val REQUEST_GALLERY_PERMISSION = 11
    }
}