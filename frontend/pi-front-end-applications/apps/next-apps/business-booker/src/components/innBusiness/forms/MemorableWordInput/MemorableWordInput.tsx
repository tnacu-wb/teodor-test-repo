'use client';

import { FormInputShowHide, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import {
  Control,
  FieldErrors,
  UseFormTrigger,
  UseFormClearErrors,
  FieldValues,
  Controller,
  Path,
} from 'react-hook-form';
import { z } from 'zod';

export type MemorableWordProps<TFormValues extends FieldValues> = {
  name: Path<TFormValues>;
  icons?: Record<string, string>;
  control: Control<TFormValues>;
  errors: FieldErrors<TFormValues>;
  trigger: UseFormTrigger<TFormValues>;
  clearErrors: UseFormClearErrors<TFormValues>;
};

export const getMemorableWordSchema = (wordValidation: string) => {
  const memorableWordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;

  return z
    .string()
    .min(10, { message: wordValidation })
    .max(64, { message: wordValidation })
    .regex(memorableWordRegex, { message: wordValidation })
    .refine(
      (value) => {
        const normalizedValue = value.toLowerCase();
        const max2sameConsecutiveCharacters = /(.)\1\1/;
        return !max2sameConsecutiveCharacters.test(normalizedValue);
      },
      {
        message: wordValidation,
      }
    );
};

export function MemorableWordInput<TFormValues extends FieldValues>(
  props: MemorableWordProps<TFormValues>
) {
  const { name, icons, control, errors, trigger } = props;
  const { t } = useTranslation('spending');
  return (
    <div data-testid="MemorableWordInput-form" className={`${containerStyle}`}>
      <div className="mb-[1.5rem]">
        <h4 data-testid="MemorableWordInput-list-title" className={listTitleStyle}>
          {t('spending.memorable.word.instructions')}
        </h4>
        <div data-testid="MemorableWordInput-list-content" className={listContentStyle}>
          <SanitizedContent>{t('spending.memorable.word.rules')}</SanitizedContent>
        </div>
      </div>
      <Controller
        name={name}
        control={control}
        render={({ field }) => (
          <FormInputShowHide
            {...field}
            id={name}
            type="password"
            placeholder={t(`spending.memorable.word.placeholder`)}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            onBlur={() => {
              trigger(name);
            }}
          />
        )}
      />
    </div>
  );
}

const containerStyle = 'flex flex-col justify-between mobile:w-full';
const listTitleStyle = 'text-base font-bold mb-[.5rem]';
const listContentStyle = 'pl-[1.5rem]';
