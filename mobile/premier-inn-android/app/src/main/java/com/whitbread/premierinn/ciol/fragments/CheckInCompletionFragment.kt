package com.whitbread.premierinn.ciol.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.uimodel.CheckInCompletionModel
import com.whitbread.premierinn.ciol.uimodel.RoomKeyInstructionsModel
import com.whitbread.premierinn.ciol.viewmodel.CheckInCompletionViewModel
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.data.search.toPropertyType
import com.whitbread.premierinn.databinding.FragmentCheckInCompletionBinding
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type.HUB_HOTEL
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckInCompletionFragment : BaseFragment() {
    private val checkInCompletionViewModel: CheckInCompletionViewModel by viewModels()
    private var completionModel: CheckInCompletionModel? = null

    private lateinit var binding: FragmentCheckInCompletionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckInCompletionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        completionModel = arguments?.parcelable<CheckInCompletionModel>(COMPLETION_KEY)
        completionModel?.let { model ->
            checkInCompletionViewModel.onScreenOpened(
                model.hotelImage,
                model.bookingReference,
                model.hotelId
            )
            setupUIElements(model.leadBooker, model.hotelBrand)
        }

        setupToolbar()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                checkInCompletionViewModel.state.collectLatest {
                    onStateChanged(it)
                }
            }
        }
    }

    private fun setupToolbar() {
        val containerActivity = activity as CheckInOnlineActivity
        containerActivity.supportActionBar?.hide()
        containerActivity.window?.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        containerActivity.window?.statusBarColor = Color.TRANSPARENT
    }

    private fun setupUIElements(leadBookerName: String, hotelBrand: String) {
        setBackgroundImage(toPropertyType(hotelBrand))
        with(binding) {
            completionDoneImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (checkInCompletionViewModel.state.value.isCountryCodeUK)
                        R.drawable.ciol_completion_done else R.drawable.ciol_completion_done_de
                )
            )
            checkedInHeadline.text =
                resources.getString(R.string.ciol_completion_headline_text, leadBookerName)

            gotItButton.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun setBackgroundImage(type: Type) {
        if (type == HUB_HOTEL) {
            binding.backgroundImage.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ciol_completion_background_hub))
        } else
            binding.backgroundImage.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ciol_completion_background_pi))
    }

    private fun setRoomKeyButtonListener() {
        binding.roomKeyInstructions.setOnClickListener {
            checkInCompletionViewModel.onRoomKeyInstructionsClicked(completionModel?.hotelBrand.toString())
        }
    }

    private fun showRoomKeyInstructionsBottomSheet(roomKeyInstructionsModel: RoomKeyInstructionsModel?) {
        RoomKeyBottomSheetFragment().apply {
            Bundle().let { bundle ->
                bundle.putParcelable(ROOM_KEY, roomKeyInstructionsModel)
                arguments = bundle
                show(this@CheckInCompletionFragment.requireActivity().supportFragmentManager, tag)
            }
        }

        checkInCompletionViewModel.onBottomSheetClosed()
    }

    private fun onStateChanged(state: CheckInCompletionViewModel.CheckInCompletionState) = with(state) {
        if(state.openBottomSheet) {
            roomKeyInstructionsModel?.let(::showRoomKeyInstructionsBottomSheet)
        }
        setRoomKeyButtonListener()
        binding.progressBar.isVisible = isLoading
    }

    companion object {
        fun newInstance(
           completionModel: CheckInCompletionModel
        ) = Bundle().apply {
            putParcelable(COMPLETION_KEY, completionModel)
        }.let { bundle ->
            CheckInCompletionFragment().apply {
                arguments = bundle
            }
        }
    }
}
