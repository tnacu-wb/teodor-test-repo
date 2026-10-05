'use client';

import { getPathForLocale, getLocaleByPathname, useTranslation } from '@whitbread-eos/utils';
import Image from 'next/image';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ReactNode } from 'react';

export type Props = {
  logoUrl: string;
  steps?: ReactNode;
  logoRedirectUrl?: string;
};

export function WizardHeader({ logoUrl, steps, logoRedirectUrl }: Props) {
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation();

  return (
    <header className={headerStyle}>
      <div className={headerContainerStyle}>
        <Link
          href={getPathForLocale(locale, logoRedirectUrl ?? 'homepage')}
          data-testid="IB-Logo"
          prefetch={false}
        >
          <Image
            src={logoUrl}
            alt={t('content.header.imageAlt')}
            className={logoStyle}
            width={160}
            height={48}
          />
        </Link>

        {steps}
      </div>
    </header>
  );
}

const logoStyle = 'w-40 h-12 max-w-none';
const headerStyle =
  'fixed mobile:static top-0 flex flex-col justify-center px-[4.125rem] py-4 border-b border-lightGrey3 h-headerHeight w-full bg-background mobile:h-auto mobile:px-4 z-40';
const headerContainerStyle = 'flex items-center justify-between mobile:flex-col mobile:items-start';
