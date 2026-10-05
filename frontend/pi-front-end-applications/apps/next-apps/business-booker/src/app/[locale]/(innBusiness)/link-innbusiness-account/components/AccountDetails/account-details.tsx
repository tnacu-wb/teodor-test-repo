'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { Language, LOCALES } from '@whitbread-eos/api';
import {
  FormPage,
  FormInput,
  Button,
  Notification,
  useToast,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import {
  getAuthCookie,
  formatAccountNumber,
  formatIBAssetsUrl,
  getPathForLocale,
  useTranslation,
} from '@whitbread-eos/utils';
import { businessTether } from '@whitbread-eos/utils/server';
import { useRouter, useSearchParams } from 'next/navigation';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import {
  MemorableWordInput,
  getMemorableWordSchema,
} from '~components/innBusiness/forms/MemorableWordInput/MemorableWordInput';

type Props = {
  icons: Record<string, string>;
  baseDataTestId: string;
  language?: Language;
  token?: string;
  locale: string;
};

const AccountDetails = ({ baseDataTestId, icons, locale }: Props) => {
  const router = useRouter();
  const params = useSearchParams();
  const linkCode = params?.get('linkCode') || '';
  const linkCodeRegex = /^[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}$/;
  const token = getAuthCookie();
  const spendingUrl = getPathForLocale(locale as LOCALES, 'spending?tab=innbusiness-pay');
  const { t: spending_t } = useTranslation('spending');
  const { t } = useTranslation('auth');
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();
  const message = (
    <SanitizedContent replacements={{ '<a>': `<a href="${spendingUrl}">` }}>
      {t('auth.linkIbPayAccount.error.invalidLinkCode')}
    </SanitizedContent>
  );

  const memorableWordSchema = getMemorableWordSchema(
    spending_t('spending.memorable.word.validation')
  );
  const schema = z.object({
    LinkCode: z.string(),
    CardNumber: z.string().regex(/^(\d{16}|\d{19})$/, {
      message: t('auth.linkIbPayAccount.error.invalidAccountNumber'),
    }),
    memorableWord: memorableWordSchema,
  });

  const {
    control,
    formState: { errors },
    trigger,
    clearErrors,
    handleSubmit,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      LinkCode: linkCode,
      CardNumber: '',
      memorableWord: '',
    },
  });

  const handleErrorCase = () => {
    router.push(getPathForLocale(locale, 'homepage'));
    toast({
      content: t('auth.linkIbPayAccount.error.linkFailed'),
      variant: 'error',
    });
  };

  const handleLinkAccountSubmit = async (data: z.infer<typeof schema>) => {
    setIsLoading(true);
    const saveInCdh = true;
    try {
      const response = await businessTether(
        data?.LinkCode,
        data?.CardNumber,
        data?.memorableWord,
        saveInCdh,
        token
      );
      setIsLoading(false);

      if (response !== null) {
        router.push(spendingUrl);
        const content = (
          <span>
            <SanitizedContent
              replacements={{
                accountNum: `<strong>${formatAccountNumber(data?.CardNumber)}</strong>`,
              }}
            >
              {t('auth.linkIbPayAccount.success')}
            </SanitizedContent>
          </span>
        );
        toast({
          content: content,
          icon: formatIBAssetsUrl(icons['icon.notification.success']),
        });
      } else {
        handleErrorCase();
      }
    } catch {
      handleErrorCase();
    }
  };

  return (
    <FormPage
      baseDataTestId={baseDataTestId}
      backIcon={icons ? formatIBAssetsUrl(icons['icon.arrow.left.purple']) : ''}
      iconClassName="px-2 py-3 max-w-auto"
      backHref={getPathForLocale(locale, 'homepage')}
      title={t('auth.linkIbPayAccount.title')}
      isCentered={false}
    >
      <p className={'text-neutral-900 pb-[3rem] pt-[0.6rem]'}>
        {t('auth.linkIbPayAccount.description')}
      </p>

      <form
        className="flex flex-col gap-6 max-w-[26.25rem] mobile:max-w-[100%]"
        onSubmit={handleSubmit(handleLinkAccountSubmit)}
      >
        {!linkCodeRegex.test(linkCode) && (
          <Notification
            className={'mb-4 max-w-[100%]'}
            type="error"
            icon={formatIBAssetsUrl(icons['icon.notification.error'])}
            title={''}
            message={message}
          />
        )}
        <Controller
          name="LinkCode"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id="LinkCode"
              type={'text'}
              placeholder={t('auth.linkIbPayAccount.linkCode.label')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('LinkCode');
              }}
              disabled={true}
            />
          )}
        />
        <Controller
          name="CardNumber"
          control={control}
          render={({ field }) => (
            <>
              <FormInput
                {...field}
                id="CardNumber"
                type={'text'}
                placeholder={t('auth.linkIbPayAccount.accountNumber.label')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger('CardNumber');
                }}
              />
              <p className={'text-neutral-900 text-xs mt-[-1rem]'}>
                {t('auth.linkIbPayAccount.accountNumber.hint')}
              </p>
            </>
          )}
        />
        <div>
          <h2 className={h2Style}>{t('auth.linkIbPayAccount.memorableWord.title')}</h2>
          <p className={'text-neutral-900'}>
            {t('auth.linkIbPayAccount.memorableWord.description')}
          </p>
        </div>
        <MemorableWordInput
          name="memorableWord"
          icons={icons}
          control={control}
          errors={errors}
          trigger={trigger}
          clearErrors={clearErrors}
        />
        <Button
          data-testid={`${baseDataTestId}-Button`}
          type="submit"
          disabled={isLoading}
          variant="default"
        >
          {t('auth.linkIbPayAccount.continueButton')}
        </Button>
      </form>
    </FormPage>
  );
};

export default AccountDetails;
const h2Style = 'text-xl leading-[1.5rem] font-bold mb-2';
