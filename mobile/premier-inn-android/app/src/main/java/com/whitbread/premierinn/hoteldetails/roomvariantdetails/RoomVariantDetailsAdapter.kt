package com.whitbread.premierinn.hoteldetails.roomvariantdetails

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.GlideApp
import com.whitbread.premierinn.common.utils.Truss
import com.whitbread.premierinn.databinding.ItemRoomVariantViewBinding
import com.whitbread.premierinn.domain.hotel.entity.Hotel

class RoomVariantDetailsAdapter(val context: Context, private val roomInfo: List<Hotel.RoomVariantDetails>) : RecyclerView.Adapter<RoomVariantDetailsAdapter.RoomDetailViewHolder>() {

    private lateinit var itemRoomVariantViewBinding: ItemRoomVariantViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomDetailViewHolder {
        itemRoomVariantViewBinding = ItemRoomVariantViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val view = itemRoomVariantViewBinding.root
        if (itemCount > 1) {
            val screenWidth = parent.resources.displayMetrics.widthPixels
            val params = view.layoutParams
            params.width = (screenWidth * 0.85).toInt()
            view.layoutParams = params
        }

        return RoomDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomDetailViewHolder, position: Int) {
        holder.bind(item = roomInfo[position])
    }

    override fun getItemCount(): Int = roomInfo.size

    inner class RoomDetailViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(item: Hotel.RoomVariantDetails) {
            itemRoomVariantViewBinding.roomDetailLabel.text = item.title
            itemRoomVariantViewBinding.roomDetailDescription.text = item.description
            itemRoomVariantViewBinding.disclaimerText.text = Html.fromHtml(item.disclaimer, Html.FROM_HTML_MODE_LEGACY)

            val textBuilder = Truss()
            for (i in item.features.indices) {
                val feature = item.features[i]
                textBuilder
                    .append("\t")
                    .pushSpan(TextAppearanceSpan(context, R.style.Header4))
                    .appendIf(DOT+feature.name, true)
                    .pushSpan(TextAppearanceSpan(context, R.style.BodySmall))
                    .appendIf(feature.details, true)
                    .popSpan()
                    .popSpan()
            }

            itemRoomVariantViewBinding.roomDetailFeatures.text = textBuilder.build()

            val additionalInfo = item.additionalInfo
            if (!TextUtils.isEmpty(additionalInfo)) {
                itemRoomVariantViewBinding.roomDetailAdditionalInfo.text = item.additionalInfo
            } else {
                itemRoomVariantViewBinding.roomDetailAdditionalInfo.visibility = View.GONE
            }

            GlideApp.with(itemRoomVariantViewBinding.roomDetailImageView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.room_types_no_hotel)
                .into(itemRoomVariantViewBinding.roomDetailImageView)
        }
    }

    companion object{
        const val DOT="• \t"
    }
}