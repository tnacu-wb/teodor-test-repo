import { z } from 'zod';

export const companyNameOfEmployeeSchema = (t: any) => {
  const errorMessage = t('payApplication.payapp.memorable.error');

  return z.object({
    titleEmployee: z.object({
      displayValue: z.string().min(1, errorMessage),
      value: z.string(),
    }),
    firstNameEmployee: z
      .string()
      .min(2, errorMessage)
      .max(30, errorMessage)
      .regex(/^[A-Za-z\s-]+$/, { message: errorMessage }),
    lastNameEmployee: z
      .string()
      .min(2, errorMessage)
      .max(30, errorMessage)
      .regex(/^[A-Za-z\s-]+$/, { message: errorMessage }),
  });
};
