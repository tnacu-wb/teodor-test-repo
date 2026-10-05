import { validateBookingFormParams } from '@whitbread-eos/api';
import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

export default function validateBookingsForm(params: validateBookingFormParams): any {
  const { t, enhancedSearch = false } = params;

  const formValidationObject = {
    bookingReference: yup.string().when('bookingReference', {
      is: (bookingReference: string) => bookingReference !== '',
      then: yup
        .string()
        .matches(
          FORM_VALIDATIONS.BOOKING_REFERENCE_CCUI.MATCHES,
          enhancedSearch
            ? t('ccui.manageBooking.reference.invalid')
            : t('ccui.manageBooking.bookingReference.invalid')
        )
        .min(
          FORM_VALIDATIONS.BOOKING_REFERENCE_CCUI.MIN,
          enhancedSearch
            ? t('ccui.manageBooking.reference.min')
            : t('ccui.manageBooking.bookingReference.invalid')
        )
        .max(
          FORM_VALIDATIONS.BOOKING_REFERENCE_CCUI.MAX,
          enhancedSearch
            ? t('ccui.manageBooking.reference.max')
            : t('ccui.manageBooking.bookingReference.invalid')
        ),
      otherwise: yup.string().notRequired(),
    }),
    bookerLastName: yup.string().when('bookerLastName', {
      is: (bookerLastName: string) => bookerLastName !== '',
      then: yup
        .string()
        .matches(
          FORM_VALIDATIONS.BOOKING_SURNAME.MATCHES,
          t('ccui.manageBooking.bookingSurname.invalid')
        )
        .min(FORM_VALIDATIONS.BOOKING_SURNAME.MIN, t('ccui.manageBooking.bookingSurname.min'))
        .max(FORM_VALIDATIONS.BOOKING_SURNAME.MAX, t('ccui.manageBooking.bookingSurname.max')),
      otherwise: yup.string().notRequired(),
    }),
    arrivalDate: yup.string().nullable().notRequired(),
    guestLastName: yup.string().when('guestLastName', {
      is: (guestLastName: string) => guestLastName !== '',
      then: yup
        .string()
        .matches(
          FORM_VALIDATIONS.GUEST_SURNAME.MATCHES,
          t('ccui.manageBooking.guestSurname.invalid')
        )
        .min(FORM_VALIDATIONS.GUEST_SURNAME.MIN, t('ccui.manageBooking.guestSurname.min'))
        .max(FORM_VALIDATIONS.GUEST_SURNAME.MAX, t('ccui.manageBooking.guestSurname.max')),
      otherwise: yup.string().notRequired(),
    }),
    cancellationDate: yup.string().nullable().notRequired(),
    companyName: yup.string().when('companyName', {
      is: (companyName: string) => companyName !== '',
      then: yup
        .string()
        .matches(
          FORM_VALIDATIONS.CCUI_COMPANY_NAME.MATCHES,
          t('ccui.manageBooking.companyName.invalid')
        )
        .min(FORM_VALIDATIONS.CCUI_COMPANY_NAME.MIN, t('ccui.manageBooking.companyName.min'))
        .max(FORM_VALIDATIONS.CCUI_COMPANY_NAME.MAX, t('ccui.manageBooking.companyName.max')),
      otherwise: yup.string().notRequired(),
    }),
    bookerPostcode: yup.string().when('bookerPostcode', {
      is: (bookerPostcode: string) => bookerPostcode !== '',
      then: yup
        .string()
        .matches(
          FORM_VALIDATIONS.CCUI_POSTAL_CODE.MATCHES,
          t('ccui.manageBooking.bookerPostcode.invalid')
        )
        .min(FORM_VALIDATIONS.CCUI_POSTAL_CODE.MIN, t('ccui.manageBooking.bookerPostcode.min'))
        .max(FORM_VALIDATIONS.CCUI_POSTAL_CODE.MAX, t('ccui.manageBooking.bookerPostcode.max')),
      otherwise: yup.string().notRequired(),
    }),
    bookerEmail: yup.string().when('bookerEmail', {
      is: (bookerEmail: string) => bookerEmail !== '',
      then: yup
        .string()
        .matches(FORM_VALIDATIONS.CCUI_EMAIL.MATCHES, t('ccui.manageBooking.bookerEmail.invalid'))
        .max(FORM_VALIDATIONS.CCUI_EMAIL.MAX, t('ccui.manageBooking.bookerEmail.max')),
      otherwise: yup.string().notRequired(),
    }),
    bookerPhone: yup.string().when('bookerPhone', {
      is: (bookerPhone: string) => bookerPhone !== '',
      then: yup
        .string()
        .matches(FORM_VALIDATIONS.CCUI_PHONE.MATCHES, t('ccui.manageBooking.bookerPhone.invalid'))
        .min(FORM_VALIDATIONS.CCUI_PHONE.MIN, t('ccui.manageBooking.bookerPhone.min'))
        .max(FORM_VALIDATIONS.CCUI_PHONE.MAX, t('ccui.manageBooking.bookerPhone.max')),
      otherwise: yup.string().notRequired(),
    }),
    hotelDetails: yup.object().notRequired(),
    hotelLocation: yup.string().notRequired(),
    thirdPartyBookingReferenceNumber: yup.string().when('thirdPartyBookingReferenceNumber', {
      is: (thirdPartyBookingReferenceNumber: string) => thirdPartyBookingReferenceNumber !== '',
      then: yup
        .string()
        .matches(
          FORM_VALIDATIONS.CCUI_THIRD_PARTY_REF.MATCHES,
          t('ccui.manageBooking.thirdPartyBookingReferenceNumber.invalid')
        )
        .min(
          FORM_VALIDATIONS.CCUI_THIRD_PARTY_REF.MIN,
          t('ccui.manageBooking.thirdPartyBookingReferenceNumber.min')
        )
        .max(
          FORM_VALIDATIONS.CCUI_THIRD_PARTY_REF.MAX,
          t('ccui.manageBooking.thirdPartyBookingReferenceNumber.max')
        ),
      otherwise: yup.string().notRequired(),
    }),
  };

  const formValidationSchema = yup.object().shape(formValidationObject, [
    ['bookingReference', 'bookingReference'],
    ['bookerLastName', 'bookerLastName'],
    ['guestLastName', 'guestLastName'],
    ['companyName', 'companyName'],
    ['bookerPostcode', 'bookerPostcode'],
    ['bookerEmail', 'bookerEmail'],
    ['bookerPhone', 'bookerPhone'],
    ['thirdPartyBookingReferenceNumber', 'thirdPartyBookingReferenceNumber'],
  ]);

  return { formValidationObject, formValidationSchema };
}
