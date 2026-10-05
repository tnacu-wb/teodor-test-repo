package com.whitbread.premierinn.hoteldetails.viewholder;

import android.view.View;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.databinding.ViewHotelDetailsFullyBookedBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.event.CheckAvailabilityClickEvent;
import com.whitbread.premierinn.hoteldetails.event.EditDatesClickEvent;
import com.whitbread.premierinn.hoteldetails.event.HotelsNearbyClickEvent;
import com.whitbread.premierinn.hoteldetails.uimodel.FullyBookedUiModel;

class FullyBookedViewHolder extends BaseRecyclerViewHolder<FullyBookedUiModel> {
    private final ViewHotelDetailsFullyBookedBinding binding;

    FullyBookedViewHolder(ViewHotelDetailsFullyBookedBinding binding, PublishRelay<Object> relay) {
        super(binding.getRoot());
        this.binding = binding;
        RxView.clicks(binding.hotelDetailsHotelNearbyButton)
                .subscribe(o -> relay.accept(new HotelsNearbyClickEvent(getAdapterPosition())));
        RxView.clicks(binding.editDateContainer)
                .subscribe(o -> relay.accept(new EditDatesClickEvent(getAdapterPosition())));
        RxView.clicks(binding.hotelDetailsCheckAvailabilityButton)
                .subscribe(o -> relay.accept(new CheckAvailabilityClickEvent(getAdapterPosition())));
    }

    @Override
    public void bind(FullyBookedUiModel item) {
        switch (item.type()) {
            case FULLY_BOOKED:
                binding.hotelDetailsHotelNearbyButton.setVisibility(View.VISIBLE);
                binding.fullyBookedContainer.setVisibility(View.VISIBLE);
                binding.editDateContainer.setVisibility(View.GONE);
                binding.hotelDetailsCheckAvailabilityButton.setVisibility(View.GONE);
                break;
            case CHECK_AVAILABILITY:
                binding.hotelDetailsHotelNearbyButton.setVisibility(View.GONE);
                binding.fullyBookedContainer.setVisibility(View.GONE);
                binding.editDateContainer.setVisibility(View.GONE);
                binding.hotelDetailsCheckAvailabilityButton.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }
}