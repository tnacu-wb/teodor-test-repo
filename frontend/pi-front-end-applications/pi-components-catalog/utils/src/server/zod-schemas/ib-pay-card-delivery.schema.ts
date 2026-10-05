import { z } from 'zod';

export const IBPayCardDeliverySchema = (t: any) => {
  return z.object({
    delivery: z.string(),
    title: z.object({
      displayValue: z.string().min(1, t('users.userMgmt.employee.add.form.title.required')),
      value: z.string(),
    }),
    firstName: z
      .string()
      .min(2, t('users.userMgmt.employee.edit.error.firstNameFormat'))
      .max(30, t('users.userMgmt.employee.edit.error.firstNameFormat'))
      .regex(/^[\p{L} \-']*$/u, {
        message: t('users.userMgmt.employee.edit.error.firstNameFormat'),
      }),
    lastName: z
      .string()
      .min(2, t('users.userMgmt.employee.edit.error.lastNameFormat'))
      .max(30, t('users.userMgmt.employee.edit.error.lastNameFormat'))
      .regex(/^[\p{L} \-']*$/u, {
        message: t('users.userMgmt.employee.edit.error.lastNameFormat'),
      }),
  });
};
