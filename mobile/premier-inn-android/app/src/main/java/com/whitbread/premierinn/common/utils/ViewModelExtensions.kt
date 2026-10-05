@file:Suppress("UNCHECKED_CAST")

package com.whitbread.premierinn.common.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * View models extensions helpers for ViewModel factory creation with args support
 * on top of fragments ktx viewModels<T>
 * or
 * on top of activities ktx activityViewModels<T>
 *
 * Ref:
 * https://developer.android.com/kotlin/ktx
 * https://youtu.be/9fn5s8_CYJI?t=1889
 */

inline fun <reified T : ViewModel> Fragment.activityViewModel(
        crossinline provider: () -> T
) = activityViewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = provider() as T
    }
}