package com.whitbread.premierinn.ciol.views.payandcheckin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.ciol.views.payandcheckin.ItemPosition.BOTTOM
import com.whitbread.premierinn.ciol.views.payandcheckin.ItemPosition.MIDDLE
import com.whitbread.premierinn.ciol.views.payandcheckin.ItemPosition.TOP
import com.whitbread.premierinn.common.mapper.toCardIconPath
import com.whitbread.premierinn.common.mapper.toCardName
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.ItemSavedCardPaymentMethodViewBinding
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod
import com.whitbread.premierinn.reviewbooking.mappers.getExpiryDate
import com.whitbread.premierinn.reviewbooking.mappers.getLastNChars

class SavedCardPaymentMethodView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    itemIndex: Int,
    totalPaymentMethods: Int,
) : FrameLayout(context, attributeSet) {

    private val binding = ItemSavedCardPaymentMethodViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private val itemPosition = getItemPosition(itemIndex, totalPaymentMethods)
    private var onPaymentMethodSelected: ((paymentMethod: ParcelablePaymentMethod) -> Unit)? = null

    fun onPaymentMethodSelected(
        onPaymentMethodSelected: (paymentMethod: ParcelablePaymentMethod) -> Unit
    ) = this.apply {
        this.onPaymentMethodSelected = onPaymentMethodSelected
    }

    fun changeClickStatus(isCardInteractionEnabled: Boolean) {
        binding.genericCardEntryViewRoot.isClickable = isCardInteractionEnabled
        binding.paymentTypeRadioButton.isClickable = isCardInteractionEnabled
    }

    fun setupView(paymentMethod: ParcelablePaymentMethod) = this.apply {
        updateItemBackground(paymentMethod)

        val displayName = "${paymentMethod.parcelableCard?.type.toCardName()} " +
            "(${
                context.getString(
                    R.string.masked_card_number,
                    paymentMethod.parcelableCard?.token?.getLastNChars(4)
                )
            })"
        val cardExpiry = context.getString(R.string.card_expires, paymentMethod.parcelableCard?.getExpiryDate())
        val cardImage = String.format(
            Urls.CREDIT_CARD_FORMAT_URL,
            paymentMethod.parcelableCard?.type.toCardIconPath()
        )

        binding.cardTypeLabel.text = displayName
        binding.cardHolderLabel.text = paymentMethod.parcelableCard?.cardHolderName ?: EMPTY_STRING
        binding.paymentCardExpiryLabel.text = cardExpiry
        binding.cardTypeImage.load(cardImage)

        binding.paymentTypeRadioButton.isChecked = paymentMethod.isSelected

        binding.genericCardEntryViewRoot.setOnClickListener { onItemClicked(paymentMethod) }
        binding.paymentTypeRadioButton.setOnClickListener { onItemClicked(paymentMethod) }
    }

    private fun onItemClicked(paymentMethod: ParcelablePaymentMethod) {
        onPaymentMethodSelected?.run { invoke(paymentMethod) }
    }

    private fun updateItemBackground(paymentMethod: ParcelablePaymentMethod) {
        val backgroundResId = if (paymentMethod.isSelected) {
            when (itemPosition) {
                TOP -> R.drawable.payment_method_top_item_selected_background
                MIDDLE -> R.drawable.payment_method_middle_item_selected_background
                BOTTOM -> R.drawable.payment_method_bottom_item_selected_background
            }
        } else {
            R.drawable.payment_method_item_unselected_background
        }
        binding.genericCardEntryViewRoot.background = ContextCompat.getDrawable(context, backgroundResId)
    }
}
