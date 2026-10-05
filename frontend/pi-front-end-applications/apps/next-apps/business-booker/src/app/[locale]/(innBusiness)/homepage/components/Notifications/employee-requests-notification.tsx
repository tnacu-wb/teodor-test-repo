'use client';

import { LOCALES } from '@whitbread-eos/api';
import { Alert, AlertDescription, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { getPathForLocale, useTranslation } from '@whitbread-eos/utils';
import { TriangleAlert } from 'lucide-react';
import Link from 'next/link';
import { useState } from 'react';

type Props = {
  locale: LOCALES;
  count: number;
};

export function EmployeeRequestsNotification({ locale, count }: Props) {
  const { t } = useTranslation(['notifications']);
  const [isVisible, setIsVisible] = useState(true);
  if (!isVisible) {
    return <></>;
  }

  const description = t('notifications.notification.employeeRequests.subtitle').replace(
    '{count}',
    count
  );

  return (
    <Alert variant="amber" className="mb-4" data-testid="Notifications-EmployeeRequests">
      <button
        className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 text-sm z-10"
        onClick={() => setIsVisible(false)}
        data-testid="Notifications-EmployeeRequests-CloseButton"
      >
        ✕
      </button>
      <TriangleAlert className="w-4 h-4" />
      <AlertDescription>
        <SanitizedContent>{description}</SanitizedContent>{' '}
        <Link
          className={linkStyle}
          href={getPathForLocale(locale.toLowerCase() as LOCALES, `manage/employees`)}
          prefetch={false}
        >
          {t('notifications.notification.employeeRequests.manage.link')}
        </Link>
      </AlertDescription>
    </Alert>
  );
}

const linkStyle = 'text-secondaryColor underline';
