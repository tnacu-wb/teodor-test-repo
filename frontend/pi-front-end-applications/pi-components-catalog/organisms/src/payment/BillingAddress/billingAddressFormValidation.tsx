import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

interface BillingAddressFormValidationArgsType {
  t: (id: string) => string;
  currentLang: string | undefined;
  isCompanyNameAdvanceEnabled?: boolean;
}

export default function billingAddressFormValidation({
  t,
  currentLang,
  isCompanyNameAdvanceEnabled,
}: BillingAddressFormValidationArgsType): any {
  const postcodeValidator = (postcode: string | undefined, countryCode: string) => {
    if (!postcode) {
      return {
        isValid: false,
        errorMessage: t('config.errorMessages.yourDetails.postcode.required'),
      };
    }

    const regex =
      countryCode === 'GB'
        ? FORM_VALIDATIONS.POSTAL_CODE.MATCHES_UK
        : FORM_VALIDATIONS.POSTAL_CODE.MATCHES_DE;

    if (countryCode === 'GB' || countryCode === 'D') {
      if (!regex.test(postcode.trim().toUpperCase())) {
        return {
          isValid: false,
          errorMessage: t('config.errorMessages.yourDetails.postcode.invalid'),
        };
      }
      return { isValid: true };
    } else {
      if (postcode.length > 12) {
        return {
          isValid: false,
          errorMessage: t('config.errorMessages.yourDetails.postcode.max'),
        };
      }
      return { isValid: true };
    }
  };

  const COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS = isCompanyNameAdvanceEnabled
    ? FORM_VALIDATIONS.COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS
    : FORM_VALIDATIONS.COMPANY_NAME;

  const formValidationObject = {
    billingAddressSelection: yup
      .string()
      .required(t('config.errorMessages.yourDetails.address.type')),
    addressLine1: yup.string().when('billingAddressSelection', {
      is: 'DifferentAddress',
      then: (schema) => {
        return schema
          .required(t('config.errorMessages.yourDetails.address1.required'))
          .max(35, t('config.errorMessages.yourDetails.address1.max'));
      },
    }),
    addressLine2: yup.string().when('billingAddressSelection', {
      is: 'DifferentAddress',
      then: (schema) => {
        return schema.max(35, t('config.errorMessages.yourDetails.address2.max')).nullable();
      },
    }),
    addressLine3: yup.string().when('billingAddressSelection', {
      is: 'DifferentAddress',
      then: (schema) => {
        return schema.max(35, t('config.errorMessages.yourDetails.address3.max')).nullable();
      },
    }),
    addressLine4: yup.string().when('billingAddressSelection', {
      is: 'DifferentAddress',
      then: (schema) => {
        return schema.max(35, t('config.errorMessages.yourDetails.address4.max')).nullable();
      },
    }),
    postalCode: yup.string().when('billingAddressSelection', {
      is: 'DifferentAddress',
      then: (schema) => {
        return schema.when('countryCode', {
          is: (countryCode: string) => countryCode === 'GB' || countryCode === 'DE',
          then: (schema) => {
            return schema.test('postcode-validator', function (this, context) {
              const validation = postcodeValidator(context, this.parent.countryCode);
              if (!validation.isValid) {
                return this.createError({
                  path: this.path,
                  message: validation.errorMessage,
                });
              }
              return true;
            });
          },
          otherwise: (schema) => {
            return schema
              .required(t('config.errorMessages.yourDetails.postcode.required'))
              .max(
                FORM_VALIDATIONS.POSTAL_CODE.MAX_OTHERS,
                t('config.errorMessages.yourDetails.postcode.max')
              );
          },
        });
      },
    }),
    cityName: yup.string().when(['countryCode', 'billingAddressSelection'], {
      is: (countryCode: string, billingAddressSelection: string) =>
        (countryCode === 'DE' || currentLang !== 'en') &&
        billingAddressSelection === 'DifferentAddress',
      then: (schema) => {
        return schema
          .required(t('config.errorMessages.yourDetails.city.required'))
          .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
            message: t('config.errorMessages.yourDetails.city.required'),
          })
          .max(FORM_VALIDATIONS.CITY.MAX, t('config.errorMessages.yourDetails.city35max.required'));
      },
    }),
    companyName: yup.string().when('addressSelection', {
      is: 'BUSINESS',
      then: (schema) => {
        return schema
          .required(t('config.errorMessages.yourDetails.companyName.required'))
          .max(
            COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS.MAX,
            t('config.errorMessages.yourDetails.companyName.max')
          )
          .matches(
            COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS.MATCHES,
            t('config.errorMessages.yourDetails.companyName.invalid')
          );
      },
    }),
  };

  const formValidationSchema = yup.object().shape(formValidationObject);

  return formValidationSchema;
}
