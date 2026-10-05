package com.whitbread.premierinn.alternativeroomselection

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.whitbread.premierinn.R
import com.whitbread.premierinn.databinding.ViewRoomTypeHeaderViewBinding

class RoomTypeHeaderView(
    context: Context,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(context, attributeSet) {

    val binding = ViewRoomTypeHeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setRoomTypeView(header: AlternativeRoomTypeHeader) {
        binding.alternativeRoomHeaderTitle.text = context.getString(R.string.room_number, header.roomNumber)
        binding.alternativeRoomHeaderRoomType.text = header.roomHeading
        binding.alternativeRoomHeaderAdults.text = header.occupantsSubHeading
    }
}