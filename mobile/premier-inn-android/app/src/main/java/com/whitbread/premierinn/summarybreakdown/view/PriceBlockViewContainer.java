package com.whitbread.premierinn.summarybreakdown.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ItemSummaryBreakdownPriceBlockBinding;
import com.whitbread.premierinn.hoteldetails.DailyRateInput;


public class PriceBlockViewContainer {

    private ItemSummaryBreakdownPriceBlockBinding binding;

    public PriceBlockViewContainer(Context context) {
        binding = ItemSummaryBreakdownPriceBlockBinding.inflate(LayoutInflater.from(context));
    }

    public PriceBlockViewContainer setDailyRate(DailyRateInput dailyRate, DeviceLocaleProvider deviceLocaleProvider) {
        binding.tvSummaryBreakdownPriceBlockPrice.setText(PriceFormat.format(dailyRate.getPrice().getAmount(),
                dailyRate.getPrice().getCurrency(), deviceLocaleProvider));
        binding.tvSummaryBreakdownPriceBlockDate.setText(
                FormatExtensionsKt.format(dailyRate.getDate(), DateFormat.DAY_DATE_MONTH_FULL_NAME));
        return this;
    }

    public View getView() {
        return binding.getRoot();
    }
}
