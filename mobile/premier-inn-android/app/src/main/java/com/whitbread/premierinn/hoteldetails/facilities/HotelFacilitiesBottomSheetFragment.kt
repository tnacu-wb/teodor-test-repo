package com.whitbread.premierinn.hoteldetails.facilities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.whitbread.premierinn.base.view.bottomsheet.FullScreenBottomSheetDialogFragment
import com.whitbread.premierinn.base.view.bottomsheet.NoMidDismissBackNavigationCallback
import com.whitbread.premierinn.common.SpaceDividerItemDecoration
import com.whitbread.premierinn.databinding.FragmentHotelFacilitiesBinding

class HotelFacilitiesBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    private lateinit var binding: FragmentHotelFacilitiesBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHotelFacilitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data: HotelFacilitiesModel? = arguments?.getParcelable(HOTEL_FACILITIES_BOTTOM_SHEET)

        with(binding) {
            hotelFacilitiesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            hotelFacilitiesRecyclerView.addItemDecoration(SpaceDividerItemDecoration(16,true))
            roomFeaturesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            roomFeaturesRecyclerView.addItemDecoration(SpaceDividerItemDecoration(16,true))
        }

        data?.let {
            binding.hotelFacilitiesRecyclerView.adapter = HotelFacilitiesAdapter(data.hotelFacilities)
            binding.roomFeaturesRecyclerView.adapter = HotelRoomFeaturesAdapter(data.roomFeatures)
        }

        binding.closeButton.setOnClickListener {
            closeDialog()
        }
    }

    override fun getSheetCallback(bottomSheetDialog: BottomSheetDialog) =
            NoMidDismissBackNavigationCallback(bottomSheetDialog, this)

    companion object {
        const val HOTEL_FACILITIES_BOTTOM_SHEET = "hotel_full_description_key"
    }
}
