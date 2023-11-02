package coding.legaspi.caviteuser.utils

import android.app.Activity

object ImagePickerHelperFactory {
    fun create(activity: Activity): ImagePickerHelper = ImagePickerHelper(activity)
}