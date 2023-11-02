package coding.legaspi.caviteuser.presentation.home.map.route

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.view.WindowManager
import androidx.core.content.ContextCompat
import coding.legaspi.caviteuser.R
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class EmbeddedRoute() {

    fun embeddedDirection(myMap: GoogleMap, context: Context, destination: String, myLocation: String, windowManager: WindowManager): Boolean {
        try {
            val requestQueue = Volley.newRequestQueue(context)
            val url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", destination)
                .appendQueryParameter("origin", myLocation)
                .appendQueryParameter("mode", "driving")
                .appendQueryParameter("key", R.string.my_map_api_key.toString())
                .toString()

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    val status = response.getString("status")
                    if (status == "OK") {
                        val routes = response.getJSONArray("routes")
                        val points: ArrayList<LatLng> = ArrayList()
                        var polylineOptions: PolylineOptions? = null

                        for (i in 0 until routes.length()) {
                            points.clear()
                            polylineOptions = PolylineOptions()
                            val legs = routes.getJSONObject(i).getJSONArray("legs")
                            for (j in 0 until legs.length()) {
                                val steps = legs.getJSONObject(j).getJSONArray("steps")
                                for (k in 0 until steps.length()) {
                                    val polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points")
                                    val list = decodePoly(polyline)
                                    for (l in list.indices) {
                                        val position = LatLng(list[l].latitude, list[l].longitude)
                                        points.add(position)
                                    }
                                }
                            }

                            polylineOptions.addAll(points)
                            polylineOptions.width(19f)
                            polylineOptions.color(ContextCompat.getColor(context, R.color.owtoRed))
                            polylineOptions.geodesic(true)
                        }

                        myMap.addPolyline(polylineOptions!!)
                        myMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(-6.9249233, 107.6345122))
                                .title("Custom Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )

                        myMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(-6.9218571, 107.6048254))
                                .title(myLocation)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )

                        val bounds = LatLngBounds.Builder()
                            .include(LatLng(-6.9249233, 107.6345122))
                            .include(LatLng(-6.9218571, 107.6048254))
                            .build()
                        val point = Point()
                        windowManager.defaultDisplay.getSize(point)
                        myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30))
                    }
                },
                { error ->
                }
            )
            val retryPolicy = DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            jsonObjectRequest.retryPolicy = retryPolicy
            requestQueue.add(jsonObjectRequest)

            return true
        } catch (e: SecurityException) {
            e.printStackTrace()
            return false
        }
    }


    fun decodePoly(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if ((result and 1) != 0) -(result ushr 1) else result ushr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b > 0x20)

            val dlng = if ((result and 1) != 0) -(result ushr 1) else result ushr 1
            lng += dlng

            val latitude = lat.toDouble() / 1E5
            val longitude = lng.toDouble() / 1E5

            val point = LatLng(latitude, longitude)
            poly.add(point)
        }

        return poly
    }

}