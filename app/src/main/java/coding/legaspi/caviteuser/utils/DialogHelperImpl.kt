package coding.legaspi.caviteuser.utils

import android.content.Context
import android.content.Intent
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.profile.ProfileImage
import coding.legaspi.caviteuser.presentation.auth.LoginActivity
import com.marsad.stylishdialogs.StylishAlertDialog

class DialogHelperImpl(private val context: Context) : DialogHelper {

    override fun showUnauthorized(error: Error) {
        StylishAlertDialog(context, StylishAlertDialog.ERROR)
            .setTitleText(error.name)
            .setContentText(error.message)
            .show()

    }

    override fun showError(error: Error) {
        StylishAlertDialog(context, StylishAlertDialog.ERROR)
            .setTitleText(error.name)
            .setContentText(error.message)
            .setCancellable(false)
            .setConfirmClickListener {
                it.dismiss()
            }
            .show()
    }

    override fun showSuccess(title: String, content: String) {
        StylishAlertDialog(context, StylishAlertDialog.SUCCESS)
            .setTitleText(title)
            .setContentText(content)
            .show()
    }

    override fun showEmailVerification(title: String, content: String) {
        StylishAlertDialog(context, StylishAlertDialog.SUCCESS)
            .setTitleText(title)
            .setContentText(content)
            .setCancellable(false)
            .show()
    }

    override fun showLogout(title: String, content: String, confirm: String, cancel: String, callback: (Boolean) -> Unit) {
        StylishAlertDialog(context, StylishAlertDialog.WARNING)
            .setTitleText(title)
            .setContentText(content)
            .setConfirmText(confirm)
            .setCancelText(cancel)
            .setConfirmClickListener{
                callback(true)
            }
            .setCancelClickListener {
                callback(false)
                it.cancel()
            }
            .show()
    }

    override fun thanksSuccess(
        title: String, content: String, callback: (Boolean) -> Unit
    ) {
        StylishAlertDialog(context, StylishAlertDialog.SUCCESS)
            .setTitleText(title)
            .setContentText(content)
            .setConfirmClickListener {
                callback(true)
            }
            .setCancellable(false)
            .show()
    }

    override fun already(
        title: String, content: String) {
        StylishAlertDialog(context, StylishAlertDialog.WARNING)
            .setTitleText(title)
            .setContentText(content)
            .setConfirmClickListener {
                it.dismiss()
            }
            .setCancellable(false)
            .show()
    }

    override fun tutorial(title: String, content: String, confirm: String, cancel: String, callback: (Boolean) -> Unit) {
        StylishAlertDialog(context, StylishAlertDialog.SUCCESS)
            .setTitleText(title)
            .setContentText(content)
            .setConfirmText(confirm)
            .setCancelText(cancel)
            .setConfirmClickListener {
                callback(true)
                it.dismiss()
            }
            .setCancelClickListener {
                callback(false)
                it.cancel()
            }
            .setCancellable(false)
            .show()
    }

    override fun wrong(
        title: String,
        content: String,
        confirm: String,
        cancel: String,
        callback: (Boolean) -> Unit
    ) {
        StylishAlertDialog(context, StylishAlertDialog.ERROR)
            .setTitleText(title)
            .setContentText(content)
            .setConfirmText(confirm)
            .setConfirmClickListener {
                callback(true)
                it.dismiss()
            }
            .setCancelClickListener {
                callback(false)
                it.setCancelButton(cancel, StylishAlertDialog::dismissWithAnimation)
            }
            .setCancellable(false)
            .show()
    }

    override fun delete(title: String, content: String, confirm: String, cancel: String, callback: (Boolean) -> Unit){
        StylishAlertDialog(context, StylishAlertDialog.WARNING)
            .setTitleText(title)
            .setContentText(content)
            .setConfirmText(confirm)
            .setConfirmClickListener {
                callback(true)
                it.dismiss()
            }
            .setCancelClickListener {
                callback(false)
            }
            .setCancelButton(cancel, StylishAlertDialog::dismissWithAnimation)
            .setCancellable(false)
            .show()
    }

    override fun connection(
        title: String,
        content: String,
        confirm: String,
        callback: (Boolean) -> Unit
    ) {
        StylishAlertDialog(context, StylishAlertDialog.ERROR)
            .setTitleText(title)
            .setContentText(content)
            .setConfirmText(confirm)
            .setConfirmClickListener {
                callback(true)
            }
            .setCancellable(false)
            .show()
    }

}