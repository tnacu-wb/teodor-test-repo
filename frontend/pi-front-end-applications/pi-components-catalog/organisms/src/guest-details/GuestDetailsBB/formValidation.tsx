import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

import checkIsUnique from '../../utils/common/uniqueValidation';

export default function validateForm(labels: any): any {
  checkIsUnique();

  const {
    titleError,
    firstNameRequiredError,
    firstNameMinError,
    lastNameRequiredError,
    firstNameInvalidError,
    lastNameInvalidError,
    emailInvalidError,
    checkUniqueError,
  } = labels;
  const formValidationObject = {
    bbGuestDetails: yup
      .array()
      .of(
        yup.object().shape({
          title: yup.string().required(titleError),
          firstName: yup
            .string()
            .transform((value) => value.trim())
            .required(firstNameRequiredError)
            .matches(FORM_VALIDATIONS.FIRST_NAME.MATCHES, firstNameInvalidError)
            .min(FORM_VALIDATIONS.FIRST_NAME.MIN, firstNameMinError)
            .max(FORM_VALIDATIONS.FIRST_NAME.MAX, firstNameRequiredError),
          lastName: yup
            .string()
            .transform((value) => value.trim())
            .required(lastNameRequiredError)
            .matches(FORM_VALIDATIONS.LAST_NAME_BB_GUEST_DETAILS.MATCHES, lastNameInvalidError)
            .min(FORM_VALIDATIONS.LAST_NAME_BB_GUEST_DETAILS.MIN, lastNameInvalidError)
            .max(FORM_VALIDATIONS.LAST_NAME_BB_GUEST_DETAILS.MAX, lastNameRequiredError),
          emailAddress: yup
            .string()
            .transform((value) => value.trim())
            .matches(FORM_VALIDATIONS.BB_EMAIL_GD.MATCHES, emailInvalidError),
        })
      )
      .unique('lastName', checkUniqueError),
    bbAccompanyingGuestDetails: yup
      .array()
      .of(
        yup.object().shape({
          title: yup.string().optional(),
          firstName: yup
            .string()
            .nullable()
            .notRequired()
            .transform((value) => value.trim())
            .test('isEmptyOrValid', firstNameMinError, (value: any) => {
              return !value || value?.length >= FORM_VALIDATIONS.FIRST_NAME.MIN;
            })
            .max(FORM_VALIDATIONS.FIRST_NAME.MAX, firstNameRequiredError),
          lastName: yup
            .string()
            .nullable()
            .notRequired()
            .transform((value) => value.trim())
            .test('isEmptyOrValid', lastNameInvalidError, (value: any) => {
              return !value || value?.length >= FORM_VALIDATIONS.LAST_NAME_BB_GUEST_DETAILS.MIN;
            })
            .max(FORM_VALIDATIONS.LAST_NAME_BB_GUEST_DETAILS.MAX, lastNameRequiredError),
          emailAddress: yup
            .string()
            .optional()
            .transform((value) => value.trim())
            .matches(FORM_VALIDATIONS.BB_EMAIL_GD.MATCHES, emailInvalidError),
        })
      )
      .optional()
      .unique('lastName', checkUniqueError),
  };
  const formValidationSchema = yup.object().shape(formValidationObject);

  return { formValidationObject, formValidationSchema, checkIsUnique };
}
