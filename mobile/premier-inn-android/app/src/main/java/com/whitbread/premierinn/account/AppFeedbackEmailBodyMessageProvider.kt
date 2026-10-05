package com.whitbread.premierinn.account;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.StringResourceProvider;
import javax.inject.Inject

const val ANDROID_LABEL = "Android version"
const val PHONE_LABEL = "Phone"
const val SPACE = " "
open class AppFeedbackEmailBodyMessageProvider @Inject constructor(private val context: Context,
                                                                   private val stringResourceProvider: StringResourceProvider){


    fun getBodyQuestion(): String {
        return "<b>" + context.getString(R.string.email_feedback_body) + "</b>";
    }

    fun getAndroidVersion(): String {
        return ANDROID_LABEL + SPACE + Build.VERSION.RELEASE;
    }

    fun getPhoneModel(): String {
        return PHONE_LABEL + SPACE + Build.MODEL;
    }
}