import { z } from 'zod';

export const hotelPolicySchema = () => {
  return z.object({
    hotelBrandPolicy: z.object({
      value: z.string().optional(),
      displayValue: z.string(),
    }),
  });
};
