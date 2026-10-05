'use client';

import { LOCALES } from '@whitbread-eos/api';
import { Button, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { WizardPage } from '@whitbread-eos/layout';
import { getPathForLocale, useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useRouter } from 'next/navigation';
import React from 'react';

interface Props {
  locale: LOCALES;
}

export default function AccessRestrictedView({ locale }: Props) {
  const baseDataTestId = 'AccessRestricted';
  const router = useRouter();

  const { t } = useTranslation(['auth', 'icons']);

  const handleGoToLogin = () => {
    router.push(getPathForLocale(locale, 'account/login'));
  };

  const lockIcon = t('icons.icon.application.lock');

  return (
    <WizardPage>
      <div data-testid={`${baseDataTestId}-wrapper`} className={containerStyle}>
        <div className={contentContainerStyle}>
          <Image
            data-testid={`${baseDataTestId}-icon`}
            src={formatIBAssetsUrl(lockIcon)}
            alt={`${t('auth.signup.accessRestricted.title')} icon`}
            width={32}
            height={32}
          />
          <h2 data-testid={`${baseDataTestId}-title`} className={titleStyle}>
            {t('auth.signup.accessRestricted.title')}
          </h2>
          <p data-testid={`${baseDataTestId}-description`}>
            {t('auth.signup.accessRestricted.description')}
          </p>
          <p data-testid={`${baseDataTestId}-contact`}>
            <SanitizedContent>{t('auth.signup.accessRestricted.contact')}</SanitizedContent>
          </p>
        </div>
        <Button
          onClick={handleGoToLogin}
          data-testid={`${baseDataTestId}-login`}
          className="w-full"
        >
          {t('auth.signup.accessRestricted.button')}
        </Button>
      </div>
    </WizardPage>
  );
}

const containerStyle =
  'max-w-[26.25rem] gap-12 flex flex-col mobile:pt-6 mobile:px-4 pt-12 mx-auto';
const contentContainerStyle = 'flex flex-col gap-4';
const titleStyle = 'text-[2.5rem] font-bold text-secondaryColor leading-[110%]';
