import { z } from 'zod';

export const estimatedAccountSpendingSchema = (t: any) => {
  return z.object({
    estMonthlySpend: z.object({
      value: z.string().min(1, { message: t('payapp.memorable.error') }),
      displayValue: z.string(),
    }),
  });
};
