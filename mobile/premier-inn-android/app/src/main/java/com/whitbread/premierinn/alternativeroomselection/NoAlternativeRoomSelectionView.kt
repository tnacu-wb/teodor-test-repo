package com.whitbread.premierinn.alternativeroomselection

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.databinding.ViewNoAlternativeSelectionRequiredBinding

class NoAlternativeRoomSelectionView(context: Context,
                                     val twinRoomOption: TwinRoomOption,
                                     val deviceLocaleProvider: DeviceLocaleProvider,
                                     attributeSet: AttributeSet? = null,
) : ConstraintLayout(context, attributeSet) {

    val binding = ViewNoAlternativeSelectionRequiredBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        setContent()
    }

    private fun setContent() {
        val price = PriceFormat.format(twinRoomOption.price.amount,
            twinRoomOption.price.currency,
            deviceLocaleProvider)
        binding.noAlternativeRoomSelectionTypePrice.text = context.resources.getString(R.string.room_total_price, price)
        binding.noAlternativeRoomSelectionTypeDescription.text = resources.getString(R.string.no_selection_required)
    }
}