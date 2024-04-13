package coding.legaspi.caviteuser.utils

import android.content.Context
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.error.Unauthorized

interface DialogHelper {

    fun showUnauthorized(error: Error, positiveButtonFunction: () -> Unit)

    fun showError(error: Error)

    fun showSuccess(title: String, content: String)

    fun showEmailVerification(title: String, content: String)

    fun showLogout(
        title: String,
        content: String,
        confirm: String,
        cancel: String,
        callback: (Boolean) -> Unit
    )

    fun thanksSuccess(title: String, content: String, callback: (Boolean) -> Unit)

    fun already(title: String, content: String)

    fun tutorial(title: String,
                 content: String,
                 confirm: String,
                 cancel: String,
                 callback: (Boolean) -> Unit)

    fun play(context: Context, title: String, content: String, confirm: String, cancel: String,
             positiveButtonFunction: () -> Unit,
             negativeButtonFunction: () -> Unit,
             leaderBoardsButtonFunction: () -> Unit)

    fun wrong(title: String,
              content: String,
              confirm: String,
              cancel: String,
              callback: (Boolean) -> Unit)

    fun delete(title: String,
              content: String,
              confirm: String,
              cancel: String,
              callback: (Boolean) -> Unit)

    fun connection(title: String,
              content: String,
              confirm: String,
              callback: (Boolean) -> Unit)
}