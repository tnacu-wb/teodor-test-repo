package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.ResetInputErrorAction;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.databinding.ViewCompoundEdittextBinding;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;

public class FormEditTextView extends TextInputLayout {

    private ViewCompoundEdittextBinding binding;
    private Boolean required;
    private CharSequence mask;
    private static final char EXPECTED_CHAR_PLACEHOLDER = '#';

    private boolean updating;
    private boolean deleting;

    public FormEditTextView(Context context) {
        super(context);
        init(null);
    }

    public FormEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FormEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        inflateLayout();

        bindCustomAttributes(attributeSet);
    }

    private void inflateLayout() {
        binding = ViewCompoundEdittextBinding.inflate(LayoutInflater.from(getContext()), this);
    }

    private void bindCustomAttributes(@Nullable AttributeSet attributeSet) {
        if (attributeSet == null) {
            return;
        }

        final TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.FormEditTextView);

        int editTextIdRes = typedArray.getResourceId(R.styleable.FormEditTextView_editTextId, 0);
        binding.editText.setId(editTextIdRes);

        int editTextInputType = typedArray.getInt(R.styleable.FormEditTextView_android_inputType, EditorInfo.TYPE_NULL);
        binding.editText.setInputType(editTextInputType);
        TextViewCompat.setTextAppearance(binding.editText, R.style.Body);

        int editTextImeOption = typedArray.getInt(R.styleable.FormEditTextView_android_imeOptions, EditorInfo.IME_NULL);
        binding.editText.setImeOptions(editTextImeOption);

        if (typedArray.hasValue(R.styleable.FormEditTextView_required)) {
            required = typedArray.getBoolean(R.styleable.FormEditTextView_required, false);
        } else {
            required = null;
        }
        if (required != null && !required) {
            setHint(getHint() + " (optional)");
        }

        setMask(typedArray.getString(R.styleable.FormEditTextView_mask));
        typedArray.recycle();
    }

    public String getInputText() {
        return getEditText().getText().toString();
    }

    public void setText(CharSequence text) {
        binding.editText.setText(text);
    }

    public Observable<Action> resetEvent() {
        return RxTextView.afterTextChangeEvents(getEditText())
                .skipInitialValue()
                .map(__ -> {
                    if (getError() == null) {
                        return StringUtils.EMPTY_STRING;
                    } else {
                        return getError();
                    }
                })
                .filter(allowIfErrorMessageExists())
                .map(__ -> ResetInputErrorAction.create(getId()));
    }

    @NonNull
    private Predicate<CharSequence> allowIfErrorMessageExists() {
        return error -> !StringUtils.isBlank(error);
    }

    private void setMask(CharSequence mask) {
        this.mask = mask;

        if (hasMask()) {
            setMaxLength(mask.length());
            getEditText().addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override public void afterTextChanged(Editable s) {
                    if (updating || !hasMask()) {
                        return;
                    }

                    updating = true;
                    applyMask(s);
                    updating = false;
                }
            });

            //detect delete
            getEditText().setOnKeyListener((v, keyCode, event) -> {
                deleting = event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL;
                return false;
            });
        }
    }

    private void applyMask(Editable textField) {
        if (TextUtils.isEmpty(textField) || !hasMask()) {
            return;
        }

        //save cursor position
        int cursorPosition = getEditText().getSelectionStart();

        //remove input filters to ignore input type
        InputFilter[] filters = textField.getFilters();
        textField.setFilters(new InputFilter[0]);

        String maskedText = textField.toString();
        String unmaskedText = getUnMaskedText(maskedText);
        textField.clear();

        if (deleting) {
            deleteWithMask(textField, cursorPosition, maskedText, unmaskedText);
            deleting = false;
        } else {
            applyMaskAfterCharacterEntered(textField, unmaskedText);
        }

        //reset filters
        textField.setFilters(filters);
    }

    private void deleteWithMask(Editable textField, int cursorPosition, String maskedText, String unmaskedText) {
        for (int i = 1; i < maskedText.length() && i < unmaskedText.length(); i++) {
            if (maskedText.charAt(maskedText.length() - 1) != unmaskedText.charAt(unmaskedText.length() - i)) {
                maskedText = maskedText.substring(0, maskedText.length() - 1);
            }
        }
        textField.append(maskedText);

        int currentTextLen = textField.length();
        //check if deleting
        if (deleting && cursorPosition < currentTextLen) {
            getEditText().setSelection(cursorPosition);
        }
    }

    private void applyMaskAfterCharacterEntered(Editable textField, String unmaskedText) {
        StringBuilder unmaskedTextSb = new StringBuilder(unmaskedText);
        for (int i = 0; i < mask.length() && (unmaskedTextSb.length() > 0 || mask.charAt(i) != EXPECTED_CHAR_PLACEHOLDER); i++) {
            char maskChar = mask.charAt(i);
            if (maskChar == EXPECTED_CHAR_PLACEHOLDER) {
                if (unmaskedTextSb.length() > 0) {
                    char unmaskedChar = unmaskedTextSb.charAt(0);
                    textField.append(unmaskedChar);
                    unmaskedTextSb.deleteCharAt(0);
                }
            } else {
                textField.append(maskChar);
            }
        }
    }

    private boolean hasMask() {
        return !TextUtils.isEmpty(mask);
    }

    private void setMaxLength(int length) {
        getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    private String getUnMaskedText(String text) {
        if (TextUtils.isEmpty(text) || !hasMask()) {
            return text;
        }

        int maskLen = mask.length();
        int textLen = text.length();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maskLen && i < textLen; i++) {
            char m = mask.charAt(i);
            char t = text.charAt(i);
            if (t != m) {
                sb.append(t);
            }
        }

        return sb.toString();
    }
}