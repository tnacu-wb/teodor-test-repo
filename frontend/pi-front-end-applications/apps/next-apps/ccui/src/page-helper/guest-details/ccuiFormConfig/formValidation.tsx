import { validateFormParams } from '@whitbread-eos/api';
import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

import checkIsUnique from '../common/uniqueValidation';

export default function validateForm(params: validateFormParams): any {
  checkIsUnique();

  const {
    t,
    bkndData,
    isGermanHotel,
    isSingleRoomRedesignEnabled,
    isBookingForSomeoneElse,
    isCompanyNameAdvanceEnabled,
    isConsolidateMobileLandlineEnabled,
  } = params;

  const COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS = isCompanyNameAdvanceEnabled
    ? FORM_VALIDATIONS.COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS
    : FORM_VALIDATIONS.CCUI_COMPANY_NAME;

  const isBillingAddressVisible = (
    isGermanHotel: boolean | undefined,
    billingAddressCheckbox: boolean
  ) => {
    return isGermanHotel && billingAddressCheckbox === true;
  };

  const addressLine1Validation = yup
    .string()
    .required(t('config.errorMessages.yourDetails.address1.required'))
    .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
      message: t('config.errorMessages.yourDetails.address1.invalid'),
    })
    .max(FORM_VALIDATIONS.ADDRESS.MAX, t('config.errorMessages.yourDetails.address1.max'));

  const addressLine2Validation = yup
    .string()
    .nullable()
    .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
      message: t('config.errorMessages.yourDetails.address2.invalid'),
    })
    .max(FORM_VALIDATIONS.ADDRESS.MAX, t('config.errorMessages.yourDetails.address2.max'))
    .nullable();

  const addressLine3Validation = yup
    .string()
    .nullable()
    .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
      message: t('config.errorMessages.yourDetails.address3.invalid'),
    })
    .max(FORM_VALIDATIONS.ADDRESS.MAX, t('config.errorMessages.yourDetails.address3.max'))
    .nullable();

  const addressLine4Validation = yup
    .string()
    .nullable()
    .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
      message: t('config.errorMessages.yourDetails.address4.invalid'),
    })
    .max(FORM_VALIDATIONS.ADDRESS.MAX, t('config.errorMessages.yourDetails.address4.max'));

  const billingAddressValidation = (schema: any) => {
    return yup.string().when('billingAddressCheckbox', {
      is: (billingAddressCheckbox: boolean) =>
        isBillingAddressVisible(isGermanHotel, billingAddressCheckbox),
      then: schema,
    });
  };

  const formValidationObject = {
    reasonForStay: yup
      .string()
      .required(t('config.errorMessages.yourDetails.businessTrip.required')),
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
    email: yup
      .string()
      .nullable()
      .transform((value) => value ?? null)
      .when('email', {
        is: (email: string) => !!email,
        then: yup
          .string()
          .matches(
            FORM_VALIDATIONS.EMAIL_GD.MATCHES,
            t('config.errorMessages.yourDetails.email.invalid')
          ),
        otherwise: yup.string().optional().nullable(),
      }),
    ...(isConsolidateMobileLandlineEnabled
      ? {
          phone: yup
            .string()
            .matches(FORM_VALIDATIONS.PHONE.MATCHES, {
              message: t('groupBooking.contactDetails.yourContact.phoneNumber.invalid'),
              excludeEmptyString: true,
            })
            .min(
              FORM_VALIDATIONS.PHONE.MIN,
              t('groupBooking.contactDetails.yourContact.phoneNumber.invalid')
            )
            .max(
              FORM_VALIDATIONS.PHONE.MAX,
              t('groupBooking.contactDetails.yourContact.phoneNumber.invalid')
            )
            .required(t('groupBooking.contactDetails.yourContact.phoneNumber.invalid')),
        }
      : {
          phone: yup
            .string()
            .matches(FORM_VALIDATIONS.PHONE.MATCHES, {
              message: t('config.errorMessages.yourDetails.mobile.invalid'),
              excludeEmptyString: true,
            })
            .min(FORM_VALIDATIONS.PHONE.MIN, t('config.errorMessages.yourDetails.mobile.invalid'))
            .max(FORM_VALIDATIONS.PHONE.MAX, t('config.errorMessages.yourDetails.mobile.invalid'))
            .nullable()
            .transform((value) => value || null)
            .when('landline', {
              is: (landline: string) => !landline,
              then: (schema) => {
                return schema.required(t('config.errorMessages.yourDetails.mobile.required'));
              },
              otherwise: yup.string().optional().nullable(),
            }),
          landline: yup
            .string()
            .matches(FORM_VALIDATIONS.LANDLINE.MATCHES, {
              message: t('config.errorMessages.yourDetails.telephone.invalid'),
              excludeEmptyString: true,
            })
            .min(FORM_VALIDATIONS.LANDLINE.MIN, t('config.errorMessages.yourDetails.telephone.min'))
            .max(FORM_VALIDATIONS.LANDLINE.MAX, t('config.errorMessages.yourDetails.telephone.max'))
            .nullable()
            .transform((value) => value || null)
            .when('phone', {
              is: (phone: string) => !phone,
              then: (schema) => {
                return schema.required(t('config.errorMessages.yourDetails.telephone.required'));
              },
              otherwise: yup.string().optional().nullable(),
            }),
        }),
    leadGuest: yup.array().when('bookingForSomeoneElse', {
      is: (bookingForSomeoneElse: boolean) =>
        (bookingForSomeoneElse === true && bkndData?.rooms.length === 1) ||
        bkndData?.rooms.length > 1 ||
        (isSingleRoomRedesignEnabled && isBookingForSomeoneElse && bkndData?.rooms.length === 1),
      then: (schema) => {
        return schema
          .of(
            yup.object().shape({
              stayInThisRoom: yup.boolean(),
              title: yup.string().when('stayInThisRoom', {
                is: (stayInThisRoom: any) => stayInThisRoom !== true,
                then: yup.string().required(t('config.errorMessages.yourDetails.title.required')),
                otherwise: yup.string().nullable(),
              }),
              firstName: yup.string().when('stayInThisRoom', {
                is: (stayInThisRoom: any) => stayInThisRoom !== true,
                then: yup
                  .string()
                  .required(t('config.errorMessages.yourDetails.firstName.required'))
                  .trim(t('config.errorMessages.yourDetails.firstName.required'))
                  .matches(FORM_VALIDATIONS.FIRST_NAME.MATCHES, {
                    message: t('config.errorMessages.yourDetails.firstName.invalid'),
                  })
                  .min(1, t('config.errorMessages.yourDetails.firstName.min'))
                  .max(20, t('config.errorMessages.yourDetails.firstName.max')),
                otherwise: yup.string().nullable(),
              }),
              lastName: yup.string().when('stayInThisRoom', {
                is: (stayInThisRoom: any) => stayInThisRoom !== true,
                then: yup
                  .string()
                  .required(t('config.errorMessages.yourDetails.lastName.required'))
                  .trim(t('config.errorMessages.yourDetails.lastName.required'))
                  .matches(FORM_VALIDATIONS.LAST_NAME.MATCHES, {
                    message: t('config.errorMessages.yourDetails.lastName.invalid'),
                  })
                  .min(1, t('config.errorMessages.yourDetails.lastName.min'))
                  .max(30, t('config.errorMessages.yourDetails.lastName.max')),
                otherwise: yup.string().nullable(),
              }),
            })
          )
          .unique('lastName', t('config.errorMessages.guestDetails.firstName.duplicate'));
      },
      otherwise: (schema) => {
        return schema.of(
          yup.object().shape({
            title: yup.string().optional().nullable(),
            firstName: yup.string().optional().nullable(),
            lastName: yup.string().optional().nullable(),
          })
        );
      },
    }),
    addressSelection: yup.string().required(t('config.errorMessages.yourDetails.address.type')),
    billing_addressSelection: yup.string().when('billingAddressCheckbox', {
      is: (billingAddressCheckbox: boolean) =>
        isBillingAddressVisible(isGermanHotel, billingAddressCheckbox),
      then: yup.string().required(t('config.errorMessages.yourDetails.address.type')),
    }),
    addressLine1: addressLine1Validation,
    addressLine2: addressLine2Validation,
    addressLine3: addressLine3Validation,
    addressLine4: addressLine4Validation,
    billing_addressLine1: billingAddressValidation(addressLine1Validation),
    billing_addressLine2: billingAddressValidation(addressLine2Validation),
    billing_addressLine3: billingAddressValidation(addressLine3Validation),
    billing_addressLine4: billingAddressValidation(addressLine4Validation),
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
    billing_postalCode: yup
      .string()
      .transform((value, originalValue) => {
        return originalValue === '' ? undefined : value;
      })
      .when(['billing_countryCode', 'billingAddressCheckbox'], {
        is: (billing_countryCode: string, billingAddressCheckbox: boolean) => {
          return billingAddressCheckbox === true && billing_countryCode === 'GB';
        },
        then: (schema) => {
          return schema
            .required(t('config.errorMessages.yourDetails.postcode.required'))
            .matches(
              FORM_VALIDATIONS.POSTAL_CODE.MATCHES_UK,
              t('config.errorMessages.yourDetails.postcode.invalid')
            );
        },
      })
      .when(['billing_countryCode', 'billingAddressCheckbox'], {
        is: (billing_countryCode: string, billingAddressCheckbox: boolean) => {
          return billingAddressCheckbox === true && billing_countryCode === 'DE';
        },
        then: (schema) => {
          return schema
            .required(t('config.errorMessages.yourDetails.postcode.required'))
            .matches(
              FORM_VALIDATIONS.POSTAL_CODE.MATCHES_DE,
              t('config.errorMessages.yourDetails.postcode.invalid')
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
    billing_cityName: yup.string().when(['billing_countryCode', 'billingAddressCheckbox'], {
      is: (billing_countryCode: string, billingAddressCheckbox: boolean) =>
        billingAddressCheckbox === true && billing_countryCode === 'DE',
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
    billing_companyName: yup.string().when('billing_addressSelection', {
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

  const dependencies: [string, string][] = [['email', 'email']];
  if (!isConsolidateMobileLandlineEnabled) {
    dependencies.push(['phone', 'landline']);
  }
  const formValidationSchema = yup.object().shape(formValidationObject, dependencies);

  return { formValidationObject, formValidationSchema };
}
