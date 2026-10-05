package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.request.booking.Breakfast;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ViewExpensesTotalPriceBinding;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpensesTotalPriceView extends RelativeLayout {

    private ViewExpensesTotalPriceBinding binding;

    public ExpensesTotalPriceView(Context context) {
        super(context);
        init();
    }

    public ExpensesTotalPriceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpensesTotalPriceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ExpensesTotalPriceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = ViewExpensesTotalPriceBinding.inflate(LayoutInflater.from(getContext()), this);
    }

    public void showBreakfastExpenseWithDescription(@NonNull String typeOfBreakfast,
                                                    @NonNull String breakfastPrice, String description) {
        binding.expensesExpenseBreakfast.setTitle(typeOfBreakfast);
        binding.expensesExpenseBreakfast.setDescription(description);
        binding.expensesExpenseBreakfast.setPrice(breakfastPrice);
        binding.expensesExpenseBreakfast.setVisibility(VISIBLE);
    }

    public void showBreakfastExpenseWithEveryonePaying(@NonNull String typeOfBreakfast,
                                                       @NonNull String breakfastPrice, int numberOfGuests, int numberOfNights) {
        String description = String.format("(%s, %s)",
                getContext().getResources().getQuantityString(R.plurals.guests, numberOfGuests, numberOfGuests),
                getContext().getResources().getQuantityString(R.plurals.nights, numberOfNights, numberOfNights));

        showBreakfastExpenseWithDescription(typeOfBreakfast, breakfastPrice, description);
    }

    public void showBreakfastExpenseWithOnlyAdultsPaying(List<UpsellItem> selectedUpsellItems, int nights,
                                                         DeviceLocaleProvider deviceLocaleProvider) {

        binding.reviewBookingMealsContainer.setVisibility(View.VISIBLE);

        Map<String, Long> itemCounts = selectedUpsellItems.stream()
                .collect(Collectors.groupingBy(UpsellItem::code, Collectors.counting()));

        binding.reviewBookingMealsContainer.removeAllViews();

        itemCounts.forEach((itemCode, count) -> {
            selectedUpsellItems.stream()
                    .filter(item -> item.code().equals(itemCode))
                    .findFirst()
                    .ifPresent(item -> {
                        float totalPrice = item.calculateTotalUpsellCostForStay(count.intValue(), nights);
                        PriceDomain updatedPrice = new PriceDomain(totalPrice, item.price().getCurrency());

                        SummaryExpensesView mealsView = new SummaryExpensesView(getContext());
                        mealsView.setVisibility(View.VISIBLE); // Ensure visibility
                        mealsView.setTitle(count + " × " + item.legend());

                        String formattedAdults = getContext().getResources()
                                .getQuantityString(R.plurals.number_of_adults, count.intValue(), count.intValue());
                        String formattedNights = getContext().getResources()
                                .getQuantityString(R.plurals.nights, nights, nights);
                        String description = "(" + getContext().getResources()
                                .getString(R.string.summary_breakdown_guests_and_nights, formattedAdults, formattedNights) + ")";

                        mealsView.setDescription(description);
                        mealsView.setPrice(PriceFormat.format(updatedPrice, deviceLocaleProvider));

                        binding.reviewBookingMealsContainer.addView(mealsView);
                    });
        });

        binding.reviewBookingMealsContainer.invalidate();
        binding.reviewBookingMealsContainer.requestLayout();
    }

    public void showChildrenBreakfastExpense(ReviewBookingInput input, int numberOfNights) {

        List<Breakfast> breakfasts = input.paymentDetailsInput().bookingFlowInput().breakfasts();

        int children = breakfasts.stream().mapToInt(Breakfast::children).sum();

        binding.expensesExpenseChildrenBreakfast.setDescription(String.format("(%s, %s)",
                getContext().getResources().getQuantityString(R.plurals.number_of_children, children, children),
                getContext().getResources().getQuantityString(R.plurals.nights, numberOfNights, numberOfNights)));
        binding.expensesExpenseChildrenBreakfast.setPrice(getContext().getString(R.string.summary_breakdown_kids_breakfast_free));
        binding.expensesExpenseChildrenBreakfast.setVisibility(VISIBLE);
    }

    public void setOtherExtras(List<ExtrasItemDomain> selectedExtras, DeviceLocaleProvider deviceLocaleProvider) {
        if (!selectedExtras.isEmpty()) {
            Map<String, Long> counts = selectedExtras.stream()
                    .collect(Collectors.groupingBy(ExtrasItemDomain::getId, Collectors.counting()));

            counts.forEach((id, count) -> {
                selectedExtras.stream()
                        .filter(extra -> extra.getId().equals(id))
                        .findFirst()
                        .ifPresent(extra -> {
                            SummaryExpensesView extrasView = new SummaryExpensesView(getContext());
                            extrasView.setVisibility(View.VISIBLE);
                            extrasView.setTitle(extra.getName());
                            extrasView.setDescription(String.format("(%s)",
                                    getContext().getResources().getQuantityString(R.plurals.rooms, count.intValue(), count)));
                            extrasView.setPrice(PriceFormat.format(new PriceDomain((float) (extra.getPrice() * count),
                                    extra.getCurrency()), deviceLocaleProvider));
                            binding.reviewBookingExtrasContainer.addView(extrasView);
                        });
            });
            binding.reviewBookingExtrasContainer.setVisibility(View.VISIBLE);
        }
    }

    public void showFormattedHotelStayDescription(int numNights, int numRooms, boolean allRoomsAreAccessible) {
        String description = String.format("(%s, %s)",
                getContext().getResources().getQuantityString(R.plurals.nights, numNights, numNights),
                getContext().getResources().getQuantityString(
                        allRoomsAreAccessible ? R.plurals.accessible_rooms : R.plurals.rooms, numRooms, numRooms));
        binding.expensesExpenseHotel.setDescription(description);
    }

    public void setDonation(@NonNull PriceDomain price,
                            @NonNull DeviceLocaleProvider deviceLocaleProvider,
                            String donationPledge) {
        binding.expensesDonation.setPrice(PriceFormat.format(price, deviceLocaleProvider));
        binding.expensesDonation.setDescription(donationPledge);
        binding.expensesDonation.setVisibility(price.getAmount() == 0 ? View.GONE : View.VISIBLE);
    }

    public void showPriceIncludesTaxesAndFeesMessage(boolean show) {
        binding.expensesTotalPriceContainer.showPriceIncludesTaxesAndFeesMessage(show);
    }

    public SummaryExpensesView getHotelExpenseView() {
        return binding.expensesExpenseHotel;
    }

    public SummaryExpensesView getCreditCardFeeView() {
        return binding.expensesCreditCardFee;
    }

    public TotalPriceView getTotalPriceContainer() {
        return binding.expensesTotalPriceContainer;
    }

}