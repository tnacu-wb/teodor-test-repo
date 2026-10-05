'use client';

import { Customer, InitializeApplicationResponse, LOCALES, Scheme } from '@whitbread-eos/api';
import { SanitizedContent, useToast } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage, WizardFooter } from '@whitbread-eos/layout';
import {
  analytics,
  getAuthCookie,
  getPathForLocale,
  useTranslation,
  getLocaleByPathname,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils';
import { initializePayApplication, updateResumeUrl } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';

import useSatelliteTrack from '~hooks/use-satellite-track';

import { Analytics } from '../analytics/analytics';
import { PayApplicationState, PayApplicationStep } from '../types';

type Props = {
  userDetails: Customer;
  isPibaEuroEnabled?: boolean;
};

declare global {
  interface Window {
    _satellite: any;
    __satelliteLoaded: boolean;
  }
}

export function Landing({ userDetails, isPibaEuroEnabled = false }: Props) {
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { wizardState, setWizardState, goToNextStep, icons } =
    useWizardContext<PayApplicationState>();
  const { t } = useTranslation('payApplication');
  const token = getAuthCookie();
  const [requestPending, setRequestPending] = useState(false);
  const { toast } = useToast();
  const scheme = (locale === LOCALES.EN ? 'GB' : 'DE') as Scheme;

  const isDeLanguage = scheme === 'DE';

  const showStartNewApplication = isDeLanguage || isPibaEuroEnabled;
  const satelliteTrack = useSatelliteTrack();

  const handleStartClick = async () => {
    satelliteTrack('startApplication');

    setRequestPending(true);
    const result: InitializeApplicationResponse | null = await initializePayApplication(
      token,
      userDetails.contactDetail?.email,
      scheme
    );

    if (result) {
      await updateResumeUrl(
        token,
        result.applicationId!,
        result.applicationGUID!,
        PayApplicationStep.YOUR_DETAILS
      );

      setWizardState({
        ...wizardState,
        applicationGuid: result.applicationGUID!,
        applicationId: result.applicationId!,
      });

      goToNextStep();
    } else {
      toast({
        content: t('notification.message.error'),
        variant: 'error',
      });

      analytics.update({
        validation: t('notification.message.error'),
      });

      setRequestPending(false);
    }
  };

  useEffect(() => {
    window?._satellite?.track('startNewApplication');
  }, []);

  return (
    <WizardPage
      className={containerStyle}
      footer={
        <WizardFooter
          buttonLabel={t('apply.startApplication')}
          onButtonClick={handleStartClick}
          buttonDisabled={requestPending}
        />
      }
    >
      <div className={topContainerStyle}>
        <Image
          src={formatIBAssetsUrl(t('logo.pi.innBusiness.pay'))}
          width="272"
          height="76"
          alt="logo"
        />
        <div className={titleStyle}>{t('apply.title')}</div>
      </div>
      <div className={middleContainerStyle}>
        <div className={subtitleStyle}>{t('apply.description')}</div>
        <div className={cardsContainerStyle}>
          <div className={cardStyle}>
            <Image
              src={formatIBAssetsUrl(icons['icon.address.icon'])}
              width="48"
              height="48"
              alt="card"
              className={cardImageStyle}
            />
            <div className={cardTitleStyle}>{t('apply.registeredAddress.label')}</div>
            <div className={cardSubtitleStyle}>{t('apply.registeredAddress.description')}</div>
          </div>
          <div className={cardStyle}>
            <Image
              src={formatIBAssetsUrl(icons['icon.info.icon'])}
              width="48"
              height="48"
              alt="card"
              className={cardImageStyle}
            />
            <div className={cardTitleStyle}>{t('apply.companyDetails.label')}</div>
            <div className={cardSubtitleStyle}>{t('apply.companyDetails.description')}</div>
          </div>
          <div className={cardStyle}>
            <Image
              src={formatIBAssetsUrl(icons['icon.employees.icon'])}
              width="48"
              height="48"
              alt="card"
              className={cardImageStyle}
            />
            <div className={cardTitleStyle}>{t('apply.requiredEmployees.label')}</div>
            <div className={cardSubtitleStyle}>
              <SanitizedContent
                replacements={{
                  '{manageEmployeesLink}': getPathForLocale(locale, 'manage/employees'),
                }}
              >
                {t('apply.requiredEmployees.description')}
              </SanitizedContent>
            </div>
          </div>
          <div className={cardStyle}>
            <Image
              src={formatIBAssetsUrl(icons['icon.file.icon'])}
              width="48"
              height="48"
              alt="card"
              className={cardImageStyle}
            />
            <div className={cardTitleStyle}>{t('apply.bankDetails.label')}</div>
            <div className={cardSubtitleStyle}>{t('apply.bankDetails.description')}</div>
          </div>
        </div>
      </div>
      {showStartNewApplication && (
        <div className={bottomContainerStyle} data-testid="landing-bottom-container">
          <div className={bottomTitleStyle}>
            {scheme === 'DE' ? t('apply.germanAccount.label.uk') : t('apply.germanAccount.label')}
          </div>
          <div className={bottomSubtitleStyle}>
            {scheme === 'DE' ? (
              <SanitizedContent
                replacements={{
                  '{applyUkIbPayLink}': getPathForLocale(LOCALES.EN, 'business-pay/apply'),
                }}
              >
                {t('apply.germanAccount.description.uk')}
              </SanitizedContent>
            ) : (
              <SanitizedContent
                replacements={{
                  '{applyGermanIbPayLink}': getPathForLocale(LOCALES.DE, 'business-pay/apply'),
                }}
              >
                {t('apply.germanAccount.description')}
              </SanitizedContent>
            )}
          </div>
        </div>
      )}
      <Analytics pageName="Pay Application: Home" />
    </WizardPage>
  );
}

const containerStyle = 'px-[66px] py-[44px] mobile:px-4 mobile:py-6 flex flex-col gap-12';
const topContainerStyle = 'flex flex-col gap-6 justify-center items-center';
const titleStyle = 'text-[64px] mobile:text-5xl font-black text-secondaryColor';
const middleContainerStyle = 'flex flex-col justify-center items-center';
const subtitleStyle = 'mb-8';
const cardsContainerStyle = 'flex mobile:flex-col gap-6';
const cardStyle =
  'w-1/4 max-w-[309px] mobile:w-full mobile:max-w-full border border-lightGrey3 px-6 pt-4 pb-6 flex flex-col gap-4 bg-white rounded-lg';
const cardImageStyle = '';
const cardTitleStyle = 'text-[23px] font-bold';
const cardSubtitleStyle = '';
const bottomContainerStyle = 'flex flex-col justify-center items-center gap-2';
const bottomTitleStyle = 'text-xl font-bold';
const bottomSubtitleStyle = 'text-center mobile:text-left';
