package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ViewTotalPriceBinding;
import com.whitbread.premierinn.domain.common.PriceDomain;


public class TotalPriceView extends RelativeLayout {

    private ViewTotalPriceBinding binding;

    public TotalPriceView(Context context) {
        super(context);
        init();
    }

    public TotalPriceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TotalPriceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TotalPriceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = ViewTotalPriceBinding.inflate(LayoutInflater.from(getContext()), this);
    }

    public void setBookingTotal(@NonNull PriceDomain bookingPrice,
                                @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        binding.totalBookingPrice.setText(PriceFormat.format(bookingPrice.getAmount(),
                bookingPrice.getCurrency(), deviceLocaleProvider));
    }

    public void setTotalPrice(@NonNull String total) {
        binding.totalBookingPrice.setText(total);
    }

    public void setTotalPrice(@NonNull SpannableString total) {
        TextViewCompat.setTextAppearance(binding.totalBookingPrice, R.style.Body);
        binding.totalBookingPrice.setText(total);
    }

    public void setRateName(@NonNull String rateName) {
        this.binding.bookingRateLabel.setVisibility(VISIBLE);
        this.binding.bookingRateName.setVisibility(VISIBLE);
        this.binding.bookingRateName.setText(rateName);
    }

    public void setBookingPriceLabel(@NonNull String priceLabel) {
        binding.totalBookingLabel.setText(priceLabel);
    }

    public void showPriceIncludesTaxesAndFeesMessage(boolean show) {
        binding.totalBookingCityTaxInclMessage.setText(R.string.review_booking_taxes_and_fees_message);
        binding.totalBookingCityTaxInclMessage.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}