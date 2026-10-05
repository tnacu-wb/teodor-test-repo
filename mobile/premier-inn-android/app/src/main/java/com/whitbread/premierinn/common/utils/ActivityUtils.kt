package com.whitbread.premierinn.common.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun getActivityFromContext(context: Context?): Activity? {
    var ctx = context
    while (ctx is ContextWrapper) {
        if (ctx is Activity) {
            return ctx
        }
        ctx = ctx.baseContext
    }
    return null
}
