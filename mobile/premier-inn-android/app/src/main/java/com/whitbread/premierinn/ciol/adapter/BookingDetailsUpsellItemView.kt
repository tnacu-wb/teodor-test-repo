package com.whitbread.premierinn.ciol.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.ciol.entity.upsells.BreakfastUiModel
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.utils.getFreeBreakfastSelections
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.databinding.ItemUpsellBinding

const val ONE_DAY_IN_HRS = 24

class BookingDetailsUpsellItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    ConstraintLayout(context, attrs) {

    private val binding = ItemUpsellBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun setState(
        upsellItem: UpsellEntry,
        numberOfNightsFormatted: String,
        numberOfNights: Int
    ) {
        when (upsellItem) {
            is BreakfastUiModel -> setupView(
                context.getString(R.string.restaurant_info_breakfast_tab_title),
                upsellItem.imageUrl
            )

            is MealUiModel -> setupView(
                upsellItem.name,
                upsellItem.imageSrc
            )

            is ExtrasItemUiModel -> setupView(upsellItem.name, upsellItem.imageSrc)
        }
        setupDescription(upsellItem, numberOfNightsFormatted, numberOfNights)
    }

    private fun setupView(title: String, imageUrl: String) {
        binding.root.setBackgroundColor(context.getColor(R.color.white))
        binding.root.setPadding(
            0,
            0,
            0,
            resources.getDimensionPixelSize(R.dimen.default_medium_margin)
        )

        binding.itemUpsellName.text = title
        binding.itemUpsellImage.load(Urls.CONTENT_BASE_URL.plus(imageUrl))
    }

    private fun setupDescription(upsellItem: UpsellEntry, numberOfNightsFormatted: String, numberOfNights: Int) {
        val description: String = when (upsellItem) {
            is BreakfastUiModel,
            is MealUiModel -> {
                val childrenCount = upsellItem.getFreeBreakfastSelections()
                val children = context.resources.getQuantityString(
                    R.plurals.number_of_children,
                    childrenCount,
                    childrenCount
                )
                val adults = context.resources.getQuantityString(
                    R.plurals.number_of_adults,
                    upsellItem.getNumberOfSelections(),
                    upsellItem.getNumberOfSelections()
                )
                if (childrenCount > 0){
                    "$adults & $children, $numberOfNightsFormatted"
                } else {
                    "$adults, $numberOfNightsFormatted"
                }

            }

            is ExtrasItemUiModel -> {
                if (upsellItem.getId().getUpsellType() == UpsellType.WIFI) {
                    "${
                        context.resources.getQuantityString(
                            R.plurals.rooms,
                            upsellItem.getNumberOfSelections(),
                            upsellItem.getNumberOfSelections()
                        )
                    }, ${
                        String.format(
                            context.getString(R.string.upsells_custom_hrs_measurement),
                            numberOfNights * ONE_DAY_IN_HRS
                        )
                    }"
                } else context.getString(R.string.extras_added)
            }
        }
        binding.itemUpsellDescription.text = description
    }
}
