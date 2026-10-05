'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { LOCALES, RegistrationCodeInfo } from '@whitbread-eos/api';
import { Button, useToast } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage } from '@whitbread-eos/layout';
import { getPathForLocale, useTranslation } from '@whitbread-eos/utils';
import { submitRegistration } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { FormProvider, useForm } from 'react-hook-form';
import { z } from 'zod';

import {
  getMemorableWordSchema,
  MemorableWordInput,
} from '~components/innBusiness/forms/MemorableWordInput/MemorableWordInput';

import { revalidateCacheOnLink } from '../../../../../../(innBusiness)/manage/cards/components/revalidate-link';
import { FormFooter } from '../FormFooter';
import { RegistrationState } from '../types';
import { ContactNumber } from './contact-number';
import { Email } from './email';
import { FullName } from './full-name';

type Props = {
  baseDataTestId: string;
  icons: Record<string, string>;
  locale: LOCALES;
  registrationInfo: RegistrationCodeInfo;
  token: string;
  isOnlyStep?: boolean;
};

export const DetailsForm = ({
  baseDataTestId,
  icons,
  locale,
  registrationInfo,
  token,
  isOnlyStep = false,
}: Props) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { toast } = useToast();
  const router = useRouter();
  const {
    goToPreviousStep,
    wizardState: { prepopulatedItems, authenticationAnswers },
  } = useWizardContext<RegistrationState>();

  const { t } = useTranslation('auth');
  const schema = z.object({
    title: z.string(),
    forename: z.string(),
    surname: z.string(),
    emailAddress: z.string().email(t('payApp.login.email.input.error')),
    landlineNumber: z.string().optional(),
    mobileNumber: z.string().optional(),
    memorableWord: getMemorableWordSchema(t('auth.linkIbPayAccount.error.invalidMemorableWord')),
  });

  type FormValues = z.infer<typeof schema>;
  const methods = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: prepopulatedItems?.title ?? '',
      forename: prepopulatedItems?.forename ?? '',
      surname: prepopulatedItems?.surname ?? '',
      emailAddress: prepopulatedItems?.emailAddress ?? '',
      landlineNumber: prepopulatedItems?.landlineNumber ?? undefined,
      mobileNumber: prepopulatedItems?.mobileNumber ?? undefined,
      memorableWord: '',
    },
  });

  const {
    handleSubmit,
    control,
    formState: { errors },
    trigger,
    clearErrors,
  } = methods;

  const handleSubmitForm = async (data: FormValues) => {
    setIsSubmitting(true);

    const { landlineNumber, mobileNumber, ...rest } = data;
    const userDetails = {
      ...rest,
      ...(landlineNumber ? { landlineNumber } : {}),
      ...(mobileNumber ? { mobileNumber } : {}),
    };

    try {
      const result = await submitRegistration(
        token,
        registrationInfo.registrationCode,
        userDetails,
        authenticationAnswers
      );
      if (result?.tetherDetails?.tetheredUserGuid) {
        const successMessage = t('payApp.login.success');
        toast({
          content: successMessage.replace('{accountNumber}', result.tetherDetails.accountNumber),
        });
        const redirectUrl = getPathForLocale(
          locale,
          `spending?tab=innbusiness-pay&account=${result.tetherDetails.tetheredUserGuid}`
        );
        await revalidateCacheOnLink(redirectUrl);
        router.push(redirectUrl);
      } else {
        toast({
          content: t('payApp.login.error'),
          variant: 'error',
        });
        router.push(getPathForLocale(locale, 'spending?tab=innbusiness-pay'));
      }
    } catch {
      toast({
        content: t('payApp.login.error'),
        variant: 'error',
      });
      router.push(getPathForLocale(locale, 'spending?tab=innbusiness-pay'));
    }
  };

  const handleGoBack = () => {
    if (isOnlyStep) {
      window.location.href = getPathForLocale(locale, 'business-pay/register');
      return;
    }
    goToPreviousStep();
  };

  const fullName = `${prepopulatedItems?.title} ${prepopulatedItems?.forename} ${prepopulatedItems?.surname}`;

  return (
    <WizardPage
      type="form"
      formTitle={t('payApp.login.security.title')}
      data-testid={baseDataTestId}
      showBackButton={true}
      onBackClick={handleGoBack}
    >
      <p className={'text-neutral-900 pt-4 pb-2'}>{t('payApp.login.done')}</p>
      <p className={'text-neutral-900 pt-2 pb-12'}>{t('payApp.login.verify')}</p>
      <FormProvider {...methods}>
        <form
          id={`${baseDataTestId}-Form`}
          onSubmit={handleSubmit(handleSubmitForm)}
          className="flex flex-col gap-6 max-w-[26.25rem] mobile:max-w-[100%] pb-[2.5rem]"
          data-testid={`${baseDataTestId}-Form`}
        >
          <div className={`${formStyle}`}>
            <FullName value={fullName} baseDataTestId={baseDataTestId} />
            <Email value={prepopulatedItems?.emailAddress ?? ''} baseDataTestId={baseDataTestId} />
            <ContactNumber
              value={prepopulatedItems?.landlineNumber ?? ''}
              baseDataTestId={`${baseDataTestId}-LandlineNumber`}
              label={t('payApp.login.landLineNumberr')}
            />
            <ContactNumber
              value={prepopulatedItems?.mobileNumber ?? ''}
              baseDataTestId={`${baseDataTestId}-MobileNumber`}
              label={t('payApp.login.mobileNumber')}
            />
          </div>
          <div className="mb-8">
            <h2 className={h2Style}>{t('auth.linkIbPayAccount.memorableWord.title')}</h2>
            <p className={pStyle}>{t('auth.linkIbPayAccount.memorableWord.description')}</p>
            <MemorableWordInput
              name="memorableWord"
              icons={icons}
              control={control}
              errors={errors}
              trigger={trigger}
              clearErrors={clearErrors}
            />
          </div>
          <Button
            type="submit"
            data-testid={`${baseDataTestId}-Button`}
            variant="saveUpdatesButton"
            className="min-w-[18rem] mobile:w-full"
            disabled={isSubmitting}
          >
            {t('payApp.submit.registration')}
          </Button>
        </form>
      </FormProvider>
      <FormFooter locale={locale} showContact={false} />
    </WizardPage>
  );
};

const formStyle = 'mb-6 p-6 border border-lightGrey3 bg-white rounded-lg';
const h2Style = 'text-xl leading-[1.5rem] font-bold mb-2';
const pStyle = 'text-neutral-900 mb-6';
