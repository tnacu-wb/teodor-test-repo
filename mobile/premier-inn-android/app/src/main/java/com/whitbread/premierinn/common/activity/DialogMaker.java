package com.whitbread.premierinn.common.activity;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class DialogMaker {
    private final AlertDialogBuilderFactory alertDialogBuilderFactory;

    public DialogMaker(AlertDialogBuilderFactory alertDialogBuilderFactory) {
        this.alertDialogBuilderFactory = alertDialogBuilderFactory;
    }

    /**
     * Show a dialog with an Alert Icon and with an OK button to dismiss
     */
    public void showDialogAlertOk(@NonNull Context context, @NonNull String title, @NonNull String description) {
        alertDialogBuilderFactory
                .getAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Just dismiss the dialog
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static class AlertDialogBuilderFactory {
        public AlertDialog.Builder getAlertDialogBuilder(@NonNull Context context) {
            return new AlertDialog.Builder(context);
        }
    }

}
