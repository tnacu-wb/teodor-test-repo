package com.whitbread.premierinn.common;

import android.content.Context;
import androidx.annotation.NonNull;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.bookingdetails.UpsellItemSummary;
import com.whitbread.premierinn.mealpreferences.BreakfastRadioButtonInput;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PreferencesContentProvider {

    private final Context context;

    @Inject
    public PreferencesContentProvider(@NonNull Context context) {
        this.context = context;
    }

    public List<BreakfastRadioButtonInput> getBreakfastContents() {
        List<BreakfastRadioButtonInput> breakfastOptions = new ArrayList<>();

        // PI BREAKFAST
        BreakfastRadioButtonInput breakfastRadioButtonInput = BreakfastRadioButtonInput.builder()
                .code(UpsellItemSummary.UpsellItemType.PI_BREAKFAST.code())
                .legend(context.getString(R.string.pi_breakfast_label))
                .price("")
                .description(context.getString(R.string.meal_preferences_pi_breakfast_description)).build();
        breakfastOptions.add(breakfastRadioButtonInput);

        // CONTINENTAL BREAKFAST
        breakfastRadioButtonInput = BreakfastRadioButtonInput.builder()
                .code(UpsellItemSummary.UpsellItemType.CONTINENTAL_BREAKFAST.code())
                .legend(context.getString(R.string.continental_breakfast_label))
                .price("")
                .description(context.getString(R.string.meal_preferences_continental_breakfast_description)).build();
        breakfastOptions.add(breakfastRadioButtonInput);

        // MEAL DEAL
        breakfastRadioButtonInput = BreakfastRadioButtonInput.builder()
                .code(UpsellItemSummary.UpsellItemType.MEAL_DEAL.code())
                .legend(context.getString(R.string.meal_preferences_meal_deal_label))
                .price("")
                .description(context.getString(R.string.meal_preferences_meal_deal_description)).build();
        breakfastOptions.add(breakfastRadioButtonInput);

        // NO PREFERENCE
        breakfastRadioButtonInput = BreakfastRadioButtonInput.builder()
                .code(UpsellItemSummary.UpsellItemType.NO_PREFERENCE.code())
                .legend(context.getString(R.string.meal_preferences_no_preference_label))
                .price("")
                .description("").build();
        breakfastOptions.add(breakfastRadioButtonInput);

        return breakfastOptions;
    }

    public String getErrorMessageUpdateCustomer() {
        return context.getString(R.string.meal_preferences_error_saving_changes);
    }

    public String getErrorMessageGetCustomer() {
        return context.getString(R.string.error_get_preferences);
    }
}
