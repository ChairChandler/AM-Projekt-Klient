package com.project.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.project.HospitalManager
import com.project.R
import com.project.models.Hospital
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.pow
import kotlin.math.sqrt

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val apiKey: String = R.string.google_maps_key.toString()
    private val hospitalsPositionMap: HashMap<LatLng, Hospital> = HashMap()

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
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                userLocalisation = p0.lastLocation
            }
        }
        //HospitalManager.addComment("Komentarz", "Ala", HospitalManager.SAMPLE_HOSPITAL_ID)

        for (info in HospitalManager.downloadHospitalData()) {
            HospitalManager.hospitals.add(info)
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

        val icon = bitmapDescriptorFromVector(this, R.drawable.ic_hospital_ico)

        // add hospitals makers
        for (hospital in HospitalManager.hospitals) {
            val place = LatLng(hospital.location.lat, hospital.location.lng)
            hospitalsPositionMap[place] = hospital
            //val icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            mMap.addMarker(MarkerOptions().position(place).icon(icon))
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
        val selectedHospitalInfo = hospitalsPositionMap[p0?.position]

        AlertDialog.Builder(this)
            .setMessage("${selectedHospitalInfo?.description}\n\nWhat do you want to know?")
            .setTitle(selectedHospitalInfo?.name)
            .setPositiveButton(
                "Information"
            ) { _, _ ->
                val intent = Intent(this, HospitalInfoActivity::class.java)

                intent.putExtra("info", selectedHospitalInfo)
                startActivity(intent)
            }.setNegativeButton(
                "Route"
            ) { _, _ ->
                var from = LatLng(userLocalisation.latitude, userLocalisation.longitude)
                //var from = LatLng(p0!!.position.latitude - 20, p0!!.position.longitude);
                //var to = p0!!.position
                var to = LatLng(37.773972, -120.431297)
                this.createRoute(from, to)
            }.show()
        return true
    }

    private fun createRoute(from: LatLng, to: LatLng) {
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections =
            "http://www.yournavigation.org/api/1.0/gosmore.php?flat=${from.latitude}&flon=${longitudeArgCorrection(
                from.longitude
            )}&tlat=${to.latitude}&tlon=${longitudeArgCorrection(to.longitude)}&format=geojson"
        println(urlDirections)
        val directionsRequest = object :
            StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                // Get routes
                val routes = jsonResponse.getJSONArray("coordinates")
                for (i in 0 until routes.length()) {
                    val latLngArray = routes[i] as JSONArray
                    val latLng = LatLng(latLngArray.get(0) as Double, latLngArray.get(1) as Double)
                    path.add(listOf(latLng))
                }
                for (i in 0 until path.size) {
                    mMap!!.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
                }
                mMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            path[0][0] as Double,
                            path[0][1] as Double
                        ), 12f
                    )
                )
            }, Response.ErrorListener { _ ->
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }

    private fun longitudeArgCorrection(longitude: Double): String =
        if (longitude < 0) "\\\\${longitude}" else longitude.toString()

    private fun calcDistance(a: LatLng, b: LatLng): Double {
        return sqrt((a.latitude - b.latitude).pow(2) + (a.longitude - b.longitude).pow(2))
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