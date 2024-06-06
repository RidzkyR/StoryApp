package com.example.submission_storyapp.view.map

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.submission_storyapp.R
import com.example.submission_storyapp.data.Result

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.submission_storyapp.databinding.ActivityMapsBinding
import com.example.submission_storyapp.view.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val boundsBuilder = LatLngBounds.Builder()
    private val viewModel: MapsViewModel by viewModels() {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  SupportMapFragment dan pemberitahuan saat peta siap digunakan.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        showMarkers()
        setMapStyle()
        getMyLocation()
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
            if (!success) {
                Log.e("Map Style", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("Map Style", "Can't find style. Error: ", exception)
        }
    }

    private fun showMarkers() {
        viewModel.getStoriesWithLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> showLoading(true)

                    is Result.Success -> {
                        val data = result.data.listStory
                        data.forEach { datas ->
                            val latLng = LatLng(datas.lat, datas.lon)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(datas.name)
                                    .snippet(datas.description)
                            )
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            boundsBuilder.include(latLng)

                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    300
                                )
                            )
                        }
                        Log.d("MapsActivity", result.data.listStory.toString())
                        showLoading(false)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        Log.d("MapsActivity", result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}