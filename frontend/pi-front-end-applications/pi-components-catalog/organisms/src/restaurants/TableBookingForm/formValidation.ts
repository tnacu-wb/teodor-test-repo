import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as z from 'zod';

/**
 * Type for form content data, used for error messages and validation.
 */
export interface FormContentData {
  [key: string]: string;
}

/**
 * Builds the validation schema for step one of the form.
 */
function buildFormStepOneValidation(
  formContentData: FormContentData,
  isMenuOptionAvailable?: boolean
) {
  return {
    adults: z.number().refine((value) => value > 0, {
      message: formContentData?.['reservationform.adult.error'],
    }),
    children: z.number().optional(),
    menuId: isMenuOptionAvailable
      ? z.string().refine((value) => value !== '', {
          message: formContentData['reservationform.menu.error'],
        })
      : z.string().optional(),
    highchair: z.number().optional(),
    wheelchair: z.boolean().optional(),
    specialRequest: z.string().optional(),
    time: z.string().nonempty(),
  };
}

/**
 * Builds the validation schema for the enquiry form.
 */
function buildEnquiryFormValidation(formContentData: FormContentData) {
  return {
    adultsByEnquiry: z
      .string()
      .nonempty(formContentData?.['reservationform.enquiry.adult.invalidlength'])
      .refine(
        (value) => !RegExp(FORM_VALIDATIONS.ADULTS_CHILDREN_BY_ENQUIRY.MATCHES).test(value),
        () => ({
          message: formContentData?.['reservationform.enquiry.adult.invalidcharacters'],
        })
      )
      .refine(
        (value) => Number(value) !== 0,
        () => ({
          message: formContentData?.['reservationform.enquiry.adult.label'],
        })
      ),
    childrenByEnquiry: z.string().refine(
      (value) => !RegExp(FORM_VALIDATIONS.ADULTS_CHILDREN_BY_ENQUIRY.MATCHES).test(value),
      () => ({
        message: formContentData?.['reservationform.enquiry.children.invalidcharacters'],
      })
    ),
  };
}

/**
 * Builds the validation schema for the complete form.
 */
function buildCompleteFormValidation(formContentData: FormContentData) {
  return {
    firstname: z
      .string()
      .nonempty(formContentData?.['reservationform.yourdetails.firstname.required'])
      .trim()
      .min(
        FORM_VALIDATIONS.FIRST_NAME.MIN,
        formContentData?.['reservationform.yourdetails.firstname.invalidlength']
      )
      .max(FORM_VALIDATIONS.FIRST_NAME.MAX)
      .refine(
        (value) => new RegExp(FORM_VALIDATIONS.FIRST_NAME.MATCHES.source).test(value),
        () => ({
          message: formContentData?.['reservationform.yourdetails.firstname.invalidcharacters'],
        })
      ),
    lastname: z
      .string()
      .nonempty(formContentData?.['reservationform.yourdetails.lastname.required'])
      .trim()
      .min(
        FORM_VALIDATIONS.LAST_NAME.MIN,
        formContentData?.['reservationform.yourdetails.lastname.invalidlength']
      )
      .max(FORM_VALIDATIONS.LAST_NAME.MAX)
      .refine(
        (value) => new RegExp(FORM_VALIDATIONS.LAST_NAME.MATCHES.source).test(value),
        () => ({
          message: formContentData?.['reservationform.yourdetails.lastname.invalidcharacters'],
        })
      ),
    emailAddress: z
      .string()
      .nonempty(formContentData?.['reservationform.yourdetails.email.incompleteerror'])
      .email({ message: formContentData?.['reservationform.yourdetails.email.invalidcharacters'] }),
    telephoneNumber: z
      .string()
      .nonempty(formContentData?.['reservationform.yourdetails.contact.invalidcontact'])
      .trim()
      .refine((value) => RegExp(FORM_VALIDATIONS.UNBRANDED_RESTAURANT_PHONE.MATCHES).test(value), {
        message: formContentData['reservationform.yourdetails.contact.invalidcharacters'],
      })
      .refine((value) => !value.includes(' '), {
        message: formContentData?.['reservationform.yourdetails.contact.invalidspacescharacters'],
      })
      .refine((value) => value.length >= FORM_VALIDATIONS.UNBRANDED_RESTAURANT_PHONE.MIN, {
        message: formContentData?.['reservationform.yourdetails.contact.invalidlengthMsg'],
      })
      .refine((value) => value.length <= FORM_VALIDATIONS.UNBRANDED_RESTAURANT_PHONE.MAX, {
        message: formContentData?.['reservationform.yourdetails.contact.invalidlengthMsg'],
      }),
    consent: z.boolean().optional(),
    privacyStatement: z.boolean().refine((value) => value === true, {
      message: formContentData?.['reservationform.policystatement.checkbox.error'],
    }),
  };
}

/**
 * Returns all validation schemas for the TableBookingForm.
 */
export default function validateForm(
  formContentData: FormContentData,
  isMenuOptionAvailable?: boolean
) {
  const formStepOneValidationSchema = z.object(
    buildFormStepOneValidation(formContentData, isMenuOptionAvailable)
  );
  const completeFormValidationSchema = z.object(buildCompleteFormValidation(formContentData));
  const enquiryFormValidationSchema = z.object(buildEnquiryFormValidation(formContentData));

  return {
    formStepOneValidationSchema,
    completeFormValidationSchema,
    enquiryFormValidationSchema,
  };
}
