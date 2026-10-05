package com.whitbread.premierinn.base.view.bottomsheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R

abstract class FullScreenBottomSheetDialogFragment :
        BottomSheetDialogFragment(),
        BackNavigation {

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
        val bottomSheetCallback = getSheetCallback(bottomSheetDialog)
        bottomSheetDialog.behavior.addBottomSheetCallback(bottomSheetCallback)
        bottomSheetDialog.behavior.skipCollapsed = true
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun configureLayout(layout: ViewGroup?) {
        val displayMetrics = acquireDisplayMetrics()
        val layoutParams = layout?.layoutParams
        layoutParams?.width = displayMetrics.widthPixels
        layoutParams?.height = displayMetrics.heightPixels
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

    protected open fun getSheetCallback(bottomSheetDialog: BottomSheetDialog): BottomSheetBehavior.BottomSheetCallback {
        return EnsureNoMidStateSheetCallback(bottomSheetDialog)
    }
}

open class EnsureNoMidStateSheetCallback(
        private val bottomSheetDialog: BottomSheetDialog
) : BottomSheetBehavior.BottomSheetCallback() {

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        // do nothing
    }
}

class NoMidDismissBackNavigationCallback(
        bottomSheetDialog: BottomSheetDialog,
        private val backNavigation: BackNavigation
) : EnsureNoMidStateSheetCallback(bottomSheetDialog) {

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        super.onStateChanged(bottomSheet, newState)
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            backNavigation.closeDialog()
        }
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