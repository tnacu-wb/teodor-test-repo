import { z } from 'zod';

export const addressSchema = (t: any) => {
  return z.object({
    addressLine1: z
      .string()
      .trim()
      .min(1, t('users.userMgmt.employee.edit.error.addressLine1.required')),
    addressLine2: z.string().trim(),
    addressLine3: z.string().trim(),
    addressLine4: z.string().trim(),
    addressLine5: z.string().trim(),
    postCode: z
      .string()
      .trim()
      .min(1, t('users.userMgmt.employee.add.companyAddress.error.invalidPostcode')),
    country: z.string().trim(),
  });
};
