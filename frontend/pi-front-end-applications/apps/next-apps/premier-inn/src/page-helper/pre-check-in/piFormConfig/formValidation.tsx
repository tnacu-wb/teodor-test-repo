import { validateRegisterFormParams } from '@whitbread-eos/api';
import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

export default function validateForm(params: validateRegisterFormParams): any {
  const { t } = params;
  const nationalitySchema = yup.object().shape({
    value: yup
      .string()
      .nullable()
      .required(t('precheckin.errors.empty.nationality'))
      .typeError(t('precheckin.errors.empty.nationality')),
    label: yup.string().nullable(),
    image: yup.string().nullable(),
  });
  const nationalitySchemaOptional = yup.object().shape({
    value: yup.string().nullable().optional().typeError(t('precheckin.errors.empty.nationality')),
    label: yup.string().nullable(),
    image: yup.string().nullable(),
  });
  const lastNameSchema = yup
    .string()
    .required(t('precheckin.errors.empty.surname'))
    .trim(t('precheckin.errors.empty.surname'))
    .matches(FORM_VALIDATIONS.LAST_NAME.MATCHES, t('precheckin.errors.onlyletters'))
    .min(FORM_VALIDATIONS.LAST_NAME.MIN, t('precheckin.errors.surname.minlength'))
    .max(FORM_VALIDATIONS.LAST_NAME.MAX, t('precheckin.errors.surname.maxlength'));
  const formValidationObject = {
    firstName: yup
      .string()
      .required(t('precheckin.errors.empty.firstname'))
      .trim(t('precheckin.errors.empty.firstname'))
      .matches(FORM_VALIDATIONS.FIRST_NAME.MATCHES, t('precheckin.errors.onlyletters'))
      .min(FORM_VALIDATIONS.FIRST_NAME.MIN, t('precheckin.errors.firstname.minlength'))
      .max(FORM_VALIDATIONS.FIRST_NAME.MAX, t('precheckin.errors.firstname.maxlength')),
    lastName: lastNameSchema,
    address: yup
      .string()
      .required(t('precheckin.errors.empty.address'))
      .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
        message: t('precheckin.errors.housenumber'),
      })
      .max(FORM_VALIDATIONS.ADDRESS.MAX, t('precheckin.errors.housenumber.maxlength')),
    postalCode: yup
      .string()
      .transform((value, originalValue) => (originalValue === '' ? undefined : value))
      .when('country', {
        is: (country: string) => country === 'GB',
        then: (schema) => {
          return schema
            .required(t('precheckin.errors.empty.postalcode'))
            .matches(FORM_VALIDATIONS.POSTAL_CODE.MATCHES_UK, t('precheckin.errors.postalcode'));
        },
      })
      .when('country', {
        is: (country: string) => country === 'DE',
        then: (schema) =>
          schema
            .required(t('precheckin.errors.empty.postalcode'))
            .matches(FORM_VALIDATIONS.POSTAL_CODE.MATCHES_DE, t('precheckin.errors.postalcode')),
        otherwise: (schema) =>
          schema
            .required(t('precheckin.errors.empty.postalcode'))
            .max(
              FORM_VALIDATIONS.POSTAL_CODE.MAX_OTHERS,
              t('config.errorMessages.yourDetails.postcode.max')
            ),
      }),
    city: yup
      .string()
      .required(t('precheckin.errors.empty.city'))
      .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
        message: t('precheckin.errors.city'),
      })
      .max(FORM_VALIDATIONS.ADDRESS.MAX, t('precheckin.errors.city.maxlength')),
    dateOfBirth: dateOfBirthValidation(t),

    nationality: nationalitySchema.nullable().required(t('precheckin.errors.empty.nationality')),
    passport: yup.string().when('nationality.value', {
      is: (value: string) => {
        const nationalityStr = value?.toLowerCase();
        return nationalityStr === 'de';
      },
      then: yup.string().notRequired(),
      otherwise: () => getPassportValidationSchema(t),
    }),
    dependents: yup.array().when('dependent', {
      is: (dependent: string) => +dependent > 0,
      then: yup.array().of(
        yup.object().shape({
          nationality: nationalitySchema
            .nullable()
            .required(t('precheckin.errors.empty.nationality')),
          passport: yup.string().when('nationality.value', {
            is: (value: string) => {
              const nationalityStr = value?.toLowerCase();
              return nationalityStr === 'de';
            },
            then: yup.string().notRequired(),
            otherwise: () => getPassportValidationSchema(t),
          }),
          firstname: yup
            .string()
            .required(t('precheckin.errors.empty.firstname'))
            .trim(t('precheckin.errors.empty.firstname'))
            .matches(FORM_VALIDATIONS.FIRST_NAME.MATCHES, t('precheckin.errors.onlyletters'))
            .min(FORM_VALIDATIONS.FIRST_NAME.MIN, t('precheckin.errors.firstname.minlength'))
            .max(FORM_VALIDATIONS.FIRST_NAME.MAX, t('precheckin.errors.firstname.maxlength')),
          lastname: yup
            .string()
            .required(t('precheckin.errors.empty.surname'))
            .trim(t('precheckin.errors.empty.surname'))
            .matches(FORM_VALIDATIONS.LAST_NAME.MATCHES, t('precheckin.errors.onlyletters'))
            .min(FORM_VALIDATIONS.LAST_NAME.MIN, t('precheckin.errors.surname.minlength'))
            .max(FORM_VALIDATIONS.LAST_NAME.MAX, t('precheckin.errors.surname.maxlength')),
          dateofbirth: yup
            .date()
            .required(t('precheckin.errors.empty.dateofbirth'))
            .typeError(t('precheckin.errors.empty.dateofbirth')),
        })
      ),
    }),
  };
  const saveFormValidation = {
    firstName: yup
      .string()
      .required(t('precheckin.errors.empty.firstname'))
      .trim(t('precheckin.errors.empty.firstname'))
      .matches(FORM_VALIDATIONS.FIRST_NAME.MATCHES, t('precheckin.errors.onlyletters'))
      .min(FORM_VALIDATIONS.FIRST_NAME.MIN, t('precheckin.errors.firstname.minlength'))
      .max(FORM_VALIDATIONS.FIRST_NAME.MAX, t('precheckin.errors.firstname.maxlength')),
    lastName: lastNameSchema,
    nationality: nationalitySchemaOptional,
    address: yup
      .string()
      .optional()
      .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
        message: t('precheckin.errors.housenumber'),
      })
      .max(FORM_VALIDATIONS.ADDRESS.MAX, t('precheckin.errors.housenumber.maxlength')),
    postalCode: yup
      .string()
      .transform((value, originalValue) => (originalValue === '' ? undefined : value))
      .when('country', {
        is: (country: string) => country === 'GB',
        then: (schema) => {
          return schema
            .optional()
            .matches(FORM_VALIDATIONS.POSTAL_CODE.MATCHES_UK, t('precheckin.errors.postalcode'));
        },
      })
      .when('country', {
        is: (country: string) => country === 'DE',
        then: (schema) =>
          schema
            .optional()
            .matches(FORM_VALIDATIONS.POSTAL_CODE.MATCHES_DE, t('precheckin.errors.postalcode')),
        otherwise: (schema) =>
          schema
            .optional()
            .max(
              FORM_VALIDATIONS.POSTAL_CODE.MAX_OTHERS,
              t('config.errorMessages.yourDetails.postcode.max')
            ),
      }),
    city: yup
      .string()
      .optional()
      .matches(FORM_VALIDATIONS.ADDRESS.MATCHES, {
        message: t('precheckin.errors.city'),
      })
      .max(FORM_VALIDATIONS.ADDRESS.MAX, t('precheckin.errors.city.maxlength')),
    dateOfBirth: yup
      .date()
      .nullable()
      .transform((value, originalValue) => (originalValue === '' ? null : value))
      .optional()
      .typeError(t('precheckin.errors.empty.dateofbirth'))
      .test(
        'is-old-enough',
        t('precheckin.dob.tooltip'),
        (value) => !value || calculateAge(value) >= 18
      ),
    passport: yup.string().when('nationality.value', {
      is: (value: string) => {
        const nationalityStr = value?.toLowerCase();
        return nationalityStr === 'de';
      },
      then: yup.string().notRequired(),
      otherwise: () => getPassportValidationSchemaOptional(t),
    }),
    dependents: yup.array().when('dependent', {
      is: (dependent: string) => +dependent > 0,
      then: yup.array().of(
        yup.object().shape({
          nationality: nationalitySchemaOptional.nullable(),
          passport: yup.string().when('nationality.value', {
            is: (value: string) => {
              const nationalityStr = value?.toLowerCase();
              return nationalityStr === 'de';
            },
            then: yup.string().notRequired(),
            otherwise: () => getPassportValidationSchemaOptional(t),
          }),
          firstname: yup
            .string()
            .required(t('precheckin.errors.empty.firstname'))
            .trim(t('precheckin.errors.empty.firstname'))
            .matches(FORM_VALIDATIONS.FIRST_NAME.MATCHES, t('precheckin.errors.onlyletters'))
            .min(FORM_VALIDATIONS.FIRST_NAME.MIN, t('precheckin.errors.firstname.minlength'))
            .max(FORM_VALIDATIONS.FIRST_NAME.MAX, t('precheckin.errors.firstname.maxlength')),
          lastname: lastNameSchema,
          dateofbirth: yup
            .date()
            .nullable()
            .transform((value, originalValue) => (originalValue === '' ? null : value))
            .optional()
            .typeError(t('precheckin.errors.empty.dateofbirth')),
        })
      ),
    }),
  };
  const formValidationSchema = yup.object().shape(formValidationObject);
  const saveFormValidationSchema = yup.object().shape(saveFormValidation);
  return { formValidationObject, formValidationSchema, saveFormValidationSchema };
}

const getPassportValidationSchema = (t: (id: string) => string) =>
  yup
    .string()
    .transform((value, originalValue) => (originalValue === null ? '' : value))
    .required(t('precheckin.errors.passportnumber'))
    .min(2, t('precheckin.errors.passportnumber.minlength'))
    .max(20, t('precheckin.errors.passportnumber.maxlength'))
    .matches(/^[a-zA-Z0-9]*$/, t('precheckin.errors.passportnumber.invalid'));

const getPassportValidationSchemaOptional = (t: (id: string) => string) =>
  yup
    .string()
    .nullable()
    .optional()
    .test(
      'is-passport',
      t('precheckin.errors.passportnumber.minlength'),
      (value) => !value || value.length >= 2
    )
    .max(20, t('precheckin.errors.passportnumber.maxlength'))
    .matches(/^[a-zA-Z0-9]*$/, t('precheckin.errors.passportnumber.invalid'));

const dateOfBirthValidation = (t: (id: string) => string) =>
  yup
    .date()
    .required(t('precheckin.errors.empty.dateofbirth'))
    .typeError(t('precheckin.errors.empty.dateofbirth'))
    .test(
      'is-old-enough',
      t('precheckin.dob.tooltip'),
      (value) => !value || calculateAge(value) >= 18
    );

const calculateAge = (dateString: Date) => {
  const dob = new Date(dateString);
  const today = new Date();
  let age = today.getFullYear() - dob.getFullYear();
  const monthDiff = today.getMonth() - dob.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) age--;

  return age;
};
