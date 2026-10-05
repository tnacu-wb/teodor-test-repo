'use client';

import { SanitizedContent, Notification } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';

interface Props {
  icons: Record<string, string>;
}

export function DataPolicySection({ icons }: Props) {
  const { t } = useTranslation('profile');

  return (
    <div data-testid="Data-Policy-Container" className={containerStyle}>
      <div className={linksStyle}>
        <a className={linkStyle} href={t('privacy.link.cookieNoticeLink_BB')}>
          {t('privacy.link.cookieNotice')}
        </a>
        <a className={linkStyle} href={t('privacy.link.privacyPolicyLink_BB')}>
          {t('privacy.link.privacyPolicy')}
        </a>
      </div>
      <Notification
        title={t('privacy.message.title')}
        message={<SanitizedContent>{t('privacy.message.body')}</SanitizedContent>}
        type="info"
        icon={formatIBAssetsUrl(icons['icon.notification.info'])}
        className="border-lightGrey4 bg-transparent"
      />
    </div>
  );
}

const containerStyle = 'flex flex-col max-w-[620px]';
const linksStyle = 'flex gap-4 text-sm mb-4';
const linkStyle = 'underline text-secondaryColor';
