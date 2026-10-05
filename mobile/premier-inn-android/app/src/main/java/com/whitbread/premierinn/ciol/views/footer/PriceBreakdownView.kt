package com.whitbread.premierinn.ciol.views.footer

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.copy
import com.whitbread.premierinn.ciol.mapper.toPriceDomainModel
import com.whitbread.premierinn.ciol.uimodel.PriceBreakdownModel
import com.whitbread.premierinn.ciol.utils.getFreeBreakfastSelections
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellPrice
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.ciol.utils.updateNumberOfSelections
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.ViewPreStayFooterBinding
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.entity.isPibaCNPBooking
import com.whitbread.premierinn.utils.getCurrencySign

private const val TOTAL_PROGRESS = 100
private const val ZERO_DOUBLE =  0.0

class PriceBreakdownView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
) : ConstraintLayout(context, attributeSet) {

    private var isDetailedViewExpanded = false

    private val binding = ViewPreStayFooterBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    fun setupProgressBar(progress: Int) = this.apply {
        binding.progressBar.apply {
            max = TOTAL_PROGRESS
            this.progress = progress
        }
    }

    fun setupContinueButton(text: String, isEnabled: Boolean) {
        binding.continueButton.apply {
            setText(text)
            this.isEnabled = isEnabled
        }
    }

    @SuppressLint("CheckResult")
    fun setupContinueButtonClickListener(clickAction: () -> Unit) = this.apply {
        binding.continueButton.onClickOnInternetAvailable().subscribe { _ ->
            clickAction()
        }
    }

    fun getContinueButton() = binding.continueButton

    fun displayPriceBreakdown(
        priceBreakdownModel: PriceBreakdownModel,
        action: ((Double) -> Unit)?,
        paymentOption: String = EMPTY_STRING,
        isPibaCpEnabled: Boolean = false
    ) {
        binding.apply {
            with(priceBreakdownModel) {
                val outstandingBalance = outstandingBalance.toPriceDomainModel()

                val currency = outstandingBalance.currency
                val allRoomsSelectedUpsells = roomSelections.flatMap { it.selectedUpsells }
                    .filter { it.getNumberOfSelections() > 0 }
                val breakDownList = getSelectedUpsells(allRoomsSelectedUpsells)
                val totalPriceUpsells: Double = breakDownList.sumOf { item ->
                    item.getUpsellPrice() * item.getNumberOfSelections()
                }

                val totalAmount = if (paymentOption.isPibaCNPBooking()) {
                    ZERO_DOUBLE
                } else {
                    outstandingBalance.amount.toDouble() + totalPriceUpsells * nights
                }

                priceBreakdownSummaryContainer.isVisible =
                    outstandingBalance.amount > 0 || allRoomsSelectedUpsells.isNotEmpty()
                totalPriceTextView.text =
                    String.format("${currency.getCurrencySign()}%.2f", totalAmount)

                addBreakDownViews(breakDownList, outstandingBalance, nights, paymentOption, isPibaCpEnabled)
                priceBreakdownSummaryContainer.setOnClickListener {
                    onPriceBreakdownClicked()
                    action?.invoke(totalAmount)
                }
            }
        }
    }

    private fun getSelectedUpsells(upsells: List<UpsellEntry>): List<UpsellEntry> {
        val noDuplicateList = upsells.copy().distinctBy { it.getId() }
        val numberOfFreeKidsBreakfast = upsells.sumOf { upsell -> upsell.getFreeBreakfastSelections() }
        noDuplicateList.forEach { item ->
            if (item is MealUiModel && item.id.isKidsMeal()) {
                item.noOfSelections = numberOfFreeKidsBreakfast
            } else {
                val itemNoOfSelections =
                    upsells.filter { it.getId() == item.getId() && !item.getId().isKidsMeal() }
                        .sumOf { it.getNumberOfSelections() }
                item.updateNumberOfSelections(itemNoOfSelections)
            }
        }

        return noDuplicateList
    }

    private fun onPriceBreakdownClicked() {
        when (isDetailedViewExpanded) {
            true -> {
                binding.priceBreakdownArrowImageView.setImageResource(R.drawable.ic_chevron_down)
                binding.priceBreakdownDetailsSeparator.visibility = INVISIBLE
                binding.priceBreakdownDetailedViewContainer.visibility = GONE
                isDetailedViewExpanded = false
            }

            false -> {
                binding.priceBreakdownArrowImageView.setImageResource(R.drawable.ic_chevron_up)
                binding.priceBreakdownDetailsSeparator.visibility = VISIBLE
                binding.priceBreakdownDetailedViewContainer.visibility = VISIBLE
                isDetailedViewExpanded = true
            }
        }
    }

    private fun addBreakDownViews(
        breakDownUpsells: List<UpsellEntry>,
        outstandingBalance: PriceDomain,
        nights: Int,
        paymentOption: String,
        isPibaCpEnabled: Boolean
    ) {
        binding.priceBreakdownDetailedViewContainer.removeAllViews()
        if (outstandingBalance.amount > 0) {
            binding.priceBreakdownDetailedViewContainer.addView(
                PriceBreakdownDetailView(context).setOutstandingBalance(outstandingBalance, paymentOption, isPibaCpEnabled)
            )
        }
        breakDownUpsells.forEach { item ->
            binding.priceBreakdownDetailedViewContainer.addView(
                PriceBreakdownDetailView(context).setUpsell(item, nights)
            )
        }
    }
}
