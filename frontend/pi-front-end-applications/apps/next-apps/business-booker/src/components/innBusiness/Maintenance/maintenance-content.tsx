'use client';

import { LOCALES } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, getPathForLocale, useTranslation, cn } from '@whitbread-eos/utils';
import Image from 'next/image';
import Link from 'next/link';
import { useState } from 'react';

type MaintenanceContentProps = {
  locale: LOCALES;
  icons?: Record<string, string>;
};

export function MaintenanceContent({ locale, icons }: MaintenanceContentProps) {
  const [isButtonClicked, setIsButtonClicked] = useState(false);
  const { t } = useTranslation(['payApplication', 'common']);
  const baseDataTestId = 'Maintenance';

  return (
    <div className={pageStyle}>
      <header className={headerStyle} data-testid={`${baseDataTestId}-header`}>
        <Link
          href={getPathForLocale(locale, 'homepage')}
          className={logoStyle}
          data-testid={`${baseDataTestId}-IB-logo-link`}
        >
          <Image
            src={formatIBAssetsUrl(t('content.header.image'))}
            alt={t('content.header.imageAlt')}
            width={160}
            height={48}
            data-testid={`${baseDataTestId}-IB-logo`}
          />
        </Link>
      </header>
      <div className={maintenanceSectionStyle}>
        <div className={containerStyle}>
          <>
            <Image
              alt={'Alert icon'}
              src={formatIBAssetsUrl(icons?.['icon.alert.message'] || '')}
              width={32}
              height={32}
              data-testid={`${baseDataTestId}-alert-icon`}
            />
            <span className={primaryTextStyle}>{t('application.sent.failed.message')}</span>
            <span className={secondaryTextStyle}>{t('application.sent.error')}</span>
          </>
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
    </div>
  );
}
const pageStyle = 'flex flex-col h-lvh';
const headerStyle =
  'top-0 flex flex-col justify-center px-[4.125rem] py-4 border-b border-lightGrey3 h-headerHeight w-full bg-background mobile:h-auto mobile:px-4 z-40';
const logoStyle = 'w-40 h-12 cursor-pointer';
const maintenanceSectionStyle = 'bg-lightGrey5 flex-[1]';
const containerStyle =
  'w-[420px] mobile:w-full mobile:px-4 mt-12 mx-auto flex flex-col items-start justify-center text-base font-normal';
const primaryTextStyle =
  'text-secondaryColor text-[2.5rem] font-black my-4 leading-[110%] ib-word-break';
const secondaryTextStyle = 'text-darkGrey1 text-[1rem] font-normal mb-12 leading-[1.5rem]';
const linkStyle = 'w-full';
const buttonStyle = 'w-full text-lg font-semibold';
