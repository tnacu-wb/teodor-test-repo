package com.whitbread.premierinn.summary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.databinding.ItemMenusAllergyInfoBinding
import com.whitbread.premierinn.summary.fragments.MenuAndAllergyInfoBottomSheet
import com.whitbread.premierinn.summary.models.ParcelableMenuAndAllergyInfo

class MenuAndAllergyInfoBottomSheetAdapter(private val bottomSheet: MenuAndAllergyInfoBottomSheet) :
    ListAdapter<ParcelableMenuAndAllergyInfo, MenuAndAllergyInfoBottomSheetAdapter.ViewMenuViewHolder>(DiffCallback) {
    class ViewMenuViewHolder(val binding: ItemMenusAllergyInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ParcelableMenuAndAllergyInfo,
            holder: ViewMenuViewHolder,
            bottomSheet: MenuAndAllergyInfoBottomSheet
        ) {
            binding.tvMenus.text = item.name
            binding.clMenus.setOnClickListener {
                holder.itemView.context.startActivity(IntentUtils.createWebLinkIntent(Urls.CONTENT_BASE_URL + item.menuOrAllergyInfoSrc))
                bottomSheet.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewMenuViewHolder {
        return ViewMenuViewHolder(
            ItemMenusAllergyInfoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewMenuViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, holder, bottomSheet)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ParcelableMenuAndAllergyInfo>() {

        override fun areItemsTheSame(oldItem: ParcelableMenuAndAllergyInfo, newItem: ParcelableMenuAndAllergyInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ParcelableMenuAndAllergyInfo, newItem: ParcelableMenuAndAllergyInfo): Boolean {
            return oldItem == newItem
        }
    }
}
