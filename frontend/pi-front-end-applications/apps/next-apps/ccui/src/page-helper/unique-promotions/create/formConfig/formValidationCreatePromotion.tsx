import { validateRegisterFormParams } from '@whitbread-eos/api';
import { isCountryPlatformValid } from '@whitbread-eos/molecules/dist/unique-promotions/Create';
import * as yup from 'yup';

export default function validateFormCreatePromotions(params: validateRegisterFormParams): any {
  const { t } = params;
  const startOfToday = () => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
  };

  const formValidationObject = {
    campaignName: yup
      .string()
      .required(t('campaignNameRequiredError'))
      .matches(/^\w+$/, t('campaignNameValidationError'))
      .typeError(t('campaignNameValidationError')),
    operaPromoCode: yup
      .string()
      .required(t('promoCodeRequiredError'))
      .matches(/^[A-Za-z0-9]+$/, t('promoCodeValidation'))
      .min(4, t('promoCodeLengthValidationError'))
      .max(20, t('promoCodeLengthValidationError')),
    hotelId: yup.string().trim().required(t('hotelIdRequiredError')),
    expiryDate: yup
      .date()
      .nullable()
      .transform((value, originalValue) =>
        originalValue === null || originalValue === '' ? null : value
      )
      .typeError(t('endDateInvalidError'))
      .required(t('endDateInvalidError'))
      .min(startOfToday(), t('endDateInvalidError')),
    isGeneric: yup.boolean().default(false),
    prefix: yup.string().when('isGeneric', {
      is: true,
      then: (schema) => schema.notRequired(),
      otherwise: (schema) =>
        schema
          .required(t('voucherPrefixRequiredError'))
          .matches(/^[A-Za-z0-9]+$/, t('voucherPrefixValidationError'))
          .max(20, t('voucherPrefixValidationError')),
    }),
    genericPromoCode: yup.string().when('isGeneric', {
      is: true,
      then: (schema) =>
        schema
          .required(t('promoCodeRequiredError'))
          .matches(/^[A-Za-z0-9]+$/, t('promoCodeValidation'))
          .max(20, t('promoCodeLengthValidationError')),
      otherwise: (schema) => schema.notRequired(),
    }),
    isLimitRedemptions: yup.boolean().default(false),
    batchCount: yup
      .number()
      .transform((value, originalValue) => (originalValue === '' ? undefined : value))
      .when('isGeneric', {
        is: true,
        then: (schema) => schema.notRequired(),
        otherwise: (schema) =>
          schema
            .required(t('vouchersCountRequiredError'))
            .typeError(t('vouchersCountInvalidError'))
            .positive(t('vouchersCountInvalidError'))
            .integer(t('vouchersCountInvalidError'))
            .max(1000000, t('vouchersCountInvalidError')),
      }),
    maxRedemptionLimit: yup
      .number()
      .transform((value, originalValue) => (originalValue === '' ? undefined : value))
      .when(['isGeneric', 'isLimitRedemptions'], {
        is: (isGeneric: boolean, isLimitRedemptions: boolean) =>
          Boolean(isGeneric && isLimitRedemptions),
        then: (schema) =>
          schema
            .notRequired()
            .typeError(t('genericPromoInvalidError'))
            .min(1, t('genericPromoInvalidError'))
            .max(1000000, t('genericPromoInvalidError')),
        otherwise: (schema) => schema.notRequired(),
      }),
    platformConfig: yup.mixed().test('platform-config-required', '', (value: any) => {
      if (!value) return false;
      const countries = ['GB', 'DE'];
      const anyCountryEnabled = countries.some((key) => value?.[key]?.enabled);
      if (!anyCountryEnabled) return false;
      return countries.every((key) => isCountryPlatformValid(value?.[key]));
    }),
  };
  const formValidationSchemaCreatePromotions = yup.object().shape(formValidationObject);
  return { formValidationObject, formValidationSchemaCreatePromotions };
}
