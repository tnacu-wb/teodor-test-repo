package com.whitbread.premierinn.hoteldetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitbread.premierinn.base.view.bottomsheet.FullScreenBottomSheetDialogFragment
import com.whitbread.premierinn.databinding.FragmentParkingBinding

class ParkingBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    private lateinit var binding: FragmentParkingBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = FragmentParkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data: HotelParkingModel? = arguments?.getParcelable(HOTEL_PARKING_KEY)
        data?.let {
            binding.parkingDetails.text = SpannableContentFactory.createContent(view.getContext(), data.parkingDescription)
        }
        binding.closeButton.setOnClickListener {
            closeDialog()
        }
    }

    companion object {
        const val HOTEL_PARKING_KEY = "hotel_parking_key"
    }
}
