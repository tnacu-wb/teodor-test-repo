package com.whitbread.premierinn.criteria.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.databinding.ViewCallUsBinding;

import io.reactivex.disposables.CompositeDisposable;

public class CallUsView extends LinearLayout {

    private ViewCallUsBinding binding;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String telPhoneNumber;

    public CallUsView(Context context) {
        super(context);
        init(context, null);
    }

    public CallUsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CallUsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        binding = ViewCallUsBinding.inflate(LayoutInflater.from(getContext()), this);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        if (attrs != null) {
            TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.CallUsView);
            String descriptiveText = attributesArray.getString(R.styleable.CallUsView_cuv_descriptive_text);
            if (descriptiveText != null && descriptiveText.length() > 0) {
                binding.viewCallUsDescription.setText(descriptiveText);
            }
            attributesArray.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        compositeDisposable.add(RxView.clicks(binding.viewCallUsButton).subscribe(__ -> {
            if (telPhoneNumber != null) {
                Intent callIntent = IntentUtils.createTelephoneIntent(telPhoneNumber);
                if (IntentUtils.checkIntentResolvedActivity(getContext(), callIntent)) {
                    getContext().startActivity(callIntent);
                } else {
                    Toast.makeText(getContext().getApplicationContext(), R.string.phone_call_action_not_supported, Toast.LENGTH_LONG)
                            .show();
                }
            }
        }));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear();
        }
    }

    public void setTelephoneNumber(@NonNull String telephoneNumber) {
        this.telPhoneNumber = telephoneNumber;
    }

    public void setDescription(@NonNull String text) {
        binding.viewCallUsDescription.setText(text);
    }

    public void setLabel(@NonNull String text) {
        binding.viewCallUsLabel.setText(text);
    }
}
