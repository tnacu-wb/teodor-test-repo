package com.whitbread.premierinn.ciol.utils

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.view.LayoutInflater
import android.view.View
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.RegCardPdfAdditionalGuest
import com.whitbread.premierinn.ciol.entity.RegCardPdfModel
import com.whitbread.premierinn.ciol.entity.RegCardPdfResult
import com.whitbread.premierinn.databinding.ViewRegCardAdditionalGuestItemBinding
import com.whitbread.premierinn.databinding.ViewRegCardPdfBinding
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Base64
import javax.inject.Inject

private const val A4_WIDTH = 595
private const val A4_HEIGHT = 842
private const val FILE_ID_MIN = 100000
private const val FILE_ID_MAX = 999999
private const val REG_CARD_PDF_NAME_TEMPLATE = "REG_RES%1\$s_ID%2\$s_P%3\$s.pdf"

class RegCardPdfGenerator @Inject constructor(
        private val context: Context
) {
    fun generatePdfBase64Encoded(model: RegCardPdfModel): RegCardPdfResult? {
        val pdfBinding = ViewRegCardPdfBinding.inflate(LayoutInflater.from(context), null, false)
        bindRegCard(pdfBinding, model)

        val pdf = PdfDocument()
        val pageInfo = PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 1).create()
        val page = pdf.startPage(pageInfo)
        val content = pdfBinding.root

        content.measure( // Measure to A4 width
                View.MeasureSpec.makeMeasureSpec(
                        A4_WIDTH, View.MeasureSpec.EXACTLY
                ),  // Measure to A4 height
                View.MeasureSpec.makeMeasureSpec(
                        A4_HEIGHT, View.MeasureSpec.EXACTLY
                )
        )
        content.layout(0, 0, content.measuredWidth, content.measuredHeight)
        content.draw(page.canvas)

        pdf.finishPage(page)

        val pdfBase64Encoded: String

        try {
            val stream = ByteArrayOutputStream()
            pdf.writeTo(stream)
            pdf.close()
            pdfBase64Encoded = Base64.getEncoder().encodeToString(stream.toByteArray())
            stream.close()
        } catch (e: IOException) {
            return null
        }

        return RegCardPdfResult(pdfBase64Encoded, generateFileName(model.reservationId, model.profileId))
    }

    private fun bindRegCard(binding: ViewRegCardPdfBinding, model: RegCardPdfModel) {
        binding.transactionId.text = context.getString(R.string.reg_card_transaction_id, model.transactionId)
        binding.hotelName.text = context.getString(R.string.reg_card_hotel_name, model.hotelName)
        binding.hotelAddress.text = context.getString(R.string.reg_card_hotel_address, model.hotelAddress)
        binding.arrivalDate.text = context.getString(R.string.reg_card_arrival_date, model.arrivalDate)
        binding.departureDate.text = context.getString(R.string.reg_card_departure_date, model.departureDate)
        binding.leadGuestFirstName.text = context.getString(R.string.reg_card_first_name, model.leadGuest.firstName)
        binding.leadGuestLastName.text = context.getString(R.string.reg_card_last_name, model.leadGuest.lastName)
        binding.leadGuestHomeAddress.text = context.getString(R.string.reg_card_home_address, model.leadGuest.homeAddress)
        binding.leadGuestPostcode.text = context.getString(R.string.reg_card_postcode, model.leadGuest.postcode)
        binding.leadGuestCity.text = context.getString(R.string.reg_card_city, model.leadGuest.city)
        binding.leadGuestCountry.text = context.getString(R.string.reg_card_country, model.leadGuest.country)
        binding.leadGuestDateOfBirth.text = context.getString(R.string.reg_card_date_of_birth, model.leadGuest.dateOfBirth)
        binding.leadGuestNationality.text = context.getString(R.string.reg_card_nationality, model.leadGuest.nationality)
        binding.leadGuestPassportNumber.text = context.getString(R.string.reg_card_passport_number, model.leadGuest.passportNumber)
        binding.additionalGuestsSeparator.visibility = if (model.additionalGuests.isNotEmpty()) View.VISIBLE else View.GONE
        binding.additionalGuestsHeader.visibility = if (model.additionalGuests.isNotEmpty()) View.VISIBLE else View.GONE

        val guestsListView = binding.additionalGuestsList

        model.additionalGuests.forEachIndexed { index, additionalGuest ->
            val additionalGuestBinding = ViewRegCardAdditionalGuestItemBinding.inflate(LayoutInflater.from(context), null, false)
            bindRegCardAdditionalGuest(additionalGuestBinding, additionalGuest, index + 1)
            guestsListView.addView(additionalGuestBinding.root)
        }
    }

    private fun bindRegCardAdditionalGuest(
            binding: ViewRegCardAdditionalGuestItemBinding,
            additionalGuest: RegCardPdfAdditionalGuest,
            guestNumber: Int) {
        binding.guestTitle.text = context.getString(R.string.reg_card_additional_guest_title, guestNumber)
        binding.guestFirstName.text = context.getString(R.string.reg_card_first_name, additionalGuest.firstName)
        binding.guestLastName.text = context.getString(R.string.reg_card_last_name, additionalGuest.lastName)
        binding.guestDateOfBirth.text = context.getString(R.string.reg_card_date_of_birth, additionalGuest.dateOfBirth)
        binding.guestNationality.text = context.getString(R.string.reg_card_nationality, additionalGuest.nationality)
    }

    private fun generateFileName(reservationId: String, profileId: String) =
        REG_CARD_PDF_NAME_TEMPLATE.format(reservationId, (FILE_ID_MIN..FILE_ID_MAX).random().toString(), profileId)
}
