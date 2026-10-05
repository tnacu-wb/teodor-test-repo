import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

const VALIDATIONS = {
  COMPANY_NAME: {
    MATCHES: RegExp(/^[a-zA-Z0-9\s.,&@#()-À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+$/g),
    MAX: 50,
  },
  ADDRESS: {
    MATCHES: RegExp(/^[a-zA-Z0-9\s.,&@#()-À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+$/g),
    MAX: 200,
  },
  POSTAL_CODE: {
    MATCHES: RegExp(/^[a-zA-Z0-9 ]*$/i),
    MAX: 7,
  },
};

export default function validateSearchAccountForm(t: (id: string) => string): any {
  const formValidationObject = {
    firstName: yup
      .string()
      .notRequired()
      .nullable()
      .transform((value) => (value === '' ? null : value))
      .matches(
        FORM_VALIDATIONS.FIRST_NAME.MATCHES,
        'Please enter a valid first name (max 20 characters)'
      )
      .max(FORM_VALIDATIONS.FIRST_NAME.MAX, 'Please enter a valid first name (max 20 characters)'),
    lastName: yup
      .string()
      .notRequired()
      .nullable()
      .transform((value) => (value === '' ? null : value))
      .matches(
        FORM_VALIDATIONS.LAST_NAME.MATCHES,
        'Please enter a valid surname (max 30 characters)'
      )
      .max(FORM_VALIDATIONS.LAST_NAME.MAX, 'Please enter a valid surname (max 30 characters)'),
    companyName: yup
      .string()
      .notRequired()
      .nullable()
      .transform((value) => (value === '' ? null : value))
      .matches(VALIDATIONS.COMPANY_NAME.MATCHES, 'Please enter a valid company (max 50 characters)')
      .max(VALIDATIONS.COMPANY_NAME.MAX, 'Please enter a valid company (max 50 characters)'),
    email: yup
      .string()
      .notRequired()
      .nullable()
      .transform((value) => (value === '' ? null : value))
      .matches(FORM_VALIDATIONS.EMAIL.MATCHES, 'Please enter a valid e-mail address')
      .max(FORM_VALIDATIONS.EMAIL.MAX, 'Please enter a valid e-mail address'),
    address: yup
      .string()
      .notRequired()
      .nullable()
      .transform((value) => (value === '' ? null : value))
      .matches(VALIDATIONS.ADDRESS.MATCHES, 'Please enter a valid address')
      .max(VALIDATIONS.ADDRESS.MAX, 'Please enter a valid address'),
    postalCode: yup
      .string()
      .notRequired()
      .nullable()
      .transform((value) => (value === '' ? null : value))
      .matches(
        VALIDATIONS.POSTAL_CODE.MATCHES,
        t('config.errorMessages.yourDetails.postcode.invalid')
      )
      .max(VALIDATIONS.POSTAL_CODE.MAX, t('config.errorMessages.yourDetails.postcode.invalid')),
    mobileNumber: yup
      .string()
      .notRequired()
      .matches(FORM_VALIDATIONS.PHONE.MATCHES, {
        message: t('config.errorMessages.yourDetails.mobile.invalid'),
        excludeEmptyString: true,
      })
      .min(FORM_VALIDATIONS.PHONE.MIN, t('config.errorMessages.yourDetails.mobile.invalid'))
      .max(FORM_VALIDATIONS.PHONE.MAX, t('config.errorMessages.yourDetails.mobile.invalid'))
      .nullable()
      .transform((value) => value || null),
    landlineNumber: yup
      .string()
      .notRequired()
      .matches(FORM_VALIDATIONS.PHONE.MATCHES, {
        message: t('config.errorMessages.yourDetails.telephone.invalid'),
        excludeEmptyString: true,
      })
      .min(FORM_VALIDATIONS.PHONE.MIN, t('config.errorMessages.yourDetails.telephone.invalid'))
      .max(FORM_VALIDATIONS.PHONE.MAX, t('config.errorMessages.yourDetails.telephone.invalid'))
      .nullable()
      .transform((value) => value || null),
  };

  const formValidationSchema = yup.object().shape(formValidationObject, [['phone', 'landline']]);

  return { formValidationObject, formValidationSchema };
}
