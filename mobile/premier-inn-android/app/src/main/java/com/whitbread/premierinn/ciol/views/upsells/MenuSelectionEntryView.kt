package com.whitbread.premierinn.ciol.views.upsells

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.ciol.entity.upsells.MenuUiModel
import com.whitbread.premierinn.ciol.utils.showPdfViaImplicitIntent
import com.whitbread.premierinn.databinding.ItemMenuSelectionEntryViewBinding

class MenuSelectionEntryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = ItemMenuSelectionEntryViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun bind(activity: Activity, menu: MenuUiModel, isLastIndex: Boolean = false, onMenuClicked: (errorOnOpening: Boolean ) -> Unit) {
        binding.apply {
            menuNameTextView.text = menu.name
            dividerView.isVisible = !isLastIndex
            menuNameTextView.setOnClickListener {
                showPdfViaImplicitIntent(
                    Urls.CONTENT_BASE_URL.plus(menu.menuSrc),
                    activity
                ) { errorOnOpening ->
                    onMenuClicked.invoke(errorOnOpening)
                }
            }
        }
    }
}
