'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { FormInput } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

type Props = {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  companyName: string;
  isAddressType?: boolean;
  variant?: string;
  formRef: MutableRefObject<HTMLFormElement | null>;
  onCompanyNameChange?: (companyName: string) => void; // Optional handler for field change
};

export function CompanyName({
  onSubmit,
  icons,
  companyName,
  formRef,
  variant,
  onCompanyNameChange,
}: Props) {
  const { t } = useTranslation(['company', 'profile', 'payApplication']);

  const errorMessage =
    variant === 'payApp'
      ? t('payApplication.payapp.memorable.error')
      : t('company.coMngt.companyInfo.company.name.required');

  const schema = z.object({
    companyName: z
      .string()
      .min(1, {
        message: errorMessage,
      })
      .max(100, { message: errorMessage })
      .regex(
        RegExp(
          /^[a-zÀÁÂÃÄÅĀẶĄẮÆǼẞÇĆĈĊČĎĐÈÉÊËĒĔĖĚĜĞĠĢĤĦÌÍÎÏĨĪǏĮİĴĶĹĻĽĿŁÑŃŅŇȠÒÓÔÕÖØŌǑŐǾŒṘŖŘŚŜṢŠŢŤŦÙÚÛÜŨŪǓŮŰŲŴẂẀẄỲÝŸŶŹŻŽàáâãäåāặąắæǽßçćĉċčďđèéêëēĕėěĝğġģĥħìíîïĩīǐįĵķĺļľŀłñńņňƞòóôõöøōǒőǿœṙŗřśŝṣšţťŧùúûüũūǔůűųŵẃẁẅỳýÿŷźżž\u005B\u005D\u005C\u002F\u00AB\u00BB\u2018.,:;_!?"“”’*%=+£$€¥&@#()\-'\d{}>< ]+$/gi
        ),
        {
          message: errorMessage,
        }
      ),
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      companyName: companyName ?? '',
    },
  });

  const renderCompanyNameInput = () => {
    return (
      <div data-testid="CompanyName-form" className={containerStyle}>
        <Controller
          name="companyName"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="Company-name"
              placeholder={t('company.coMngt.companyInfo.company.name') + ' *'}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('companyName');
              }}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                field.onChange(e);
                onCompanyNameChange?.(e.target.value);
              }}
            />
          )}
        />
      </div>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} ref={formRef as MutableRefObject<HTMLFormElement>}>
      {renderCompanyNameInput()}
    </form>
  );
}

const containerStyle = 'flex flex-col justify-between mt-2 mobile:mt-4 gap-4 w-full';
