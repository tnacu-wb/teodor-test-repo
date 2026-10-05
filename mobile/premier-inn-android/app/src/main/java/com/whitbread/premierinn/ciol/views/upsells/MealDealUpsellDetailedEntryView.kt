package com.whitbread.premierinn.ciol.views.upsells

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.details.MealSelectionRules
import com.whitbread.premierinn.ciol.utils.formatPrice
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.databinding.ViewUpsellMealDealEntryViewBinding

class MealDealUpsellDetailedEntryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FoodUpsellBaseEntryView(context, attrs) {

    override val binding = ViewUpsellMealDealEntryViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    override fun onViewInitialized() {
        binding.apply {
            if(upsell.id.isKidsMeal()) {
                descriptionTextView.text = context.getString(R.string.upsells_breakfast_for_kids)
            } else {
                descriptionTextView.text = Html.fromHtml(upsell.shortDescription, Html.FROM_HTML_MODE_COMPACT)
            }
            priceInfoTextView.isVisible = !upsell.id.isKidsMeal()
            priceInfoTextView.text = String.format(
                locale,
                "%s %s %s",
                formatPrice(upsell.price, upsell.currency, locale),
                context.getString(R.string.upsells_price_info_per),
                context.getString(R.string.upsells_adult_per_day_measurement)
            )
            freeBreakfastForKidsTextView.isVisible = upsell.freeBreakfastOption == true && bookingContainsChildren
            numberOfSelectionsTextView.text = upsell.noOfSelections.toString()
        }
    }

    override fun handleMealSelectionRules(mealSelectionRules: MealSelectionRules) {
        // Hide free breakfast info when no free meal is offered or booking doesn't contain children
        binding.freeBreakfastForKidsTextView.isVisible = upsell.freeBreakfastOption == true && bookingContainsChildren

        if (upsell.id.isKidsMeal()) {
            // Disable KidsMeal interaction when no adult meal is selected
            mealSelectionRules.isKidsMenuSelectionEnabled.let { isEnabled ->
                val backgroundColor = getButtonBackgroundColor(isEnabled)
                binding.apply {
                    minusImageView.setColorFilter(backgroundColor)
                    minusImageView.setOnClickListener { if (isEnabled) onMinusClicked() else null }
                    plusImageView.setColorFilter(backgroundColor)
                    plusImageView.setOnClickListener { if (isEnabled) onPlusClicked() else null }
                    numberOfSelectionsTextView.setTextColor(backgroundColor)
                }
            }

            // Disable KidsMeal increment when max number of selected meals was reached
            mealSelectionRules.canIncrementForChildren.let { canIncrement ->
                binding.plusImageView.setColorFilter(getButtonBackgroundColor(canIncrement))
                binding.plusImageView.setOnClickListener { if (canIncrement) onPlusClicked() else null }
            }

            // Disable KidsMeal decrement when selection is 0
            mealSelectionRules.canDecrementForChildren.let { canDecrement ->
                binding.minusImageView.setColorFilter(getButtonBackgroundColor(canDecrement))
                binding.minusImageView.setOnClickListener { if (canDecrement) onMinusClicked() else null }
            }
        } else {
            // Disable Meal for adults increment when max number of selected meals was reached
            binding.plusImageView.setColorFilter(getButtonBackgroundColor(mealSelectionRules.canIncrementForAdults))
            binding.plusImageView.setOnClickListener { if (mealSelectionRules.canIncrementForAdults) onPlusClicked() else null }

            // Disable Meal for adults decrement when selection is 0
            binding.minusImageView.setColorFilter(getButtonBackgroundColor(mealSelectionRules.canDecrementForAdults))
            binding.minusImageView.setOnClickListener { if (mealSelectionRules.canDecrementForAdults) onMinusClicked() else null }
        }
    }

    override fun onBackgroundUpdated(background: Drawable?) {
        binding.root.background = background
    }
}