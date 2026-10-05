package com.whitbread.premierinn.ciol.views.upsells

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.MealSelectionRules
import com.whitbread.premierinn.ciol.views.payandcheckin.ItemPosition
import com.whitbread.premierinn.ciol.views.payandcheckin.getItemPosition
import java.util.Locale

abstract class FoodUpsellBaseEntryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    internal abstract val binding: ViewBinding

    internal lateinit var upsell: MealUiModel
    internal lateinit var locale: Locale
    internal var bookingContainsChildren = false
    internal var onIncrementAction: ((upsell: MealUiModel) -> Unit)? = null
    internal var onDecrementAction: ((upsell: MealUiModel) -> Unit)? = null

    internal abstract fun onViewInitialized()
    internal abstract fun onBackgroundUpdated(background: Drawable?)
    internal abstract fun handleMealSelectionRules(mealSelectionRules: MealSelectionRules)

    fun initialize(
        upsell: MealUiModel,
        mealSelectionRules: MealSelectionRules,
        locale: Locale,
        currentItemPosition: Int,
        totalItems: Int,
        bookingContainsChildren: Boolean
    ) = this.apply {
        this.upsell = upsell
        this.locale = locale
        this.bookingContainsChildren = bookingContainsChildren
        updateViewBackground(currentItemPosition, totalItems)
        onViewInitialized()
        handleMealSelectionRules(mealSelectionRules)
    }

    fun onIncrement(onIncrementAction: (upsell: MealUiModel) -> Unit) = this.apply {
        this.onIncrementAction = onIncrementAction
    }

    fun onDecrement(onDecrementAction: (upsell: MealUiModel) -> Unit) = this.apply {
        this.onDecrementAction = onDecrementAction
    }

    private fun updateViewBackground(currentItemPosition: Int, totalItems: Int) {
        val backgroundResId = when(getItemPosition(currentItemPosition, totalItems)) {
            ItemPosition.TOP -> R.drawable.meal_top_item_background
            ItemPosition.BOTTOM -> R.drawable.meal_bottom_item_background
            ItemPosition.MIDDLE -> R.drawable.payment_method_item_unselected_background
        }
        onBackgroundUpdated(ContextCompat.getDrawable(context, backgroundResId))
    }

    internal fun onMinusClicked() {
        onDecrementAction?.invoke(upsell)
    }

    internal fun onPlusClicked() {
        onIncrementAction?.invoke(upsell)
    }

    internal fun getButtonBackgroundColor(isEnabled: Boolean) =
        if (isEnabled) ContextCompat.getColor(context, R.color.medium_black)
        else ContextCompat.getColor(context, R.color.grey)
}