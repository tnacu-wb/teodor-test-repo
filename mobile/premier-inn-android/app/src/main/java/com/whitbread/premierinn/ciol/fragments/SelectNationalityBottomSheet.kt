package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.adapter.SelectNationalityCountriesAdapter
import com.whitbread.premierinn.ciol.viewmodel.SelectNationalityViewModel
import com.whitbread.premierinn.ciol.viewmodel.SelectNationalityViewModel.SelectNationalityState
import com.whitbread.premierinn.databinding.ViewSelectNationalityBinding
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val GUEST_NATIONALITY_KEY = "GUEST_NATIONALITY_KEY"
const val SHOW_COUNTRIES_KEY = "SHOW_COUNTRIES_KEY"

@AndroidEntryPoint
class SelectNationalityBottomSheet(
    private val onCountrySelected: (selectedCountry: CountryDomain) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: ViewSelectNationalityBinding
    private lateinit var countriesAdapter: SelectNationalityCountriesAdapter
    private val selectNationalityViewModel: SelectNationalityViewModel by viewModels()

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewSelectNationalityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCancelButtonListener()
        selectNationalityViewModel.onScreenOpened(arguments?.getString(GUEST_NATIONALITY_KEY))

        searchCountryOrNationality(arguments?.getBoolean(SHOW_COUNTRIES_KEY) ?: true)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectNationalityViewModel.state.collectLatest(::onStateUpdated)
            }
        }
    }

    private fun onStateUpdated(state: SelectNationalityState) = with(state) {
        updateContentLoading(isLoading)
        countries?.let { countries ->
            selectedPosition?.let { selectedPosition ->
                setupCountryListAdapter(countries, selectedPosition)
            }
        }
        filteredCountries?.let(::updateFilteredCountries)
    }

    private fun updateContentLoading(isLoading: Boolean) {
        binding.searchCountryEditText.isEnabled = !isLoading
        binding.selectNationalityProgressBar.isVisible = isLoading
        binding.selectNationalityDescriptionTextView.isVisible = !isLoading
        binding.selectNationalityRecyclerView.isVisible = !isLoading
    }

    private fun setupCountryListAdapter(countries: List<CountryDomain>, selectedPosition: Int) {
        binding.selectNationalityRecyclerView.apply {
            countriesAdapter = SelectNationalityCountriesAdapter(
                countries, arguments?.getBoolean(SHOW_COUNTRIES_KEY) ?: true
            ) { selectedCountry ->
                onCountrySelected(selectedCountry)
                this@SelectNationalityBottomSheet.dismiss()
            }
            this.adapter = countriesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateFilteredCountries(countries: List<CountryDomain>) {
        if (::countriesAdapter.isInitialized) {
            countriesAdapter.updateFilteredCountries(countries)
        }
    }

    private fun searchCountryOrNationality(showCountryName: Boolean) {
        binding.selectNationalityDescriptionTextView.text = getString(if (showCountryName) R.string.pre_stay_country_select else R.string.pre_stay_nationality_select)
        binding.searchCountryEditText.apply {
            hint = getString( if (showCountryName) R.string.pre_stay_country_search else R.string.pre_stay_nationality_search )
            addTextChangedListener { text ->
                selectNationalityViewModel.onSearchTextChanged(text.toString(), showCountryName) }
        }
    }

    private fun setupCancelButtonListener() {
        binding.cancelSearchButton.setOnClickListener {
            this.dismiss()
        }
    }

    fun show(manager: FragmentManager) {
        manager.commit(allowStateLoss = true) {
            add(
                this@SelectNationalityBottomSheet,
                SelectNationalityBottomSheet::class.java.simpleName
            )
        }
    }
}