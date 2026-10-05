package com.whitbread.premierinn.hoteldetails.viewholder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.common.view.RoomRatesView;
import com.whitbread.premierinn.databinding.ViewRoomPlansBinding;
import com.whitbread.premierinn.domain.hotel.entity.RoomVariant;
import com.whitbread.premierinn.hoteldetails.event.FindOutMoreClickEvent;
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.HDPExtensionsKt;
import com.whitbread.premierinn.hoteldetails.uimodel.RateBoxUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomRatesUiModel;

import java.util.List;

/**
 * This view can accommodate up to two roomTypes options - each with multiple rates (i.e Flex, Non-Flex, Semi-Flex)
 * <p>
 * Possible combinations are
 * -------------------------
 * Premium - Standard
 * Business - Standard
 * Bigger - Standard
 * <p>
 * or
 * <p>
 * Just Standard
 * Just Accessible (without showing title)
 * <p>
 * RoomType Title and Learn More link Logic
 * ----------------------------------------
 * Show always title and link of the respective room variant option unless there is accessible letting type in any of the rooms offered
 * Also show Standard title when there is silent substitution.
 */
public class RoomsView extends LinearLayout {

    private ViewRoomPlansBinding binding;

    public RoomsView(Context context) {
        super(context);
        init(context);
    }

    public RoomsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoomsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RoomsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewRoomPlansBinding.inflate(inflater, this, true);
    }

    public void setData(RoomRatesUiModel roomRatesUiModel, PublishRelay<Object> relay) {
        binding.ratesCityTaxMessage.setVisibility(roomRatesUiModel.showCityTaxMessage() ? View.VISIBLE : View.GONE);

        // Show invalid discount code error message if present
        if (roomRatesUiModel.invalidDiscountCodeMessage() != null && !roomRatesUiModel.invalidDiscountCodeMessage().isEmpty()) {
            binding.invalidDiscountCodeContainer.setVisibility(View.VISIBLE);
            binding.invalidDiscountCodeMessageText.setText(roomRatesUiModel.invalidDiscountCodeMessage());
        } else {
            binding.invalidDiscountCodeContainer.setVisibility(View.GONE);
        }

        // every item in the List is another RoomType (Standard or Bigger etc)
        for (List<RateBoxUiModel> listOfRateTypes : roomRatesUiModel.roomRates()) {
            RateBoxUiModel rate = listOfRateTypes.get(0);
            List<RateBoxUiModel> listOfRateTypesFiltered = HDPExtensionsKt.filterDuplicates(listOfRateTypes);
            final RoomVariant roomVariant = rate.roomVariant();

            if (rate.alternativeType()) {
                RoomRatesView alternateRoomRatesView = new RoomRatesView(getContext());

                alternateRoomRatesView.setTag(roomVariant);
                alternateRoomRatesView.setVisibility(View.VISIBLE);
                alternateRoomRatesView.setData(listOfRateTypesFiltered, getRoomVariantTitle(roomVariant), relay, true);

                RxView.clicks(alternateRoomRatesView).subscribe(__ -> relay.accept(FindOutMoreClickEvent.INSTANCE));

                binding.viewRoomPlanMain.addView(alternateRoomRatesView);
            } else {
                RoomRatesView standardRoomRatesView = new RoomRatesView(getContext());

                standardRoomRatesView.setTag(roomVariant);
                standardRoomRatesView.setData(listOfRateTypes, getRoomVariantTitle(roomVariant), relay, false);

                RxView.clicks(standardRoomRatesView).subscribe(__ -> relay.accept(FindOutMoreClickEvent.INSTANCE));

                binding.viewRoomPlanMain.addView(standardRoomRatesView);
            }
        }
    }

    @Nullable
    public String getRoomVariantTitle(@NonNull RoomVariant variant) {
        return variant.getVariant();
    }

}