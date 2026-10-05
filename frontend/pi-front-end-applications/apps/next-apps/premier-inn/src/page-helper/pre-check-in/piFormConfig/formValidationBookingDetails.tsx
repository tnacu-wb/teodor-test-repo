import { validateRegisterFormParams } from '@whitbread-eos/api';
import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

export default function validateFormBookingDetails(params: validateRegisterFormParams): any {
  const { t } = params;
  const formValidationObject = {
    bookingNumber: yup
      .string()
      .required(t('precheckin.errors.empty.bookingnumber'))
      .trim(t('precheckin.errors.empty.bookingnumber'))
      .min(8, t('precheckin.errors.bookingnumber'))
      .matches(FORM_VALIDATIONS.BOOKING_REFERENCE.MATCHES, t('precheckin.errors.bookingnumber')),
    arrivalDate: yup
      .date()
      .required(t('precheckin.errors.empty.arrivaldate'))
      .typeError(t('precheckin.errors.empty.arrivaldate')),
    surname: yup
      .string()
      .required(t('precheckin.errors.empty.surname'))
      .trim(t('precheckin.errors.empty.surname'))
      .matches(FORM_VALIDATIONS.LAST_NAME.MATCHES, t('precheckin.errors.onlyletters'))
      .min(FORM_VALIDATIONS.LAST_NAME.MIN, t('precheckin.errors.surname.minlength'))
      .max(FORM_VALIDATIONS.LAST_NAME.MAX, t('precheckin.errors.surname.maxlength')),
  };
  const formValidationSchemaBookingDetails = yup.object().shape(formValidationObject);
  return { formValidationObject, formValidationSchemaBookingDetails };
}
