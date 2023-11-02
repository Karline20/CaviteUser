package coding.legaspi.caviteuser.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHelper(private val activity: Activity) {

    // Callback to notify the result of permission request
    interface PermissionCallback {
        fun onPermissionGranted()
        fun onPermissionDenied()
    }

    // Request code for camera and gallery permissions
    private val CAMERA_PERMISSION_REQUEST = 101
    private val GALLERY_PERMISSION_REQUEST = 102

    // Check if the camera permission is granted
    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Check if the gallery permission is granted
    fun isGalleryPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission
    fun requestCameraPermission(callback: PermissionCallback) {
        if (!isCameraPermissionGranted()) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            callback.onPermissionGranted()
        }
    }

    // Request gallery permission
    fun requestGalleryPermission(callback: PermissionCallback) {
        if (!isGalleryPermissionGranted()) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_PERMISSION_REQUEST
            )
        } else {
            callback.onPermissionGranted()
        }
    }

    // Handle permission request results
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        callback: PermissionCallback
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callback.onPermissionGranted()
                } else {
                    callback.onPermissionDenied()
                }
            }
            GALLERY_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callback.onPermissionGranted()
                } else {
                    callback.onPermissionDenied()
                }
            }
        }
    }
}
