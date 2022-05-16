package com.example.alertia

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.alertia.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val pERMISSION_ID = 42
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var vibrator : Vibrator
    private lateinit var ringtone : Ringtone
    private var kilometers = 0.0
    private var startLocation = "null"
    private var endLocation = "null"

    private var currentLocation: LatLng = LatLng(20.5, 78.9)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        val apiKey = getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        val placesClient = Places.createClient(this)

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onError(status: Status) {
                Log.i("TAG", "An error occurred: $status")
            }

            override fun onPlaceSelected(place: Place) {
                Log.i("TAG", "Place: ${place.name}, ${place.id}")
            }
        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.currLoc.setOnClickListener {
            getLastLocation()
        }

        binding.stopRemainder.setOnClickListener {
            binding.remainderDialog.visibility = View.GONE
            binding.stopRemainder.visibility = View.GONE
            vibrator.cancel()
            ringtone.stop()
            binding.remainderText.setText(R.string.relax_we_ll_notify_you_when_you_reach_the_destination)
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(currentLocation)
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLastLocation()
        mMap.setOnMapLongClickListener {
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(it)
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16F))

            val result = FloatArray(1)
            Location.distanceBetween(it.latitude , it.longitude , currentLocation.latitude , currentLocation.longitude , result)
            Log.i("Distance--" , "${result[0]}")
            kilometers = String.format("%.2f", result[0]/1000.0).toDouble()

            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses : List<Address> = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
            val addresses1 : List<Address> = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            startLocation = if(addresses.isNotEmpty())
                addresses[0].locality
            else
                "Current Location"

            endLocation = if(addresses1.isNotEmpty())
                addresses1[0].locality
            else
                "Pin Drop Location"


            getBottomSheet()
        }
    }

    private fun getLastLocation() {
        if(checkPermission()){
            if(isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(currentLocation))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
                    }
                }
            }
            else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else {
            requestPermissions()
        }
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if(checkPermission())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
        else{
            requestPermissions()
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            pERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == pERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun destinationReached(){

        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(1000,1000,1000,1000) , intArrayOf(1,0,1,0) , 0 ))

        var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        // setting default ringtone
        ringtone = RingtoneManager.getRingtone(this, alarmUri)
        ringtone.isLooping = true

        // play ringtone
        ringtone.play()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getBottomSheet(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet)

        val kilometerCount = dialog.findViewById<TextView>(R.id.kilometerCount)
        val startLoc = dialog.findViewById<TextView>(R.id.startLocation)
        val endLoc = dialog.findViewById<TextView>(R.id.endLocation)
        val startRemainder = dialog.findViewById<Button>(R.id.setRemainder)

        startRemainder.setOnClickListener {
            if(kilometers<=0.1) {
                binding.remainderText.text = "You have reached your destination."
                binding.stopRemainder.visibility = View.VISIBLE
                destinationReached()
            }
            binding.remainderDialog.visibility = View.VISIBLE
            dialog.dismiss()
        }

        kilometerCount.text = kilometers.toString()
        startLoc.text = startLocation
        endLoc.text = endLocation

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations  = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }




}
