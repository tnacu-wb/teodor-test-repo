package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.whitbread.premierinn.base.view.BaseBottomSheetDialogFragment
import com.whitbread.premierinn.ciol.uimodel.InfoBottomSheetData
import com.whitbread.premierinn.utils.parcelable

const val INFO_KEY = "INFO_KEY"

class CheckInInformationComposeBottomSheetFragment : BaseBottomSheetDialogFragment() {

    private var onButtonClickListener: (() -> Unit)? = null

    fun setOnButtonClickListener(listener: () -> Unit) {
        onButtonClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val infoBottomSheetData = arguments?.parcelable<InfoBottomSheetData>(INFO_KEY) ?: InfoBottomSheetData()

                CheckInInformationBottomSheetContent(
                    headerTitle = infoBottomSheetData.headerTitle,
                    headerSubTitle = infoBottomSheetData.headerSubTitle,
                    understoodMessage = infoBottomSheetData.understoodMessage,
                    buttonText = infoBottomSheetData.buttonText,
                    onCloseClick = { dismiss() },
                    onButtonClick = {
                        onButtonClickListener?.invoke()
                        dismiss()
                    }
                )
            }
        }
    }

    companion object {
        fun newInstance(
            infoBottomSheetData: InfoBottomSheetData,
            onButtonClick: () -> Unit
        ): CheckInInformationComposeBottomSheetFragment {
            return CheckInInformationComposeBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(INFO_KEY, infoBottomSheetData)
                }
                setOnButtonClickListener(onButtonClick)
            }
        }
    }
}
