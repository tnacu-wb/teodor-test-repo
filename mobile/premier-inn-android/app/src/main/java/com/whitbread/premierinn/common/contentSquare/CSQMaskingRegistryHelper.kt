package com.whitbread.premierinn.common.contentSquare

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import com.businessbooker.paymentmethods.BusinessBookerPaymentMethodsActivity
import com.contentsquare.android.Contentsquare
import com.whitbread.premierinn.R
import com.whitbread.premierinn.account.AccountActivity
import com.whitbread.premierinn.amend.AmendReservationActivity
import com.whitbread.premierinn.amend.amendAndPay.AmendAndPayActivity
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity
import com.whitbread.premierinn.amend.amendguestsrooms.AmendGuestsRoomsActivity
import com.whitbread.premierinn.changepassword.ChangePasswordActivity
import com.whitbread.premierinn.ciol.CheckOutConfirmationActivity
import com.whitbread.premierinn.ciol.fragments.CheckInCompletionFragment
import com.whitbread.premierinn.ciol.fragments.PayAndCheckInFragment
import com.whitbread.premierinn.ciol.fragments.PreStayEditFragment
import com.whitbread.premierinn.ciol.fragments.PreStayFragment
import com.whitbread.premierinn.ciol.fragments.RegCardGuestDetailsFragment
import com.whitbread.premierinn.ciol.fragments.RoomSelectionFragment
import com.whitbread.premierinn.createaccount.CreateAccountActivity
import com.whitbread.premierinn.editguest.EditGuestActivity
import com.whitbread.premierinn.editpaymentmethods.EditPaymentMethodsActivity
import com.whitbread.premierinn.findbooking.FindBookingActivity
import com.whitbread.premierinn.guestdetails.GuestDetailsActivity
import com.whitbread.premierinn.login.LoginActivity
import com.whitbread.premierinn.mybookings.MyBookingsActivity
import com.whitbread.premierinn.newsletterpreferences.NewsletterPreferencesActivity
import com.whitbread.premierinn.paymentmethods.PaymentMethodsActivity
import com.whitbread.premierinn.personaldetails.PersonalDetailsActivity
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity
import com.whitbread.premierinn.qrkiosk.QRCodeActivity
import com.whitbread.premierinn.resetpassword.ResetPasswordActivity
import com.whitbread.premierinn.reviewbooking.ReviewBookActivity

sealed class Host {
    data class ActivityHost(val activityClass: Class<out Activity>) : Host()
    data class FragmentHost(val fragmentClass: Class<out Fragment>) : Host()
}

class CSQMaskingRegistryHelper {

    private val registry: Map<Host, IntArray> = mapOf(
        // Activities
        Host.ActivityHost(LoginActivity::class.java) to intArrayOf(
            R.id.account_login_email_input,
            R.id.account_login_password_input
        ),
        Host.ActivityHost(GuestDetailsActivity::class.java) to intArrayOf(
            R.id.guest_details_first_name_input,
            R.id.guest_details_last_name_input,
            R.id.guest_details_contact_number_input,
            R.id.guest_details_email_input,
            R.id.s_address_form_countries,
            R.id.et_address_form_postcode,
            R.id.et_address_form_company,
            R.id.et_address_form_address_line1,
            R.id.et_address_form_address_line2,
            R.id.et_address_form_address_line3,
            R.id.passwordEditText
        ),
        Host.ActivityHost(PostcodeFinderActivity::class.java) to intArrayOf(
            R.id.et_postcode_finder_search_input,
        ),
        Host.ActivityHost(ReviewBookActivity::class.java) to intArrayOf(
            R.id.review_booking_guest_details_name_header,
            R.id.review_booking_guest_details_email_header,
            R.id.s_address_form_countries,
            R.id.et_address_form_postcode,
            R.id.et_address_form_company,
            R.id.tv_address_form_manual_address,
            R.id.et_address_form_address_line1,
            R.id.et_address_form_address_line2,
            R.id.et_address_form_address_line3,
            R.id.tv_payment_details_same_address_label,
            R.id.tv_lead_guest_name,
            R.id.tv_lead_guest_email,
        ),
        Host.ActivityHost(EditGuestActivity::class.java) to intArrayOf(
            R.id.guest_details_first_name_input,
            R.id.guest_details_last_name_input,
            R.id.guest_details_contact_number_input,
            R.id.guest_details_email_input
        ),
        Host.ActivityHost(MyBookingsActivity::class.java) to intArrayOf(
            R.id.my_bookings_lead_guest_name
        ),
        Host.ActivityHost(FindBookingActivity::class.java) to intArrayOf(
            R.id.et_find_booking_last_name
        ),
        Host.ActivityHost(AccountActivity::class.java) to intArrayOf(
            R.id.tv_my_account_name,
            R.id.tv_my_account_email
        ),
        Host.ActivityHost(NewsletterPreferencesActivity::class.java) to intArrayOf(
            R.id.newsletter_prefs_email
        ),
        Host.ActivityHost(ResetPasswordActivity::class.java) to intArrayOf(
            R.id.et_reset_password_email,
        ),
        Host.ActivityHost(PaymentMethodsActivity::class.java) to intArrayOf(
            R.id.payment_method_card_number,
            R.id.payment_method_card_holder_name,
            R.id.payment_method_card_expiry,
        ),
        Host.ActivityHost(EditPaymentMethodsActivity::class.java) to intArrayOf(
            R.id.til_card_details_form_card_number,
            R.id.til_card_details_form_name_on_card,
            R.id.til_card_details_form_start_date,
            R.id.til_card_details_form_expiry_date,
            R.id.til_card_details_form_issue_number,
            R.id.til_card_details_form_cvv_number,
            R.id.s_address_form_countries,
            R.id.et_address_form_postcode,
            R.id.et_address_form_company,
            R.id.et_address_form_address_line1,
            R.id.et_address_form_address_line2,
            R.id.et_address_form_address_line3,
            R.id.add_new_card_memorable_word_input,
            R.id.add_new_card_memorable_word_input
        ),
        Host.ActivityHost(CreateAccountActivity::class.java) to intArrayOf(
            R.id.account_first_name_input,
            R.id.account_last_name_input,
            R.id.account_contact_number_input,
            R.id.account_email_input,
            R.id.passwordEt,
            R.id.address_form_countries_spinner,
            R.id.country_item_name,
            R.id.country_flag_character,
            R.id.address_form_lookup_postcode_input,
            R.id.address_form_company_input,
            R.id.address_form_line1_input,
            R.id.address_form_line2_input,
            R.id.address_form_town_city_input,
            R.id.address_form_postcode_input
        ),
        Host.ActivityHost(PersonalDetailsActivity::class.java) to intArrayOf(
            R.id.account_first_name_input,
            R.id.account_last_name_input,
            R.id.account_contact_number_input,
            R.id.account_email_input,
            R.id.personal_details_passport_number,
            R.id.personal_details_nationality_dropdown,
            R.id.address_form_countries_spinner,
            R.id.country_item_name,
            R.id.country_flag_character,
            R.id.address_form_lookup_postcode_input,
            R.id.address_form_company_input,
            R.id.address_form_line1_input,
            R.id.address_form_line2_input,
            R.id.address_form_town_city_input,
            R.id.address_form_postcode_input,
            R.id.personal_details_car_registration_input
        ),
        Host.ActivityHost(ChangePasswordActivity::class.java) to intArrayOf(
            R.id.change_password_new_password_input,
            R.id.change_password_confirm_password_input,
        ),
        Host.ActivityHost(AmendGuestsRoomsActivity::class.java) to intArrayOf(
            R.id.first_name_input,
            R.id.last_name_input
        ),
        Host.ActivityHost(AmendAddRoomActivity::class.java) to intArrayOf(
            R.id.first_name_input,
            R.id.last_name_input
        ),
        Host.ActivityHost(AmendReservationActivity::class.java) to intArrayOf(
            R.id.amend_room_lead_guest_title
        ),

        Host.ActivityHost(BusinessBookerPaymentMethodsActivity::class.java) to intArrayOf(
            R.id.paymentCardType,
            R.id.paymentCardNumber,
            R.id.paymentCardName,
            R.id.paymentCardExpiryDate
        ),
        Host.ActivityHost(AmendAndPayActivity::class.java) to intArrayOf(
            R.id.cardTypeLabel,
            R.id.cardHolderLabel,
            R.id.paymentCardExpiryLabel,
            R.id.amend_and_pay_payment_billing_address_details
        ),
        Host.ActivityHost(QRCodeActivity::class.java) to intArrayOf(
            R.id.iv_qr_code
        ),
        Host.ActivityHost(CheckOutConfirmationActivity::class.java) to intArrayOf(
            R.id.checkedOutTitle
        ),

        // Fragments
        Host.FragmentHost(CheckInCompletionFragment::class.java) to intArrayOf(
            R.id.checkedInHeadline
        ),
        Host.FragmentHost(CheckInCompletionFragment::class.java) to intArrayOf(
            R.id.firstNameTextInput,
            R.id.lastNameTextInput,
            R.id.addressLine1TextInput,
            R.id.addressLine2TextInput,
            R.id.addressLine3TextInput,
            R.id.dateOfBirthLayout,
            R.id.postcodeTextInput,
            R.id.cityTextInput,
            R.id.countryTextInput,
            R.id.country_item_name,
            R.id.country_flag_character,
            R.id.nationalityTextInput,
            R.id.passportNumberTextInput,
        ),
        Host.FragmentHost(PayAndCheckInFragment::class.java) to intArrayOf(
            R.id.billingAddressCode,
            R.id.s_address_form_countries,
            R.id.et_address_form_postcode,
            R.id.et_address_form_company,
            R.id.et_address_form_address_line1,
            R.id.et_address_form_address_line2,
            R.id.et_address_form_address_line3
        ),
        Host.FragmentHost(PreStayEditFragment::class.java) to intArrayOf(
            R.id.firstNameView,
            R.id.lastNameView,
            R.id.nationalityEditText,
            R.id.country_item_name,
            R.id.country_flag_character,
            R.id.country_item_name,
            R.id.country_flag_character,
            R.id.identificationTypeSpinner,
            R.id.idNumberEditText
        ),
        Host.FragmentHost(PreStayFragment::class.java) to intArrayOf(
            R.id.leadBookerContainer,
            R.id.emailContainer,
            R.id.phoneContainer,
            R.id.addressContainer,
            R.id.guestName,
            R.id.guestNameSecond
        ),
        Host.FragmentHost(RegCardGuestDetailsFragment::class.java) to intArrayOf(
            R.id.regCardGuestNameLayout,
            R.id.regCardGuestHomeAddressLayout,
            R.id.regCardGuestDateOfBirthLayout,
            R.id.regCardGuestNationalityLayout,
            R.id.country_item_name,
            R.id.country_flag_character,
            R.id.country_item_name,
            R.id.country_flag_character,
            R.id.regCardGuestPassportNumberLayout
        ),
        Host.FragmentHost(RoomSelectionFragment::class.java) to intArrayOf(
            R.id.item_guest_name
        )
    )

    /** Masks all views registered for this Activity class. */
    fun maskRegisteredViews(activity: Activity) {
        // Lookup the IDs for this Activity's class
        val ids = registry[Host.ActivityHost(activity::class.java)] ?: return

        // Mask each one if it exists in the current layout
        ids.forEach { id ->
            activity.findViewById<View?>(id)?.let { view ->
                Contentsquare.mask(view)
            }
        }
    }

    /** Masks all views registered for this Fragment class. */
    fun maskRegisteredViews(fragment: Fragment) {
        // Lookup the IDs for this Fragments class
        val ids = registry[Host.FragmentHost(fragment::class.java)] ?: return
        val rootView = fragment.view ?: return

        // Mask each one if it exists in the current layout
        ids.forEach { id ->
            rootView.findViewById<View?>(id)?.let { view ->
                Contentsquare.mask(view)
            }
        }
    }
}