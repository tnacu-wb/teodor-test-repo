package com.whitbread.premierinn.ciol.views.payandcheckin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.ciol.mapper.toAcceptedCreditCardList
import com.whitbread.premierinn.ciol.views.payandcheckin.ItemPosition.BOTTOM
import com.whitbread.premierinn.ciol.views.payandcheckin.ItemPosition.MIDDLE
import com.whitbread.premierinn.ciol.views.payandcheckin.ItemPosition.TOP
import com.whitbread.premierinn.common.PaymentMethodType
import com.whitbread.premierinn.common.mapper.toCardIconPathOpera
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.ItemGenericPaymentMethodViewBinding
import com.whitbread.premierinn.reviewbooking.ParcelableAcceptedCardType
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod
import com.whitbread.premierinn.reviewbooking.PaymentCardType
import com.whitbread.premierinn.reviewbooking.excludeNonPibaAcceptedCards
import com.whitbread.premierinn.reviewbooking.getPayPalAcceptedCardType
import com.whitbread.premierinn.reviewbooking.getPibaAcceptedCardType
import com.whitbread.premierinn.reviewbooking.getPibaAcceptedCards
import com.whitbread.premierinn.reviewbooking.newPaymentTypeAcceptedCards

class GenericPaymentMethodView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    itemIndex: Int,
    totalPaymentMethods: Int,
) : FrameLayout(context, attributeSet) {

    private val binding = ItemGenericPaymentMethodViewBinding.inflate(
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
        binding.itemCardEntryViewRoot.isClickable = isCardInteractionEnabled
        binding.genericPaymentTypeRadioButton.isClickable = isCardInteractionEnabled
    }

    fun setupView(paymentMethod: ParcelablePaymentMethod) = this.apply {
        updateItemBackground(paymentMethod)
        setPaymentTypeTitle(paymentMethod.type)
        displayPaymentCreditCardsImages(paymentMethod)

        binding.genericPaymentTypeRadioButton.isChecked = paymentMethod.isSelected

        binding.itemCardEntryViewRoot.setOnClickListener { onItemClicked(paymentMethod) }
        binding.genericPaymentTypeRadioButton.setOnClickListener { onItemClicked(paymentMethod) }
    }

    private fun setPaymentTypeTitle(paymentMethodType: String) {
        binding.paymentTypeTitle.text = when (paymentMethodType) {
            PaymentMethodType.NEW_PIBA.name -> context.getString(R.string.business_account_card)
            PaymentMethodType.PAYPAL.name -> context.getString(R.string.paypal_accepted)
            PaymentMethodType.GP.name -> context.getString(R.string.google_pay_accepted)
            else -> context.getString(R.string.credit_debit_card_accepted)
        }
    }

    private fun displayPaymentCreditCardsImages(paymentMethod: ParcelablePaymentMethod) {
        when (paymentMethod.type) {
            PaymentMethodType.NEW_CARD.name -> {
                val hotelAcceptsPiba = paymentMethod.parcelableAcceptedCardTypes.getPibaAcceptedCards().isNotEmpty()
                val newCardAcceptedCards = paymentMethod.parcelableAcceptedCardTypes.newPaymentTypeAcceptedCards()

                val acceptedCards = if (hotelAcceptsPiba) {
                    newCardAcceptedCards.excludeNonPibaAcceptedCards()
                } else {
                    newCardAcceptedCards
                }

                binding.paymentCreditCardImages.displayCreditCards(acceptedCards.toAcceptedCreditCardList())
            }
            PaymentMethodType.NEW_PIBA.name -> {
                getPibaAcceptedCardType(paymentMethod.parcelableAcceptedCardTypes)?.let { acceptedCard ->
                    binding.paymentCreditCardImages.displayCreditCards(
                        listOf(acceptedCard).toAcceptedCreditCardList()
                    )
                }
            }
            PaymentMethodType.PAYPAL.name -> {
                getPayPalAcceptedCardType(paymentMethod.parcelableAcceptedCardTypes)?.let {
                    if (it.type == PaymentCardType.PP.name) {
                        binding.paymentCreditCardImages.displayCreditCards(
                            listOf(it).toAcceptedCreditCardList()
                        )
                    }
                }
            }
            PaymentMethodType.GP.name -> {
                // Manually handle this case as it's not coming from backend
                binding.paymentCreditCardImages.displayCreditCards(
                    listOf(
                        ParcelableAcceptedCardType(
                            logoUrl = String.format(
                                Urls.GOOGLE_PAY_FORMAT_URL,
                                PaymentMethodType.GP.name.toCardIconPathOpera()
                            ).removePrefix(Urls.CONTENT_BASE_URL),
                            type = PaymentMethodType.GP.name,
                            name = EMPTY_STRING
                        )
                    ).toAcceptedCreditCardList()
                )
            }
        }
    }

    private fun onItemClicked(paymentMethod: ParcelablePaymentMethod) {
        onPaymentMethodSelected?.run { invoke(paymentMethod) }
    }

    private fun updateItemBackground(paymentMethod: ParcelablePaymentMethod) {
        val backgroundResId = if (paymentMethod.isSelected) {
            when(itemPosition) {
                TOP -> R.drawable.payment_method_top_item_selected_background
                MIDDLE -> R.drawable.payment_method_middle_item_selected_background
                BOTTOM -> R.drawable.payment_method_bottom_item_selected_background
            }
        } else {
            R.drawable.payment_method_item_unselected_background
        }
        binding.itemCardEntryViewRoot.background = ContextCompat.getDrawable(context, backgroundResId)
    }
}
