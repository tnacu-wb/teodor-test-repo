package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.base.view.BaseBottomSheetDialogFragment
import com.whitbread.premierinn.ciol.CheckOutConfirmationActivity
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.viewmodel.PreCheckOutConfirmationViewModel
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.FragmentReadyToLeaveBinding
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReadyToLeaveBottomSheetFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentReadyToLeaveBinding
    private val preCheckOutConfirmationViewModel: PreCheckOutConfirmationViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentReadyToLeaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener { dismiss() }

        binding.leftMyRoomButton.setOnClickListener {
            preCheckOutConfirmationViewModel.onConfirmCheckOut(
                arguments?.getString(BASKET_REFERENCE_EXTRA) ?: EMPTY_STRING,
                arguments?.parcelable<PreStayUiModel>(PRE_STAY_UI_MODEL))
        }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                preCheckOutConfirmationViewModel.state.collect { state ->
                    binding.leftMyRoomButton.setLoadingState(state.isLoading)

                    when {
                        state.isConfirmed -> {
                            openCheckOutConfirmation()
                        }

                        state.isError -> {
                            showCheckOutError()
                        }
                    }
                }
            }
        }
    }

    private fun openCheckOutConfirmation() {
        requireActivity().startActivityForResult(
                CheckOutConfirmationActivity.newIntent(requireActivity(), arguments?.getString(GUEST_NAME_EXTRA)
                        ?: EMPTY_STRING),
                CheckOutConfirmationActivity.REQUEST_CODE)
        dismiss()
    }

    private fun showCheckOutError() {
        Toast.makeText(requireActivity(), requireActivity().getString(R.string.start_ciol_generic_error), Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val GUEST_NAME_EXTRA = "guest_name_extra"
        private const val BASKET_REFERENCE_EXTRA = "basket_reference_extra"
        private const val PRE_STAY_UI_MODEL = "pre_stay_ui_model"

        @JvmStatic
        fun newInstance(guestName: String?, basketReference: String?, preStayUiModel: PreStayUiModel): ReadyToLeaveBottomSheetFragment =
                ReadyToLeaveBottomSheetFragment().apply {
                    Bundle().let { bundle ->
                        guestName?.let {
                            bundle.putString(GUEST_NAME_EXTRA, it)
                        }
                        basketReference?.let {
                            bundle.putString(BASKET_REFERENCE_EXTRA, it)
                        }
                        bundle.putParcelable(PRE_STAY_UI_MODEL, preStayUiModel)

                        arguments = bundle
                    }
                }
    }
}
