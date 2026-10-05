package com.whitbread.premierinn.hoteldetails.hotelfulldescription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.whitbread.premierinn.base.view.bottomsheet.FullScreenBottomSheetDialogFragment
import com.whitbread.premierinn.base.view.bottomsheet.NoMidDismissBackNavigationCallback
import com.whitbread.premierinn.databinding.FragmentAboutThisHotelBinding

class AboutThisHotelBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    private lateinit var binding: FragmentAboutThisHotelBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAboutThisHotelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data: HotelFullDescription? = arguments?.getParcelable(HOTEL_FULL_DESCRIPTION)
        data?.let {
            binding.title.text = it.name()
            binding.description.text = it.description()
            binding.directions.text = it.directions()
        }
        binding.closeButton.setOnClickListener {
            closeDialog()
        }
    }

    override fun getSheetCallback(bottomSheetDialog: BottomSheetDialog) =
            NoMidDismissBackNavigationCallback(bottomSheetDialog, this)

    companion object {
        const val HOTEL_FULL_DESCRIPTION = "hotel_full_description_key"
        const val TAG = "HotelFullDescriptionBottomSheet"
    }
}
