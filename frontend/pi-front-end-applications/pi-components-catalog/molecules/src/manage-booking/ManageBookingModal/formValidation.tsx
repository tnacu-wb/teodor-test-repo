import type { HeaderInformationData } from '@whitbread-eos/api';
import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

export default function validateForm(labels: HeaderInformationData): any {
  const invalidReferenceLabel = labels?.headerInformation?.form?.invalidReference;
  const invalidSurnameLabel = labels?.headerInformation?.form?.invalidSurname;
  const formValidationObject = {
    bookingReference: yup
      .string()
      .required(invalidReferenceLabel)
      .matches(FORM_VALIDATIONS.MANAGE_BOOKING_BOOKING_REFERENCE.MATCHES, invalidReferenceLabel)
      .min(FORM_VALIDATIONS.MANAGE_BOOKING_BOOKING_REFERENCE.MIN, invalidReferenceLabel),
    bookingSurname: yup
      .string()
      .transform((value) => value.replace(/^(\s+|\s+$)/gm, ''))
      .required(invalidSurnameLabel)
      .matches(FORM_VALIDATIONS.MANAGE_BOOKING_MODAL_SURNAME.MATCHES, invalidSurnameLabel)
      .min(FORM_VALIDATIONS.MANAGE_BOOKING_MODAL_SURNAME.MIN, invalidSurnameLabel)
      .max(FORM_VALIDATIONS.MANAGE_BOOKING_MODAL_SURNAME.MAX, invalidSurnameLabel),
  };
  const formValidationSchema = yup.object().shape(formValidationObject);

  return { formValidationObject, formValidationSchema };
}
