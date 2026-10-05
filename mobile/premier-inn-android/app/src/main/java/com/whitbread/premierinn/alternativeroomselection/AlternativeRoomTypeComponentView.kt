package com.whitbread.premierinn.alternativeroomselection

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.databinding.ViewAlternativeRoomTypeComponentBinding
import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class AlternativeRoomTypeComponentView(
    context: Context,
    alternativeRoomTypeHeader: AlternativeRoomTypeHeader,
    val listOfRoomTypes: Single<List<TwinRoomInfoItem>>,
    val roomTypeChoices: RoomTypeChoices,
    val disposable: AutoCompositeDisposable,
    val deviceLocaleProvider: DeviceLocaleProvider,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(context, attributeSet) {

    val binding = ViewAlternativeRoomTypeComponentBinding.bind(
        LayoutInflater.from(context)
            .inflate(R.layout.view_alternative_room_type_component, this, true)
    )

    private val roomTypeViews = mutableListOf<RoomTypeRadioButtonView>()
    private var selectedRoomTypeViewRelay: PublishRelay<RoomTypeRadioButtonView> =
        PublishRelay.create()
    private var compositeDisposable = CompositeDisposable()

    init {
        binding.alternativeRoomTypeComponent.removeAllViews()
        binding.roomTypeHeaderView.setRoomTypeView(alternativeRoomTypeHeader)
        showAlternativeSelectionView(context)
        showNonAlternativeSelectionView(deviceLocaleProvider)
        listenToRoomTypeSelection()
    }

    private fun showAlternativeSelectionView(context: Context) {
        roomTypeChoices.firstTwinRoomOption.let { option ->
            if (option.isTwinRoomOption) {
                createRoomTypeView(context, option, true)
            }
        }

        roomTypeChoices.secondTwinRoomOption?.let { option ->
            createRoomTypeView(context, option, false)
        }
    }

    private fun createRoomTypeView(
        context: Context,
        twinRoomOption: TwinRoomOption,
        selected: Boolean
    ) {
        val item = RoomTypeRadioButtonView(context, listOfRoomTypes,
            twinRoomOption, disposable, deviceLocaleProvider = deviceLocaleProvider)
        binding.alternativeRoomTypeComponent.addView(item)
        setClickListener(item)
        roomTypeViews.add(item)
        setSelectedRoomType(item, selected)
    }

    private fun showNonAlternativeSelectionView(deviceLocaleProvider: DeviceLocaleProvider) {
        if (!roomTypeChoices.firstTwinRoomOption.isTwinRoomOption) {
            val item = NoAlternativeRoomSelectionView(context,
                roomTypeChoices.firstTwinRoomOption, deviceLocaleProvider)
            binding.alternativeRoomTypeComponent.addView(item)
        }
    }

    private fun setClickListener(item: RoomTypeRadioButtonView) {
        item.binding.alternativeRoomTypeRadioButton.setOnClickListener {
            selectedRoomTypeViewRelay.accept(item)
        }
    }

    private fun setSelectedRoomType(
        selectedRoomType: RoomTypeRadioButtonView,
        isSelected: Boolean
    ) {
        selectedRoomType.setRadioButtonCheck(isSelected)
    }

    private fun selectRoomType(roomTypeRadioButtonView: RoomTypeRadioButtonView) {
        for (roomTypeView in roomTypeViews) {
            roomTypeView.setRadioButtonCheck(false)
        }

        setSelectedRoomType(roomTypeRadioButtonView, true)
    }

    private fun listenToRoomTypeSelection() {
        compositeDisposable.add(selectedRoomTypeViewRelay
            .subscribe { roomType ->
                selectRoomType(roomType)
            }
        )
    }

    fun onSelectionClicked(): PublishRelay<RoomTypeRadioButtonView> {
        return selectedRoomTypeViewRelay
    }

    data class SelectedRoomClickEvent(
        val id: Int,
        val twinRoomOption: TwinRoomOption
    )
}