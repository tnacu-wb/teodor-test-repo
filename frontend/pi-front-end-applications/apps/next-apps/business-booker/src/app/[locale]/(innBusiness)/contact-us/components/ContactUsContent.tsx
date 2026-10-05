'use client';

import { CountryCode, FT_IB_PAY_PIBA_EURO } from '@whitbread-eos/api';
import {
  useFeatureToggle,
  useTranslation,
  formatIBAssetsUrl,
  getLocaleByPathname,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils';
import { usePathname } from 'next/navigation';

import { Card } from './Card';

type Props = {
  icons?: Record<string, string>;
  isContactUsLiveChatCardEnabled?: boolean;
};

export function ContactUsContent({ icons, isContactUsLiveChatCardEnabled }: Props) {
  const baseDataTestId = 'ContactUsPage';
  const { t } = useTranslation(['contact']);
  const { [FT_IB_PAY_PIBA_EURO]: isPibaEuroEnabled } = useFeatureToggle();
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { language } = getCountryLanguageByLocale(locale);
  const isDeLanguage = language === CountryCode.DE;

  const hideInnBusinessPay = !isPibaEuroEnabled && isDeLanguage;
  return (
    <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
      <h1 data-testid={`${baseDataTestId}-container-title`} className={h1Style}>
        {t('contact.contactUs.title')}
      </h1>
      <div data-testid={`${baseDataTestId}-container-subtitle`} className={subtitleStyle}>
        {t('contact.contactUs.subtitle')}
      </div>
      <div>
        <div data-testid={`${baseDataTestId}-bookings-container`} className={sectionStyle}>
          <h2 className={sectionTitleStyle}>{t('contact.contactUs.general.heading')}</h2>
          <span className={sectionSubtitleStyle}>{t('contact.contactUs.general.subtitle')}</span>
          <div
            data-testid={`${baseDataTestId}-general-contact-cards`}
            className={cardsSectionStyle}
          >
            {isContactUsLiveChatCardEnabled && (
              <Card
                title={t('contact.contactUs.general.liveChat.title')}
                description={t('contact.contactUs.general.liveChat.description')}
                info={t('contact.contactUs.general.liveChat.title')}
                testId={`${baseDataTestId}-livechat-card`}
                link="#"
                isButton={true}
                onClick={() => {
                  const liveChatElement = document.querySelector('.LPMcontainer');
                  if (liveChatElement) {
                    // click on the live chat element to open the chat window
                    (liveChatElement as HTMLElement).click();
                  }
                }}
              />
            )}
            <Card
              title={t('contact.contactUs.general.email.title')}
              description={t('contact.contactUs.general.email.description')}
              info={t('contact.contactUs.general.email.address')}
              imageProps={{
                src: formatIBAssetsUrl(icons?.['icon.email.icon']),
                alt: 'Email icon',
                testId: `${baseDataTestId}-email-icon`,
              }}
              testId={`${baseDataTestId}-email-card`}
              isEmail={true}
            />
          </div>
        </div>
        {!hideInnBusinessPay && (
          <div data-testid={`${baseDataTestId}-innBusiness-pay-container`} className={sectionStyle}>
            <h2 className={sectionTitleStyle}>{t('contact.contactUs.ibpay.heading')}</h2>
            <span className={sectionSubtitleStyle}>{t('contact.contactUs.ibpay.subtitle')}</span>
            <div
              data-testid={`${baseDataTestId}-innBusiness-pay-cards`}
              className={cardsSectionStyle}
            >
              <Card
                title={t('contact.contactUs.ibpay.phone.title')}
                description={t('contact.contactUs.ibpay.phone.description')}
                info={t('contact.contactUs.ibpay.phone.number')}
                imageProps={{
                  src: formatIBAssetsUrl(icons?.['icon.phone.icon']),
                  alt: 'Phone icon',
                  testId: `${baseDataTestId}-phone-icon`,
                }}
                testId={`${baseDataTestId}-phone-card`}
              />
              <Card
                title={t('contact.contactUs.ibpay.email.title')}
                description={t(t('contact.contactUs.ibpay.email.description'))}
                info={t('contact.contactUs.ibpay.email.address')}
                imageProps={{
                  src: formatIBAssetsUrl(icons?.['icon.email.icon']),
                  alt: 'Email icon',
                  testId: `${baseDataTestId}-email-icon`,
                }}
                testId={`${baseDataTestId}-email-business-account-card`}
                isEmail={true}
                descriptionCustomStyle="mb-6 w-full"
              />
              <Card
                title={t('contact.contactUs.ibpay.address.title')}
                description={t('contact.contactUs.ibpay.address.description')}
                info={t('contact.contactUs.ibpay.address.full')}
                imageProps={{
                  src: formatIBAssetsUrl(icons?.['icon.address.icon.purple']),
                  alt: 'Address icon',
                  testId: `${baseDataTestId}-address-icon`,
                }}
                testId={`${baseDataTestId}-address-card`}
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

const pageStyle =
  'px-12 pt-12 mobile:pt-6 min-w-[700px] mobile:min-w-full mobile:px-4 pb-12 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] mobile:leading-9 text-secondaryColor mobile:text-[1.75rem] max-w-[620px] mobile:max-w-full break-words';
const sectionStyle = 'mt-[3rem]';
const subtitleStyle = 'mt-4 text-darkGrey1 font-normal';
const sectionTitleStyle = 'text-darkGrey1 text-xl font-bold leading-normal';
const sectionSubtitleStyle = 'text-darkGrey1 text-base font-normal leading-normal mb-2';
const cardsSectionStyle = 'grid grid-cols-2 mobile:grid-cols-1 gap-5 w-full mt-[1.5rem]';
