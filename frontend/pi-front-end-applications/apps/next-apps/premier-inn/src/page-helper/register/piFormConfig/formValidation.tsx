import { validateRegisterFormParams } from '@whitbread-eos/api';
import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

import checkIsUnique from '../common/uniqueValidation';

export default function validateForm(params: validateRegisterFormParams): any {
  checkIsUnique();

  const { t, isCompanyNameAdvanceEnabled } = params;

  const COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS = isCompanyNameAdvanceEnabled
    ? FORM_VALIDATIONS.COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS
    : FORM_VALIDATIONS.COMPANY_NAME;

  const formValidationObject = {
    title: yup.string().required(t('config.errorMessages.yourDetails.title.required')),
    firstName: yup
      .string()
      .required(t('config.errorMessages.yourDetails.firstName.required'))
      .trim(t('config.errorMessages.yourDetails.firstName.required'))
      .matches(
        FORM_VALIDATIONS.FIRST_NAME.MATCHES,
        t('config.errorMessages.yourDetails.firstName.invalid')
      )
      .min(FORM_VALIDATIONS.FIRST_NAME.MIN, t('config.errorMessages.yourDetails.firstName.min'))
      .max(FORM_VALIDATIONS.FIRST_NAME.MAX, t('config.errorMessages.yourDetails.firstName.max')),
    lastName: yup
      .string()
      .required(t('config.errorMessages.yourDetails.lastName.required'))
      .trim(t('config.errorMessages.yourDetails.lastName.required'))
      .matches(
        FORM_VALIDATIONS.LAST_NAME.MATCHES,
        t('config.errorMessages.yourDetails.lastName.invalid')
      )
      .min(FORM_VALIDATIONS.LAST_NAME.MIN, t('config.errorMessages.yourDetails.lastName.min'))
      .max(FORM_VALIDATIONS.LAST_NAME.MAX, t('config.errorMessages.yourDetails.lastName.max')),
    password: yup
      .string()
      .required(t('config.errorMessages.myAccount.password.required'))
      .trim(t('config.errorMessages.myAccount.password.required'))
      .matches(
        FORM_VALIDATIONS.PASSWORD.MATCHES,
        t('config.errorMessages.myAccount.password.auth0Invalid')
      )
      .min(FORM_VALIDATIONS.PASSWORD.MIN, t('config.errorMessages.yourDetails.password.min'))
      .max(FORM_VALIDATIONS.PASSWORD.MAX, t('config.errorMessages.yourDetails.password.exceeded')),
    confirmPassword: yup
      .string()
      .required(t('config.errorMessages.myAccount.password.required'))
      .trim(t('config.errorMessages.myAccount.password.required'))
      .matches(
        FORM_VALIDATIONS.PASSWORD.MATCHES,
        t('config.errorMessages.yourDetails.password.auth0Invalid')
      )
      .min(FORM_VALIDATIONS.PASSWORD.MIN, t('config.errorMessages.yourDetails.password.min'))
      .max(FORM_VALIDATIONS.PASSWORD.MAX, t('config.errorMessages.yourDetails.password.exceeded'))
      .oneOf([yup.ref('password')], t('config.errorMessages.yourDetails.confirmPassword.Invalid')),
    email: yup
      .string()
      .required(t('config.errorMessages.yourDetails.email.required'))
      .matches(
        FORM_VALIDATIONS.EMAIL_GD.MATCHES,
        t('config.errorMessages.yourDetails.email.invalid')
      ),
    phone: yup
      .string()
      .required(t('config.errorMessages.yourDetails.mobile.required'))
      .matches(FORM_VALIDATIONS.PHONE.MATCHES, {
        message: t('config.errorMessages.yourDetails.mobile.invalid'),
        excludeEmptyString: true,
      })
      .min(FORM_VALIDATIONS.PHONE.MIN, t('config.errorMessages.yourDetails.mobile.invalid'))
      .max(FORM_VALIDATIONS.PHONE.MAX, t('config.errorMessages.yourDetails.mobile.invalid')),
    addressSelection: yup.string().required(t('config.errorMessages.yourDetails.address.type')),
    addressLine1: yup
      .string()
      .required(t('config.errorMessages.yourDetails.address1.required'))
      .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
        message: t('config.errorMessages.yourDetails.address1.invalid'),
      })
      .max(FORM_VALIDATIONS.ADDRESS.MAX, t('config.errorMessages.yourDetails.address1.max')),
    addressLine2: yup
      .string()
      .nullable()
      .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
        message: t('config.errorMessages.yourDetails.address2.invalid'),
      })
      .max(FORM_VALIDATIONS.ADDRESS.MAX, t('config.errorMessages.yourDetails.address2.max'))
      .nullable(),
    addressLine3: yup
      .string()
      .nullable()
      .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
        message: t('config.errorMessages.yourDetails.address3.invalid'),
      })
      .max(FORM_VALIDATIONS.ADDRESS.MAX, t('config.errorMessages.yourDetails.address3.max'))
      .nullable(),
    addressLine4: yup
      .string()
      .nullable()
      .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
        message: t('config.errorMessages.yourDetails.address4.invalid'),
      })
      .max(FORM_VALIDATIONS.ADDRESS.MAX, t('config.errorMessages.yourDetails.address4.max')),
    postalCode: yup
      .string()
      .transform((value, originalValue) => {
        return originalValue === '' ? undefined : value;
      })
      .when('countryCode', {
        is: (countryCode: string) => countryCode === 'GB',
        then: (schema) => {
          return schema
            .required(t('config.errorMessages.yourDetails.postcode.required'))
            .matches(
              FORM_VALIDATIONS.POSTAL_CODE.MATCHES_UK,
              t('config.errorMessages.yourDetails.postcode.invalid')
            );
        },
      })
      .when('countryCode', {
        is: (countryCode: string) => countryCode === 'DE',
        then: (schema) => {
          return schema
            .required(t('config.errorMessages.yourDetails.postcode.required'))
            .matches(
              FORM_VALIDATIONS.POSTAL_CODE.MATCHES_DE,
              t('config.errorMessages.yourDetails.postcode.invalid')
            );
        },
        otherwise: (schema) => {
          return schema
            .required(t('config.errorMessages.yourDetails.postcode.required'))
            .max(
              FORM_VALIDATIONS.POSTAL_CODE.MAX_OTHERS,
              t('config.errorMessages.yourDetails.postcode.max')
            );
        },
      }),
    cityName: yup.string().when('countryCode', {
      is: (countryCode: string) => countryCode === 'DE',
      then: (schema) => {
        return schema
          .required(t('config.errorMessages.yourDetails.city.required'))
          .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
            message: t('config.errorMessages.yourDetails.city.required'),
          })
          .max(FORM_VALIDATIONS.CITY.MAX, t('config.errorMessages.yourDetails.city35max.required'));
      },
    }),
    acceptTermsConditions: yup
      .mixed()
      .default(false)
      .oneOf([true], t('account.register.error.termsConditions.empty')),
    companyName: yup.string().when('addressSelection', {
      is: 'BUSINESS',
      then: (schema) => {
        return schema
          .required(t('config.errorMessages.yourDetails.companyName.required'))
          .matches(COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS.MATCHES, {
            message: t('config.errorMessages.yourDetails.companyName.invalid'),
          })
          .max(
            COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS.MAX,
            t('config.errorMessages.yourDetails.companyName.max')
          );
      },
      otherwise: () => {
        return yup.string().nullable();
      },
    }),
  };

  const formValidationSchema = yup.object().shape(formValidationObject, [['phone', 'landline']]);

  return { formValidationObject, formValidationSchema };
}
