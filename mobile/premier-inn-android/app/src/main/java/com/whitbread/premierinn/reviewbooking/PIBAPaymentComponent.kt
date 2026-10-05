package com.whitbread.premierinn.reviewbooking

import android.content.Context
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.textChanges
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.Truss
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.PibaCnpPaymentDetailsBinding
import io.reactivex.disposables.CompositeDisposable

class PIBAPaymentComponent(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    val pibaCnpPaymentDetailsBinding =
        PibaCnpPaymentDetailsBinding.inflate(LayoutInflater.from(context), this, true)
    private var compositeDisposable = CompositeDisposable()
    private var dinnerBudgetUserInput = 0
    var showDinnerAllowance = false
    var isAlcoholSelected = false
    var isPaymentCnpSelected = false
    var isDinnerAllowanceSelected = false
    var isBusinessParkingSelected = false
    var isWifiSelected = false

    init {
        initView()
    }

    private fun initView() {
        listenOnCnpToggleOptions()
    }

    private fun listenOnCnpToggleOptions() {
        compositeDisposable.add(
            pibaCnpPaymentDetailsBinding.paymentCnpToggle.checkedChanges().subscribe { isChecked ->
                isPaymentCnpSelected = isChecked
                showOrHideCnpPaymentOptions(isChecked)
                if (isChecked &&  pibaCnpPaymentDetailsBinding.dinnerAllowanceSwitch.text.isNullOrEmpty()
                    &&  pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.text.isNullOrEmpty()
                ) {
                    setDinnerAllowanceText()
                    setIncludeAlcoholText()
                    setParkingToggleText()
                    setWifiToggleText()
                }

                if (!isChecked) {
                    showOrHideCnpDinnerOptions(false)
                    resetCnpOptions()
                }
            }
        )

        compositeDisposable.add(
            pibaCnpPaymentDetailsBinding.dinnerAllowanceSwitch.checkedChanges().subscribe { isChecked ->
                isDinnerAllowanceSelected = isChecked
                showOrHideCnpDinnerOptions(isChecked)

                if (!isChecked) {
                    pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.isChecked = false
                    pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.isEnabled = false
                    pibaCnpPaymentDetailsBinding.dinnerBudgetInput.setText(EMPTY_STRING)
                    dinnerBudgetUserInput = 0
                } else {
                    val hasBudget = pibaCnpPaymentDetailsBinding.dinnerBudgetInput.text.isNotEmpty()
                    pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.isEnabled = hasBudget
                }
            }
        )

        compositeDisposable.add( pibaCnpPaymentDetailsBinding.dinnerBudgetInput.textChanges()
            .map { it.toString() }
            .subscribe { dinnerBudgetNumbers ->
                dinnerBudgetNumbers.isNotEmpty().let { hasValue ->
                    pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.isEnabled = hasValue

                    if (hasValue) {
                        dinnerBudgetUserInput = dinnerBudgetNumbers.toInt()
                    }
                }
            })


        compositeDisposable.add(pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.checkedChanges().subscribe { isChecked ->
            isAlcoholSelected = isChecked
        })

        compositeDisposable.add(pibaCnpPaymentDetailsBinding.bookingParkingToggle.checkedChanges().subscribe { isChecked ->
            isBusinessParkingSelected = isChecked
        })

        compositeDisposable.add(pibaCnpPaymentDetailsBinding.wifiToggle.checkedChanges().subscribe { isChecked ->
            isWifiSelected = isChecked
        })
    }

    private fun showOrHideCnpPaymentOptions(show: Boolean) {
        pibaCnpPaymentDetailsBinding.memorableWord.isVisible = show
        pibaCnpPaymentDetailsBinding.memorableWordInfoIcon.isVisible = show
        pibaCnpPaymentDetailsBinding.memorableWordInfoLabel.isVisible = show
        pibaCnpPaymentDetailsBinding.dinnerAllowanceSeparator.isVisible = show
        hideOrShowDinnerAllowance(show && showDinnerAllowance)
        pibaCnpPaymentDetailsBinding.bookingParkingToggle.isVisible = show
        pibaCnpPaymentDetailsBinding.wifiToggle.isVisible = show
        pibaCnpPaymentDetailsBinding.bottomSeparator.isVisible = show
    }

    private fun setDinnerAllowanceText() {
        val spannableText = Truss()
        spannableText
            .pushSpan(TextAppearanceSpan(context, R.style.Header4))
            .append(context.getString(R.string.review_booking_dinner_allowance_label))
            .popSpan()
            .append(StringUtils.LINE_BREAK)
            .pushSpan(TextAppearanceSpan(context, R.style.BodySmall))
            .append(context.getString(R.string.review_booking_dinner_allowance_description))
            .popSpan()
        pibaCnpPaymentDetailsBinding.dinnerAllowanceSwitch.text = spannableText.build()
    }

    private fun setIncludeAlcoholText() {
        val spannableText = Truss()
        spannableText
            .pushSpan(TextAppearanceSpan(context, R.style.Header4))
            .append(context.getString(R.string.review_booking_alcohol_within_budget_label))
            .popSpan()
            .append(StringUtils.LINE_BREAK)
            .pushSpan(TextAppearanceSpan(context, R.style.BodySmall))
            .append(context.getString(R.string.review_booking_alcohol_within_budget_description))
            .popSpan()
        pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.text = spannableText.build()
    }

    private fun setWifiToggleText() {
        val spannableText = Truss()
        spannableText
            .pushSpan(TextAppearanceSpan(context, R.style.Header4))
            .append(context.getString(R.string.review_booking_card_type_not_present_wifi_toggle_label_part_1))
            .popSpan()
            .append(StringUtils.LINE_BREAK)
            .pushSpan(TextAppearanceSpan(context, R.style.BodySmall))
            .append(context.getString(R.string.review_booking_card_type_not_present_wifi_toggle_label_part_2))
            .popSpan()
        pibaCnpPaymentDetailsBinding.wifiToggle.text = spannableText.build()
    }

    private fun setParkingToggleText() {
        val spannableText = Truss()
        spannableText
            .pushSpan(TextAppearanceSpan(context, R.style.Header4))
            .append(context.getString(R.string.review_booking_card_type_not_present_parking_toggle_label_part_1))
            .popSpan()
            .append(StringUtils.LINE_BREAK)
            .pushSpan(TextAppearanceSpan(context, R.style.BodySmall))
            .append(context.getString(R.string.review_booking_card_type_not_present_parking_toggle_label_part_2))
            .popSpan()
        pibaCnpPaymentDetailsBinding.bookingParkingToggle.text = spannableText.build()
    }

    private fun showOrHideCnpDinnerOptions(show: Boolean) {
        pibaCnpPaymentDetailsBinding.dinnerBudgetPerPerson.isVisible = show
        pibaCnpPaymentDetailsBinding.dinnerBudgetWrapper.isVisible = show
    }

    fun resetCnpOptions() {
        pibaCnpPaymentDetailsBinding.paymentCnpToggle.isChecked = false
        pibaCnpPaymentDetailsBinding.dinnerBudgetInput.text.isNotEmpty().apply {
            pibaCnpPaymentDetailsBinding.dinnerBudgetInput.setText(EMPTY_STRING)
        }
        pibaCnpPaymentDetailsBinding.dinnerAllowanceSwitch.isChecked = false
        pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.isChecked = false
        pibaCnpPaymentDetailsBinding.bookingParkingToggle.isChecked = false
        pibaCnpPaymentDetailsBinding.wifiToggle.isChecked = false
        pibaCnpPaymentDetailsBinding.memorableWord.error = EMPTY_STRING
    }

    private fun validateMemWord(): Boolean {
        listenOnMemWordTextChanges()
        return  if (pibaCnpPaymentDetailsBinding.paymentCnpToggle.isChecked && pibaCnpPaymentDetailsBinding.memorableWordInput.text.isEmpty()) {
            pibaCnpPaymentDetailsBinding.memorableWord.error =
                context.getString(R.string.review_booking_card_type_not_present_memorable_word_error_message)
            false
        } else {
            pibaCnpPaymentDetailsBinding.memorableWord.error = EMPTY_STRING
            true
        }
    }

    private fun validateDinnerBudget(): Boolean {
        return if (pibaCnpPaymentDetailsBinding.dinnerAllowanceSwitch.isChecked
            && dinnerBudgetUserInput > ReviewBookingPresenter.MAXIMUM_DINNER_BUDGET_PER_PERSON) {
            pibaCnpPaymentDetailsBinding.dinnerBudgetWrapper.error =
                context.getString(R.string.review_booking_dinner_allowance_error_max_value)
            false
        } else if (pibaCnpPaymentDetailsBinding.dinnerAllowanceSwitch.isChecked && dinnerBudgetUserInput <= 0) {
            pibaCnpPaymentDetailsBinding.dinnerBudgetWrapper.error =
                context.getString(R.string.review_booking_dinner_allowance_error_min_value)
            false
        } else {
            true
        }
    }

    private fun hideOrShowDinnerAllowance(show: Boolean) {
        pibaCnpPaymentDetailsBinding.dinnerAllowanceSwitch.isVisible = show
        pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.isVisible = show

        if (!show || !pibaCnpPaymentDetailsBinding.dinnerAllowanceSwitch.isChecked) {
            pibaCnpPaymentDetailsBinding.includeAlcoholSwitch.isChecked = false
        }
    }

    fun areCnpOptionsValid(): Boolean {
        return validateMemWord() && validateDinnerBudget()
    }

    fun isDinnerBudgetValid(): Boolean {
        return validateDinnerBudget()
    }

    private fun listenOnMemWordTextChanges() {
        compositeDisposable.add(pibaCnpPaymentDetailsBinding.memorableWordInput.textChanges()
            .map { it.toString() }
            .subscribe { password ->
                password.isNotEmpty().let { hasValue ->
                    if (hasValue) {
                        pibaCnpPaymentDetailsBinding.memorableWord.error = EMPTY_STRING
                    }
                }
            })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear();
        }
    }
}