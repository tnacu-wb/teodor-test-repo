package com.whitbread.premierinn.paymentdetails;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ViewCreditCardCvvBinding;

import io.reactivex.Observable;

public class CreditCardCvvView extends LinearLayout {

    public static final int FRONT_OF_CARD_CVV_LENGTH = 4;
    public static final int BACK_OF_CARD_CVV_LENGTH = 3;

    private ViewCreditCardCvvBinding binding;

    public CreditCardCvvView(Context context) {
        super(context);
        init(context);
    }

    public CreditCardCvvView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CreditCardCvvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CreditCardCvvView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public Observable<Pair<CharSequence, Boolean>> getCvvAndFocus() {
        return Observable.combineLatest(RxTextView.textChanges(binding.etCreditCardCvv),
                RxView.focusChanges(binding.etCreditCardCvv).skipInitialValue(), Pair::new);
    }

    public void showCvvError(boolean show) {
        binding.tvCreditCardCvvError.setVisibility(show ? VISIBLE : GONE);
        binding.tvCreditCardCvvError.setText(getContext().getString(R.string.card_details_form_cvv_validation_error));
    }

    public void showFrontOfCardCvv(boolean isFront) {
        binding.ivCreditCardCvvIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),
                isFront ? R.drawable.ic_cvv_front : R.drawable.ic_cvv_back));
        binding.tvCreditCardCvvText.setText(isFront ? R.string.card_details_form_cvv_front : R.string.card_details_form_cvv_back);

        InputFilter[] maxLength = new InputFilter[]
                {new InputFilter.LengthFilter(isFront ? FRONT_OF_CARD_CVV_LENGTH : BACK_OF_CARD_CVV_LENGTH)};
        binding.etCreditCardCvv.setFilters(maxLength);
    }

    public void clearCvv() {
        binding.etCreditCardCvv.setText("");
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            binding.tvCreditCardCvvError.setVisibility(GONE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void init(@NonNull Context context) {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewCreditCardCvvBinding.inflate(inflater, this, true);
    }

    private void init(@NonNull Context context, @NonNull AttributeSet attrs) {
        init(context);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CreditCardCvvView);
        if (typedArray.hasValue(R.styleable.CreditCardCvvView_cvvVisible)) {
            setVisibility(typedArray.getBoolean(R.styleable.CreditCardCvvView_cvvVisible, true)
                    ? VISIBLE : GONE);
        }
        typedArray.recycle();
    }
}
