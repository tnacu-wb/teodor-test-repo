package com.whitbread.premierinn.additionalinformation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.whitbread.premierinn.additionalinformation.compose.AdditionalInformationScreen
import com.whitbread.premierinn.common.activity.BaseComponentActivity
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.ADDITIONAL_INFO
import com.whitbread.premierinn.compose.ui.theme.PremierInnHolBornAndroidTheme
import com.whitbread.premierinn.reviewbooking.AdditionalInformation
import com.whitbread.premierinn.reviewbooking.ReviewBookActivity
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val CHANGE_BUTTON_KEY = "change_button"

@AndroidEntryPoint
class AdditionalInformationActivity : BaseComponentActivity() {

    var isFromChangeButton: Boolean? = false
    var reviewBookInput: ReviewBookingInput? = null
    var lastValidInformation = emptyList<AdditionalInformation>()

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            isFromChangeButton = data?.getBooleanExtra(CHANGE_BUTTON_KEY, false)
        } else {
            isFromChangeButton = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // we need to set isEnabled = false because we need to stop listening for on back press else it
                // immediately loops back into the same callback again → infinite recursion.
                isEnabled = false
                handleBackPress()
                //we need to re-enable it here so the callback will work again
                isEnabled = true
            }
        })

        reviewBookInput =
            intent.getParcelableExtra(REVIEW_BOOKING_INPUT_WITH_ADDITIONAL_INFO) as? ReviewBookingInput

        val additionalInformation =
            reviewBookInput?.listOfEmployeeQuestionsModel()!!.toMutableList()

        setContent {
            PremierInnHolBornAndroidTheme {
                AdditionalInformationScreen(
                    listOfQuestionListItem = additionalInformation,
                    onClickToRnB = { listOfAdditionalInfoSelected ->
                        lastValidInformation = listOfAdditionalInfoSelected
                        reviewBookInput?.let {
                            val intent = ReviewBookActivity.createIntent(
                                this@AdditionalInformationActivity,
                                it
                                    .toBuilder()
                                    .additionalInformation(listOfAdditionalInfoSelected).build()
                            )
                            launcher.launch(intent)
                        }
                    },
                    onBackClicked = { onBackPressedDispatcher.onBackPressed() })
            }
        }
        reviewBookInput?.let {
            trackAnalytics(it)
        }
    }

    private fun handleBackPress() {
        if (isFromChangeButton == true) {
            reviewBookInput?.let {
                val intent = ReviewBookActivity.createIntent(
                    this@AdditionalInformationActivity,
                    it
                        .toBuilder()
                        .additionalInformation(lastValidInformation).build()
                )
                launcher.launch(intent)
            }
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun trackAnalytics(input: ReviewBookingInput) {
        analytics.track(ADDITIONAL_INFO, AdditionalInformationAnalyticsData(
            rateCode = input.paymentDetailsInput()?.bookingFlowInput()?.chosenRate()!!.code(),
            selectedRooms = listOfNotNull(
                input.paymentDetailsInput()?.bookingFlowInput()!!.roomBookings(),
                input.paymentDetailsInput()?.bookingFlowInput()!!.accessibleRoomBookings(),
                input.paymentDetailsInput()?.bookingFlowInput()!!.twinRoomBookings()
            ).flatten()
        ))

    }

    companion object {
        const val REVIEW_BOOKING_INPUT_WITH_ADDITIONAL_INFO =
            "REVIEW_BOOKING_INPUT_WITH_ADDITIONAL_INFO"

        fun createIntent(
            context: Context,
            input: ReviewBookingInput
        ): Intent {
            return Intent(context, AdditionalInformationActivity::class.java).apply {
                putExtra(REVIEW_BOOKING_INPUT_WITH_ADDITIONAL_INFO, input)
            }
        }
    }
}