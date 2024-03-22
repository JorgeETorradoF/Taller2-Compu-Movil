package com.example.taller2compumovil

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.Manifest
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.taller2compumovil.databinding.CameraActivityBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: CameraActivityBinding
    private lateinit var picture: ImageView
    private lateinit var video: VideoView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_VIDEO_CAPTURE = 2
    private val PERM_CAMERA_CODE = 101
    private val PERM_GALLERY_PICTURES = 102
    private val PERM_GALLERY_VIDEOS = 103
    private val REQUEST_PICTURE_FROM_GALLERY = 3
    private val REQUEST_VIDEO_FROM_GALLERY = 4
    private val alerts = Alerts(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val takeButton = binding.buttonTake
        picture = binding.picture
        video = binding.video
        val galleryButton = binding.buttonPick
        val videoSwitch = binding.videoSwitch

        takeButton.setOnClickListener {
            val isVideo = videoSwitch.isChecked
            var canAccessCamera = false
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    canAccessCamera = true
                }

                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    alerts.indefiniteSnackbar(
                        binding.root,
                        "El permiso de Camara es necesario para usar esta actividad 😭"
                    )
                }

                else -> {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), PERM_CAMERA_CODE)
                }
            }
            if(canAccessCamera)
            {
                if (isVideo)
                {
                    val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    startActivityForResult(videoIntent, REQUEST_VIDEO_CAPTURE)
                    picture.visibility = android.view.View.GONE
                    video.visibility = android.view.View.VISIBLE

                }
                else
                {
                    val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(imageCaptureIntent, REQUEST_IMAGE_CAPTURE)
                    picture.visibility = android.view.View.VISIBLE
                    video.visibility = android.view.View.GONE
                }
            }
        }

        galleryButton.setOnClickListener {
            var canAccessGallery = false
            val isVideo = videoSwitch.isChecked
            var galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

            if (ContextCompat.checkSelfPermission(this, galleryPermission) == PackageManager.PERMISSION_GRANTED) {
                canAccessGallery = true
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this, arrayOf(galleryPermission), PERM_GALLERY_PICTURES)
            }

            galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_VIDEO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

            if (ContextCompat.checkSelfPermission(this, galleryPermission) == PackageManager.PERMISSION_GRANTED) {
                canAccessGallery = true
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this, arrayOf(galleryPermission), PERM_GALLERY_VIDEOS)

            }

            if(canAccessGallery)
            {
                val intentPick = Intent(Intent.ACTION_PICK)
                intentPick.type = if (isVideo) "video/*" else "image/*"
                val code = if(isVideo)  REQUEST_VIDEO_FROM_GALLERY else REQUEST_PICTURE_FROM_GALLERY
                startActivityForResult(intentPick, code)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    picture.setImageBitmap(imageBitmap)
                    picture.visibility = android.view.View.VISIBLE
                    video.visibility = android.view.View.GONE
                }
                REQUEST_VIDEO_CAPTURE -> {
                    val videoUri = data?.data
                    video.setVideoURI(videoUri)
                    video.visibility = android.view.View.VISIBLE
                    picture.visibility = android.view.View.GONE
                    video.start()
                }
                REQUEST_PICTURE_FROM_GALLERY -> {
                    val imageUri = data?.data
                    picture.setImageURI(imageUri)
                    picture.visibility = android.view.View.VISIBLE
                    video.visibility = android.view.View.GONE
                }
                REQUEST_VIDEO_FROM_GALLERY -> {
                    val videoUri = data?.data
                    video.setVideoURI(videoUri)
                    video.visibility = android.view.View.VISIBLE
                    picture.visibility = android.view.View.GONE
                    video.start()
                }
            }
        }
    }
}
