package com.whitbread.premierinn.hoteldetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitbread.premierinn.R
import com.whitbread.premierinn.base.view.bottomsheet.FullScreenBottomSheetDialogFragment
import com.whitbread.premierinn.databinding.FragmentAccessibilityBinding

class HotelAccessibilityBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    private lateinit var binding: FragmentAccessibilityBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAccessibilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.disabledWebView.apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.domStorageEnabled = true
            loadUrl(view.context.getString(R.string.accessibility_info_web_url))
        }

        binding.closeButton.setOnClickListener {
            closeDialog()
        }
    }

}
