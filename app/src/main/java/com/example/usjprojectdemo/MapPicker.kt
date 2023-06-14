package com.example.usjprojectdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.usjprojectdemo.databinding.ActivityMapPickerBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapPicker : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapPickerBinding
    private var latlng : LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var macau = LatLng(22.160633878316617,113.55766072869301)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(macau, 13f))
        val marker = mMap.addMarker(MarkerOptions().position(macau))


        mMap.setOnCameraMoveListener {
            val newPos = mMap.cameraPosition.target
            Log.d("test",newPos.toString())
            marker?.position = newPos
            latlng = marker?.position
        }

        findViewById<Button>(R.id.pickLocationButton).setOnClickListener{
            passLatLng()
        }

    }

    private fun passLatLng(){
        val result = Intent()
        result.putExtra("lat",latlng?.latitude)
        result.putExtra("lng",latlng?.longitude)
        setResult(RESULT_OK,result)
        finish()
    }



}