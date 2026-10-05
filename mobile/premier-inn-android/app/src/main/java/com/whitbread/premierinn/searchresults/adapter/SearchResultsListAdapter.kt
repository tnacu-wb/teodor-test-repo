package com.whitbread.premierinn.searchresults.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorFilter
import androidx.core.graphics.toColorInt
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.utils.bind
import com.whitbread.premierinn.common.view.*
import com.whitbread.premierinn.mybookings.BaseListAdapter
import com.whitbread.premierinn.mybookings.GDPRUiModelViewHolder
import com.whitbread.premierinn.searchresults.ErrorItem
import com.whitbread.premierinn.searchresults.FullyBookedHotelListItem
import com.whitbread.premierinn.searchresults.HotelListItem

/**
 *
 */
const val ITEM_SELECTED = "ITEM_SELECTED"
const val ITEM_UNSELECTED = "ITEM_UNSELECTED"

class SearchResultsListAdapter(callback: DiffUtil.ItemCallback<ListItem>? = null,
                               private val listener: (Int) -> Unit,
                               private val editListener: () -> Unit) : BaseListAdapter(callback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListViewHolder<ListItem> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_gdpr -> GDPRUiModelViewHolder(view)
            R.layout.view_hotel_details_rate_loading -> SearchResultsLoadingViewHolder(view)
            R.layout.item_search_results_horizontal ->  SearchResultHorizontalItemViewHolder(view, listener)
            R.layout.item_search_results_hotel_details -> SearchResultItemViewHolder(view, listener)
            R.layout.item_search_results_hotel_fully_booked -> SearchResultFullyBookedItemViewHolder(view, listener, editListener)
            R.layout.item_search_results_hotel_error_message -> SearchResultsErrorViewHolder(view)
            else -> throw IllegalAccessException("SearchResultsListAdapter cannot support type $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseListViewHolder<ListItem>, position: Int, payloads: MutableList<Any>) {
        if (holder is SearchResultHorizontalItemViewHolder && !payloads.isEmpty()) {
            holder.view.background = ContextCompat.getDrawable(holder.view.context, if (payloads.contains(ITEM_SELECTED)) R.drawable.shape_white_rect_with_top_border else R.color.white)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class SearchResultItemViewHolder(val view: View, private val listener: (Int) -> Unit) : BaseListViewHolder<ListItem>(view) {
        val flagView by view.bind<TextView>(R.id.search_result_label)
        val employeeDiscountView by view.bind<TextView>(R.id.employee_rate_label)
        val hotelName by view.bind<TextView>(R.id.search_result_hotel_name)
        val networkImageView by view.bind<NetworkImageView>(R.id.search_result_hotel_image)
        val distanceLabel by view.bind<TextView>(R.id.search_result_distance)
        val parkingIcon by view.bind<ImageView>(R.id.search_result_parking_icon)
        val parkingDescription by view.bind<TextView>(R.id.search_result_parking_description)
        val lastFewRoomsLabel by view.bind<View>(R.id.search_result_last_few_rooms_label)
        val tripAdvisorView by view.bind<TripAdvisorView>(R.id.search_results_trip_advisor)
        val pricesFrom by view.bind<TextView>(R.id.search_result_price)

        init {
            view.setOnClickListener { listener(adapterPosition) }
            view.isClickable = false
        }

        override fun bind(item: ListItem) {
            item as HotelListItem
            view.isClickable = true

            flagView.visibility = if (!item.flag?.label.isNullOrEmpty() &&
                !item.flag?.colorHex.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE

            employeeDiscountView.isVisible = item.cellCode.isNotEmpty()
            item.cellCode.isNotEmpty().let {
                employeeDiscountView.text = employeeDiscountView.context.getString(R.string.search_results_employee_discount)

            }
            item.flag?.apply {
                flagView.text = this.label
                try {
                    flagView.background = getColorFilteredDrawable(flagView.context, R.drawable.rounded_corner_2dp,
                            "#".plus(this.colorHex).toColorInt())
                } catch (e: IllegalArgumentException) {
                    Log.w(SearchResultItemViewHolder::class.java.canonicalName, "${this.colorHex} is not valid HEX color", e)
                    //Todo Use logService
                }
            }
            parkingIcon.isVisible = item.parkingDrawableDescription != null
            parkingDescription.isVisible = item.parkingDrawableDescription != null

            item.parkingDrawableDescription?.apply {
                parkingIcon.setImageDrawable(ContextCompat.getDrawable(parkingIcon.context, this.first))
                parkingDescription.text = this.second
            }
            hotelName.text = item.name
            networkImageView.load(Urls.CONTENT_BASE_URL.plus(item.imagePath), R.drawable.image_no_hotel)
            distanceLabel.text = item.formattedDistance.parseAsHtml()
            lastFewRoomsLabel.isVisible = item.limitedAvailability
            tripAdvisorView.setTripAdvisorViewState(item.tripAdvisorRating)
            pricesFrom.text = item.priceFrom
        }

        private fun getColorFilteredDrawable(context: Context, targetResDrawable: Int, newColor: Int): Drawable? {
            val drawable = ContextCompat.getDrawable(context, targetResDrawable)
            drawable?.colorFilter = PorterDuff.Mode.SRC.toColorFilter(newColor)
            return drawable
        }
    }

    internal class SearchResultFullyBookedItemViewHolder(private val view: View,
                                                         private val itemListener: (Int) -> Unit,
                                                         private val editListener: () -> Unit) : BaseListViewHolder<ListItem>(view) {
        val hotelName by view.bind<TextView>(R.id.search_result_hotel_name)
        val editButton by view.bind<TextView>(R.id.callToActionButtonLayout)
        val fullyBookedLabel by view.bind<TextView>(R.id.search_results_fully_booked_label)
        val networkImageView by view.bind<NetworkImageView>(R.id.search_result_hotel_image)

        init {
            view.setOnClickListener { itemListener(adapterPosition) }
            view.isClickable = false
            editButton.apply {
                setOnClickListener { editListener() }
            }
        }

        override fun bind(item: ListItem) {
            if (item is FullyBookedHotelListItem) {
                view.isClickable = true
                hotelName.text = item.name
                networkImageView.load(Urls.CONTENT_BASE_URL.plus(item.imagePath), R.drawable.image_no_hotel)
            } else if (item is HotelListItem) {
                view.isClickable = true
                hotelName.text = item.name
                fullyBookedLabel.text = item.fullyBookedText
                networkImageView.load(Urls.CONTENT_BASE_URL.plus(item.imagePath), R.drawable.image_no_hotel)
            }
        }
    }

    internal class SearchResultHorizontalItemViewHolder(val view: View, private val listener: (Int) -> Unit) : BaseListViewHolder<ListItem>(view) {
        val hotelName by view.bind<TextView>(R.id.search_result_hotel_name)
        val distanceLabel by view.bind<TextView>(R.id.search_result_distance)
        val pricesFrom by view.bind<TextView>(R.id.search_result_price)
        val networkImageView by view.bind<NetworkImageView>(R.id.search_result_hotel_image)
        val fullyBookedLabel by view.bind<TextView>(R.id.search_results_fully_booked_label)
        val parkingDescription by view.bind<TextView>(R.id.search_result_parking_description)
        val tripAdvisorNetworkImageView by view.bind<NetworkImageView>(R.id.search_results_trip_advisor_image)

        init {
            view.setOnClickListener { listener(adapterPosition) }
            view.isClickable = false
        }

        override fun bind(item: ListItem) {
            view.background = ContextCompat.getDrawable(view.context, R.color.white)
            item as HotelListItem
            view.isClickable = true
            hotelName.text = item.name

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                networkImageView.foreground = if (item.fullyBooked) ContextCompat.getDrawable(view.context, R.color.grey_semi_transparent) else null
            }
            fullyBookedLabel.text = item.fullyBookedText
            fullyBookedLabel.isVisible = item.fullyBooked

            networkImageView.load(Urls.CONTENT_BASE_URL.plus(item.imagePath), R.drawable.image_no_hotel)
            pricesFrom.text = item.priceFrom
            distanceLabel.text = item.formattedDistance.parseAsHtml()

            tripAdvisorNetworkImageView.load(item.tripAdvisorRating?.imageUrl)
            tripAdvisorNetworkImageView.isVisible = item.tripAdvisorRating != null

            parkingDescription.isVisible = item.parkingDrawableDescription != null
            item.parkingDrawableDescription?.apply {
                val (iconRes, text) = this
                parkingDescription.text = text
                //parkingDescription.compoundDrawablePadding = view.context.resources.getDimensionPixelSize(R.dimen.default_small_margin)
                // parkingDescription.setCompoundDrawablesRelativeWithIntrinsicBounds(iconRes, 0, 0, 0)
            }

        }
    }

    internal class SearchResultsLoadingViewHolder(view: View) : BaseListViewHolder<ListItem>(view) {
        override fun bind(item: ListItem) {
            //Void
        }
    }

    internal class SearchResultsErrorViewHolder(val view: View) : BaseListViewHolder<ListItem>(view) {
        val errorInfoView by view.bind<InfoMessageBoxView>(R.id.search_results_error_info)

        override fun bind(item: ListItem) {
            item as ErrorItem
            if (item.errorResId != null && item.errorResId != 0) {
                errorInfoView.setText(view.context.getString(item.errorResId))
            }
        }
    }
}

