package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class AccessibilityInformationUiModel(private val listOrder: Int,
                                      val phoneNumber: String,
                                      val callButtonLabel: String,
                                      val emailAddress: String,
                                      val description: String,
                                      val accessibilityLink: String) : UiModelListItem<AccessibilityInformationUiModel> {

    companion object {
        const val LAYOUT_TYPE = R.layout.view_accessibility_info_panel
    }

    override fun bind(viewHolder: BaseRecyclerViewHolder<AccessibilityInformationUiModel>) {
        viewHolder.bind(this)
    }

    override fun getLayout(): Int {
        return LAYOUT_TYPE
    }

    override fun listOrder(): Int {
        return listOrder
    }
}