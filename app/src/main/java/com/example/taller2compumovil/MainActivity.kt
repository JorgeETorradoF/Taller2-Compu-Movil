package com.example.taller2compumovil

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.Manifest
import androidx.core.content.ContextCompat
import com.example.taller2compumovil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val PERM_LOCATION_CODE = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cameraButton = binding.cameraButton
        val mapButton = binding.mapButton
        val alerts = Alerts(this)

        cameraButton.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        mapButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startActivity(Intent(this, MapActivity::class.java))
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    alerts.indefiniteSnackbar(
                        binding.root,
                        "Location permission is required to access this functionality 😭"
                    )
                }
                else -> {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERM_LOCATION_CODE)
                }
            }
        }
    }
}