package com.whitbread.premierinn.ciol.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.UpsellHeader
import com.whitbread.premierinn.ciol.entity.upsells.UpsellsSelectedHeader
import com.whitbread.premierinn.ciol.entity.upsells.UpsellsUnselectedHeader
import com.whitbread.premierinn.databinding.HeaderUpsellBinding

class UpsellHeaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val binding = HeaderUpsellBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun setState(upsellHeader: UpsellHeader) {
        binding.headerTextView.text = when(upsellHeader) {
            is UpsellsSelectedHeader -> String.format(context.getString(R.string.upsells_selected_header_text), upsellHeader.selectedUpsells)
            is UpsellsUnselectedHeader -> context.getString(R.string.upsells_unselected_header_text)
        }
    }
}
