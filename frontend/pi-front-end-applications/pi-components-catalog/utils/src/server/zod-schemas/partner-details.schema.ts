import { z } from 'zod';

export const partnerDetailsSchema = (t: any) => {
  const genericErrorMessage = t('payApplication.payapp.memorable.error');
  const titleErrorMessage = t('payApplication.companyDetails.nameTitles.required');
  const firstNameErrorMessage = t('payApplication.companyDetails.name.firstName.invalid');
  const lastNameErrorMessage = t('payApplication.companyDetails.name.lastName.invalid');

  return z.object({
    numberOfPartners: z
      .string()
      .regex(/^\d+$/, { message: genericErrorMessage })
      .refine((value) => Number(value) >= 0 && Number(value) <= 999, {
        message: genericErrorMessage,
      }),
    title: z.object({
      displayValue: z.string().min(1, titleErrorMessage),
      value: z.string(),
    }),
    foreName: z
      .string()
      .min(2, firstNameErrorMessage)
      .max(30, firstNameErrorMessage)
      .regex(/^[A-Za-z\s-]+$/, { message: firstNameErrorMessage }),
    lastName: z
      .string()
      .min(2, lastNameErrorMessage)
      .max(30, lastNameErrorMessage)
      .regex(/^[A-Za-z\s-]+$/, { message: lastNameErrorMessage }),
  });
};
