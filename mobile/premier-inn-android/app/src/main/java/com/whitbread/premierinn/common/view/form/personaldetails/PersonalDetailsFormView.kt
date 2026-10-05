package com.whitbread.premierinn.common.view.form.personaldetails

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.jakewharton.rxbinding3.widget.itemSelections
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.adapter.*
import com.whitbread.premierinn.common.utils.bind
import com.whitbread.premierinn.common.view.FormEditTextView
import com.whitbread.premierinn.common.view.form.*
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PersonalDetailsFormView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    enum class FieldType {
        TITLE,
        FIRST_NAME,
        LAST_NAME,
        CONTACT_NUMBER,
        EMAIL,
        NATIONALITY,
        PASSPORT,
        NEXT_DESTINATION
    }

    private var titleSpinner: AppCompatSpinner
    private var titleOptions: List<String> = listOf()

    private var firstNameView: FormEditTextView
    private var lastNameView: FormEditTextView
    private var contactNumberView: FormEditTextView
    private var emailAddressView: FormEditTextView

    private var countryOptions: List<CountryDomain> = listOf()
    private val nationalitySpinnerAdapter = DynamicCountryListAdapter(context)
    private var nationalitySpinner: AppCompatSpinner
    private val nationalitySpinnerLabel: View

    private var passportNumberView: FormEditTextView
    private var nextDestinationView: FormEditTextView

    private val nationalityChanges = PublishSubject.create<CountrySpinnerItem>()

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.form_personal_details, this, true)

        titleSpinner = root.bind<AppCompatSpinner>(R.id.titles_spinner).value
        firstNameView = root.bind<FormEditTextView>(R.id.first_name_input).value
        lastNameView = root.bind<FormEditTextView>(R.id.last_name_input).value
        contactNumberView = root.bind<FormEditTextView>(R.id.contact_number_input).value
        emailAddressView = root.bind<FormEditTextView>(R.id.email_input).value

        nationalitySpinner = root.bind<AppCompatSpinner>(R.id.nationality_spinner).value
        passportNumberView = root.bind<FormEditTextView>(R.id.passport_number_input).value
        nextDestinationView = root.bind<FormEditTextView>(R.id.next_destination_input).value

        nationalitySpinner.adapter = nationalitySpinnerAdapter
        nationalitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val country = nationalitySpinnerAdapter.getItem(position)
                nationalityChanges.onNext(country)
            }
        }
        nationalitySpinnerLabel = root.bind<View>(R.id.nationality_spinner_label).value
    }

    fun showFields(fields: Set<FieldType>) {
        titleSpinner.isVisible = fields.contains(FieldType.TITLE)
        firstNameView.isVisible = fields.contains(FieldType.FIRST_NAME)
        lastNameView.isVisible = fields.contains(FieldType.LAST_NAME)
        contactNumberView.isVisible = fields.contains(FieldType.CONTACT_NUMBER)
        emailAddressView.isVisible = fields.contains(FieldType.EMAIL)
        nationalitySpinner.isVisible = fields.contains(FieldType.NATIONALITY)
        nationalitySpinnerLabel.isVisible = fields.contains(FieldType.NATIONALITY)
        passportNumberView.isVisible = fields.contains(FieldType.PASSPORT)
        nextDestinationView.isVisible = fields.contains(FieldType.NEXT_DESTINATION)
    }

    fun updateValues(fields: Map<FieldType, FormField>) {
        fields.forEach { entry ->
            when (entry.key) {
                FieldType.TITLE -> {
                    val titleField = fields[FieldType.TITLE] as DropDown<String>
                    titleOptions = titleField.options
                    val titleSpinnerAdapter = ArrayAdapter(context, R.layout.list_popup_selected_item, titleField.options)
                    titleSpinnerAdapter.setDropDownViewResource(R.layout.list_popup_dropdown_items)
                    titleSpinner.adapter = titleSpinnerAdapter
                    titleField.selectedOption?.let { titleSpinner.setSelection(it) }
                }

                FieldType.FIRST_NAME -> {
                    val firstNameField = fields[FieldType.FIRST_NAME] as Text
                    if (firstNameView.editText!!.text.toString() != firstNameField.value) {
                        firstNameView.setText(firstNameField.value)
                    }
                }

                FieldType.LAST_NAME -> {
                    val lastNameField = fields[FieldType.LAST_NAME] as Text
                    if (lastNameView.editText!!.text.toString() != lastNameField.value) {
                        lastNameView.setText(lastNameField.value)
                    }
                }

                FieldType.CONTACT_NUMBER -> {
                    val contactNumberField = fields[FieldType.CONTACT_NUMBER] as Text
                    if (contactNumberView.editText!!.text.toString() != contactNumberField.value) {
                        contactNumberView.setText(contactNumberField.value)
                    }
                }

                FieldType.EMAIL -> {
                    val emailField = fields[FieldType.EMAIL] as Text
                    if (emailAddressView.editText!!.text.toString() != emailField.value) {
                        emailAddressView.setText(emailField.value)
                    }
                }

                FieldType.NATIONALITY -> {
                    val nationalityField = fields[FieldType.NATIONALITY] as DropDown<CountryDomain>
                    countryOptions = nationalityField.options
                    nationalitySpinnerAdapter.setCountries(nationalityField.options.map { country -> CountryItem(country) },
                            NothingSelectedItem(R.layout.country_dropdown_no_item_selected_warning),
                            Divider(R.layout.country_dropdown_divider))
                    if (nationalityField.selectedOption != null) {
                        nationalitySpinner.setSelection(nationalityField.selectedOption!! + 7)
                    } else {
                        nationalitySpinner.setSelection(0)
                    }
                }

                FieldType.PASSPORT -> {
                    val passportField = fields[FieldType.PASSPORT] as Text
                    if (passportNumberView.editText!!.text.toString() != passportField.value) {
                        passportNumberView.setText(passportField.value)
                    }
                }
                FieldType.NEXT_DESTINATION -> {
                    val nextDestinationField = fields[FieldType.NEXT_DESTINATION] as Text
                    if (nextDestinationView.editText!!.text.toString() != nextDestinationField.value) {
                        nextDestinationView.setText(nextDestinationField.value)
                    }
                }
            }
        }
    }

    fun updateErrors(errors: Map<FieldType, Validity>) {
        errors.forEach { entry ->
            when (entry.key) {
                FieldType.TITLE -> { }
                FieldType.FIRST_NAME -> firstNameView.error = (entry.value as? Invalid)?.error
                FieldType.LAST_NAME -> lastNameView.error = (entry.value as? Invalid)?.error
                FieldType.CONTACT_NUMBER -> contactNumberView.error = (entry.value as? Invalid)?.error
                FieldType.EMAIL -> emailAddressView.error = (entry.value as? Invalid)?.error
                FieldType.NATIONALITY -> { }
                FieldType.PASSPORT -> passportNumberView.error = (entry.value as? Invalid)?.error
                FieldType.NEXT_DESTINATION -> nextDestinationView.error = (entry.value as? Invalid)?.error
            }
        }
    }

    fun inputChanges(): Observable<Pair<FieldType, Comparable<*>>> {
        return Observable.merge(
            titleChanges().map { value -> Pair(FieldType.TITLE, value) },
            firstNameChanges().map { value -> Pair(FieldType.FIRST_NAME, value) },
            lastNameChanges().map { value -> Pair(FieldType.LAST_NAME, value) },
            nationalityChanges().map { value ->
                when (value) {
                    is NothingSelectedItem -> Pair(FieldType.NATIONALITY, 0)
                    is CountryItem -> Pair(FieldType.NATIONALITY, value.country)
                    is Divider -> Pair(FieldType.NATIONALITY, 0)
                } })
            .mergeWith(phoneNumberChanges().map { value -> Pair(FieldType.CONTACT_NUMBER, value) })
            .mergeWith(emailChanges().map { value -> Pair(FieldType.EMAIL, value) })
            .mergeWith(passportChanges().map { value -> Pair(FieldType.PASSPORT, value) })
            .mergeWith(nextDestinationChanges().map { value -> Pair(FieldType.NEXT_DESTINATION, value) })
    }


    private fun titleChanges(): Observable<String> {
        return titleSpinner.itemSelections().skipInitialValue()
                .map { titleOptions[it] }
                .distinctUntilChanged()
    }

    private fun firstNameChanges(): Observable<String> {
        return firstNameView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun lastNameChanges(): Observable<String> {
        return lastNameView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun nationalityChanges(): Observable<CountrySpinnerItem> {
        return nationalityChanges
                .distinctUntilChanged()
    }

    private fun phoneNumberChanges(): Observable<String> {
        return contactNumberView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun emailChanges(): Observable<String> {
        return emailAddressView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun passportChanges(): Observable<String> {
        return passportNumberView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }

    private fun nextDestinationChanges(): Observable<String> {
        return nextDestinationView.editText!!.afterTextChangeEvents().skipInitialValue()
                .map { it.view.text.toString() }
                .distinctUntilChanged()
    }
}