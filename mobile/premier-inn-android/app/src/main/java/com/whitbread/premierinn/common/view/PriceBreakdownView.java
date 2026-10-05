package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ViewPriceBreakdownBinding;
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownRoom;
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownRoomAdapter;

import java.util.List;


public class PriceBreakdownView extends LinearLayout {

    private ViewPriceBreakdownBinding binding;

    public PriceBreakdownView(Context context) {
        super(context);
        init();
    }

    public PriceBreakdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PriceBreakdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PriceBreakdownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        binding = ViewPriceBreakdownBinding.inflate(LayoutInflater.from(getContext()), this);
    }

    public void setFields(int guests, int nights, @NonNull String formattedArrivalDate, @NonNull String formattedDepartureDate,
                          @NonNull List<SummaryBreakdownRoom> rooms, @NonNull DeviceLocaleProvider deviceLocaleProvider,
                          String checkInInfo, String checkOutInfo) {
        String formattedGuests = getResources().getQuantityString(R.plurals.guests, guests, guests);
        String formattedNights = getResources().getQuantityString(R.plurals.nights, nights, nights);
        String guestsAndNights = getResources().getString(R.string.summary_breakdown_guests_and_nights, formattedGuests, formattedNights);
        binding.tvSummaryBreakdownGuestsAndNights.setText(guestsAndNights);
        binding.tvSummaryBreakdownArrivalDate.setText(formattedArrivalDate);
        binding.tvSummaryBreakdownDepartureDate.setText(formattedDepartureDate);
        binding.rvSummaryBreakdownRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSummaryBreakdownRooms.setNestedScrollingEnabled(false);
        binding.rvSummaryBreakdownRooms.setAdapter(new SummaryBreakdownRoomAdapter(rooms, deviceLocaleProvider));
        binding.tvSummaryBreakdownCheckInTime.setText(checkInInfo);
        binding.tvSummaryBreakdownCheckOutTime.setText(checkOutInfo);
    }
}
