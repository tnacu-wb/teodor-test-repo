import { z } from 'zod';

export const dateOfBirthSchema = (t: any) => {
  return z.object({
    day: z.object({
      value: z.string().min(1, { message: t('payApplication.payapp.memorable.error') }),
      displayValue: z.string(),
    }),
    month: z.object({
      value: z.string().min(1, { message: t('payApplication.payapp.memorable.error') }),
      displayValue: z.string(),
    }),
    year: z.object({
      value: z.string().min(1, { message: t('payApplication.payapp.memorable.error') }),
      displayValue: z.string(),
    }),
  });
};
