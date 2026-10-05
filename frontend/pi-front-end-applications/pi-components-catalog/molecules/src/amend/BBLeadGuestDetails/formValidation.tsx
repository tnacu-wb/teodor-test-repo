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
  const formValidationObject = {
    bbGuestDetails: yup.array().of(
      yup.object().shape({
        title: yup.string().required(titleError),
        firstName: yup
          .string()
          .transform((value) => value.replace(/^(\s+|\s+$)/gm, ''))
          .required(firstNameRequiredError)
          .matches(FORM_VALIDATIONS.FIRST_NAME.MATCHES, firstNameInvalidError)
          .min(FORM_VALIDATIONS.FIRST_NAME.MIN, firstNameMinError)
          .max(FORM_VALIDATIONS.FIRST_NAME.MAX, firstNameRequiredError),
        lastName: yup
          .string()
          .transform((value) => value.replace(/^(\s+|\s+$)/gm, ''))
          .required(lastNameRequiredError)
          .matches(FORM_VALIDATIONS.LAST_NAME_BB_GUEST_DETAILS.MATCHES, lastNameInvalidError)
          .min(FORM_VALIDATIONS.LAST_NAME_BB_GUEST_DETAILS.MIN, lastNameInvalidError)
          .max(FORM_VALIDATIONS.LAST_NAME_BB_GUEST_DETAILS.MAX, lastNameRequiredError),
        emailAddress: yup
          .string()
          .transform((value) => value.replace(/^(\s+|\s+$)/gm, ''))
          .matches(FORM_VALIDATIONS.BB_EMAIL_GD.MATCHES, emailInvalidError),
      })
    ),
  };
  const formValidationSchema = yup.object().shape(formValidationObject);

  return { formValidationObject, formValidationSchema };
}
