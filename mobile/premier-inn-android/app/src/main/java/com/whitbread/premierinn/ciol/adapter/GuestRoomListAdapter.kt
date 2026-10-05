package com.whitbread.premierinn.ciol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.contentsquare.android.Contentsquare
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.GuestRoomClicked
import com.whitbread.premierinn.ciol.entity.GuestsRoomUiModel
import com.whitbread.premierinn.ciol.utils.isSecondGuestPresent
import com.whitbread.premierinn.ciol.viewmodel.state.utils.FieldType
import com.whitbread.premierinn.common.view.InfoMessageBoxView
import com.whitbread.premierinn.databinding.ViewPreStayRoomOverviewBinding
import com.whitbread.premierinn.domain.common.translateTitleToGermanIfApplicable

class GuestRoomListAdapter(
    private val items: List<GuestsRoomUiModel>,
    private val language: String,
    private val onRoomSelected: (GuestRoomClicked) -> Unit
) :
    RecyclerView.Adapter<GuestRoomListAdapter.GuestRoomViewHolder>() {

    private var onContinueButtonClicked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestRoomViewHolder {
        val binding = ViewPreStayRoomOverviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GuestRoomViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: GuestRoomViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position, items.size, language)
    }

    override fun getItemCount() = items.size

    fun onContinueClicked() {
        onContinueButtonClicked = true
        notifyDataSetChanged()
    }


    inner class GuestRoomViewHolder(
        private val binding: ViewPreStayRoomOverviewBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(room: GuestsRoomUiModel, position: Int, listSize: Int, language: String) {

            room.let {
                if (listSize > 1) {
                    binding.roomLabel.text =
                        context.getString(
                            R.string.pre_stay_confirm_room_number_guests,
                            position + 1
                        )
                }
                if (room.isSecondGuestPresent()) {
                    binding.guestNameSecond.setTextColor(
                        context.resources.getColor(
                            R.color.grey_dark,
                            null
                        )
                    )
                    binding.guestNameSecond.text = context.getString(
                        R.string.guest_details_booker_detail_format,
                        room.accompanyingGuestTitle.translateTitleToGermanIfApplicable(language),
                        room.accompanyingGuestFirstName,
                        room.accompanyingGuestLastName
                    )
                }

                if (onContinueButtonClicked && !room.isSecondGuestPresent() && it.numberOfAdults > 1) {
                    binding.guestNameSecond.setTextColor(
                        context.resources.getColor(R.color.new_error_red, null)
                    )
                    binding.preStayErrorBox.isVisible = true
                    binding.preStayErrorBox.setText(
                        context.resources.getString(
                            R.string.pre_stay_second_guest_error
                        )
                    )
                }

                binding.guestName.text = context.getString(
                    R.string.guest_details_booker_detail_format,
                    room.leadGuestTitle.translateTitleToGermanIfApplicable(language),
                    room.leadGuestFirstName,
                    room.leadGuestLastName
                )
                if (room.numberOfAdults == 1) {
                    binding.secondGuest.isVisible = false
                    val params = binding.childContainer.layoutParams as ViewGroup.MarginLayoutParams
                    params.topMargin = 0
                    binding.childContainer.layoutParams = params
                }
                if (room.numberOfChildren > 0) {
                    binding.childContainer.isVisible = true
                    binding.child.text = context.resources.getQuantityString(
                        R.plurals.number_of_children,
                        room.numberOfChildren,
                        room.numberOfChildren
                    )
                }

                binding.leadGuestNationalityWarningBox.handleNationalityMessage(
                    room.isNationalityAndIdValidForLeadGuest(),
                    room.isNationalityMissingForLeadGuest()
                )

                binding.accompanyingGuestNationalityErrorBox.handleNationalityMessage(
                    room.isNationalityAndIdValidForAccompanyingGuest(),
                    room.isNationalityMissingForAccompanyingGuest()
                )

                binding.guestName.setOnClickListener {
                    onRoomSelected.invoke(
                        GuestRoomClicked(
                            room.roomId,
                            FieldType.LEAD_GUEST,
                            room.leadGuestTitle,
                            room.leadGuestFirstName,
                            room.leadGuestLastName,
                            room.leadGuestNationality,
                            room.leadGuestPassportNumber
                        )
                    )
                }

                binding.secondGuest.setOnClickListener {
                    onRoomSelected.invoke(
                        GuestRoomClicked(
                            room.roomId,
                            FieldType.SECOND_GUEST,
                            room.accompanyingGuestTitle,
                            room.accompanyingGuestFirstName,
                            room.accompanyingGuestLastName,
                            room.accompanyingGuestNationality,
                            room.accompanyingGuestPassportNumber
                        )
                    )
                }
            }

            Contentsquare.mask(binding.guestName)
            Contentsquare.mask(binding.guestNameSecond)
        }

        private fun InfoMessageBoxView.handleNationalityMessage(isNationalityAndIdValid: Boolean, isNationalityMissing: Boolean) {
            this.apply {
                isVisible = onContinueButtonClicked && !isNationalityAndIdValid
                setText(
                    if (isNationalityMissing) R.string.pre_stay_nationality_not_filled_error
                    else R.string.pre_stay_nationality_pre_filled_error
                )
            }
        }
    }
}
