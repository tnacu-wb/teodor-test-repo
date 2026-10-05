import { FormProps } from '@whitbread-eos/atoms';
import * as yup from 'yup';

const VALIDATIONS = {
  FIRST_NAME: {
    MATCHES: RegExp(
      /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/
    ),
    MIN: 2,
    MAX: 20,
  },
  LAST_NAME: {
    MATCHES: RegExp(
      /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/
    ),
    MIN: 2,
    MAX: 30,
  },
  COMPANY_NAME: {
    MATCHES: RegExp(/^[a-zA-Z0-9\s.,&@#()-À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+$/),
    MIN: 2,
    MAX: 50,
  },
  EMAIL: {
    MATCHES: RegExp(
      /^[a-z0-9]+(?:[._-][a-z0-9]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i
    ),
    MAX: 50,
    MIN: 2,
  },
  ADDRESS: {
    MATCHES: RegExp(/^[a-zA-Z0-9\s.,&@#()-À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+$/),
    MIN: 2,
    MAX: 200,
  },
  POSTAL_CODE: {
    MATCHES: RegExp(/^[a-zA-Z0-9 ]*$/i),
    MIN: 2,
    MAX: 7,
  },
  PHONE: {
    MATCHES: RegExp(/^\s*\+?\s*(\d[\s-]*){6,}$/im),
    MIN: 9,
    MAX: 15,
  },
};

type ValidationRuleType = {
  match: RegExp;
  max: number;
  min: number;
  matchErrorMsg: string;
  maxErrorMsg: string;
  minErrorMsg: string;
};

const standarValidationForUpdateForm = (
  nameOfTest: string,
  defaultValue: unknown,
  ValidationRule: ValidationRuleType
) => {
  return yup
    .string()
    .transform((value) => (value === '' ? null : value))
    .nullable()
    .test(nameOfTest, '', function (value, ctx) {
      const isDefaultValue = defaultValue === value;

      if (isDefaultValue) {
        return true; // No validation needed for default value
      }

      const isEmptyByDefault = (value ?? '').trim() === '' && defaultValue === '';
      if (isEmptyByDefault) {
        return true; // No validation needed for empty value
      }

      const isInvalidLength = (value?.length ?? 0) < ValidationRule.min;
      const isNotMatching =
        typeof value === 'string' && value.trim() !== '' && !ValidationRule.match.test(value);
      const isTooLong = (value?.length ?? 0) > ValidationRule.max;

      if (isInvalidLength) {
        throw ctx.createError({
          message: ValidationRule.minErrorMsg,
        });
      }

      if (isNotMatching) {
        throw ctx.createError({
          message: ValidationRule.matchErrorMsg,
        });
      }

      if (isTooLong) {
        throw ctx.createError({
          message: ValidationRule.maxErrorMsg,
        });
      }

      return true;
    });
};

export default function validateUpdateAccountForm(
  t: (id: string) => string,
  defaultValues: FormProps['defaultValues']
): any {
  const firstNameCodeValidationRules = {
    match: VALIDATIONS.FIRST_NAME.MATCHES,
    max: VALIDATIONS.FIRST_NAME.MAX,
    min: VALIDATIONS.FIRST_NAME.MIN,
    matchErrorMsg: 'Please enter a valid first name (max 20 characters)',
    maxErrorMsg: 'Please enter a valid first name (max 20 characters)',
    minErrorMsg: 'Please enter at least 2 characters',
  };

  const lastnameCodeValidationRules = {
    match: VALIDATIONS.LAST_NAME.MATCHES,
    max: VALIDATIONS.LAST_NAME.MAX,
    min: VALIDATIONS.LAST_NAME.MIN,
    matchErrorMsg: 'Please enter a valid surname (max 30 characters)',
    maxErrorMsg: 'Please enter a valid surname (max 30 characters)',
    minErrorMsg: 'Please enter at least 2 characters',
  };

  const companynameCodeValidationRules = {
    match: VALIDATIONS.COMPANY_NAME.MATCHES,
    max: VALIDATIONS.COMPANY_NAME.MAX,
    min: VALIDATIONS.COMPANY_NAME.MIN,
    matchErrorMsg: 'Please enter a valid company (max 50 characters)',
    maxErrorMsg: 'Please enter a valid company (max 50 characters)',
    minErrorMsg: 'Please enter at least 2 characters',
  };

  const emailCodeValidationRules = {
    match: VALIDATIONS.EMAIL.MATCHES,
    max: VALIDATIONS.EMAIL.MAX,
    min: VALIDATIONS.EMAIL.MIN,
    matchErrorMsg: 'Please enter a valid e-mail address',
    maxErrorMsg: 'Please enter a valid e-mail address',
    minErrorMsg: 'Please enter a valid e-mail address',
  };

  const addressCodeValidationRules = {
    match: VALIDATIONS.ADDRESS.MATCHES,
    max: VALIDATIONS.ADDRESS.MAX,
    min: VALIDATIONS.ADDRESS.MIN,
    matchErrorMsg: 'Please enter a valid address',
    maxErrorMsg: 'Please enter a valid address',
    minErrorMsg: 'Please enter the home or business address',
  };

  const postalCodeValidationRules = {
    match: VALIDATIONS.POSTAL_CODE.MATCHES,
    max: VALIDATIONS.POSTAL_CODE.MAX,
    min: VALIDATIONS.POSTAL_CODE.MIN,
    matchErrorMsg: t('config.errorMessages.yourDetails.postcode.invalid'),
    maxErrorMsg: t('config.errorMessages.yourDetails.postcode.invalid'),
    minErrorMsg: t('config.errorMessages.yourDetails.postcode.invalid'),
  };

  const formValidationObject = {
    firstName: standarValidationForUpdateForm(
      'FirstnameCodeValidation',
      defaultValues.firstName,
      firstNameCodeValidationRules
    ),

    lastName: standarValidationForUpdateForm(
      'lastNameValidation',
      defaultValues.lastName,
      lastnameCodeValidationRules
    ),

    companyName: standarValidationForUpdateForm(
      'companyNameValidation',
      defaultValues.companyName,
      companynameCodeValidationRules
    ),

    email: standarValidationForUpdateForm(
      'emailValidation',
      defaultValues.email,
      emailCodeValidationRules
    ),

    address: standarValidationForUpdateForm(
      'addressValidation',
      defaultValues.address,
      addressCodeValidationRules
    ),

    postalCode: standarValidationForUpdateForm(
      'postalCodeValidation',
      defaultValues.postalCode,
      postalCodeValidationRules
    ),
    mobileNumber: yup
      .string()
      .transform((value) => value || null)
      .nullable()
      .test(
        'mobileAndLandlineValidation',
        'Please enter a valid phone number',
        function (value, ctx: any) {
          const { landlineNumber } = ctx.parent;
          const mobileIsDefaultValue = defaultValues.mobileNumber === value;
          const landlineIsDefaultValue = defaultValues.landlineNumber === landlineNumber;

          if (!mobileIsDefaultValue && !landlineIsDefaultValue && !value && !landlineNumber) {
            throw ctx.createError({
              message: 'Please enter a valid phone number',
            });
          }

          // Validate mobile number format, min, and max
          if (value && !VALIDATIONS.PHONE.MATCHES.test(value)) {
            throw ctx.createError({
              message: t('config.errorMessages.yourDetails.mobile.invalid'),
            });
          }

          if (
            value &&
            (value.length < VALIDATIONS.PHONE.MIN || value.length > VALIDATIONS.PHONE.MAX)
          ) {
            throw ctx.createError({
              message: t('config.errorMessages.yourDetails.mobile.invalid'),
            });
          }

          // Clear errors for both fields if this validation passes
          ctx.parent.mobileError = null;
          ctx.parent.landlineError = null;

          return true;
        }
      ),

    landlineNumber: yup
      .string()
      .transform((value) => value || null)
      .nullable()
      .test(
        'mobileAndLandlineValidation',
        'Please enter a valid phone number',
        function (value, ctx: any) {
          const { mobileNumber } = ctx.parent;
          const landlineIsDefaultValue = defaultValues.landlineNumber === value;
          const mobileIsDefaultValue = defaultValues.mobileNumber === mobileNumber;

          if (!landlineIsDefaultValue && !mobileIsDefaultValue && !value && !mobileNumber) {
            throw ctx.createError({
              message: 'Please enter a valid phone number',
            });
          }

          // Validate landline number format, min, and max
          if (value && !VALIDATIONS.PHONE.MATCHES.test(value)) {
            throw ctx.createError({
              message: t('config.errorMessages.yourDetails.telephone.invalid'),
            });
          }

          if (
            value &&
            (value.length < VALIDATIONS.PHONE.MIN || value.length > VALIDATIONS.PHONE.MAX)
          ) {
            throw ctx.createError({
              message: t('config.errorMessages.yourDetails.telephone.invalid'),
            });
          }

          // Clear errors for both fields if this validation passes

          ctx.parent.mobileError = null;
          ctx.parent.landlineError = null;

          return true;
        }
      ),
  };

  const formValidationSchema = yup.object().shape(formValidationObject, [['phone', 'landline']]);

  return { formValidationObject, formValidationSchema };
}
