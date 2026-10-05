'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { LOCALES } from '@whitbread-eos/api';
import { Button, FormInput, FormPage } from '@whitbread-eos/atoms/ui';
import { getPathForLocale, formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { getRegistrationInfo } from '@whitbread-eos/utils/server';
import { Loader2 } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useState, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

type Props = {
  locale: LOCALES;
  baseDataTestId: string;
  icons: Record<string, string>;
  token: string;
};

export const RegistrationCodeForm = ({ locale, baseDataTestId, icons, token }: Props) => {
  const router = useRouter();
  const { t } = useTranslation(['auth']);
  const [isPageLoaded, setIsPageLoaded] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    setIsPageLoaded(true);
  }, []);

  const schema = z.object({
    registrationCode: z
      .string()
      .min(10, { message: t('auth.payApp.code.validation.error') })
      .max(64, { message: t('auth.payApp.code.validation.error') })
      .regex(/^[A-Z\d]{4}-[A-Z\d]{4}-[A-Z\d]{4}-[A-Z\d]{4}$/, {
        message: t('auth.payApp.code.validation.error'),
      }),
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    setError,
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      registrationCode: '',
    },
  });

  const checkRegistrationCode = async (data: Record<string, string>) => {
    if (!data.registrationCode) {
      return;
    }

    setIsLoading(true);

    try {
      const result = await getRegistrationInfo(token, data.registrationCode);
      if (!result?.registrationCodeInfo?.registrationRole) {
        setError('registrationCode', {
          type: 'manual',
          message: t('auth.payApp.code.general.error'),
        });
        setIsLoading(false);
        return;
      }

      router.push(getPathForLocale(locale, `business-pay/register/${data.registrationCode}`));
    } catch {
      setError('registrationCode', {
        type: 'manual',
        message: t('auth.payApp.code.general.error'),
      });
      setIsLoading(false);
    }
  };

  return (
    <FormPage
      baseDataTestId={baseDataTestId}
      iconClassName="px-2 py-3 max-w-auto"
      title={t('auth.payApp.application.newAccount.register')}
      isCentered={false}
    >
      <p data-testid={`${baseDataTestId}-description`} className={descriptionStyle}>
        {t('auth.payApp.application.details')}
      </p>
      <ul data-testid={`${baseDataTestId}-description-list`} className="pt-2 list-disc pl-6">
        <li>
          <p>{t('auth.payApp.application.details.info1')}</p>
        </li>
        <li>
          <p>{t('auth.payApp.application.details.info2')}</p>
        </li>
      </ul>
      <h4
        data-testid={`${baseDataTestId}-registration-code-form-title`}
        className={whereTitleStyle}
      >
        {t('auth.payApp.application.registrationCode')}
      </h4>
      <form
        onSubmit={handleSubmit(checkRegistrationCode)}
        data-testid={`${baseDataTestId}-form`}
        className="flex flex-col gap-6 max-w-[26.25rem] mobile:max-w-[100%] mt-4"
      >
        <div>
          <Controller
            name={'registrationCode'}
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                id={'registrationCode'}
                type="text"
                placeholder={t('auth.payApp.application.registrationCode.example')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger('registrationCode');
                }}
              />
            )}
          />
          <p className={infoTextStyle}>{t('auth.payApp.application.registrationCode.format')}</p>
        </div>
        <Button
          data-testid={`${baseDataTestId}-Submit-Button`}
          variant="default"
          disabled={isLoading || !isPageLoaded}
          type="submit"
        >
          {t('auth.payApp.application.register')}
          <Loader2 className={`ml-2 w-4 h-4 ${isLoading ? 'animate-spin' : 'hidden'}`} />
        </Button>
      </form>
      <h4 data-testid={`${baseDataTestId}-registration-code`} className={whereTitleStyle}>
        {t('auth.payApp.application.registerCode.find')}
      </h4>
      <p data-testid={`${baseDataTestId}-registration-code-subtitle`} className={descriptionStyle}>
        {t('auth.payApp.application.registerCode.details')}
      </p>
    </FormPage>
  );
};

const infoTextStyle = 'text-xs text-lightGrey9 pl-4 mt-2';
const descriptionStyle = 'font-normal pt-[0.6rem] text-base';
const whereTitleStyle = 'text-xl font-bold mt-8';
