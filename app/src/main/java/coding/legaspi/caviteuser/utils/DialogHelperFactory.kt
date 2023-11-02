package coding.legaspi.caviteuser.utils

import android.content.Context

object DialogHelperFactory {
    fun create(context: Context): DialogHelper = DialogHelperImpl(context)
}