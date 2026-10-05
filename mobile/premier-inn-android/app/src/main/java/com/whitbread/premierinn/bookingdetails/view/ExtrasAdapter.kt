package com.whitbread.premierinn.bookingdetails.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.ciol.adapter.BookingDetailsUpsellItemView
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.data.common.EMPTY_STRING
import java.util.Locale

class ExtrasAdapter : RecyclerView.Adapter<ExtrasAdapter.ExtrasViewHolder>() {
    var upsellItems: MutableList<UpsellItem> = mutableListOf()
    var deviceLocale: Locale = Locale.getDefault()
    var numberOfNightsFormatted: String = EMPTY_STRING
    var numberOfNights: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtrasViewHolder {
        return ExtrasViewHolder(BookingDetailsUpsellItemView(parent.context))
    }

    override fun getItemCount(): Int = upsellItems.size

    override fun onBindViewHolder(holder: ExtrasViewHolder, position: Int) {
        holder.bindItem(upsellItems[position] as UpsellEntry)
    }

    inner class ExtrasViewHolder(private val upsellItemView: BookingDetailsUpsellItemView) :
        RecyclerView.ViewHolder(upsellItemView) {
        fun bindItem(upsellItem: UpsellEntry) {
            upsellItemView.apply {
                setState(upsellItem, numberOfNightsFormatted, numberOfNights)
            }
        }
    }
}