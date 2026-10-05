'use client';

import { LOCALES } from '@whitbread-eos/api';
import { Alert, AlertTitle, AlertDescription } from '@whitbread-eos/atoms/ui';
import { getPathForLocale } from '@whitbread-eos/utils';
import { TriangleAlert } from 'lucide-react';
import Link from 'next/link';
import { useState } from 'react';

type Props = {
  locale: LOCALES;
  title: string;
  subtitle: string;
  linkLabel: string;
  isVisible: boolean;
};

export function ProfileNotification({ locale, title, subtitle, linkLabel, isVisible }: Props) {
  const [isProfileNotificationVisible, setIsProfileNotificationVisible] = useState(isVisible);
  if (!isProfileNotificationVisible) {
    return <></>;
  }

  return (
    <Alert variant="amber" className="mb-4" data-testid="Notifications-ProfileUpdateRequired">
      <button
        className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 text-sm"
        onClick={() => setIsProfileNotificationVisible(false)}
        data-testid="Notifications-ProfileUpdateRequired-CloseButton"
      >
        ✕
      </button>
      <TriangleAlert className="w-4 h-4" />
      <AlertTitle className="text-sm font-semibold">{title}</AlertTitle>
      <AlertDescription>
        {subtitle}{' '}
        <Link
          className={linkStyle}
          href={getPathForLocale(locale.toLowerCase() as LOCALES, `profile`)}
          prefetch={false}
        >
          {linkLabel}
        </Link>
      </AlertDescription>
    </Alert>
  );
}

const linkStyle = 'text-secondaryColor underline';
