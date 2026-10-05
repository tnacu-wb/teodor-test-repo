package com.whitbread.premierinn.guestdetails.view;

import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.CONTACT_NUMBER;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.EMAIL;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.FIRST_NAME;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.LAST_NAME;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.TITLE;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.FormBind;
import com.whitbread.premierinn.common.Validator;
import com.whitbread.premierinn.domain.common.DEExtensionsKt;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class GuestDetailsFormView extends LinearLayout {

    private Spinner sTitle;
    private TextInputLayout tilFirstName;
    private TextInputLayout tilLastName;
    private TextInputLayout tilContactNumber;
    private TextInputLayout tilEmail;
    private EditText etContactNumber;
    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;

    private List<String> titles;

    public GuestDetailsFormView(Context context) {
        super(context);
        init(context, null);
    }

    public GuestDetailsFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GuestDetailsFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public GuestDetailsFormView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public Observable<GuestDetailsFormDataOutput> getFormWithTextAndFocus() {
        return Observable.merge(
                getTitle(),
                getFirstNameTextAndFocus(),
                getLastNameTextAndFocus(),
                getEmailTextAndFocus()
        ).mergeWith(getContactNumberTextAndFocus());
    }

    private Observable<GuestDetailsFormDataOutput> getTitle() {
        return RxAdapterView.itemSelections(sTitle)
                .map(index -> GuestDetailsFormDataOutput.create(titles.get(index), false, TITLE));
    }

    private Observable<GuestDetailsFormDataOutput> getFirstNameTextAndFocus() {
        return getGuestFieldTextAndFocus(etFirstName, FIRST_NAME);
    }

    private Observable<GuestDetailsFormDataOutput> getLastNameTextAndFocus() {
        return getGuestFieldTextAndFocus(etLastName, LAST_NAME);
    }

    private Observable<GuestDetailsFormDataOutput> getContactNumberTextAndFocus() {
        return getGuestFieldTextAndFocus(etContactNumber, CONTACT_NUMBER);
    }

    private Observable<GuestDetailsFormDataOutput> getEmailTextAndFocus() {
        return getGuestFieldTextAndFocus(etEmail, EMAIL);
    }

    private Observable<GuestDetailsFormDataOutput> getGuestFieldTextAndFocus(EditText field, GuestDetailsFormDataOutput.Form form) {
        return FormBind.getFieldTextAndFocus(field)
                .map(pair -> GuestDetailsFormDataOutput.create(pair.first.toString(), pair.second, form));
    }

    public void showFormValidationError(boolean show, GuestDetailsFormDataOutput.Form form) {
        if (FIRST_NAME.equals(form) && tilFirstName.isErrorEnabled() != show) {
            tilFirstName.setError(getContext().getString(R.string.guest_details_first_name_validation_error));
            tilFirstName.setErrorEnabled(show);
        } else if (LAST_NAME.equals(form) && tilLastName.isErrorEnabled() != show) {
            tilLastName.setError(getContext().getString(R.string.guest_details_last_name_validation_error));
            tilLastName.setErrorEnabled(show);
        } else if (CONTACT_NUMBER.equals(form) && tilContactNumber.isErrorEnabled() != show) {
            tilContactNumber.setError(getContext().getString(R.string.guest_details_contact_number_validation_error));
            tilContactNumber.setErrorEnabled(show);
        } else if (EMAIL.equals(form) && tilEmail.isErrorEnabled() != show) {
            tilEmail.setError(getContext().getString(R.string.invalid_email_error));
            tilEmail.setErrorEnabled(show);
        }
    }

    public void hideEmailField() {
        tilEmail.setVisibility(View.GONE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_guest_details_form, this, true);

        sTitle = (Spinner) getChildAt(0);
        tilFirstName = (TextInputLayout) getChildAt(1);
        etFirstName = tilFirstName.getEditText();
        tilLastName = (TextInputLayout) getChildAt(2);
        etLastName = tilLastName.getEditText();
        tilContactNumber = (TextInputLayout) getChildAt(3);
        etContactNumber = tilContactNumber.getEditText();
        tilEmail = (TextInputLayout) getChildAt(4);
        etEmail = tilEmail.getEditText();

        if (!isInEditMode()) {
            this.titles = Arrays.asList(getResources().getStringArray(R.array.titles));
            setupTitlesOptions();
        }

        if (attrs != null) {
            TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.GuestDetailsFormView);
            if (attributesArray.getBoolean(R.styleable.GuestDetailsFormView_gdfv_show_contact_number, false)) {
                tilContactNumber.setVisibility(VISIBLE);
            }
            attributesArray.recycle();
        }

        applyCharacterFilters();
    }

    private void applyCharacterFilters() {
        InputFilter firstNameCharacterFilter = (source, start, end, dest, destStart, destEnd) -> {
            if (!Validator.isFirstNameValid(source.toString()) && !source.toString().isEmpty()) {
                return ""; // Reject input
            } else {
                return null; // Accepts input
            }
        };
        InputFilter lastNameCharacterFilter = (source, start, end, dest, destStart, destEnd) -> {
            if (!Validator.isLastNameValid(source.toString()) && !source.toString().isEmpty()) {
                return ""; // Reject input
            } else {
                return null; // Accepts input
            }
        };

        InputFilter firstNameLengthFilter = new InputFilter.LengthFilter(Validator.MAX_FIRST_NAME_LENGTH);
        InputFilter lastNameLengthFilter = new InputFilter.LengthFilter(Validator.MAX_LAST_NAME_LENGTH);
        etFirstName.setFilters(new InputFilter[]{firstNameLengthFilter, firstNameCharacterFilter});
        etLastName.setFilters(new InputFilter[]{lastNameLengthFilter, lastNameCharacterFilter});
    }

    private void setupTitlesOptions() {
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list_popup_selected_item, titles);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.list_popup_dropdown_items);
        sTitle.setAdapter(spinnerArrayAdapter);
    }

    public void setValue(GuestDetailsFormDataInput guestDetailsFormDataInput, boolean onlyErrors) {
        if (!onlyErrors) {
            etFirstName.setText(guestDetailsFormDataInput.firstName());
            // Checking that the input filters have accepted the input. If not, show an inline error
            if (!etFirstName.getText().toString().equals(guestDetailsFormDataInput.firstName())) {
                guestDetailsFormDataInput = guestDetailsFormDataInput.toBuilder().errorFirstName(true).build();
            }

            etLastName.setText(guestDetailsFormDataInput.lastName());
            // Checking that the input filters have accepted the input. If not, show an inline error
            if (!etLastName.getText().toString().equals(guestDetailsFormDataInput.lastName())) {
                guestDetailsFormDataInput = guestDetailsFormDataInput.toBuilder().errorLastName(true).build();
            }

            etEmail.setText(guestDetailsFormDataInput.email());
            String title = guestDetailsFormDataInput.title();
            if (!title.isEmpty()) {
                String mappedTitle = DEExtensionsKt.translateTitleToGermanIfApplicable(title, guestDetailsFormDataInput.language());
                sTitle.setSelection(((ArrayAdapter<String>) sTitle.getAdapter()).getPosition(mappedTitle));
            }
            if (guestDetailsFormDataInput.phoneNumber() != null) {
                etContactNumber.setText(guestDetailsFormDataInput.phoneNumber());
            }
        }
        showFormValidationError(guestDetailsFormDataInput.errorFirstName(), FIRST_NAME);
        showFormValidationError(guestDetailsFormDataInput.errorLastName(), LAST_NAME);
        showFormValidationError(guestDetailsFormDataInput.errorEmail(), EMAIL);
    }
}
