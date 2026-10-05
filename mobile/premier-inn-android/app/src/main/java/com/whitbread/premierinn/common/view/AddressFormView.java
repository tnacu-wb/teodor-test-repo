package com.whitbread.premierinn.common.view;

import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.ADDRESS_LINE_1;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.ADDRESS_LINE_2;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.ADDRESS_LINE_3;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.COMPANY;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.COMPANY_SPECIAL_CHARACTER;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.POSTCODE;
import static com.whitbread.premierinn.common.Validator.COMPANY_NAME_MAX_LENGTH;
import static com.whitbread.premierinn.common.Validator.COMPANY_NAME_MIN_LENGTH;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.AddressField;
import com.whitbread.premierinn.common.AddressFormDataOutput;
import com.whitbread.premierinn.common.FormBind;
import com.whitbread.premierinn.common.adapter.CountryListAdapter;
import com.whitbread.premierinn.common.utils.ActivityUtilsKt;
import com.whitbread.premierinn.databinding.LayoutAddressFormBinding;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class AddressFormView extends RelativeLayout {

    public enum AddressState {
        HOME,
        WORK
    }

    private static final int INITIAL_SKIP_FOR_SPINNER = 2;
    private boolean isBusinessAddressSelected;

    public LayoutAddressFormBinding getBinding() {
        return binding;
    }

    LayoutAddressFormBinding binding;

    public AddressFormView(Context context) {
        super(context);
        init(context);
    }

    public AddressFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AddressFormView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context) {
        binding = LayoutAddressFormBinding.inflate(LayoutInflater.from(context), this);

        binding.cabAddressFormFindAddress.setOnClickListener(v -> onFindAddressClick());
    }

    public void onFindAddressClick() {
        Activity activity = ActivityUtilsKt.getActivityFromContext(getContext());
        if (activity != null) {
            PostcodeFinderActivity.start(getContext(), activity,
                    binding.etAddressFormPostcode.getText().toString());
        }
        // hardcoded to true until PostCodeFInder is Cleaned up at which point this will be removed
    }

    public void setCountries(@NonNull List<CountryDomain> countries) {
        CountryListAdapter countryAdapter = new CountryListAdapter(getContext(), countries);
        binding.sAddressFormCountries.setAdapter(countryAdapter);
    }

    public void setCountrySelection(int spinnerPosition) {
        binding.sAddressFormCountries.setSelection(spinnerPosition);
    }

    public void isAddressBusinessSelected(boolean isBusinessAddressSelected) {
        this.isBusinessAddressSelected = isBusinessAddressSelected;
    }

    public Observable<AddressFormDataOutput> getFormWithTextAndFocus() {
        return Observable
                .merge(Arrays.asList(
                        getCountryIndexSelected(),
                        getPostcode(),
                        getCompany(),
                        getAddressLine1(),
                        getAddressLine2(),
                        getCity()));
    }

    private Observable<AddressFormDataOutput> getCountryIndexSelected() {
        return RxAdapterView.itemSelections(binding.sAddressFormCountries).skip(INITIAL_SKIP_FOR_SPINNER)
                .map(index -> AddressFormDataOutput.create(index, AddressFormDataOutput.Form.COUNTRY));
    }

    private Observable<AddressFormDataOutput> getPostcode() {
        return getAddressFieldTextAndFocus(binding.etAddressFormPostcode, POSTCODE);
    }

    private Observable<AddressFormDataOutput> getAddressLine1() {
        return getAddressFieldTextAndFocus(binding.etAddressFormAddressLine1, ADDRESS_LINE_1);
    }

    private Observable<AddressFormDataOutput> getAddressLine2() {
        return getAddressFieldTextAndFocus(binding.etAddressFormAddressLine2, ADDRESS_LINE_2);
    }

    private Observable<AddressFormDataOutput> getCity() {
        return getAddressFieldTextAndFocus(binding.etAddressFormAddressLine3, ADDRESS_LINE_3);
    }

    private Observable<AddressFormDataOutput> getCompany() {
        return getAddressFieldTextAndFocus(binding.etAddressFormCompany, COMPANY);
    }

    private Observable<AddressFormDataOutput> getAddressFieldTextAndFocus(EditText field, AddressFormDataOutput.Form formType) {
        return FormBind.getFieldTextAndFocus(field)
                .map(pair -> AddressFormDataOutput.create(pair.first.toString(), pair.second, formType));
    }

    public Observable<Unit> onClickEnterManualAddress() {
        return RxView.clicks(binding.tvAddressFormManualAddress);
    }

    public void showValidationError(boolean showError, AddressFormDataOutput.Form form) {
        if (POSTCODE == form) {
            binding.tilAddressFormPostcode.setError(getContext().getString(R.string.address_form_postcode_validation_error));
            binding.tilAddressFormPostcode.setErrorEnabled(showError);
        } else if (ADDRESS_LINE_1 == form) {
            binding.tilAddressFormAddressLine1.setError(getContext().getString(R.string.address_form_address_line_1_validation_error));
            binding.tilAddressFormAddressLine1.setErrorEnabled(showError);
        } else if (COMPANY == form) {
            binding.tilAddressFormCompany.setError(
                    getContext().getString(
                            R.string.form_field_company_name_criteria,
                            COMPANY_NAME_MIN_LENGTH, COMPANY_NAME_MAX_LENGTH));
            binding.tilAddressFormCompany.setErrorEnabled(showError);
        } else if (COMPANY_SPECIAL_CHARACTER == form) {
            binding.tilAddressFormCompany.setError(
                    getContext().getString(
                            R.string.form_field_company_name_special_character_criteria));
            binding.tilAddressFormCompany.setErrorEnabled(showError);
        }
    }

    public void setAddressFields(@NonNull AddressField addressField) {
        if (addressField.postcode() != null && !addressField.postcode()
                .equals(binding.etAddressFormPostcode.getText().toString())) {
            binding.etAddressFormPostcode.setText(addressField.postcode());
        }
        if (addressField.addressLine1() != null && !addressField.addressLine1()
                .equals(binding.etAddressFormAddressLine1.getText().toString())) {
            binding.etAddressFormAddressLine1.setText(addressField.addressLine1());
        }
        if (addressField.addressLine2() != null && !addressField.addressLine2()
                .equals(binding.etAddressFormAddressLine2.getText().toString())) {
            binding.etAddressFormAddressLine2.setText(addressField.addressLine2());
        }
        if (addressField.city() != null && !addressField.city()
                .equals(binding.etAddressFormAddressLine3.getText().toString())) {
            binding.etAddressFormAddressLine3.setText(addressField.city());
        }
        if (addressField.companyName() != null && !addressField.companyName()
                .equals(binding.etAddressFormCompany.getText().toString())) {
            binding.etAddressFormCompany.setText(addressField.companyName());
        }
    }

    public void setCompanyVisibility(boolean visible) {
        binding.tilAddressFormCompany.setVisibility(visible ? VISIBLE : GONE);
    }

    public void showManualAddressSection(boolean show) {
        binding.rlManualAddressContainer.setVisibility(show ? VISIBLE : GONE);
        binding.tvAddressFormManualAddress.setVisibility(show ? GONE : VISIBLE);
    }

    public void showPostcodeAndFindAddressButton(boolean show) {
        binding.tilAddressFormPostcode.setVisibility(show ? VISIBLE : GONE);
        binding.cabAddressFormFindAddress.setVisibility(show ? VISIBLE : GONE);
    }

    public void showFindAddressButton(boolean show) {
        binding.cabAddressFormFindAddress.setVisibility(show ? VISIBLE : GONE);
    }

    public void showPostcode(boolean show) {
        binding.tilAddressFormPostcode.setVisibility(show ? VISIBLE : GONE);
    }

    public void clearFormFields() {
        binding.etAddressFormPostcode.setText(EMPTY);
        binding.etAddressFormAddressLine1.setText(EMPTY);
        binding.etAddressFormAddressLine2.setText(EMPTY);
        binding.etAddressFormAddressLine3.setText(EMPTY);
        binding.etAddressFormCompany.setText(EMPTY);
    }
}