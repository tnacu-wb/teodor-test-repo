import { z } from 'zod';

export const timeTradingSchema = (t: any) => {
  return z.object({
    timeTradingId: z.object({
      value: z.string().min(1, { message: t('payApplication.payapp.memorable.error') }),
      displayValue: z.string(),
    }),
  });
};
