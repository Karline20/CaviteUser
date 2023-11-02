package coding.legaspi.caviteuser.presentation.home.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.databinding.ActivityMapBinding
import coding.legaspi.caviteuser.presentation.home.map.adapter.CustomInfoWindowAdapter
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import kotlin.math.pow


class MapActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mapBinding: ActivityMapBinding
    private lateinit var dialogHelper: DialogHelper
    private lateinit var myMap: GoogleMap
    private val FINE_PERMISSION_CODE = 1
    var currentLocation: Location? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var location: String
    lateinit var imageProfile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapBinding = ActivityMapBinding.inflate(layoutInflater)
        val view = mapBinding.root
        setContentView(view)
        dialogHelper = DialogHelperFactory.create(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        latitude = intent.getStringExtra("latitude").toString()
        longitude = intent.getStringExtra("longitude").toString()
        location = intent.getStringExtra("location").toString()

        setProfile()
    }

    private fun setButton() {
        val myLocation = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        mapBinding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }

        mapBinding.googleMaps.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = (Uri.parse("http://maps.google.com/maps?daddr=$latitude,$longitude"))
            startActivity(intent)
        }

        mapBinding.waze.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = (Uri.parse("waze://?ll=$latitude,$longitude&navigate=yes"))
            startActivity(intent)
        }
    }

    private fun setProfile() {
        mapBinding.progressBar.visibility = VISIBLE
        val (token, userId) = SharedPreferences().checkToken(this)
        if (userId != null){
            FirebaseManager().fetchProfileFromFirebase(userId){
                if (it!=null){
                    Glide.with(this)
                        .load(it.imageUri)
                        .placeholder(R.drawable.baseline_broken_image_24)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(mapBinding.loggedInTopNav.imgProfile)

                    imageProfile = it.imageUri.toString()
                    try {
                        getLastLocation()
                    }catch (e: Exception){
                        dialogHelper.connection("Internet Connection", "Check your internet connection!", "Yes", ){yes ->
                            if (yes){
                                getLastLocation()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getLastLocation() {
        Log.d("getLastLocation", "checkSelfPermission")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_PERMISSION_CODE)
            return
        }

        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null){
                Log.d("getLastLocation", "task $it")
                currentLocation = it
                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                mapFragment?.getMapAsync(this)
                if(currentLocation != null){
                    mapBinding.progressBar.visibility = GONE
                    setButton()
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        val myLocation = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)

        val customLocation = LatLng(latitude.toDouble(), longitude.toDouble())

        val distanceInKm = calculateDistanceInKm(
            myLocation.latitude,
            myLocation.longitude,
            customLocation.latitude,
            customLocation.longitude
        )

        mapBinding.tvKm.text = "$distanceInKm km"
        //Log.d("Distance", "Distance between locations: $distanceInKm km")

//        val isTrue = EmbeddedRoute().embeddedDirection(myMap, this,customLocation.toString(), myLocation.toString(), windowManager)
//        if (isTrue){
//
//        }
       // Log.d("isTrue", "$isTrue")
        val myLocationBitmap = vectorToBitmap(R.drawable.baseline_person_pin_24)
        val customLocationBitmap = vectorToBitmap(R.drawable.baseline_place_24)

        val myLocationMarker = myMap.addMarker(
            MarkerOptions()
                .position(myLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(myLocationBitmap))
                .title("My Location")
        )

        val customLocationMarker = myMap.addMarker(
            MarkerOptions()
                .position(customLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(customLocationBitmap))
                .title(location)
        )

        myLocationMarker?.showInfoWindow()
        customLocationMarker?.showInfoWindow()

        val customInfoWindowAdapter = CustomInfoWindowAdapter(this)
        myMap.setInfoWindowAdapter(customInfoWindowAdapter)

        val builder = LatLngBounds.Builder()
        builder.include(myLocation)
        builder.include(customLocation)
        val bounds = builder.build()
        val padding = 200

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)

        myMap.moveCamera(cu)

        myMap.uiSettings.isZoomControlsEnabled = true
        myMap.uiSettings.isCompassEnabled = true
        myMap.uiSettings.isZoomGesturesEnabled = true
        myMap.uiSettings.isScrollGesturesEnabled = true

//        val routePoints = mutableListOf<LatLng>()
//        routePoints.add(myLocation)
//        routePoints.add(customLocation)
//
//        val polylineOptions = PolylineOptions()
//            .addAll(routePoints)
//            .color(Color.BLUE)
//            .width(5f)
//
//        myMap.addPolyline(polylineOptions)
    }

    fun calculateDistanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radiusOfEarth = 6371 // Earth's radius in kilometers

        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad

        val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radiusOfEarth * c
    }

    fun vectorToBitmap(vectorDrawable: Int): Bitmap {
        val vector = VectorDrawableCompat.create(resources, vectorDrawable, null)
            ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        val bitmap = Bitmap.createBitmap(
            vector.intrinsicWidth,
            vector.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vector.setBounds(0, 0, canvas.width, canvas.height)
        vector.draw(canvas)
        return bitmap
    }
}