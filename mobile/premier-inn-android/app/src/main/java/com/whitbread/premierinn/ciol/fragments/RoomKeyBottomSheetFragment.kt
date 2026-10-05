package com.whitbread.premierinn.ciol.fragments

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.base.view.BaseBottomSheetDialogFragment
import com.whitbread.premierinn.ciol.uimodel.RoomKeyInstructionsModel
import com.whitbread.premierinn.common.usecase.GetQrKioskHotelUseCase
import com.whitbread.premierinn.databinding.FragmentRoomKeyInstructionsBinding
import com.whitbread.premierinn.qrkiosk.QRCodeActivity
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val ROOM_KEY = "room_key"

@AndroidEntryPoint
class RoomKeyBottomSheetFragment : BaseBottomSheetDialogFragment() {
    @Inject
    lateinit var getQrKioskHotelUseCase: GetQrKioskHotelUseCase

    private lateinit var binding : FragmentRoomKeyInstructionsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoomKeyInstructionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.parcelable<RoomKeyInstructionsModel>(ROOM_KEY)?.let { model ->
            with(binding) {
                model.hotelImage?.let {
                    headerContainer.hotelImage.load(Urls.CONTENT_BASE_URL.plus(model.hotelImage))
                } ?: run {
                    headerContainer.hotelImage.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(),R.drawable.ic_map_hotel_head))
                }
                headerContainer.headerTitle.text = model.contentTitle
                setUpRoomKeyInstructionsText(model.roomKeyInstructions, model.bookingReference, model.hotelId)
                headerContainer.closeButton.setOnClickListener {
                    closeDialog()
                }
            }
        }
    }

    private fun setUpRoomKeyInstructionsText(
        roomKeyInstructions: String,
        bookingReferenceId: String,
        hotelId: String
    ) {
        val stringBuilder =
            SpannableStringBuilder(Html.fromHtml(roomKeyInstructions, Html.FROM_HTML_MODE_LEGACY))
        val getListOfQRHotelsId = getQrKioskHotelUseCase()

        stringBuilder.getSpans(0, stringBuilder.length, URLSpan::class.java).forEach { span ->
            val start = stringBuilder.getSpanStart(span)
            val end = stringBuilder.getSpanEnd(span)
            stringBuilder.removeSpan(span)
            stringBuilder.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        if (getListOfQRHotelsId.any { hotelId == it.hotelCode }) {
                            requireActivity().startActivity(
                                QRCodeActivity.createIntent(
                                    requireContext(),
                                    bookingReferenceId
                                )
                            )
                        } else {
                            context?.let {
                                showInfoDialog(it)
                            }
                        }
                    }
                },
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }


        binding.roomKeyInstructions.text = stringBuilder
        binding.roomKeyInstructions.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showInfoDialog(context: Context) {
        AlertDialog.Builder(context, R.style.PurpleDialog)
            .setTitle(getString(R.string.qr_code_missing_title))
            .setMessage(getString(R.string.qr_code_missing_message))
            .setPositiveButton(getText(R.string.dialog_ok_button))
            { _, _ ->  }
            .create()
            .show()
    }
}
