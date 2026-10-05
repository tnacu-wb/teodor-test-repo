package com.whitbread.premierinn.alternativeroomselection

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.databinding.ViewRoomTypeComponentBinding
import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class RoomTypeRadioButtonView(
    context: Context,
    private val twinRoomInfo: Single<List<TwinRoomInfoItem>>,
    val twinRoomOption: TwinRoomOption,
    val disposable: AutoCompositeDisposable,
    attributeSet: AttributeSet? = null,
    val deviceLocaleProvider: DeviceLocaleProvider
) : ConstraintLayout(context, attributeSet) {

    val binding = ViewRoomTypeComponentBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        setContent(deviceLocaleProvider)
    }

    private fun setContent(deviceLocaleProvider: DeviceLocaleProvider) {
        disposable.add(twinRoomInfo.subscribeOn(Schedulers.io())
            .subscribe { listOfTwinRoomTypes ->
                val roomType = listOfTwinRoomTypes.firstOrNull { roomType ->
                    roomType.lettingType.substring(0, 2) == twinRoomOption.lettingType.substring(0, 2)
                }

                roomType?.let { roomInfo ->
                    val price = PriceFormat.format(
                        twinRoomOption.price.amount,
                        twinRoomOption.price.currency,
                        deviceLocaleProvider
                    )
                    binding.alternativeRoomTypeTitle.text = roomInfo.label
                    binding.alternativeRoomTypePrice.text =
                        context.resources.getString(R.string.room_total_price, price)
                    binding.alternativeRoomTypeDescription.text = roomInfo.description
                }
            })
    }

    fun setRadioButtonCheck(check: Boolean) {
        binding.alternativeRoomTypeRadioButton.isSelected = check
    }
}