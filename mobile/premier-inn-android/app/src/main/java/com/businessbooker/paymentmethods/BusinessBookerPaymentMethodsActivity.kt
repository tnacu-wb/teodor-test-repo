package com.businessbooker.paymentmethods

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.ActivityBusinessBookerPaymentMethodsBinding
import com.whitbread.premierinn.domain.customer.entity.AccessLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessBookerPaymentMethodsActivity : BaseActivity<ActivityBusinessBookerPaymentMethodsBinding>() {

    private val disposables = AutoCompositeDisposable(lifecycle)
    private val viewModel: BusinessBookerPaymentMethodsViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater): ActivityBusinessBookerPaymentMethodsBinding {
        return ActivityBusinessBookerPaymentMethodsBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(resources.getString(R.string.payment_methods_label), true)

        viewModel.states()
            .distinctUntilChanged()
            .subscribe(::render)
            .addTo(disposables)
    }

    private fun render(state: BusinessBookerPaymentMethodsState) {
        if (state.businessAccountCard != null) {
            val businessAccountCardNotification = setBusinessAccountCardNotification(state)
            val businessAccountCardView = BusinessBookerPaymentCardView(
                context = this,
                businessPaymentCard = state.businessAccountCard!!,
                personalPaymentCard = null,
                cardLabel = getString(R.string.business_booker_account_card),
                notificationText = businessAccountCardNotification
            )
            binding.paymentCardContainer.addView(businessAccountCardView.view)
        }
        if (state.personalCard != null) {
            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(
                0,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                    .toInt(),
                0,
                0
            )
            val personalCardView = BusinessBookerPaymentCardView(
                context = this,
                personalPaymentCard = state.personalCard!!,
                businessPaymentCard = null,
                cardLabel = getString(R.string.business_booker_personal_card),
                notificationText = EMPTY_STRING
            )
            personalCardView.layoutParams = layoutParams

            binding.paymentCardContainer.addView(personalCardView.view)
        }
        binding.paymentCardInformationText.text = setPaymentMethodNotification(state)
        binding.notificationGroup.visibility =
            if (setPaymentMethodNotification(state).isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun setPaymentMethodNotification(state: BusinessBookerPaymentMethodsState): String {
        return when (state.customerAccessLevel) {
            AccessLevel.SUPER -> {
                when {
                    state.isCompanyCardAllocated && state.isPersonalCardAllowed && state.personalCard == null -> getString(
                        R.string.business_booker_notification2
                    )
                    state.isCompanyCardAllocated && state.isPersonalCardAllowed.not() && state.personalCard == null -> getString(
                        R.string.business_booker_notification4
                    )
                    state.isCompanyCardAllocated && state.isPersonalCardAllowed.not() && state.personalCard != null -> getString(
                        R.string.business_booker_notification2
                    )
                    state.isCompanyCardAllocated.not() && state.isPersonalCardAllowed.not() && state.personalCard != null -> EMPTY_STRING
                    else -> EMPTY_STRING
                }
            }
            AccessLevel.BOOKER,
            AccessLevel.STAYER,
            AccessLevel.SELF -> {
                when {
                    state.isCompanyCardAllocated && state.isPersonalCardAllowed && state.personalCard == null -> getString(
                        R.string.business_booker_notification1
                    )
                    state.isCompanyCardAllocated && state.isPersonalCardAllowed.not() && state.personalCard == null -> getString(
                        R.string.business_booker_notification3
                    )
                    state.isCompanyCardAllocated && state.isPersonalCardAllowed.not() && state.personalCard != null -> getString(
                        R.string.business_booker_notification3
                    )
                    state.isCompanyCardAllocated.not() && state.isPersonalCardAllowed.not() && state.personalCard != null -> EMPTY_STRING
                    else -> EMPTY_STRING
                }
            }
            else -> {
                EMPTY_STRING
            }
        }
    }

    private fun setBusinessAccountCardNotification(state: BusinessBookerPaymentMethodsState): String {
        return if (state.isCompanyCardAllocated && state.businessAccountCard != null) {
            getString(R.string.business_booker_notification5)
        } else {
            EMPTY_STRING
        }
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, BusinessBookerPaymentMethodsActivity::class.java)
        }
    }
}