package com.whitbread.premierinn.hoteldetails.viewholder

import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewAccessibilityInfoPanelBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.event.AccessibilityRoomTypeInfoClickEvent
import com.whitbread.premierinn.hoteldetails.event.MakePhoneCallEvent
import com.whitbread.premierinn.hoteldetails.event.OpenWebLinkEvent
import com.whitbread.premierinn.hoteldetails.event.SendEmailEvent
import com.whitbread.premierinn.hoteldetails.uimodel.AccessibilityInformationUiModel

class AccessibilityViewHolder(
    val binding: ViewAccessibilityInfoPanelBinding,
    val clicks: PublishRelay<Any>
) : BaseRecyclerViewHolder<AccessibilityInformationUiModel>(binding.root) {

    override fun bind(item: AccessibilityInformationUiModel) {
        with(binding){
            callDescription.text = item.description
            callBtnText.text = item.callButtonLabel
            callBtn.setOnClickListener { clicks.accept(MakePhoneCallEvent(item.phoneNumber)) }
            emailBtn.setOnClickListener { clicks.accept(SendEmailEvent(item.emailAddress)) }
            accessibleRoomTypeInfoBtn.setOnClickListener {
                clicks.accept(
                    AccessibilityRoomTypeInfoClickEvent
                )
            }
            accessibilityInfoBtn.setOnClickListener { clicks.accept(OpenWebLinkEvent(item.accessibilityLink)) }
        }

    }
}