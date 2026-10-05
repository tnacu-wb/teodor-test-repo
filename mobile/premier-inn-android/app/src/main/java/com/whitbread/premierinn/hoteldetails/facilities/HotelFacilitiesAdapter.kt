package com.whitbread.premierinn.hoteldetails.facilities

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.availability.Facility
import com.whitbread.premierinn.databinding.ItemHotelFacilityBinding

class HotelFacilitiesAdapter(private val items: List<Facility>) : RecyclerView.Adapter<HotelFacilitiesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHotelFacilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ItemHotelFacilityBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(facility: Facility) {
            facility.code()?.let { codes ->
                if (codes.drawable != -1) {
                    binding.itemIcon.setImageDrawable(ContextCompat.getDrawable(context, codes.drawable))
                } else {
                    binding.itemIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_available))
                }
            } ?: binding.itemIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_available))
            binding.itemText.text = facility.legend()
        }
    }
}
