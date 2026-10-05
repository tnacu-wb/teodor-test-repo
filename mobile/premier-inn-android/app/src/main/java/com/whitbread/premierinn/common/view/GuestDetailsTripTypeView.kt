package com.whitbread.premierinn.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.databinding.LayoutTripTypeBinding
import io.reactivex.Observable

const val TRIP_TYPE_LEISURE = 0
const val TRIP_TYPE_BUSINESS = 1
const val TRIP_TYPE_UNSELECTED = -1

class GuestDetailsTripTypeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutTripTypeBinding = LayoutTripTypeBinding.inflate(LayoutInflater.from(context), this, true)
    private var isError = false
    private var selectionRelay: Relay<Int>? = null

    init {
        with(binding) {
            leisureRadioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    businessRadioButton.isChecked = false

                    leisureBox.setBackgroundResource(R.drawable.trip_type_rect_border_selected)
                    businessBox.setBackgroundResource(0)

                    if (isError) {
                        isError = false
                        toggleErrorVisibility(false)
                    }

                    selectionRelay?.accept(TRIP_TYPE_LEISURE)
                }
            }

            businessRadioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    leisureRadioButton.isChecked = false

                    businessBox.setBackgroundResource(R.drawable.trip_type_rect_border_selected)
                    leisureBox.setBackgroundResource(0)

                    if (isError) {
                        isError = false
                        toggleErrorVisibility(false)
                    }

                    selectionRelay?.accept(TRIP_TYPE_BUSINESS)
                }
            }
        }
    }

    fun getTripTypeSelection(): Int {
        return with(binding) {
            when {
                leisureRadioButton.isChecked -> TRIP_TYPE_LEISURE
                businessRadioButton.isChecked -> TRIP_TYPE_BUSINESS
                else -> TRIP_TYPE_UNSELECTED
            }
        }
    }

    fun observeSelection(): Observable<Int> {
        if (selectionRelay != null) {
            return selectionRelay as PublishRelay
        } else {
            selectionRelay = PublishRelay.create()
            return selectionRelay as PublishRelay
        }
    }

    fun showSelectionError() {
        with(binding) {
            if (!leisureRadioButton.isChecked && !businessRadioButton.isChecked) {
                isError = true
                toggleErrorVisibility(true)
            }
        }
    }

    private fun toggleErrorVisibility(isVisible: Boolean) {
        with(binding) {
            tripTypeSelectorContainer.setBackgroundResource(
                if (isVisible) R.drawable.trip_type_rect_border_error else R.drawable.rectangular_grey_all_bordered_background
            )
            tripTypeError.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}
