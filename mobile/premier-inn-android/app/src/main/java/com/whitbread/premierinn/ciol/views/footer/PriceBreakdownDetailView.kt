package com.whitbread.premierinn.ciol.views.footer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.utils.getCurrency
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getUpsellName
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellPrice
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.databinding.ItemCiolPriceBreakdownDetailViewBinding
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.entity.isPibaCNPBooking
import com.whitbread.premierinn.domain.common.entity.isPibaCPBooking
import com.whitbread.premierinn.utils.POUND_SIGN
import com.whitbread.premierinn.utils.getCurrencySign

class PriceBreakdownDetailView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
) : RelativeLayout(context, attributeSet) {
    private var currencySign: String = POUND_SIGN

    private val binding = ItemCiolPriceBreakdownDetailViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    fun setOutstandingBalance(
        outstandingBalance: PriceDomain,
        paymentOption: String,
        isPibaCpEnabled: Boolean
    ) = this.apply {
        currencySign = outstandingBalance.currency.getCurrencySign()
        outstandingBalance.apply {
            binding.outstandingItemValueTextView.text = String.format("${currencySign}%.2f", amount)
        }

        binding.outstandingItemNameTextView.text = when {
            paymentOption.isPibaCNPBooking() -> {
                context.getString(
                    R.string.piba_cnp_breakdown_message
                )
            }
            isPibaCpEnabled && paymentOption.isPibaCPBooking() -> {
                context.getString(
                    R.string.piba_cp_breakdown_message
                )
            }

            else -> {
                context.getString(
                    R.string.price_breakdown_outstanding_booking_balance
                )
            }
        }
    }


    fun setUpsell(item: UpsellEntry, nights: Int) = this.apply {
        currencySign = item.getCurrency().getCurrencySign()
        val count = item.getNumberOfSelections()
        val prefix = if (count > 1) "${count}x " else ""

        val text = when (item.getId().getUpsellType()) {
            UpsellType.BREAKFAST,
            UpsellType.MEAL_DEAL -> {
                if (item is MealUiModel && item.id.isKidsMeal()) {
                    "$prefix${item.getUpsellName()}"
                } else {
                    "$prefix${item.getUpsellName()} (${currencySign}${item.getUpsellPrice()} pp)"
                }
            }

            UpsellType.ECI,
            UpsellType.LCO -> item.getUpsellName()
            UpsellType.WIFI -> "$prefix${item.getUpsellName()} (${currencySign}${item.getUpsellPrice()} " +
                    "${context.getString(R.string.upsells_price_info_per)} " +
                            "${context.getString(R.string.upsells_24_hrs_measurement)})"
        }
        binding.outstandingItemValueTextView.text =
            String.format("${currencySign}%.2f", item.getUpsellPrice() * item.getNumberOfSelections() * nights)

        binding.outstandingItemNameTextView.text = text
    }
}
