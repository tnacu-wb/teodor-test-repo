'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { FormInput } from '@whitbread-eos/atoms/ui';
import { analytics } from '@whitbread-eos/utils';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

type Props = {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  formRef: MutableRefObject<HTMLFormElement | null>;
  onDirtyChange?: (isDirty: boolean) => void;
};

export function ChangePasswordForm({ onSubmit, icons, formRef, onDirtyChange }: Props) {
  const { t } = useTranslation('profile');
  const passwordRegex2 = /^(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;

  const passwordSchema = z
    .string()
    .min(8, { message: t('password.error.invalid') })
    .regex(passwordRegex2, { message: t('password.error.invalid') })
    .refine(
      (value) => {
        const normalizedValue = value.toLowerCase();
        const max2sameConsecutiveCharacters = /(.)\1\1/;
        return !max2sameConsecutiveCharacters.test(normalizedValue);
      },
      {
        message: t('password.error.invalid'),
      }
    );

  const schema = z
    .object({
      currentPassword: z.string().min(1, { message: t('password.error.empty') }),
      newPassword: passwordSchema,
      confirmPassword: z.string(),
    })
    .superRefine((data, ctx) => {
      if (data.newPassword !== data.confirmPassword) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: t('password.error.different'),
          path: ['confirmPassword'],
        });
      }
      if (data.currentPassword === data.newPassword) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: t('password.error.same'),
          path: ['newPassword'],
        });
      }
      if (data.currentPassword === data.confirmPassword) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: t('password.error.same'),
          path: ['confirmPassword'],
        });
      }
    });

  const {
    control,
    handleSubmit,
    formState: { errors, isDirty },
    trigger,
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
  });

  useEffect(() => {
    onDirtyChange?.(isDirty);
  }, [isDirty]);

  const updateAnalytics = () => {
    const passwordErrors = Object.values(errors).map((error) => error?.message);
    const currentValidation = window?.analyticsData?.validation ?? '';
    const registrationQuestionsValidation =
      currentValidation.split(';').length > 1
        ? currentValidation.split(';')[1]
        : currentValidation.split(';')[0];

    analytics.update({
      validation:
        passwordErrors.length > 0
          ? `password: ${passwordErrors.join(', ')}; ${registrationQuestionsValidation}`.trim()
          : registrationQuestionsValidation.trim(),
    });
  };

  updateAnalytics();

  const renderComponent = () => {
    return (
      <div data-testid="ChangePassword-form" className={containerStyle}>
        <Controller
          name="currentPassword"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="currentPassword"
              type="password"
              placeholder={t('password.currentpassword')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('currentPassword');
              }}
            />
          )}
        />
        <Controller
          name="newPassword"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="newPassword"
              type="password"
              placeholder={t('password.newpassword')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('newPassword');
              }}
            />
          )}
        />
        <Controller
          name="confirmPassword"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="confirmPassword"
              type="password"
              placeholder={t('password.confirmpassword')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('confirmPassword');
              }}
            />
          )}
        />
        <div className={listWrapper}>
          <ul className={listStyle}>
            <li>{t('password.requirements.min')}</li>
            <li>{t('password.requirements.number')}</li>
            <li>{t('password.requirements.capital')}</li>
            <li>{t('password.requirements.special')}</li>
          </ul>
        </div>
      </div>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} ref={formRef as MutableRefObject<HTMLFormElement>}>
      {renderComponent()}
    </form>
  );
}

const containerStyle =
  'flex flex-col justify-between mt-4 mb-6 mobile:mt-4 gap-4 w-1/2 mobile:w-full';
const listStyle = 'list-disc list-inside space-y-2';
const listWrapper = 'p-4 mt-2';
