package com.whitbread.premierinn.hoteldetails.viewholder;

import android.widget.LinearLayout;

import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ViewHotelDetailsRatePlanBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomRatesUiModel;


public class RatePlanViewHolder extends BaseRecyclerViewHolder<RoomRatesUiModel> {
    private final ViewHotelDetailsRatePlanBinding binding;
    private final PublishRelay<Object> relay;


    RatePlanViewHolder(ViewHotelDetailsRatePlanBinding binding, PublishRelay<Object> relay) {
        super(binding.getRoot());
        this.relay = relay;
        this.binding = binding;
    }

    @Override
    public void bind(RoomRatesUiModel item) {
        LinearLayout viewById = binding.hotelDetailsRoomPlans.findViewById(R.id.view_room_plan_main);
        viewById.removeAllViews();
        binding.hotelDetailsRoomPlans.setData(item, relay);
    }
}