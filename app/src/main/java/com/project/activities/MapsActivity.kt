package com.project.activities

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.project.HospitalManager
import com.project.R
import kotlin.math.pow
import kotlin.math.sqrt

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var userLocalisation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //TODO w ten sposób przełączać na dodawanie komentarza
        val i = Intent(this, AddingScoreActivity::class.java)
        val hospital = HospitalManager.downloadHospitalFullData(HospitalManager.SAMPLE_HOSPITAL_ID)
        i.putExtra("name", hospital.name)
        i.putExtra("id", hospital.id)
        startActivity(i)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                userLocalisation = p0.lastLocation
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        this.createLocationRequest()
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

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
            return ContextCompat.getDrawable(context, vectorResId)?.run {
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val bitmap =
                    Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
                draw(Canvas(bitmap))
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
        }

        val icon = bitmapDescriptorFromVector(this,
            R.drawable.ic_hospital_ico
        )
        // add hospitals makers
        for (pos in HospitalManager.hospitals) {
            val place = LatLng(pos.location.lat, pos.location.lng)
            //val icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            mMap.addMarker(
                MarkerOptions().position(place).title(pos.name).snippet(pos.description).icon(icon)
            )
        }

        this.setUpMap()
    }

    private fun setUpMap() {
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                userLocalisation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        AlertDialog.Builder(this)
            .setMessage("What do you want to know?")
            .setTitle("Hospital")
            .setPositiveButton(
                "Information",
                DialogInterface.OnClickListener { _, _ ->

                }
            ).setNegativeButton(
                "Route",
                DialogInterface.OnClickListener { _, _ ->
                    this.createRouteSounds(p0!!)
                }
            ).show()
        return true
    }

    private fun createRouteSounds(p0: Marker) {
        var from = LatLng(userLocalisation.latitude, userLocalisation.longitude)
        var to = p0.position

        val baseDist = calcDistance(from, to)
        val baseTime: Long = 1000 //ms
        var time: Long = baseTime
        fun repeat() {
            Handler().postDelayed({
                this.playSound()
                from = LatLng(userLocalisation.latitude, userLocalisation.longitude)
                to = p0.position
                val dist = calcDistance(from, to)
                val percentDist = dist / baseDist
                if (percentDist < 0.05) {
                    AlertDialog.Builder(this)
                        .setMessage("You have arrived!")
                        .setTitle("Please strict to the health rules.")
                        .setPositiveButton(
                            "Close",
                            DialogInterface.OnClickListener { _, _ ->

                            }
                        ).show()
                } else {
                    time = (baseTime * percentDist).toLong()
                    repeat()
                }
            }, time)
        }

        repeat()
    }

    private fun calcDistance(a: LatLng, b: LatLng): Double {
        return sqrt((a.latitude - b.latitude).pow(2) + (a.longitude - b.longitude).pow(2))
    }

    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI)
        mediaPlayer.start()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        this@MapsActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }
}