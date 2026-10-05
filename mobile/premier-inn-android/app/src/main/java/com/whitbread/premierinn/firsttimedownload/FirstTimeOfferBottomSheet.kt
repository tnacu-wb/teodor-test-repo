package com.whitbread.premierinn.firsttimedownload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.common.utils.openIncentiveTermsAndConditions
import com.whitbread.premierinn.common.ParcelablePromoContent

class FirstTimeOfferBottomSheet(
    private val promoContent: ParcelablePromoContent,
    private val promoCode: String
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FirstTimeOfferSheet(
                    promoContent,
                    onClose = { dismiss() },
                    onTermsClick = {
                        openIncentiveTermsAndConditions(
                            activity = requireActivity(),
                            url = promoContent.homepageBanner.terms.url,
                            promoCode = promoCode
                        )
                    })
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { dialogView ->
                val behaviour = BottomSheetBehavior.from(dialogView)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
    }

    fun show(manager: FragmentManager) {
        manager.commit(allowStateLoss = true) {
            add(this@FirstTimeOfferBottomSheet, FirstTimeOfferBottomSheet::class.java.simpleName)
        }
    }

}