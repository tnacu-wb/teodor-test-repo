import { z } from 'zod';

export const registeredCharityNumberSchema = (t: any) => {
  return z.object({
    charityNumber: z.string().min(1, { message: t('payApplication.payapp.memorable.error') }),
  });
};
