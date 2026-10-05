package com.whitbread.premierinn.common.view;

import static com.whitbread.premierinn.common.utils.AppExtensions.dpToPx;
import static com.whitbread.premierinn.domain.common.Constants.EMPLOYEE_RATE_PLAN_CODE;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ViewPriceBoxBinding;
import com.whitbread.premierinn.hoteldetails.uimodel.RateBoxUiModel;

import io.reactivex.Observable;
import kotlin.Unit;

public class PriceBoxView extends ConstraintLayout {

    private ViewPriceBoxBinding binding;

    public PriceBoxView(Context context) {
        super(context);
        init(context, null);
    }

    public PriceBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PriceBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewPriceBoxBinding.inflate(inflater, this);
    }

    public void setData(RateBoxUiModel model) {
        this.binding.priceBoxRateName.setText(model.title());
        this.binding.tvPriceBoxPrice.setText(model.price());

        if (model.formattedBaseRate() != null) {
            this.binding.tvPriceBoxPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_dark));
            this.binding.tvPriceBoxStrikethrough.setText(model.formattedBaseRate());
            binding.tvPriceBoxStrikethrough.setPaintFlags(
                    binding.tvPriceBoxStrikethrough.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.binding.tvPriceBoxStrikethrough.setVisibility(VISIBLE);
        }

        if (model.promoTag() != null && !model.promoTag().isEmpty()) {
            binding.llLabels.setVisibility(VISIBLE);
            binding.promoLozenge.setText(model.promoTag());
            binding.promoLozenge.setVisibility(VISIBLE);
        }

        this.binding.tvPriceBoxDetails.setText(model.description());
        if (model.rateClassification().equals(EMPLOYEE_RATE_PLAN_CODE)) {
            binding.llLabels.setVisibility(VISIBLE);
            binding.tvEmployeeOfferLabel.setVisibility(VISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.tvPriceBoxPrice.getLayoutParams();
            params.topMargin = dpToPx(getContext(), 6);
            binding.tvPriceBoxPrice.setLayoutParams(params);
        }
        if (model.alternateRoomSelectionAvailable()) {
            binding.calSaverButton.binding.tvCallToActionButtonText.setText(getResources().getString(R.string.price_box_select));
        } else {
            binding.calSaverButton.binding.tvCallToActionButtonText.setText(getResources().getString(R.string.price_box_book));
        }

        String nightsString = getResources().getQuantityString(R.plurals.nights, model.nights(), model.nights());
        String roomsString = getResources().getQuantityString(R.plurals.rooms, model.rooms(), model.rooms());
        this.binding.ratePriceBoxNightsRooms.setText(getResources().getString(R.string.hotel_details_rate_price_box_nights_rooms,
                nightsString, roomsString));
        setVisibility(VISIBLE);
        setBackgroundColor(getResources().getColor(R.color.white));
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_hotel_details_saver_rate_background));

    }

    public void setEnabled(boolean state) {
        binding.calSaverButton.setEnabled(state);
    }

    public void setLoadingState(boolean loadingState) {
        binding.calSaverButton.setLoadingState(loadingState);
    }

    public Observable<Unit> onPriceSelectClicked() {
        return binding.calSaverButton.onClickOnInternetAvailable();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
