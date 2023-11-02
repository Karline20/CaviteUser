package coding.legaspi.caviteuser.utils

import android.app.Activity

object PermissionHelperFactory {
    fun create(activity: Activity): PermissionHelper = PermissionHelper(activity)
}