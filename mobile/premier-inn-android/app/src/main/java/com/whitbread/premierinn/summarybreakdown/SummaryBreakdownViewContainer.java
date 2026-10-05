package com.whitbread.premierinn.summarybreakdown;


import android.view.View;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ActivitySummaryBreakdownBinding;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain;
import com.whitbread.premierinn.common.view.SummaryExpensesView;

import java.util.List;
import java.util.stream.Collectors;


public class SummaryBreakdownViewContainer extends ViewContainer implements SummaryBreakdownPresenter.View {

    private ActivitySummaryBreakdownBinding binding;

    public SummaryBreakdownViewContainer(@NonNull BaseActivity activity, ActivitySummaryBreakdownBinding binding) {
        super(activity);
        this.binding = binding;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // SummaryBreakdownPresenter.View
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void setToolBar() {
        getActivity().setToolbar(getActivity().getString(R.string.summary_breakdown_toolbar_title), true);
    }

    @Override
    public void setBookingBanner(@NonNull String hotelImageReference, @NonNull String hotelName,
                                 @NonNull String commaSeparatedAddress) {
        binding.ivSummaryBreakdownBannerImage.load(hotelImageReference, R.drawable.image_no_hotel);
        binding.tvSummaryBreakdownHotelName.setText(hotelName);
        binding.tvSummaryBreakdownHotelAddress.setText(commaSeparatedAddress);
    }

    @Override
    public void setPriceBreakdown(int guests, int nights, @NonNull String formattedArrivalDate, @NonNull String formattedDepartureDate,
                                  @NonNull List<SummaryBreakdownRoom> rooms, String checkInInfo,
                                  String checkOutInfo, @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        binding.pbvSummaryBreakdownView.setFields(guests, nights, formattedArrivalDate,
                formattedDepartureDate, rooms, deviceLocaleProvider, checkInInfo, checkOutInfo);
    }

    @Override
    public void setTotalPrice(@NonNull PriceDomain totalPrice,
                              @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        binding.summaryTotalPriceContainer.setBookingTotal(totalPrice, deviceLocaleProvider);
    }

    @Override
    public void setRateName(@NonNull String rateName) {
        binding.summaryTotalPriceContainer.setRateName(rateName);
    }

    @Override
    public void setChildrenBreakfastSummary(int children, int nights) {
        binding.breakdownExtrasLayout.summaryChildrenBreakfast.setDescription(String.format("(%s, %s)",
                getActivity().getResources().getQuantityString(R.plurals.number_of_children, children, children),
                getActivity().getResources().getQuantityString(R.plurals.nights, nights, nights)));
        binding.breakdownExtrasLayout.summaryChildrenBreakfast.setPrice(
                getActivity().getString(R.string.summary_breakdown_kids_breakfast_free));
        binding.breakdownExtrasLayout.summaryChildrenBreakfast.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPriceIncludesTaxesAndFeesMessage(boolean show) {
        binding.summaryTotalPriceContainer.showPriceIncludesTaxesAndFeesMessage(show);
    }
    @Override
    public void setMealExtra(int nights, DeviceLocaleProvider deviceLocaleProvider,
                             List<UpsellItem> selectedItemsList, boolean hasChildrenBreakfast) {
        binding.breakdownExtrasLayout.summaryBreakdownExtrasContainer.setVisibility(View.VISIBLE);
        binding.breakdownExtrasLayout.summaryMealsContainer.setVisibility(View.VISIBLE);

        binding.breakdownExtrasLayout.summaryMealsContainer.removeAllViews();
        binding.breakdownExtrasLayout.summaryMealsContainer.addView(binding.breakdownExtrasLayout.summaryBreakdownMealsHeading);

        selectedItemsList.stream()
                .collect(Collectors.groupingBy(UpsellItem::code))
                .forEach((itemCode, items) -> {
                    UpsellItem item = items.get(0);
                    int count = items.size();
                    float totalPrice = item.calculateTotalUpsellCostForStay(count, nights);
                    PriceDomain updatedPrice = new PriceDomain(totalPrice, item.price().getCurrency());

                    SummaryExpensesView mealsView = new SummaryExpensesView(getActivity());
                    mealsView.setVisibility(View.VISIBLE);
                    mealsView.setTitle(count + " × " + item.legend());

                    String formattedAdults = getActivity().getResources()
                            .getQuantityString(R.plurals.number_of_adults, count, count);
                    String formattedNights = getActivity().getResources()
                            .getQuantityString(R.plurals.nights, nights, nights);
                    String description = "(" + getActivity().getResources()
                            .getString(R.string.summary_breakdown_guests_and_nights, formattedAdults, formattedNights) + ")";

                    mealsView.setDescription(description);
                    mealsView.setPrice(PriceFormat.format(updatedPrice, deviceLocaleProvider));

                    binding.breakdownExtrasLayout.summaryMealsContainer.addView(mealsView);
                });

        if (hasChildrenBreakfast && selectedItemsList.stream().anyMatch(UpsellItem::freeBreakfastOption)) {
            binding.breakdownExtrasLayout.summaryMealsContainer.addView(binding.breakdownExtrasLayout.summaryChildrenBreakfast);
            binding.breakdownExtrasLayout.summaryChildrenBreakfast.setVisibility(View.VISIBLE);
        }

        binding.breakdownExtrasLayout.summaryMealsContainer.invalidate();
        binding.breakdownExtrasLayout.summaryMealsContainer.requestLayout();
    }

    @Override
    public void setOtherExtras(List<ExtrasItemDomain> selectedExtras, DeviceLocaleProvider deviceLocaleProvider) {
        if (!selectedExtras.isEmpty()) {
            selectedExtras.stream()
                    .collect(Collectors.groupingBy(ExtrasItemDomain::getId, Collectors.counting()))
                    .forEach((id, count) -> {
                        ExtrasItemDomain extra = selectedExtras.stream()
                                .filter(e -> e.getId().equals(id))
                                .findFirst()
                                .orElse(null);
                        if (extra != null) {
                            SummaryExpensesView extrasView = new SummaryExpensesView(getActivity());
                            extrasView.setVisibility(View.VISIBLE);
                            extrasView.setTitle(extra.getName());
                            extrasView.setDescription(String.format("(%s)",
                                    getActivity().getResources().getQuantityString(R.plurals.rooms, count.intValue(), count)));
                            extrasView.setPrice(PriceFormat.format(new PriceDomain((float) (extra.getPrice() * count),
                                    extra.getCurrency()), deviceLocaleProvider));
                            binding.breakdownExtrasLayout.summaryExtrasContainer.addView(extrasView);
                        }
                    });

            binding.breakdownExtrasLayout.summaryExtrasContainer.setVisibility(View.VISIBLE);
            binding.breakdownExtrasLayout.summaryBreakdownExtrasContainer.setVisibility(View.VISIBLE);
        }
    }
}
