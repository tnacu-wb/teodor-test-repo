package com.whitbread.premierinn.hoteldetails

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.whitbread.premierinn.base.view.bottomsheet.FullScreenBottomSheetDialogFragment
import com.whitbread.premierinn.base.view.bottomsheet.NoMidDismissBackNavigationCallback
import com.whitbread.premierinn.common.SpaceDividerItemDecoration
import com.whitbread.premierinn.databinding.FragmentHotelImportantInfoBinding
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem
import com.whitbread.premierinn.importanthotelinfo.ImportantInfoAdapter


class ImportantInfoBottomSheetFragment : FullScreenBottomSheetDialogFragment() {

    private lateinit var binding: FragmentHotelImportantInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHotelImportantInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val importantInfos =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(HOTEL_IMPORTANT_INFOS, ArrayList<InfoItem>().javaClass)
            } else {
                arguments?.getSerializable(HOTEL_IMPORTANT_INFOS) as ArrayList<InfoItem>?
            }

        binding.rvImportantHotelInfoNotesList.addItemDecoration(
            SpaceDividerItemDecoration(16, true)
        )

        importantInfos?.let {
            binding.rvImportantHotelInfoNotesList.adapter = ImportantInfoAdapter(it)
        }

        binding.closeButton.setOnClickListener {
            closeDialog()
        }
    }

    override fun getSheetCallback(bottomSheetDialog: BottomSheetDialog) =
        NoMidDismissBackNavigationCallback(bottomSheetDialog, this)

    companion object {
        const val HOTEL_IMPORTANT_INFOS = "hotel_important_info_key"
        const val TAG = "ImportantInfoBottomSheetFragment"
    }
}