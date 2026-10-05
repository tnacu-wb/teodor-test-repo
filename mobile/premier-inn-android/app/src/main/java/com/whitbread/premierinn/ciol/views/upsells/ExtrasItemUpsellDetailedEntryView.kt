package com.whitbread.premierinn.ciol.views.upsells

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.utils.formatPrice
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.databinding.ViewUpsellExtraItemEntryViewBinding
import java.util.Locale

class ExtrasItemUpsellDetailedEntryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = ViewUpsellExtraItemEntryViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun initialize(upsell: ExtrasItemUiModel, locale: Locale, showPriceForMultiRoomReservation: Boolean) = this.apply {
        binding.apply {
            descriptionTextView.text = Html.fromHtml(upsell.description, Html.FROM_HTML_MODE_COMPACT)

            if (upsell.id.getUpsellType() == UpsellType.WIFI && showPriceForMultiRoomReservation) {
                priceInfoTextView.isVisible = true
                priceInfoTextView.text = String.format(
                    locale,
                    "%s %s %s",
                    formatPrice(upsell.price, upsell.currency, locale),
                    context.getString(R.string.upsells_price_info_per),
                    context.getString(R.string.upsells_24_hrs_measurement)
                )
            } else {
                priceInfoTextView.isVisible = false
            }
        }
    }
}
