package com.whitbread.premierinn.landing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.common.utils.dpToPx
import com.whitbread.premierinn.databinding.FrequentlyBookedWidgetBinding
import com.whitbread.premierinn.domain.dashboard.entity.FrequentBooking

class FrequentBookingItemAdapter(private val frequentlyBooked: List<FrequentBooking>,
                                 val selectHotelName: (FrequentBooking) -> Unit,
                                 val selectDate: (FrequentBooking) -> Unit) :
        RecyclerView.Adapter<FrequentBookingItemAdapter.FrequentBookingViewHolder>() {

    private lateinit var frequentlyBookedWidgetBinding: FrequentlyBookedWidgetBinding
    private var screenWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrequentBookingViewHolder {
        // get screen width
        screenWidth = parent.resources.displayMetrics.widthPixels
        frequentlyBookedWidgetBinding = FrequentlyBookedWidgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FrequentBookingViewHolder(frequentlyBookedWidgetBinding.root)
    }

    override fun onBindViewHolder(holder: FrequentBookingViewHolder, position: Int) {
        holder.bind(item = frequentlyBooked[position])
        setLayoutParams(holder.view)
        setMargin(holder.view, position)
    }

    override fun getItemCount() = frequentlyBooked.size

    private fun setLayoutParams(view: View) {
        if (itemCount > 1) {
            val itemWidth = screenWidth / 1.2
            val layoutParams = view.layoutParams
            layoutParams.height = layoutParams.height
            layoutParams.width = itemWidth.toInt()
            view.layoutParams = layoutParams
        }
    }

    private fun setMargin(view: View, position: Int) {
        val marginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        when {
            position == 0 && itemCount == 1 -> {
                marginLayoutParams.marginStart = view.context.dpToPx(16f)
                marginLayoutParams.marginEnd = view.context.dpToPx(8f)
            }
            position == 0 && itemCount != 1 -> marginLayoutParams.marginStart = view.context.dpToPx(16f)
            position == itemCount - 1 -> marginLayoutParams.marginEnd = view.context.dpToPx(8f)
        }
    }

    inner class FrequentBookingViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        fun bind(item: FrequentBooking) {
            frequentlyBookedWidgetBinding.frequentlyBookedHotelImage.load(item.hotelImage)
            frequentlyBookedWidgetBinding.frequentlyBookedHotelName.text = item.hotelName

            frequentlyBookedWidgetBinding.frequentlyBookedSelectDate.setOnClickListener {
                selectDate(item)
            }

            frequentlyBookedWidgetBinding.frequentlyBookedHotelName.setOnClickListener {
                selectHotelName(item)
            }
        }
    }
}