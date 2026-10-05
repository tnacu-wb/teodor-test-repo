import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

export default function validateForm(labels: any): any {
  const {
    titleError,
    firstNameRequiredError,
    firstNameMinError,
    lastNameRequiredError,
    firstNameInvalidError,
    lastNameInvalidError,
    emailInvalidError,
  } = labels;
  const formValidationSchema = yup.object().shape({
    title: yup.string().required(titleError),
    firstName: yup
      .string()
      .transform((value) => value?.trim() ?? '')
      .required(firstNameRequiredError)
      .matches(FORM_VALIDATIONS.FIRST_NAME.MATCHES, firstNameInvalidError)
      .min(FORM_VALIDATIONS.FIRST_NAME.MIN, firstNameMinError)
      .max(FORM_VALIDATIONS.FIRST_NAME.MAX, firstNameRequiredError),
    lastName: yup
      .string()
      .transform((value) => value?.trim() ?? '')
      .required(lastNameRequiredError)
      .matches(FORM_VALIDATIONS.LAST_NAME_AMEND_BOOKING_GUEST_DETAILS.MATCHES, lastNameInvalidError)
      .min(FORM_VALIDATIONS.LAST_NAME_AMEND_BOOKING_GUEST_DETAILS.MIN, lastNameInvalidError)
      .max(FORM_VALIDATIONS.LAST_NAME_AMEND_BOOKING_GUEST_DETAILS.MAX, lastNameRequiredError),
    emailAddress: yup
      .string()
      .nullable()
      .transform((v, o) => (o === '' ? null : (v?.trim() ?? '')))
      .matches(FORM_VALIDATIONS.EMAIL_GD.MATCHES, emailInvalidError),
  });
  return formValidationSchema;
}
