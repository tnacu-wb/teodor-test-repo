package com.whitbread.premierinn.search.adapter.adapteritem

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder

class EndOfResultsUiModel : SearchAdapterItem<EndOfResultsUiModel> {

    companion object {
        const val LAYOUT_ID = R.layout.item_search_end
    }

    override fun getLayoutId(): Int {
        return LAYOUT_ID
    }

    override fun onBind(holder: BaseRecyclerViewHolder<EndOfResultsUiModel>) {
        holder.bind(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}