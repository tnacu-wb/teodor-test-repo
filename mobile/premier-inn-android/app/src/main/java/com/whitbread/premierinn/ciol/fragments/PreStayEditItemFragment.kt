package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.entity.LeadBookerDetailsUiModel
import com.whitbread.premierinn.ciol.viewmodel.PreStayEditItemViewModel
import com.whitbread.premierinn.ciol.viewmodel.PreStaySharedViewModel
import com.whitbread.premierinn.ciol.viewmodel.state.utils.PreStayEditItemFieldType
import com.whitbread.premierinn.ciol.views.compose.PreStayEditItemScreen
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.FragmentPreStayEditItemBinding
import com.whitbread.premierinn.postcodefinder.ParcelableAddress
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreStayEditItemFragment: BaseFragment() {
    private val preStaySharedViewModel: PreStaySharedViewModel by activityViewModels()

    private val preStayEditItemViewModel: PreStayEditItemViewModel by viewModels ()
    private lateinit var binding: FragmentPreStayEditItemBinding

    companion object {
        const val ITEM_TYPE_EMAIL_ADDRESS = "type_email_address"
        const val ITEM_TYPE_PHONE_NUMBER = "type_phone_number"
        const val ITEM_TYPE_ADDRESS = "type_address"
        private const val ITEM_TYPE_KEY = "item_type"
        private const val LEAD_BOOKER_DETAILS_KEY = "lead_booker_details"

        fun newInstance(itemType: String, leadBookerDetails: LeadBookerDetailsUiModel): PreStayEditItemFragment = PreStayEditItemFragment().apply {
            arguments = Bundle().apply {
                putString(ITEM_TYPE_KEY, itemType)
                putParcelable(LEAD_BOOKER_DETAILS_KEY, leadBookerDetails)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreStayEditItemBinding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            val type = arguments?.getString(ITEM_TYPE_KEY, EMPTY_STRING) ?: EMPTY_STRING
            val leadBookerDetails = arguments?.parcelable<LeadBookerDetailsUiModel>(LEAD_BOOKER_DETAILS_KEY)
            preStayEditItemViewModel.onScreenOpened(type, leadBookerDetails)

            setContent {
                // In Compose world
                val state by preStayEditItemViewModel.state.collectAsState()

                state.event?.let { event ->
                    when (event) {
                        is PreStayEditItemViewModel.PreStayEditItemEvent.ItemsValidated -> {
                            when(type) {
                                ITEM_TYPE_EMAIL_ADDRESS -> preStaySharedViewModel.updateEmail(
                                    event.itemTypeValueMap[PreStayEditItemFieldType.EMAIL_ADDRESS]?.value ?: EMPTY_STRING
                                )

                                ITEM_TYPE_PHONE_NUMBER -> preStaySharedViewModel.updatePhoneNumber(
                                    event.itemTypeValueMap[PreStayEditItemFieldType.PHONE_NUMBER]?.value ?: EMPTY_STRING
                                )

                                ITEM_TYPE_ADDRESS -> preStaySharedViewModel.updateAddress(
                                    addressLine1 = event.itemTypeValueMap[PreStayEditItemFieldType.ADDRESS_LINE_1]?.value ?: EMPTY_STRING,
                                    addressLine2 = event.itemTypeValueMap[PreStayEditItemFieldType.ADDRESS_LINE_2]?.value ?: EMPTY_STRING,
                                    addressLine3 = event.itemTypeValueMap[PreStayEditItemFieldType.ADDRESS_LINE_3]?.value ?: EMPTY_STRING,
                                    postalCode = event.itemTypeValueMap[PreStayEditItemFieldType.POST_CODE]?.value ?: EMPTY_STRING,
                                    country = event.itemTypeValueMap[PreStayEditItemFieldType.COUNTRY]?.value ?: EMPTY_STRING
                                )
                            }
                            parentFragmentManager.popBackStack()
                        }

                        is PreStayEditItemViewModel.PreStayEditItemEvent.OpenCountrySelector -> {
                            SelectNationalityBottomSheet { selectedCountry ->
                                preStayEditItemViewModel.processAction(
                                    action = PreStayEditItemViewModel.PreStayEditItemAction.CountryValueSelected(selectedCountry.countryName)
                                )
                            }.apply {
                                arguments = Bundle().apply {
                                    putBoolean(SHOW_COUNTRIES_KEY, true)
                                }
                            }.show(parentFragmentManager, SelectNationalityBottomSheet::class.java.simpleName)
                        }

                        is PreStayEditItemViewModel.PreStayEditItemEvent.OpenAddressFinder -> {
                            startAddressFinderForResult(
                                postcode = event.postcode
                            )
                        }
                    }

                    preStayEditItemViewModel.onEventConsumed()
                }

                PreStayEditItemScreen(type, state) { action ->
                    preStayEditItemViewModel.processAction(action)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val containerActivity = (activity as CheckInOnlineActivity)
        containerActivity.setupToolbarTitle(resources.getString(R.string.pre_stay_edit_details))
        containerActivity.changeToolbarIconVisibility(isVisible = true)

        containerActivity.setOnToolbarIconClickListener {
            preStayEditItemViewModel.processAction(PreStayEditItemViewModel.PreStayEditItemAction.Validate)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as CheckInOnlineActivity).changeToolbarIconVisibility(isVisible = false)
    }

    fun startAddressFinderForResult(postcode: String) {
        activity?.let {
            PostcodeFinderActivity.start(requireContext(), it, postcode)
        }
    }

    fun onPostcodeAddressSelected(address: ParcelableAddress) {
        preStayEditItemViewModel.processAction(
            PreStayEditItemViewModel.PreStayEditItemAction.UpdateAddress(
                addressLine1 = address.line1,
                addressLine2 = address.line2 ?: EMPTY_STRING,
                addressLine3 = address.line3 ?: EMPTY_STRING,
                postalCode = address.postcode ?: EMPTY_STRING
            )
        )
    }
}
