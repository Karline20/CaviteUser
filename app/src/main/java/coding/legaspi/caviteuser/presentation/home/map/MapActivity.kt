package coding.legaspi.caviteuser.presentation.home.map

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coding.legaspi.caviteuser.BuildConfig
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.databinding.ActivityMapBinding
import coding.legaspi.caviteuser.presentation.home.map.adapter.CustomInfoWindowAdapter
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute
import com.bumptech.glide.Glide
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.draw_path.DirectionUtil
import com.example.easywaylocation.draw_path.PolyLineDataBean
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import kotlin.math.pow
import com.example.easywaylocation.Listener
import com.google.android.material.snackbar.Snackbar


class MapActivity : AppCompatActivity(){

    companion object {
        private const val serverKey = "AIzaSyCfL4ZVlbCeWADMD0gXSzNSFWq17vtRm5c"
        //private val origin = LatLng(37.7849569, -122.4068855)
        //private val destination = LatLng(14.637975,121.0232631)
    }

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

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapBinding = ActivityMapBinding.inflate(layoutInflater)
        val view = mapBinding.root
        setContentView(view)
        dialogHelper = DialogHelperFactory.create(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        mapBinding.loggedInTopNav.labelTitle.text = "Routes"
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync { googleMap ->
            this.googleMap = googleMap
        }

        latitude = intent.getStringExtra("latitude").toString()
        longitude = intent.getStringExtra("longitude").toString()
        location = intent.getStringExtra("location").toString()

        setProfile()
    }


    private fun setButton() {
        mapBinding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
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
//                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
//                mapFragment?.getMapAsync(this)
                if(currentLocation != null){
                    mapBinding.progressBar.visibility = GONE
                    val myLocation = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                    val customLocation = LatLng(latitude.toDouble(), longitude.toDouble())
                    //val customLocation = LatLng(14.637975,121.0232631)
                    requestDirection(myLocation, customLocation)
                    setButton()
                }
            }
        }
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


    private fun requestDirection(origin: LatLng, destination: LatLng) {
        showSnackbar(getString(R.string.direction_requesting))
        GoogleDirectionConfiguration.getInstance().isLogEnabled = BuildConfig.DEBUG
        GoogleDirection.withServerKey(serverKey)
            .from(origin)
            .to(destination)
            .transportMode(TransportMode.DRIVING)
            .execute(
                onDirectionSuccess = { direction -> onDirectionSuccess(direction, origin, destination) },
                onDirectionFailure = { t -> onDirectionFailure(t) }
            )
    }

    private fun onDirectionSuccess(direction: Direction?, origin: LatLng, destination: LatLng) {
        val myLocationBitmap = vectorToBitmap(R.drawable.baseline_person_pin_24)
        val customLocationBitmap = vectorToBitmap(R.drawable.baseline_place_24)
        direction?.let {
            showSnackbar(getString(R.string.success_with_status, direction.status))
            if (direction.isOK) {
                val route = direction.routeList[0]
                // ...
                // Add this line to get the distance in meters
                val distanceInMeters = route.legList[0].distance.value
                // Convert meters to kilometers
                val distanceInKilometers = distanceInMeters / 1000f
                // Display the distance
                //showSnackbar("Distance: $distanceInKilometers")
                mapBinding.tvKm.text = "$distanceInKilometers kilometers"

                val myLocationMarker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(origin)
                        .icon(BitmapDescriptorFactory.fromBitmap(myLocationBitmap))
                        .title("My Location")
                )
                val customLocationMarker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(destination)
                        .icon(BitmapDescriptorFactory.fromBitmap(customLocationBitmap))
                        .title(location)
                )
                myLocationMarker?.showInfoWindow()
                customLocationMarker?.showInfoWindow()

                val directionPositionList = route.legList[0].directionPoint
                googleMap?.addPolyline(
                    DirectionConverter.createPolyline(
                        this,
                        directionPositionList,
                        10,
                        getColor(R.color.colorPrimary)
                    )
                )

                setCameraWithCoordinationBounds(route)
            } else {
                showSnackbar(direction.status)
            }
        } ?: run {
            showSnackbar(getString(R.string.success_with_empty))
        }
    }

    private fun onDirectionFailure(t: Throwable) {
        Log.e("MAPROUTER", "message ${t.message}")
        showSnackbar(t.message)
    }

    private fun setCameraWithCoordinationBounds(route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

    }

    private fun showSnackbar(message: String?) {
        if(message == "REQUEST_DENIED") {
            dialogHelper.wrong(
                "Routing error!",
                "Please select your route provider!",
                "Google Maps",
                "Cancel"
            ) {
                if (it){
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = (Uri.parse("http://maps.google.com/maps?daddr=${latitude.toDouble()},${longitude.toDouble()}"))
                    startActivity(intent)
                }
            }
        }
        message?.let {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
        }
    }

}