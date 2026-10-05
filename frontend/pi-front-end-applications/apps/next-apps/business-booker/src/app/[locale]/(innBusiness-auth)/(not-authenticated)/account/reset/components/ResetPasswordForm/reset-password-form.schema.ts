import { z } from 'zod';

const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)[A-Za-z\d]{8,}$/;

export const registerSchema = (t: (key: string) => string) => {
  return z
    .object({
      password: z
        .string()
        .min(1, { message: t('reset.password.input.required') })
        .regex(passwordRegex, t('reset.password.input.error'))
        .refine(
          (value) => {
            const normalizedValue = value.toLowerCase();
            const max2sameConsecutiveCharacters = /(.)\1\1/;
            return !max2sameConsecutiveCharacters.test(normalizedValue);
          },
          {
            message: t('reset.password.input.error'),
          }
        ),
      confirmPassword: z.string().min(1, { message: t('reset.password.input.required') }),
    })
    .refine((data) => data.password === data.confirmPassword, {
      path: ['confirmPassword'],
      message: t('reset.passwords.notMatch'),
    });
};
