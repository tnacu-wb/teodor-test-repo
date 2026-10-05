import { validateGroupBookingParams } from '@whitbread-eos/api';
import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

export default function validateForm(params: validateGroupBookingParams): any {
  const { t, isSchoolYouthEnabled, hideCompanyName, hideReasonForVisit } = params;

  const formValidationObject = {
    title: yup.string().required(t('groupBooking.contactDetails.yourContact.nameTitles.required')),
    firstName: yup
      .string()
      .required(t('groupBooking.contactDetails.yourContact.firstName.required'))
      .trim(t('groupBooking.contactDetails.yourContact.firstName.required'))
      .matches(
        FORM_VALIDATIONS.FIRST_NAME.MATCHES,
        t('groupBooking.contactDetails.yourContact.firstName.invalid')
      )
      .min(
        FORM_VALIDATIONS.FIRST_NAME.MIN,
        t('groupBooking.contactDetails.yourContact.firstName.min')
      )
      .max(
        FORM_VALIDATIONS.FIRST_NAME.MAX,
        t('groupBooking.contactDetails.yourContact.firstName.required')
      ),
    lastName: yup
      .string()
      .required(t('groupBooking.contactDetails.yourContact.lastName.required'))
      .trim(t('groupBooking.contactDetails.yourContact.lastName.required'))
      .matches(
        FORM_VALIDATIONS.LAST_NAME.MATCHES,
        t('groupBooking.contactDetails.yourContact.lastName.invalid')
      )
      .min(
        FORM_VALIDATIONS.LAST_NAME.MIN,
        t('groupBooking.contactDetails.yourContact.lastName.min')
      )
      .max(
        FORM_VALIDATIONS.LAST_NAME.MAX,
        t('groupBooking.contactDetails.yourContact.lastName.required')
      ),
    phoneNumber: yup
      .string()
      .required(t('groupBooking.contactDetails.yourContact.phoneNumber.required'))
      .matches(FORM_VALIDATIONS.PHONE.MATCHES, {
        message: t('groupBooking.contactDetails.yourContact.phoneNumber.invalid'),
        excludeEmptyString: true,
      })
      .min(
        FORM_VALIDATIONS.PHONE.MIN,
        t('groupBooking.contactDetails.yourContact.phoneNumber.required')
      )
      .max(
        FORM_VALIDATIONS.PHONE.MAX,
        t('groupBooking.contactDetails.yourContact.phoneNumber.required')
      ),
    emailAddress: yup
      .string()
      .required(t('groupBooking.contactDetails.yourContact.emailAddress.required'))
      .matches(
        FORM_VALIDATIONS.EMAIL_GD.MATCHES,
        t('groupBooking.contactDetails.yourContact.emailAddress.required')
      ),
    BookerType: yup.string().required(t('groupBooking.bookingDetails.bookingType.required')),
    companyName: hideCompanyName
      ? yup.string().optional()
      : yup
          .string()
          .required(t('groupBooking.bookingDetails.companyName.required'))
          .matches(
            FORM_VALIDATIONS.CCUI_COMPANY_NAME.MATCHES,
            t('ccui.manageBooking.companyName.invalid')
          )
          .min(FORM_VALIDATIONS.CCUI_COMPANY_NAME.MIN, t('ccui.manageBooking.companyName.min'))
          .max(FORM_VALIDATIONS.CCUI_COMPANY_NAME.MAX, t('ccui.manageBooking.companyName.max')),
    purposeOfStay: yup.string().required(t('groupBooking.bookingDetails.purposeOfStay.required')),
    reasonForVisit: yup.string().required(t('groupBooking.bookingDetails.reasonForVisit.required')),
    reasonForVisitOther: !hideReasonForVisit
      ? yup.string().required(t('groupBooking.bookingDetails.reasonForVisitOther.required'))
      : yup.string().optional(),

    datepicker: yup
      .array()
      .compact() //removes null values from array
      .length(2, t('groupBooking.bookingDetails.bookingDetails.checkInCheckOut.required'))
      .required(t('groupBooking.bookingDetails.bookingDetails.checkInCheckOut.required')),
    hotels: yup
      .string()
      .required(t('groupBooking.bookingDetails.bookingDetails.hotelSelection.required')),

    comments: isSchoolYouthEnabled
      ? yup
          .string()
          .required(t('groupBooking.roomRequirements.additionalInfo.comments.required'))
          .min(10, t('groupBooking.roomRequirements.additionalInfo.comments.required'))
      : yup.string().optional(),
  };
  const formValidationSchema = yup
    .object()
    .shape(formValidationObject, [['companyName', 'reasonForVisitOther']]);

  return { formValidationObject, formValidationSchema };
}
