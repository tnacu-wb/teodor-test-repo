'use client';

import { LOCALES } from '@whitbread-eos/api';
import { SanitizedContent } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation, getPathForLocale } from '@whitbread-eos/utils';
import Image from 'next/image';

type Props = {
  icons: Record<string, string>;
  locale: LOCALES;
  mobile?: boolean;
};

export function CompanyDeleteInfo({ icons, locale }: Props) {
  const baseDataTestId = 'CompanyDeleteInfo';
  const { t } = useTranslation('company');

  return (
    <div className={tableActionsLeftStyle} data-testid={`${baseDataTestId}-container`}>
      <Image
        alt={'Security icon'}
        src={formatIBAssetsUrl(icons['icon.security'])}
        width={15}
        height={15}
        className={imageStyle}
        data-testid={`${baseDataTestId}-security-icon`}
      />
      <SanitizedContent
        replacements={{ '{contactUsLink}': getPathForLocale(locale, 'contact-us') }}
      >
        {t('coMngt.infoBox.deleteAccount.message')}
      </SanitizedContent>
    </div>
  );
}

const tableActionsLeftStyle = 'flex';
const imageStyle = 'mr-2 h-[15px] mt-1';
