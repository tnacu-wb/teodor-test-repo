import { z } from 'zod';

export const companyRegistrationNumberSchema = (t: any) => {
  return z.object({
    companyRegNum: z
      .string()
      .trim()
      .min(1, {
        message: t('payApplication.payapp.memorable.error'),
      })
      .min(3, {
        message: t('payApplication.companyDetails.registeredNumber.failure.notification'),
      })
      .max(64, {
        message: t('payApplication.companyDetails.registeredNumber.failure.notification'),
      }),
  });
};
