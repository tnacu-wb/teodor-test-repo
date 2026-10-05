package com.whitbread.premierinn.common.view.form.personaldetails

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.adapter.CountryItem
import com.whitbread.premierinn.common.adapter.CountrySpinnerItem
import com.whitbread.premierinn.common.adapter.DynamicCountryListAdapter
import com.whitbread.premierinn.common.adapter.NothingSelectedItem
import com.whitbread.premierinn.common.adapter.Divider
import com.whitbread.premierinn.common.utils.bind
import com.whitbread.premierinn.common.view.CallToActionButtonLayout
import com.whitbread.premierinn.common.view.FormEditTextView
import com.whitbread.premierinn.common.view.form.DropDown
import com.whitbread.premierinn.common.view.form.FormField
import com.whitbread.premierinn.common.view.form.Invalid
import com.whitbread.premierinn.common.view.form.Text
import com.whitbread.premierinn.common.view.form.Validity
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class AddAddressFormView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {


    enum class AddressFieldType {
        COUNTRY,
        LOOKUP_POSTCODE,
        ADDRESS_LINE_1,
        ADDRESS_LINE_2,
        CITY,
        POSTCODE
    }

    private var countryOptions: List<CountryDomain> = listOf()
    private val countrySpinnerAdapter = DynamicCountryListAdapter(context)
    private var countrySpinner: AppCompatSpinner
    private var countrySpinnerLabel: View

    private var lookupPostcodeView: FormEditTextView
    private var findAddressButton: CallToActionButtonLayout
    private var addressLine1View: FormEditTextView
    private var addressLine2View: FormEditTextView
    private var cityView: FormEditTextView
    private var postcodeView: FormEditTextView

    private val countryChanges = PublishSubject.create<CountrySpinnerItem>()

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.form_add_address, this, true)

        countrySpinner = root.bind<AppCompatSpinner>(R.id.country_spinner).value
        countrySpinner.adapter = countrySpinnerAdapter

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val country = countrySpinnerAdapter.getItem(position)
                countryChanges.onNext(country)
            }
        }
        countrySpinnerLabel = root.bind<AppCompatSpinner>(R.id.country_spinner_label).value

        lookupPostcodeView = root.bind<FormEditTextView>(R.id.lookup_postcode).value
        findAddressButton = root.bind<CallToActionButtonLayout>(R.id.find_address_btn).value

        addressLine1View = root.bind<FormEditTextView>(R.id.address_line_1).value
        addressLine2View = root.bind<FormEditTextView>(R.id.address_line_2).value
        cityView = root.bind<FormEditTextView>(R.id.city).value
        postcodeView = root.bind<FormEditTextView>(R.id.postcode).value
    }

    fun showFields(fields: Set<AddressFieldType>) {
        countrySpinner.isVisible = fields.contains(AddressFieldType.COUNTRY)
        countrySpinnerLabel.isVisible = fields.contains(AddressFieldType.COUNTRY)
        lookupPostcodeView.isVisible = fields.contains(AddressFieldType.LOOKUP_POSTCODE)
        findAddressButton.isVisible = fields.contains(AddressFieldType.LOOKUP_POSTCODE)
        addressLine1View.isVisible = fields.contains(AddressFieldType.ADDRESS_LINE_1)
        addressLine2View.isVisible = fields.contains(AddressFieldType.ADDRESS_LINE_2)
        cityView.isVisible = fields.contains(AddressFieldType.CITY)
        postcodeView.isVisible = fields.contains(AddressFieldType.POSTCODE)
    }

    fun updateValues(fields: Map<AddressFieldType, FormField>) {
        fields.forEach { entry ->
            when (entry.key) {
                AddressFieldType.COUNTRY -> {
                    val countryField = fields[AddressFieldType.COUNTRY] as DropDown<CountryDomain>
                    countryOptions = countryField.options
                    countrySpinnerAdapter.setCountries(countryField.options.map { country -> CountryItem(country) },
                            NothingSelectedItem(R.layout.country_dropdown_no_item_selected_warning),
                            Divider(R.layout.country_dropdown_divider))
                    if (countryField.selectedOption != null) {
                        countrySpinner.setSelection(countryField.selectedOption!! + 7)
                    } else {
                        countrySpinner.setSelection(0)
                    }
                }
                AddressFieldType.LOOKUP_POSTCODE -> {
                    val lookupPostcodeField = fields[AddressFieldType.LOOKUP_POSTCODE] as Text
                    if (lookupPostcodeView.editText!!.text.toString() != lookupPostcodeField.value) {
                        lookupPostcodeView.setText(lookupPostcodeField.value)
                    }
                }
                AddressFieldType.ADDRESS_LINE_1 -> {
                    val addressLine1Field = fields[AddressFieldType.ADDRESS_LINE_1] as Text
                    if (addressLine1View.editText!!.text.toString() != addressLine1Field.value) {
                        addressLine1View.setText(addressLine1Field.value)
                    }
                }
                AddressFieldType.ADDRESS_LINE_2 -> {
                    val addressLine2Field = fields[AddressFieldType.ADDRESS_LINE_2] as Text
                    if (addressLine2View.editText!!.text.toString() != addressLine2Field.value) {
                        addressLine2View.setText(addressLine2Field.value)
                    }
                }
                AddressFieldType.CITY -> {
                    val cityField = fields[AddressFieldType.CITY] as Text
                    if (cityView.editText!!.text.toString() != cityField.value) {
                        cityView.setText(cityField.value)
                    }
                }
                AddressFieldType.POSTCODE -> {
                    val postcodeField = fields[AddressFieldType.POSTCODE] as Text
                    if (postcodeView.editText!!.text.toString() != postcodeField.value) {
                        postcodeView.setText(postcodeField.value)
                    }
                }
            }

        }
    }

    fun updateErrors(errors: Map<AddressFieldType, Validity>) {
        errors.forEach { entry ->
            when (entry.key) {
                AddressFieldType.COUNTRY -> {
                }
                AddressFieldType.LOOKUP_POSTCODE -> lookupPostcodeView.error = (entry.value as? Invalid)?.error
                AddressFieldType.ADDRESS_LINE_1 -> addressLine1View.error = (entry.value as? Invalid)?.error
                AddressFieldType.ADDRESS_LINE_2 -> addressLine2View.error = (entry.value as? Invalid)?.error
                AddressFieldType.CITY -> cityView.error = (entry.value as? Invalid)?.error
                AddressFieldType.POSTCODE -> postcodeView.error = (entry.value as? Invalid)?.error
            }
        }
    }

    fun inputChanges(): Observable<Pair<AddressFieldType, Comparable<*>>>? {
        return Observable.merge(
            countryChanges.distinctUntilChanged().map { value ->
                when (value) {
                    is NothingSelectedItem -> Pair(AddressFieldType.COUNTRY, 0)
                    is CountryItem -> Pair(AddressFieldType.COUNTRY, value.country)
                    is Divider -> Pair(AddressFieldType.COUNTRY, 0)
                }
            },
            line1Changes().map { value -> Pair(AddressFieldType.ADDRESS_LINE_1, value) },
            line2Changes().map { value -> Pair(AddressFieldType.ADDRESS_LINE_2, value) },
            cityChanges().map { value -> Pair(AddressFieldType.CITY, value) })
            .mergeWith(postcodeChanges().map { value -> Pair(AddressFieldType.POSTCODE, value) })
            .mergeWith(lookupPostcodeChanges().map { value -> Pair(AddressFieldType.LOOKUP_POSTCODE, value) })
    }

    fun lookupPostcodeClickAction(action: (String) -> Unit) {
        findAddressButton.setOnClickListener { action(lookupPostcodeView.inputText) }
    }

    private fun line1Changes(): Observable<String> {
        return addressLine1View.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun line2Changes(): Observable<String> {
        return addressLine2View.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun cityChanges(): Observable<String> {
        return cityView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun postcodeChanges(): Observable<String> {
        return postcodeView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun lookupPostcodeChanges(): Observable<String> {
        return lookupPostcodeView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

}