package com.whitbread.premierinn.roomcriteria

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.roomcriteria.viewmodel.DialogContentEvent

fun Context.createAlertDialog(event: DialogContentEvent): AlertDialog {
    return AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(event.title)
            .setMessage(event.message)
            .setNegativeButton(R.string.cancel_dialog) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.call_us) { _, _ ->
                val callIntent = IntentUtils.createTelephoneIntent(event.phoneNumber)
                if (IntentUtils.checkIntentResolvedActivity(this, callIntent)) {
                    startActivity(callIntent)
                } else {
                    Toast.makeText(applicationContext, R.string.phone_call_action_not_supported, Toast.LENGTH_LONG).show()
                }
            }.create()
}

fun Context.createAlertDialog(
    message: String,
    phoneNumber: String,
    isGroupFormDialog: Boolean
): AlertDialog {
    return AlertDialog.Builder(this, R.style.PurpleDialog)
        .setTitle(R.string.max_rooms_title)
        .setMessage(message)
        .setNegativeButton(R.string.cancel_dialog) { dialog, _ -> dialog.dismiss() }
        .setPositiveButton(if (isGroupFormDialog) R.string.button_text_continue else R.string.call_us) { _, _ ->

            if (isGroupFormDialog) {
                startActivity(IntentUtils.createWebLinkIntent(getString(R.string.max_rooms_group_booking_form_link)))
            } else {
                val callIntent = IntentUtils.createTelephoneIntent(phoneNumber)
                if (IntentUtils.checkIntentResolvedActivity(this, callIntent)) {
                    startActivity(callIntent)
                } else {
                    Toast.makeText(applicationContext, R.string.phone_call_action_not_supported, Toast.LENGTH_LONG).show()
                }
            }
        }.create()
}
