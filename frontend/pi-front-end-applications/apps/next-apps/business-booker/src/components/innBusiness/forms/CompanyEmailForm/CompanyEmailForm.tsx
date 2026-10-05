'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { FormInput } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation, cn } from '@whitbread-eos/utils';
import { MutableRefObject } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

type Props = {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  email?: string;
  formRef?: MutableRefObject<HTMLFormElement | null>;
  className?: string;
};

export function CompanyEmailForm({ onSubmit, icons, email, formRef, className }: Props) {
  const { t } = useTranslation('users');

  const schema = z.object({
    email: z.string().email({ message: t('userMgmt.employee.edit.error.emailFormat') }),
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      email: email ?? '',
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} ref={formRef as MutableRefObject<HTMLFormElement>}>
      <div data-testid="CompanyEmail-Form" className={cn(containerStyle, className)}>
        <Controller
          name="email"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="CompanyEmail"
              placeholder={`${t('userMgmt.employee.invite.email')} *`}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('email');
              }}
            />
          )}
        />
      </div>
    </form>
  );
}

const containerStyle = 'flex flex-col justify-between mt-2 mobile:mt-4 gap-4 w-full';
