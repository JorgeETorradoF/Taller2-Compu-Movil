package com.example.taller2compumovil

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import android.Manifest
import android.location.Location
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment

class MapActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var map: FrameLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var alerts :Alerts
    private var currentLoc: Location = Location("default").apply {
        latitude = 0.0
        longitude = 0.0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)
        map = findViewById(R.id.map)
        Toast.makeText(this, "Remember to turn on your device's location for this functionality to work :)", Toast.LENGTH_LONG).show()
        var mapFragment : SupportMapFragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.lastLocation?.let { loc ->
                    currentLoc = loc
                    if (::mMap.isInitialized) {
                        updateMap()
                    }
                }
            }
        }
        startLocationUpdates()
        mapFragment.getMapAsync(this)

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (currentLoc != null) {
            updateMap()
        }
    }

    private fun updateMap() {
        mMap.clear()
        val currentPos = LatLng(currentLoc!!.latitude, currentLoc!!.longitude)
        mMap.addMarker(MarkerOptions().position(currentPos).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos))
    }
    private fun startLocationUpdates() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

}