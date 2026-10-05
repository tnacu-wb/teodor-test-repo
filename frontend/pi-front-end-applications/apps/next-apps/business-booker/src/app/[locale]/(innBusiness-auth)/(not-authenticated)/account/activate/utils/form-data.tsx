import { z } from 'zod';

const passwordRegex = /^(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;

export const employeeActivationSchema = (t: any) => {
  return z.object({
    title: z.object({
      displayValue: z.string().min(1, t('auth.signup.personalInfo.titleDropdown.error')),
      value: z.string(),
    }),
    firstName: z
      .string()
      .min(2, t('auth.signup.personalInfo.firstName.error'))
      .max(30, t('auth.signup.personalInfo.firstName.error'))
      .regex(/^[A-Za-z\s-]+$/, {
        message: t('auth.signup.personalInfo.firstName.error'),
      }),
    lastName: z
      .string()
      .min(2, t('auth.signup.personalInfo.lastName.error'))
      .max(30, t('auth.signup.personalInfo.lastName.error'))
      .regex(/^[A-Za-z\s-]+$/, { message: t('auth.signup.personalInfo.lastName.error') }),
    emailAddress: z
      .string()
      .regex(
        RegExp(
          /^[a-z0-9]+(?:[._'-][a-z0-9]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i
        ),
        { message: t('auth.signup.contact.email.error') }
      ),
    phoneNumber: z.object({
      prefix: z.string(),
      phoneNumber: z
        .string()
        .min(6, t('auth.signup.contact.phone.error'))
        .regex(/^\d+$/, { message: t('auth.signup.contact.phone.error') }),
    }),
    alternatePhoneNumber: z.object({
      prefix: z.string().optional(),
      phoneNumber: z.string().optional(),
    }),
    createPassword: z
      .string()
      .min(8, { message: t('auth.signup.password.error') })
      .regex(passwordRegex, { message: t('auth.signup.password.error') })
      .refine(
        (value) => {
          const normalizedValue = value.toLowerCase();
          const max2sameConsecutiveCharacters = /(.)\1\1/;
          return !max2sameConsecutiveCharacters.test(normalizedValue);
        },
        {
          message: t('auth.signup.password.error'),
        }
      ),
  });
};
