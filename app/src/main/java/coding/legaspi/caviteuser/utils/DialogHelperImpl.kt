package coding.legaspi.caviteuser.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.profile.ProfileImage
import coding.legaspi.caviteuser.presentation.auth.LoginActivity
import com.marsad.stylishdialogs.StylishAlertDialog

class DialogHelperImpl(private val context: Context) : DialogHelper {

    override fun showUnauthorized(error: Error, positiveButtonFunction: () -> Unit) {
        StylishAlertDialog(context, StylishAlertDialog.ERROR)
            .setTitleText(error.name)
            .setContentText(error.message)
            .setConfirmText("Okay, try again")
            .setConfirmClickListener {
                positiveButtonFunction.invoke()
                it.dismiss()
            }
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
    override fun play(context: Context, title: String, content: String, confirm: String, cancel: String,
                      positiveButtonFunction: (Boolean) -> Unit,
                      negativeButtonFunction: () -> Unit,
                      leaderBoardsButtonFunction: () -> Unit) {

        val dialog = Dialog(context, R.style.alert_dialog_light)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)

        val dialogTitle: TextView = dialog.findViewById(R.id.title_text)
        val dialogDescription: TextView = dialog.findViewById(R.id.content_text)
        val dialogPositiveButton: Button = dialog.findViewById(R.id.confirm_button)
        val dialogNegativeButton: Button = dialog.findViewById(R.id.cancel_button)
        val leaderBoardsButton: Button = dialog.findViewById(R.id.leaderboards_button)

        dialogTitle.text = title
        dialogDescription.text = content

        confirm.let {
            dialogPositiveButton.text = it
        }
        cancel.let {
            dialogNegativeButton.text = it
        }

        dialogPositiveButton.setOnClickListener {
            positiveButtonFunction.invoke(true)
            dialog.dismiss()
        }
        dialogNegativeButton.setOnClickListener {
            negativeButtonFunction.invoke()
            dialog.dismiss()
        }
        leaderBoardsButton.setOnClickListener {
            leaderBoardsButtonFunction.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun wrong(
        title: String,
        content: String,
        confirm: String,
        cancel: String,
        callback: (Boolean) -> Unit
    ) {
        if (context is Activity) {
            val activity = context as Activity
            if (!activity.isFinishing && !activity.isDestroyed) {
                StylishAlertDialog(context, StylishAlertDialog.ERROR)
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
                        it.dismiss()
                    }
                    .setCancellable(false)
                    .show()
            } else {
                Log.e("DialogHelper", "Activity is not valid. Dialog cannot be shown.")
            }
        } else {
            Log.e("DialogHelper", "Context is not an instance of Activity.")
        }
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