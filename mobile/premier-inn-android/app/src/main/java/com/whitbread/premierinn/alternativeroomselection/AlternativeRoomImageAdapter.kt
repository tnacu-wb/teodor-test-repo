package com.whitbread.premierinn.alternativeroomselection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.utils.dpToPx
import com.whitbread.premierinn.databinding.ItemAlternativeRoomImageViewBinding
import com.whitbread.premierinn.domain.alternativeroom.entity.TwinRoomInfoItem

class AlternativeRoomImageAdapter(private val roomInfo: List<TwinRoomInfoItem>) : RecyclerView.Adapter<AlternativeRoomImageAdapter.AlternativeViewHolder>() {

    private lateinit var itemAlternativeRoomImageViewBinding : ItemAlternativeRoomImageViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlternativeViewHolder {
        itemAlternativeRoomImageViewBinding = ItemAlternativeRoomImageViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view = itemAlternativeRoomImageViewBinding.root
        val screenWidth = parent.resources.displayMetrics.widthPixels
        val params = view.layoutParams
        params.width = (screenWidth * 0.85).toInt()
        view.layoutParams = params
        return AlternativeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlternativeViewHolder, position: Int) {
        holder.bind(item = roomInfo[position])
        setMargin(holder.view, position)
    }

    override fun getItemCount(): Int = roomInfo.size

    private fun setMargin(view: View, position: Int) {
        val marginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        when {
            position == 0 && itemCount == 1 -> {
                marginLayoutParams.marginStart = view.context.dpToPx(12f)
            }
            position == 0 && itemCount != 1 -> {
                marginLayoutParams.marginStart = view.context.dpToPx(12f)
                marginLayoutParams.marginEnd = view.context.dpToPx(4f)
            }
            position == itemCount - 1 -> marginLayoutParams.marginEnd = view.context.dpToPx(12f)
        }
    }

    inner class AlternativeViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(item: TwinRoomInfoItem) {
            val pictureUrl = Urls.CONTENT_BASE_URL + item.image
            itemAlternativeRoomImageViewBinding.alternativeRoomLabel.text = item.label
            itemAlternativeRoomImageViewBinding.alternativeRoomImageView.load(pictureUrl, R.drawable.image_no_hotel)
        }
    }
}