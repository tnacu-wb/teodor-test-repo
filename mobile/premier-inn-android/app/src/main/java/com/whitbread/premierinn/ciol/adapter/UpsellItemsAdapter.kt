package com.whitbread.premierinn.ciol.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.UpsellHeader
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.utils.isEnabled
import com.whitbread.premierinn.ciol.utils.isPreselected
import java.util.Locale

private const val UPSELL_ITEM = 0
private const val UPSELL_HEADER = 1

class UpsellItemsAdapter : Adapter<RecyclerView.ViewHolder>() {

    var upsellItems: MutableList<UpsellItem> = mutableListOf()
    var deviceLocale: Locale = Locale.getDefault()
    var isMultiRoomBooking: Boolean = false
    var preselectedRoomSelections = listOf<RoomSelection>()
    var roomSelections = listOf<RoomSelection>()
    var totalRoomsAdults = 0
    var onUpsellClicked: ((upsellItem: UpsellEntry) -> Unit)? = null
    var isLoadingDisplayed = false
    var shouldHideAddUpsellsSection = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UPSELL_ITEM -> UpsellsViewHolder(UpsellItemView(parent.context))
            else -> HeaderViewHolder(UpsellHeaderView(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UpsellsViewHolder) {
            holder.bindItem(upsellItems[position] as UpsellEntry)
        }
        if (holder is HeaderViewHolder) {
            holder.bindItem(upsellItems[position] as UpsellHeader)
        }
    }

    override fun getItemCount(): Int = upsellItems.size

    override fun getItemViewType(position: Int) = when (upsellItems[position]) {
        is UpsellEntry -> UPSELL_ITEM
        is UpsellHeader -> UPSELL_HEADER
    }

    inner class UpsellsViewHolder(private val upsellItemView: UpsellItemView) : RecyclerView.ViewHolder(upsellItemView) {
        fun bindItem(upsellItem: UpsellEntry) {
            upsellItemView.apply {
                setState(upsellItem, isMultiRoomBooking, preselectedRoomSelections, roomSelections, totalRoomsAdults, deviceLocale, shouldHideAddUpsellsSection)
                setOnClickListener {
                    if (upsellItem.isEnabled() && !upsellItem.isPreselected() && !isLoadingDisplayed) {
                        onUpsellClicked?.invoke(upsellItem)
                    } else {
                        null
                    }
                }
            }
        }
    }

    inner class HeaderViewHolder(private val upsellHeaderView: UpsellHeaderView) : RecyclerView.ViewHolder(upsellHeaderView) {
        fun bindItem(upsellHeader: UpsellHeader) {
            upsellHeaderView.setState(upsellHeader)
        }
    }
}