package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.adapter.SpecialOccasionsAdapter
import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.databinding.ViewSelectGuestOccasionBinding

const val OCCASIONS_KEY = "OCCASIONS_KEY"

class SelectGuestsOccasionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ViewSelectGuestOccasionBinding
    private lateinit var occasionsAdapter: SpecialOccasionsAdapter
    private var onSpecialOccasionSelected: ((specialOccasion: HotelPreferenceUiModel) -> Unit)? = null

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewSelectGuestOccasionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelableArrayList<HotelPreferenceUiModel>(OCCASIONS_KEY)?.let { setupSpecialOccasionsAdapter(it) }
    }

    private fun setupSpecialOccasionsAdapter(occasions: List<HotelPreferenceUiModel>) {
        binding.selectOccasionRecyclerView.apply {
            occasionsAdapter = SpecialOccasionsAdapter(occasions) { selectedOccasion ->
                onSpecialOccasionSelected?.let { it(selectedOccasion) }
                this@SelectGuestsOccasionBottomSheet.dismiss()
            }
            this.adapter = occasionsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun show(manager: FragmentManager, onOccasionSelected: (HotelPreferenceUiModel) -> Unit) {
        manager.commit(allowStateLoss = true) {
            add(this@SelectGuestsOccasionBottomSheet, SelectGuestsOccasionBottomSheet::class.java.simpleName)
                this@SelectGuestsOccasionBottomSheet.onSpecialOccasionSelected = onOccasionSelected
        }
    }
}
