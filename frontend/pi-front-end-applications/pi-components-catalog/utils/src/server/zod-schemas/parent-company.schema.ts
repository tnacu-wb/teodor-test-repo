import { z } from 'zod';

export const parentCompanySchema = () => {
  return z.object({
    parentCompanyName: z.string().trim().optional(),
  });
};
