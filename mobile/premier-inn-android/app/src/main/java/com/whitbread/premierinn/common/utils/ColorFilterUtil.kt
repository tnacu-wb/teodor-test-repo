package com.whitbread.premierinn.common.utils

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.widget.ProgressBar
import androidx.core.content.ContextCompat

object ColorFilterUtil {
    fun setProgressColour(context:Context, progressBar: ProgressBar, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progressBar.indeterminateDrawable.colorFilter = BlendModeColorFilter(ContextCompat.getColor(context, color), BlendMode.SRC_IN)
        } else {
            progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
        }
    }
}