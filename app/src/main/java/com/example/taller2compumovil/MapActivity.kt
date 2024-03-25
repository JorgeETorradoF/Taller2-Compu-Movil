package com.example.taller2compumovil
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.widget.SearchView
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.IOException

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var polylineOptions = PolylineOptions()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)
        Toast.makeText(this, "Remember to turn on your device's location for this functionality to work", Toast.LENGTH_LONG).show()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val mapSearch = findViewById<SearchView>(R.id.mapSearch)
        mapSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location = mapSearch.query.toString()
                var addressList: List<Address>? = null

                if (location.isNotEmpty()) {
                    val geocoder = Geocoder(this@MapActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    if (addressList != null && addressList.isNotEmpty()) {
                        mMap.clear()
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        mMap.addMarker(MarkerOptions().position(latLng).title(location))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
                    }
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true

        polylineOptions = PolylineOptions().width(7f).color(Color.BLUE)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) {
                    updateMap(LatLng(location.latitude, location.longitude))
                }
            }
        }

        startLocationUpdates()
    }

    private fun updateMap(latLng: LatLng) {
        if (findViewById<SearchView>(R.id.mapSearch).query.isNullOrEmpty())
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
        }
        polylineOptions.add(latLng)
        mMap.addPolyline(polylineOptions)
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap)
            }
        }
    }
}
