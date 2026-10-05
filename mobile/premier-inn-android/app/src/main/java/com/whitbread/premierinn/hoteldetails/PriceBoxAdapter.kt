package com.whitbread.premierinn.hoteldetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.databinding.PriceBoxLayoutBinding
import com.whitbread.premierinn.databinding.ShowMoreLayoutBinding
import com.whitbread.premierinn.databinding.ViewPriceBoxBinding
import com.whitbread.premierinn.hoteldetails.uimodel.RateBoxUiModel

class PriceBoxAdapter(
    private val rateBoxUiModels: List<RateBoxUiModel>?,
    private val onPriceClick: (rateType: String, rateClassification: String, pmsRoomType: String,
                               roomClass: String, formattedBaseRate: String?,
                               promotionCode: String?, promotionTag: String?) -> Unit,
    private val onShowMoreClick: (Boolean) -> Unit,
    private var isListExpanded: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filteredList: MutableList<RateBoxUiModel> = mutableListOf()
    private lateinit var priceBoxLayoutBinding: PriceBoxLayoutBinding
    private lateinit var viewPriceBoxLayoutBinding: ViewPriceBoxBinding
    private lateinit var showMoreLayoutBinding: ShowMoreLayoutBinding

    init {
        rateBoxUiModels?.let {
            filteredList = if (it.size > 3 && !isListExpanded) {
                it.subList(0, 3).toMutableList()
            } else {
                it.toMutableList()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        priceBoxLayoutBinding = PriceBoxLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        showMoreLayoutBinding = ShowMoreLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.price_box_layout -> PriceAdapterViewHolder(priceBoxLayoutBinding)
            R.layout.show_more_layout -> ShowMoreViewHolder(showMoreLayoutBinding)
            else -> throw IllegalArgumentException("Not supported type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PriceAdapterViewHolder -> {
                if (filteredList.isNotEmpty()) {
                    holder.setData(filteredList[position])
                } else {
                    holder.hide()
                }
            }

            is ShowMoreViewHolder -> {
                holder.setClickListener()
                if (filteredList.size <= 3 && !isListExpanded) {
                    rateBoxUiModels?.let {
                        holder.setShowMoreText(it.size.minus(filteredList.size))
                    }
                } else if (filteredList.size > 3) {
                    holder.setShowLessText()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        rateBoxUiModels?.let {
            return if (rateBoxUiModels.size <= 3) filteredList.size else filteredList.size + 1
        }
        return 0
    }

    override fun getItemViewType(position: Int) =
        if (position == filteredList.size) R.layout.show_more_layout else R.layout.price_box_layout

    private fun showFullList() {
        rateBoxUiModels?.let {
            filteredList.clear()
            filteredList.addAll(it)
        }
        notifyItemRangeChanged(3, filteredList.size - 3)
    }

    private fun showPartialList() {
        rateBoxUiModels?.let {
            filteredList.clear()
            filteredList.addAll(it.subList(0, 3))
        }
        notifyDataSetChanged()
    }

    inner class PriceAdapterViewHolder(val binding: PriceBoxLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(rateBoxUiModel: RateBoxUiModel) {
            viewPriceBoxLayoutBinding = ViewPriceBoxBinding.bind(priceBoxLayoutBinding.root)
            binding.hotelDetailsRatePlan.setData(rateBoxUiModel)
            viewPriceBoxLayoutBinding.calSaverButton.setOnClickListener {
                onPriceClick(
                    viewPriceBoxLayoutBinding.priceBoxRateName.toString(),
                    rateBoxUiModel.rateClassification(), rateBoxUiModel.pmsRoomType(),
                    rateBoxUiModel.roomVariant().roomClass,
                    rateBoxUiModel.formattedBaseRate(),
                    rateBoxUiModel.promotionCode(),
                    rateBoxUiModel.promoTag()
                )
            }
        }

        fun hide() {
            priceBoxLayoutBinding.hotelDetailsRatePlan.visibility = View.GONE
        }
    }

    inner class ShowMoreViewHolder(val binding: ShowMoreLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setShowMoreText(hiddenItems: Int?) {
            val label = String.format(
                binding.root.context.resources.getString(R.string.hotel_details_activity_show_more_button),
                hiddenItems.toString()
            )
            showMoreLayoutBinding.showMore.text = label
        }

        fun setShowLessText() {
            showMoreLayoutBinding.showMore.text =
                binding.root.context.resources.getText(R.string.hotel_details_activity_show_less_button)
        }

        fun setClickListener() {
            showMoreLayoutBinding.showMore.setOnClickListener {
                if (!isListExpanded) {
                    isListExpanded = true
                    onShowMoreClick(true)
                    showFullList()
                } else {
                    isListExpanded = false
                    onShowMoreClick(false)
                    showPartialList()
                }
            }
        }
    }
}
