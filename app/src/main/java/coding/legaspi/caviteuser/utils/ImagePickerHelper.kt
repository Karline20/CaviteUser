package coding.legaspi.caviteuser.utils
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagePickerHelper(private val activity: Activity) {
    private var currentPhotoPath: String? = null

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }

    fun openCameraOrGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val packageManager = activity.packageManager
        val resolvedActivityList = packageManager.queryIntentActivities(galleryIntent, 0)
        val isCameraAvailable = resolvedActivityList.isNotEmpty()

        val photoFile: File? = createImageFile() // Ensure createImageFile is called first

        if (isCameraAvailable && photoFile != null) {
            // Add both camera and gallery options
            val chooserIntent = Intent.createChooser(intent, "Select Image")
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                arrayOf(getCameraIntent(photoFile))
            )
            activity.startActivityForResult(chooserIntent, REQUEST_IMAGE_PICK)
        } else {
            activity.startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }


    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    private fun getCameraIntent(photoFile: File): Intent {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoURI: Uri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            photoFile
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        return cameraIntent
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Camera photo was taken
                    return Uri.fromFile(File(currentPhotoPath ?: ""))
                }
                REQUEST_IMAGE_PICK -> {
                    // Photo was picked from gallery
                    return data?.data
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Handle the case where the user canceled the operation
            // You can add your own logic here, like showing a message to the user
        }
        return null
    }

}
