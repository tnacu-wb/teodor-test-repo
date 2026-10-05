package com.whitbread.premierinn.accessiblebathroomselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.roomtype.RoomTypeView
import com.whitbread.premierinn.databinding.OverlayAccessibleRoomOptionsBinding

class RoomOptionsFragment : BottomSheetDialogFragment() {

    private lateinit var selectedOption: AccessibleRoomSizeOption
    private var changeable: Boolean = true
    private var callback: Callback? = null
    private var _binding: OverlayAccessibleRoomOptionsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val SELECTED_OPTION = "SELECTED_OPTION"
        private const val CHANGEABLE = "CHANGEABLE"

        fun create(selectedOption: AccessibleRoomSizeOption,
                   changeable: Boolean): RoomOptionsFragment {
            val bundle = Bundle()
            bundle.putInt(SELECTED_OPTION, selectedOption.ordinal)
            bundle.putBoolean(CHANGEABLE, changeable)

            val fragment = RoomOptionsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    interface Callback {
        fun onAccessibleRoomSelected(option: AccessibleRoomSizeOption)
        fun onCanceled()
    }

    fun callback(callback: Callback) {
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = OverlayAccessibleRoomOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = savedInstanceState ?: arguments!!
        selectedOption = when (bundle.getInt(SELECTED_OPTION)) {
            AccessibleRoomSizeOption.DOUBLE.ordinal -> AccessibleRoomSizeOption.DOUBLE
            AccessibleRoomSizeOption.TWIN.ordinal -> AccessibleRoomSizeOption.TWIN
            else -> throw IllegalArgumentException()
        }
        changeable = bundle.getBoolean(CHANGEABLE)

        redraw()

        if (changeable) {
            binding.optionDouble.setOnClickListener {
                selectedOption = AccessibleRoomSizeOption.DOUBLE
                redraw()
                callback?.onAccessibleRoomSelected(AccessibleRoomSizeOption.DOUBLE)
            }

            binding.optionTwin.setOnClickListener {
                selectedOption = AccessibleRoomSizeOption.TWIN
                redraw()
                callback?.onAccessibleRoomSelected(AccessibleRoomSizeOption.TWIN)
            }
        }

        binding.close.setOnClickListener { callback?.onCanceled() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SELECTED_OPTION, selectedOption.ordinal)
        outState.putBoolean(CHANGEABLE, changeable)
        super.onSaveInstanceState(outState)
    }

    private fun redraw() {
        val selectedView: RoomTypeView
        val notSelectedView: RoomTypeView

        if (selectedOption == AccessibleRoomSizeOption.DOUBLE) {
            selectedView = binding.optionDouble
            notSelectedView = binding.optionTwin
        } else {
            selectedView = binding.optionTwin
            notSelectedView = binding.optionDouble
        }

        selectedView.setOptionBackgroundDrawable(R.drawable.accessible_room_option_selected_bg)
        selectedView.setOptionTextColor(R.color.mint_dark_x)
        selectedView.setOptionIconColor(R.color.mint_dark_x)
        selectedView.isEnabled = true

        notSelectedView.setOptionBackgroundDrawable(R.drawable.accessible_room_option_bg)
        if (changeable) {
            notSelectedView.setOptionTextColor(R.color.grey_dark)
            notSelectedView.setOptionIconColor(R.color.grey_dark)
            notSelectedView.isEnabled = true
        } else {
            notSelectedView.setOptionTextColor(R.color.grey_medium)
            notSelectedView.setOptionIconColor(R.color.grey_medium)
            notSelectedView.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}