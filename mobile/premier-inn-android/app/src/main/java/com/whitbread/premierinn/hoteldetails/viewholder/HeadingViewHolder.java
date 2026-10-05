package com.whitbread.premierinn.hoteldetails.viewholder;

import static com.whitbread.premierinn.common.utils.ViewUtils.convertPixelsToDp;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Facility;
import com.whitbread.premierinn.common.view.NetworkImageView;
import com.whitbread.premierinn.databinding.ViewHotelDetailsHeadingBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.event.FacilitiesClickEvent;
import com.whitbread.premierinn.hoteldetails.uimodel.HeadingUiModel;

import java.util.List;

public class HeadingViewHolder extends BaseRecyclerViewHolder<HeadingUiModel> {

    private static final float FACILITY_ICON_SIZE = 24f;
    private static final int FACILITY_MARGIN = 8;
    private final ViewHotelDetailsHeadingBinding binding;

    HeadingViewHolder(ViewHotelDetailsHeadingBinding binding, PublishRelay<Object> publishRelay) {
        super(binding.getRoot());
        this.binding = binding;
        binding.facilitiesContainer.setOnClickListener(v -> publishRelay.accept(new FacilitiesClickEvent()));
    }

    @Override
    public void bind(HeadingUiModel model) {
        addFacilities(model.facilities());

        binding.tvHotelDetailsDistance.setText(
                itemView.getContext().getString(R.string.hotel_details_distance_from_your_search, model.distance())
        );
        if (model.tripAdvisor() != null && model.tripAdvisorRating() != null) {
            binding.hotelDetailsTripAdvisor.updateTripAdvisorViewState(model.tripAdvisorRating(), model.tripAdvisor());
        }

        if (!model.extraMessage().isEmpty()) {
            binding.hotelDetailsColorMessage.setVisibility(View.VISIBLE);
            binding.hotelDetailsColorMessage.setText(model.extraMessage());
            binding.premierMessageContainer.setVisibility(View.VISIBLE);
        }

        if (model.isPremierPlus()) {
            binding.hotelDetailsPremierHotel.setVisibility(View.VISIBLE);
            binding.premierMessageContainer.setVisibility(View.VISIBLE);
        }

        if (model.limitedAvailability()) {
            binding.hotelDetailsLastFewRooms.setVisibility(View.VISIBLE);
        }
    }

    private void addFacilities(List<Facility> facilities) {
        if (facilities.isEmpty()) {
            return;
        }

        binding.facilitiesContainer.removeAllViews();

        int imageSize = (int) convertPixelsToDp(FACILITY_ICON_SIZE, itemView.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        layoutParams.setMargins(0, 0, (int) convertPixelsToDp(FACILITY_MARGIN, itemView.getContext()), 0);

        for (Facility facility : facilities) {
            NetworkImageView facilityIcon = new NetworkImageView(itemView.getContext());
            facilityIcon.loadSvg(facility.icon());

            ImageViewCompat.setImageTintList(
                    facilityIcon,
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.premier_inn_purple)));

            binding.facilitiesContainer.addView(facilityIcon, layoutParams);
        }
    }
}