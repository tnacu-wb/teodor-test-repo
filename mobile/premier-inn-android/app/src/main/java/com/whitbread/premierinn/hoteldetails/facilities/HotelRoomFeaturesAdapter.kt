package com.whitbread.premierinn.hoteldetails.facilities

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.databinding.ItemHotelFacilityBinding

class HotelRoomFeaturesAdapter(private val items: List<RoomFeaturesModel>) : RecyclerView.Adapter<HotelRoomFeaturesAdapter.ViewHolder>() {

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
        fun bind(item: RoomFeaturesModel) {
            with(binding) {
                itemIcon.setImageDrawable(ContextCompat.getDrawable(context, item.drawableId))
                itemText.text = item.title
            }
        }
    }
}
