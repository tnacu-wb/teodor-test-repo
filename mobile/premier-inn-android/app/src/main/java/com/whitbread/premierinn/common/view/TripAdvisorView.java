package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.HotelInfo;
import com.whitbread.premierinn.api.response.availability.TripAdvisor;
import com.whitbread.premierinn.databinding.ViewTripAdvisorBinding;
import com.whitbread.premierinn.domain.common.TripAdvisorRating;

public class TripAdvisorView extends LinearLayout {

    private ViewTripAdvisorBinding binding;

    public TripAdvisorView(Context context) {
        super(context);
        init();
    }

    public TripAdvisorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TripAdvisorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TripAdvisorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = ViewTripAdvisorBinding.inflate(LayoutInflater.from(getContext()), this);
    }

    public void update(String text) {
        binding.tvTripAdvisorNumberReviews.setText(text);
    }

    public void setTripAdvisorViewState(@Nullable TripAdvisorRating rating) {
        if (rating != null) {
            binding.tvTripAdvisorNoReviewsAvailable.setVisibility(GONE);
            binding.ivSearchResultsTripAdvisorRatingImage.setVisibility(VISIBLE);
            binding.ivSearchResultsTripAdvisorRatingImage.load(rating.getImageUrl());
            binding.tvTripAdvisorNumberReviews.setVisibility(View.VISIBLE);
            binding.tvTripAdvisorNumberReviews.setText(getContext().getString(R.string.search_results_rating_sample_size,
                    rating.getNumberOfReviews()));

        } else {
            binding.ivSearchResultsTripAdvisorRatingImage.setVisibility(GONE);
            binding.tvTripAdvisorNumberReviews.setVisibility(GONE);
            binding.tvTripAdvisorNoReviewsAvailable.setVisibility(VISIBLE);
        }
    }

    public void updateTripAdvisorViewState(@NonNull HotelInfo.HotelRating state, @NonNull TripAdvisor tripAdvisor) {
        switch (state) {
            case UNAVAILABLE:
                binding.ivSearchResultsTripAdvisorRatingImage.setVisibility(GONE);
                binding.tvTripAdvisorNumberReviews.setVisibility(GONE);
                binding.tvTripAdvisorNoReviewsAvailable.setVisibility(VISIBLE);
                break;
            case ALL_AVAILABLE:
                binding.tvTripAdvisorNoReviewsAvailable.setVisibility(GONE);
                binding.ivSearchResultsTripAdvisorRatingImage.setVisibility(VISIBLE);
                binding.ivSearchResultsTripAdvisorRatingImage.load(tripAdvisor.ratingImageUrl());
                binding.tvTripAdvisorNumberReviews.setVisibility(View.VISIBLE);
                binding.tvTripAdvisorNumberReviews.
                        setText(getContext().getString(R.string.search_results_rating_sample_size, tripAdvisor.sampleSize()));
                break;
            case AVAILABLE_NO_IMAGE:
                binding.tvTripAdvisorNoReviewsAvailable.setVisibility(GONE);
                binding.ivSearchResultsTripAdvisorRatingImage.setVisibility(GONE);
                binding.tvTripAdvisorNumberReviews.setVisibility(View.VISIBLE);
                binding.tvTripAdvisorNumberReviews.
                        setText(getContext().getString(R.string.search_results_rating_sample_size, tripAdvisor.sampleSize()));
                break;
            case AVAILABLE_NO_NUMBER:
                binding.tvTripAdvisorNoReviewsAvailable.setVisibility(GONE);
                binding.ivSearchResultsTripAdvisorRatingImage.setVisibility(VISIBLE);
                binding.ivSearchResultsTripAdvisorRatingImage.load(tripAdvisor.ratingImageUrl());
                binding.tvTripAdvisorNumberReviews.setVisibility(View.GONE);
                break;
            default:
        }
    }
}
