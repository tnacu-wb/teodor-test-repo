'use client';

import { LOCALES } from '@whitbread-eos/api';
import { SanitizedContent, Button } from '@whitbread-eos/atoms/ui';
import { useWizardContext } from '@whitbread-eos/layout';
import {
  getPathForLocale,
  useTranslation,
  getLocaleByPathname,
  formatIBAssetsUrl,
  cn,
} from '@whitbread-eos/utils';
import Image from 'next/image';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState } from 'react';

import { PayApplicationState } from '../types';
import { AppDetailsCard } from './../../../../../(innBusiness)/business-pay/components/AppDetailsCard';

export const getContactInfoEmailCaptionKey = (locale = LOCALES.EN): string => {
  return locale.toLowerCase() === LOCALES.DE
    ? 'payApp.innBusiness.contactEmail.germany'
    : 'payApp.innBusiness.contactEmail.uk';
};

const ApplicationSent = () => {
  const [isButtonClicked, setIsButtonClicked] = useState(false);
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation('payApplication');
  const { wizardState, icons } = useWizardContext<PayApplicationState>();
  const { accountName, applicationId } = wizardState;
  const startedBy = wizardState.contactDetails?.email ?? '';

  const baseDataTestId = 'AppSuccessfullySubmitted';
  const contactInfoEmail = t(getContactInfoEmailCaptionKey(locale));

  return (
    <div className={containerStyle} data-testid="AppSuccessfullySubmitted-container">
      <div className={`${wrapperContainerStyle} gap-4`}>
        <Image
          data-testid="CheckIcon"
          className={''}
          src={formatIBAssetsUrl(icons?.['icon.checkmark-white-purple'])}
          width={32}
          alt="Check Icon"
          height={32}
        />
        <h1 className={h1Style} data-testid={`${baseDataTestId}-title`}>
          {t('application.sent.submit')}
        </h1>
        <p className="text-lg">{t('application.sent.contact.timeline')}</p>
      </div>

      <div className={`${wrapperContainerStyle} gap-12 mt-12`}>
        <div className={appDetailsContainerStyle}>
          <AppDetailsCard
            baseDataTestId={baseDataTestId}
            companyName={accountName}
            applicationReference={applicationId}
            startedBy={startedBy}
          />
        </div>

        <p>
          <SanitizedContent
            replacements={{
              '{contactUsLink}': getPathForLocale(locale, 'contact-us'),
              '{email}': `<a class="font-semibold" href="mailto:${contactInfoEmail}">${contactInfoEmail}</a>`,
            }}
          >
            {t('application.sent.getInTouch')}
          </SanitizedContent>
        </p>

        <Link
          href={getPathForLocale(locale, 'homepage')}
          className={cn(linkStyle, isButtonClicked && 'cursor-default')}
          data-testid={`${baseDataTestId}-back-to-home-link`}
        >
          <Button
            data-testid={`${baseDataTestId}-back-to-home-button`}
            variant="dialogDefault"
            className={buttonStyle}
            onClick={() => setIsButtonClicked(true)}
            disabled={isButtonClicked}
          >
            {t('payapp.backToHome')}
          </Button>
        </Link>
      </div>
    </div>
  );
};

ApplicationSent.displayName = 'AppplicationSent';
export default ApplicationSent;

const wrapperContainerStyle = 'mobile:w-full flex flex-col';
const appDetailsContainerStyle = 'p-6 border border-lightGrey3 bg-white rounded-lg';
const containerStyle =
  'w-[420px] mobile:w-full mobile:px-4 mt-12 mx-auto flex flex-col items-start justify-center text-base font-normal';
const buttonStyle = 'w-full text-lg font-semibold';
const h1Style =
  'flex text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';
const linkStyle = 'w-full';
