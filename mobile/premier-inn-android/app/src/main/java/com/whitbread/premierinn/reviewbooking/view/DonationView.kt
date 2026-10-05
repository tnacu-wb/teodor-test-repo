package com.whitbread.premierinn.reviewbooking.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.databinding.ViewDonationBinding
import com.whitbread.premierinn.domain.common.MAX_DONATION
import com.whitbread.premierinn.domain.common.MIN_DONATION
import com.whitbread.premierinn.domain.common.NO_DONATION
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.hoteldetails.DonationsInput
import io.reactivex.Observable
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class DonationView @JvmOverloads constructor
    (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    FrameLayout(context, attrs, defStyleAttr) {

    private var buttonClicksRelay: PublishRelay<DonationType> = PublishRelay.create()
    private var donationsRelay: PublishRelay<DonationsInput?> = PublishRelay.create()
    private var binding: ViewDonationBinding = ViewDonationBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initialiseViewObservables()
    }

    private fun initialiseViewObservables() {
        setUpDonations()
    }

    private fun populateButtonText(input: DonationsInput) {
        val maxDonation = input.maxDonation?.toBigDecimal().toString()
        val minDonation = input.minDonation?.toBigDecimal().toString()
        binding.maxDonationButton.isVisible = PriceDomain.maxDonation != 0f
        binding.minDonationButton.isVisible = PriceDomain.minDonation != 0f
        binding.maxDonationButton.text = String.format("£%s", maxDonation)
        binding.minDonationButton.text = String.format("£%s", minDonation)
        binding.noDonationButton.text = context.getString(R.string.review_booking_no_donation)
    }

    private fun setUpButtonClicksRelay(donationInput : DonationsInput) {
        val maxDonationClicks: Observable<DonationType> = binding.maxDonationButton.clicks()
            .map { DonationType(MAX_DONATION, donationInput.maxDonation ?: 0f,
                String.format("£%s", donationInput.maxDonation)) }
        val minDonationClicks: Observable<DonationType> = binding.minDonationButton.clicks()
            .map { DonationType(MIN_DONATION, donationInput.minDonation ?: 0f,
                String.format("£%s", donationInput.minDonation)) }
        val noDonationClicks: Observable<DonationType> = binding.noDonationButton.clicks()
            .map { DonationType(NO_DONATION, 0f,
                context.getString(R.string.no_donation)) }

        Observable.merge(maxDonationClicks, minDonationClicks, noDonationClicks)
            .doOnEach { donationButton -> updateSelectedButton(donationButton.value?.typeOfDonation) }
            .subscribe{ buttonClicked ->
                buttonClicksRelay.accept(buttonClicked)
            }
    }

    fun enableAnimation(konfettiView: KonfettiView) {

        buttonClicksRelay.subscribe { button ->
            var selectedButton: Button? = null
            when (button.typeOfDonation) {
                // Green BD
                MAX_DONATION -> selectedButton = binding.maxDonationButton
                MIN_DONATION -> selectedButton = binding.minDonationButton
                else -> {} // Nothing to do
            }
            if (selectedButton != null) {
                val buttonCoordinates = IntArray(2)
                selectedButton.getLocationInWindow(buttonCoordinates)
                val buttonCentreX = buttonCoordinates[0] + (selectedButton.width / 2)
                val buttonCentreY = buttonCoordinates[1] - (selectedButton.height / 2)

                val burst = if (button.typeOfDonation == MAX_DONATION) 100 else 50
                konfettiView.build()
                        .addColors(ContextCompat.getColor(context, R.color.teal_light))
                        .setDirection(0.0, 359.0)
                        .setSpeed(2f, 20f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(350L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(Size(6, 50f))
                        .setPosition(buttonCentreX.toFloat(), buttonCentreY.toFloat())
                        .burst(burst)
            }
        }
    }

    fun buttonClicks(): Observable<DonationType> {
        return buttonClicksRelay
    }

    fun donations(): PublishRelay<DonationsInput?> {
        return donationsRelay
    }

    private fun setUpDonations() {
        donationsRelay.subscribe { input ->
            input?.let {
                populateButtonText(it)
                setUpButtonClicksRelay(it)
            }
        }
    }

    private fun updateSelectedButton(buttonSelected: String?) {
        buttonSelected?.let {
            binding.maxDonationButton.isSelected = buttonSelected == MAX_DONATION
            binding.minDonationButton.isSelected = buttonSelected == MIN_DONATION
            binding.noDonationButton.isSelected = buttonSelected == NO_DONATION
        }
    }

    fun setDonationMessage(donationDescription: String) {
        binding.donationMessage.text = donationDescription
    }

    fun setDonationHeading(donationTitle: String) {
        binding.donationHeading.text = donationTitle
    }

    fun setDonationImage(donationImageUrl: String) {
        binding.donationImageView.load(donationImageUrl)
    }

    data class DonationType(val typeOfDonation : String, val donationAmount: Float, val donationString: String)
}