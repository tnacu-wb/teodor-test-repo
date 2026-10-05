package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ViewBalanceOutstandingBinding;
import com.whitbread.premierinn.domain.common.PriceDomain;


public class BalanceOutstandingView extends RelativeLayout {

    private ViewBalanceOutstandingBinding binding;

    public BalanceOutstandingView(Context context) {
        super(context);
        init();
    }

    public BalanceOutstandingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BalanceOutstandingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        binding = ViewBalanceOutstandingBinding.inflate(LayoutInflater.from(getContext()), this);
    }

    public void setBalanceOutstandingPrice(@NonNull PriceDomain balanceOutstanding,
                                           @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        binding.balanceOutstandingPrice.setText(PriceFormat.format(balanceOutstanding.getAmount(),
                balanceOutstanding.getCurrency(), deviceLocaleProvider));
    }
}