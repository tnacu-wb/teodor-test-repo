package com.whitbread.premierinn.parking

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.databinding.ViewParkingDetailsBinding

const val PARKING_DESCRIPTION_KEY = "PARKING_DESCRIPTION_KEY"

class ParkingBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ViewParkingDetailsBinding

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewParkingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(PARKING_DESCRIPTION_KEY)?.let { parkingDescription ->
            binding.parkingDetailsDescriptionTextView.text =
                Html.fromHtml(parkingDescription, Html.FROM_HTML_MODE_COMPACT)
        }

        binding.parkingDetailsCloseImageView.setOnClickListener { this@ParkingBottomSheet.dismiss() }
    }

    fun show(manager: FragmentManager) {
        manager.commit(allowStateLoss = true) {
            add(this@ParkingBottomSheet, ParkingBottomSheet::class.java.simpleName)
        }
    }
}
