package com.whitbread.premierinn.ciol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.databinding.ViewSelectGuestOccasionItemViewBinding

class SpecialOccasionsAdapter(
    private val occasions: List<HotelPreferenceUiModel>,
    private val onSpecialOccasionSelected: (occasion: HotelPreferenceUiModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SelectGuestsOccasionViewHolder(
            ViewSelectGuestOccasionItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = occasions.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SelectGuestsOccasionViewHolder) {
            holder.bindItem(occasions[position])
        }
    }

    inner class SelectGuestsOccasionViewHolder(private val binding: ViewSelectGuestOccasionItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(occasion: HotelPreferenceUiModel) {
            setState(occasion, onSpecialOccasionSelected)
        }

        private fun setState(occasion: HotelPreferenceUiModel, onSpecialOccasionSelected: (occasion: HotelPreferenceUiModel) -> Unit) {
            binding.selectOccasionTextView.text = occasion.label
            if (occasion == occasions.last()) {
                binding.divider.isVisible = false
            }
            binding.occasionsContainer.setOnClickListener {
                onSpecialOccasionSelected(occasion)
            }
        }
    }
}
