package com.whitbread.premierinn.paymentdetails;

import static com.whitbread.premierinn.paymentdetails.CreditCardCvvView.BACK_OF_CARD_CVV_LENGTH;
import static com.whitbread.premierinn.paymentdetails.CreditCardCvvView.FRONT_OF_CARD_CVV_LENGTH;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.whitbread.premierinn.R;

import com.whitbread.premierinn.databinding.ViewCreditCardDetailsFormBinding;

import io.reactivex.Observable;
import kotlin.Pair;

public class CreditCardDetailsFormView extends LinearLayout {
    private @NonNull ViewCreditCardDetailsFormBinding binding;


    public CreditCardDetailsFormView(Context context) {
        super(context);
        init(context);
    }

    public CreditCardDetailsFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CreditCardDetailsFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CreditCardDetailsFormView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Card Number
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Observable<CharSequence> getCreditCardNumber() {
        return RxTextView.textChanges(binding.etCardDetailsFormCardNumber);
    }

    public void showCreditCardIcon(@Nullable String cardType) {
        if (cardType != null) {
            binding.ivCardDetailsFormCardIcon.setVisibility(VISIBLE);
            binding.ivCardDetailsFormCardIcon.load(cardType);
        } else {
            binding.ivCardDetailsFormCardIcon.setVisibility(GONE);
        }
    }

    public void showCreditCardError(boolean show) {
        binding.tilCardDetailsFormCardNumber.setError(getContext().getString(R.string.card_details_form_card_number_validation_error));
        binding.tilCardDetailsFormCardNumber.setErrorEnabled(show);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Name On Card
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Observable<Pair<CharSequence, Boolean>> getNameOnCard() {
        return Observable.combineLatest(
                RxTextView.textChanges(binding.etCardDetailsFormNameOnCard),
                RxView.focusChanges(binding.etCardDetailsFormNameOnCard).skip(1), Pair::new);
    }

    public void showNameOnCardError(boolean show) {
        binding.tilCardDetailsFormNameOnCard.setError(getContext().getString(R.string.card_details_form_name_on_card_validation_error));
        binding.tilCardDetailsFormNameOnCard.setErrorEnabled(show);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Start Date
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Observable<DateFieldInfo> getStartDateFieldInfo() {
        return Observable.combineLatest(
                        RxTextView.textChanges(binding.etCardDetailsFormStartDate),
                        RxView.focusChanges(binding.etCardDetailsFormStartDate).skip(1), Pair::new
                )
                .map(dateAndFocus ->
                        DateFieldInfo.create(
                                dateAndFocus.getFirst(),
                                dateAndFocus.getSecond(),
                                binding.etCardDetailsFormStartDate.getSelectionEnd())
                );
    }

    public void showStartDateError(boolean show) {
        binding.tilCardDetailsFormStartDate.setError(getContext().getString(R.string.card_details_form_start_date_validation_error));
        binding.tilCardDetailsFormStartDate.setErrorEnabled(show);
    }

    public void setFormattedStartDate(@NonNull String formattedDate, int cursorPosition) {
        binding.etCardDetailsFormStartDate.setText(formattedDate);
        binding.etCardDetailsFormStartDate.setSelection(cursorPosition);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Expiry Date

    /// /////////////////////////////////////////////////////////////////////////////////////////////
    public Observable<DateFieldInfo> getExpiryDateFieldInfo() {
        return Observable.combineLatest(
                        RxTextView.textChanges(binding.etCardDetailsFormExpiryDate),
                        RxView.focusChanges(binding.etCardDetailsFormExpiryDate).skip(1), Pair::new
                )
                .map(dateAndFocus ->
                        DateFieldInfo.create(
                                dateAndFocus.getFirst(),
                                dateAndFocus.getSecond(),
                                binding.etCardDetailsFormExpiryDate.getSelectionEnd())
                );
    }

    public void showExpiryDateError(boolean show) {
        binding.tilCardDetailsFormExpiryDate.setError(getContext().getString(R.string.card_details_form_expiry_date_validation_error));
        binding.tilCardDetailsFormExpiryDate.setErrorEnabled(show);
    }

    public void setFormattedExpiryDate(@NonNull String formattedDate, int cursorPosition) {
        binding.etCardDetailsFormExpiryDate.setText(formattedDate);
        binding.etCardDetailsFormExpiryDate.setSelection(cursorPosition);
    }

    public void showStartDateField(boolean show) {
        binding.tilCardDetailsFormStartDate.setVisibility(show ? VISIBLE : GONE);
    }

    public boolean isStartDateVisible() {
        return binding.tilCardDetailsFormStartDate.getVisibility() == VISIBLE;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Issue Number
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Observable<Pair<CharSequence, Boolean>> getIssueNumberAndFocus() {
        return Observable.combineLatest(
                RxTextView.textChanges(binding.etCardDetailsFormIssueNumber),
                RxView.focusChanges(binding.etCardDetailsFormIssueNumber).skip(1), Pair::new
        );
    }

    public void showIssueNumberError(boolean show) {
        binding.tilCardDetailsFormIssueNumber.setError(getContext().getString(R.string.card_details_form_issue_number_validation_error));
        binding.tilCardDetailsFormIssueNumber.setErrorEnabled(show);
    }

    public void showIssueNumberField(boolean show) {
        binding.tilCardDetailsFormIssueNumber.setVisibility(show ? VISIBLE : GONE);
    }

    public boolean isIssueNumberVisible() {
        return binding.tilCardDetailsFormIssueNumber.getVisibility() == VISIBLE;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Credit Card Fee
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void showCreditCardFee(@Nullable String fee) {
        if (fee != null) {
            binding.tvCardDetailsFormFee.setVisibility(VISIBLE);
            binding.tvCardDetailsFormFee.setText(getContext().getString(R.string.card_details_form_fee_format, fee));
        } else {
            binding.tvCardDetailsFormFee.setVisibility(INVISIBLE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CVV
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void showCvv(Boolean show) {
        binding.tilCardDetailsFormCvv.setVisibility(show ? VISIBLE : GONE);
        binding.ivCreditCardCvvIcon.setVisibility(show ? VISIBLE : GONE);
        binding.tvCreditCardCvvText.setVisibility(show ? VISIBLE : GONE);
        if (!show) {
            binding.etCardDetailsFormCvv.setText("");
        }
    }

    public Observable<Pair<CharSequence, Boolean>> getCvv() {
        return Observable.combineLatest(
                RxTextView.textChanges(binding.etCardDetailsFormCvv),
                RxView.focusChanges(binding.etCardDetailsFormCvv).skipInitialValue(), Pair::new
        );
    }

    public void showFrontOfCardCvv(boolean isFront) {
        binding.ivCreditCardCvvIcon.setImageDrawable(
                ContextCompat.getDrawable(
                        getContext(),
                        isFront ? R.drawable.ic_cvv_front : R.drawable.ic_cvv_back
                ));
        binding.tvCreditCardCvvText.setText(isFront ? R.string.card_details_form_cvv_front : R.string.card_details_form_cvv_back);

        InputFilter[] maxLength = new InputFilter[]
                {new InputFilter.LengthFilter(isFront ? FRONT_OF_CARD_CVV_LENGTH : BACK_OF_CARD_CVV_LENGTH)};
        binding.etCardDetailsFormCvv.setFilters(maxLength);
    }

    public void showCvvError(boolean show) {
        binding.tilCardDetailsFormCvv.setError(getContext().getString(R.string.card_details_form_cvv_validation_error));
        binding.tilCardDetailsFormCvv.setErrorEnabled(show);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void init(@NonNull Context context) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewCreditCardDetailsFormBinding.inflate(inflater, this);
        setOrientation(VERTICAL);
    }

    public void setState(@NonNull String cardNumber, @NonNull String name, @NonNull String expiryDate) {
        if (!cardNumber.isEmpty()) {
            binding.etCardDetailsFormCardNumber.setText(cardNumber);
        }
        if (!name.isEmpty()) {
            binding.etCardDetailsFormNameOnCard.setText(name);
        }
        if (!expiryDate.isEmpty()) {
            binding.etCardDetailsFormExpiryDate.setText(expiryDate);
        }
    }

    public void clearFormFields() {
        binding.etCardDetailsFormCardNumber.setText(EMPTY);
        binding.etCardDetailsFormNameOnCard.setText(EMPTY);
        binding.etCardDetailsFormStartDate.setText(EMPTY);
        binding.etCardDetailsFormExpiryDate.setText(EMPTY);
        binding.etCardDetailsFormIssueNumber.setText(EMPTY);
        binding.ivCardDetailsFormCardIcon.load(null);
    }
}
