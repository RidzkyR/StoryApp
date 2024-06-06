package com.example.submission_storyapp.view.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.submission_storyapp.R
import com.example.submission_storyapp.data.Result
import com.example.submission_storyapp.databinding.ActivityAddBinding
import com.example.submission_storyapp.utils.getImageUri
import com.example.submission_storyapp.utils.reduceFileImage
import com.example.submission_storyapp.utils.uriToFile
import com.example.submission_storyapp.view.ViewModelFactory
import com.example.submission_storyapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding

    private var currentImageUri: Uri? = null
    private var lat: Float? = null
    private var lon: Float? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: AddViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    // Location Permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> { getMyLastLocation()}
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> { getMyLastLocation()}
                else -> { Log.d("Permission", "No Permission")}
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getUser()
        setupAction()
    }

    private fun setupAction() {
        with(binding) {
            buttonGallery.setOnClickListener { startGallery() }
            buttonCamera.setOnClickListener { startCamera() }
            buttonBack.setOnClickListener { backToMain() }
            buttonAdd.setOnClickListener { addStory() }
            checkBox.setOnClickListener {
                if (checkBox.isChecked) {
                    getMyLastLocation()
                } else {
                    lat = null
                    lon = null
                }
            }
        }
    }

    private fun getMyLastLocation(){
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toFloat()
                    lon = location.longitude.toFloat()
                   showToast("Location found. You can post a story")
                } else {
                    binding.checkBox.isChecked = false
                    Toast.makeText(this@AddActivity, "Location is not found. Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun backToMain() {
        intent = Intent(this@AddActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finishAffinity()
    }

    private fun getUser() {
        viewModel.getUser().observe(this) { user ->
            with(binding) {
                tvUploadUsername.text = user.name
                tvUploadUserId.text = user.userId
            }
        }
    }

    private fun addStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            Log.d("Image File", "showImage: ${imageFile.path}")
            viewModel.addStory(imageFile, description, lat, lon).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            showToast(result.data.message)
                            showLoading(false)
                        }
                        is Result.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.error_no_image))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivUploadPhoto.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}