package com.whitbread.premierinn.base.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R

abstract class BaseBottomSheetDialogFragment :
    BottomSheetDialogFragment(), BackNavigation {

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = NavigateBackDialog(this, requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheetLayout = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            configureLayout(bottomSheetLayout)
            configureBottomSheetBehavior(bottomSheetDialog)
        }

        return dialog
    }

    private fun configureBottomSheetBehavior(bottomSheetDialog: BottomSheetDialog) {
        bottomSheetDialog.behavior.skipCollapsed = true
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun configureLayout(layout: ViewGroup?) {
        val displayMetrics = acquireDisplayMetrics()
        val layoutParams = layout?.layoutParams
        layoutParams?.width = displayMetrics.widthPixels
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layout?.layoutParams = layoutParams
    }

    private fun acquireDisplayMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    override fun closeDialog() {
        dismiss()
    }

    override fun navigateBack() {
        dismiss()
    }
}

private class NavigateBackDialog(
    val backNavigation: BackNavigation,
    context: Context,
    theme: Int
) : BottomSheetDialog(context, theme) {
    override fun onBackPressed() {
        backNavigation.navigateBack()
    }
}

interface BackNavigation {
    /**
     * Provide navigation information
     *  on back button or x press and swipe down.
     */
    fun closeDialog()
    fun navigateBack()
}
