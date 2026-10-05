package com.whitbread.premierinn.hoteldetails.viewholder

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.compose.DiscountCodeSection
import com.whitbread.premierinn.hoteldetails.event.DiscountCodeClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.DiscountCodeUiModel

class DiscountCodeViewHolder(
    composeView: ComposeView,
    private val relay: PublishRelay<Any>
) : BaseRecyclerViewHolder<DiscountCodeUiModel>(composeView) {

    private var uiModelState by mutableStateOf<DiscountCodeUiModel?>(null)

    init {
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        composeView.setContent {
            uiModelState?.let { model ->
                if (model.visible()) {
                    DiscountCodeSection(
                        hasAppliedCode = model.appliedDiscountCode().isNotEmpty(),
                        onDiscountCodeClick = {
                            handleDiscountCodeClick()
                        },
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                    )
                }
            }
        }
    }

    override fun bind(item: DiscountCodeUiModel) {
        uiModelState = item
    }

    private fun handleDiscountCodeClick() {
        val position = bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            relay.accept(DiscountCodeClickEvent(position))
        }
    }
}