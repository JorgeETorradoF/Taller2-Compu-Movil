package com.example.taller2compumovil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.Button
import com.example.taller2compumovil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cameraButton = binding.cameraButton
        val mapButton = binding.mapButton
        cameraButton.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        mapButton.setOnClickListener {
            //startActivity(Intent(this, MapActivity::class.java))
        }
    }
}