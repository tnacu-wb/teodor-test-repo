package com.whitbread.premierinn.paymentbreakdown;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ActivityPaymentBreakdownBinding;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain;
import com.whitbread.premierinn.common.view.SummaryExpensesView;
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownRoom;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentBreakdownViewContainer extends ViewContainer implements PaymentBreakdownPresenter.View {

    private final ActivityPaymentBreakdownBinding binding;

    public PaymentBreakdownViewContainer(@NonNull BaseActivity activity, ActivityPaymentBreakdownBinding binding) {
        super(activity);
        this.binding = binding;
        activity.setToolbar(activity.getString(R.string.payment_breakdown_title), true);
    }

    @Override
    public void setFields(int guests, int nights, @NonNull String formattedArrivalDate,
                          @NonNull String formattedDepartureDate, @NonNull PriceDomain totalBookingPrice, @Nullable String rateName,
                          @NonNull List<SummaryBreakdownRoom> rooms,  @NonNull PriceDomain donation,
                          @NonNull String checkInInfo, @NonNull String checkOutInfo,
                          @NonNull DeviceLocaleProvider deviceLocaleProvider, String donationPledgeString) {
        binding.pbvPaymentBreakdownView.setFields(guests, nights, formattedArrivalDate,
                formattedDepartureDate, rooms, deviceLocaleProvider, checkInInfo, checkOutInfo);
        binding.paymentBreakdownTotalPriceContainer.setBookingTotal(totalBookingPrice, deviceLocaleProvider);
        binding.paymentBreakdownTotalPriceContainer.setRateName(rateName);
        if (!donationPledgeString.isEmpty()) {
            if (donation.getAmount() > 0f) {
                setNonMealExtra(donationPledgeString, donation, deviceLocaleProvider);
            } else {
                binding.layoutBreakdownExtras.summaryBreakdownExtrasNonmeals.setVisibility(View.GONE);
            }
        } else {
            binding.layoutBreakdownExtras.summaryBreakdownExtrasNonmeals.setVisibility(View.GONE);
        }
    }

    @Override
    public void setNonMealExtra(String title, PriceDomain price,
                                DeviceLocaleProvider deviceLocaleProvider) {
        binding.layoutBreakdownExtras.summaryBreakdownExtrasNonmeals.setTitle(title);
        binding.layoutBreakdownExtras.summaryBreakdownExtrasNonmeals.setPrice(
                PriceFormat.format(price, deviceLocaleProvider));
        binding.layoutBreakdownExtras.summaryBreakdownExtrasContainer.setVisibility(View.VISIBLE);
        binding.layoutBreakdownExtras.summaryBreakdownExtrasNonmeals.setVisibility(View.VISIBLE);
    }

    @Override
    public void setMealExtra(int nights, DeviceLocaleProvider deviceLocaleProvider,
                             List<UpsellItem> selectedItemsList, boolean hasChildrenBreakfast) {
        binding.layoutBreakdownExtras.summaryBreakdownExtrasContainer.setVisibility(View.VISIBLE);
        binding.layoutBreakdownExtras.summaryMealsContainer.setVisibility(View.VISIBLE);

        binding.layoutBreakdownExtras.summaryMealsContainer.removeAllViews();
        binding.layoutBreakdownExtras.summaryMealsContainer.addView(binding.layoutBreakdownExtras.summaryBreakdownMealsHeading);

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

                    binding.layoutBreakdownExtras.summaryMealsContainer.addView(mealsView);
                });

        if (hasChildrenBreakfast && selectedItemsList.stream().anyMatch(UpsellItem::freeBreakfastOption)) {
            binding.layoutBreakdownExtras.summaryMealsContainer.addView(binding.layoutBreakdownExtras.summaryChildrenBreakfast);
            binding.layoutBreakdownExtras.summaryChildrenBreakfast.setVisibility(View.VISIBLE);
        }

        binding.layoutBreakdownExtras.summaryMealsContainer.invalidate();
        binding.layoutBreakdownExtras.summaryMealsContainer.requestLayout();
    }

    @Override
    public void setChildrenBreakfast(int children, int nights) {
        binding.layoutBreakdownExtras.summaryChildrenBreakfast.setDescription(String.format("(%s, %s)",
                getActivity().getResources().getQuantityString(R.plurals.number_of_children, children, children),
                getActivity().getResources().getQuantityString(R.plurals.nights, nights, nights)));
        binding.layoutBreakdownExtras.summaryChildrenBreakfast.setPrice(
                getActivity().getString(R.string.summary_breakdown_kids_breakfast_free));
        binding.layoutBreakdownExtras.summaryChildrenBreakfast.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPriceIncludesTaxesAndFeesMessage(boolean show) {
        binding.paymentBreakdownTotalPriceContainer.showPriceIncludesTaxesAndFeesMessage(show);
    }

    @Override
    public void setOtherExtras(List<ExtrasItemDomain> selectedExtras, int totalRooms, DeviceLocaleProvider deviceLocaleProvider) {
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
                            binding.layoutBreakdownExtras.summaryExtrasContainer.addView(extrasView);
                        }
                    });
            binding.layoutBreakdownExtras.summaryExtrasContainer.setVisibility(View.VISIBLE);
            binding.layoutBreakdownExtras.summaryBreakdownExtrasContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setCCFee(@NonNull String label, @NonNull String amount) { //TODO: Remove and related xml Views
        binding.paymentBreakdownCcProcessingFee.setTitle(label);
        binding.paymentBreakdownCcProcessingFee.setPrice(amount);
        binding.paymentBreakdownCcProcessingFee.setVisibility(View.VISIBLE);
    }
}