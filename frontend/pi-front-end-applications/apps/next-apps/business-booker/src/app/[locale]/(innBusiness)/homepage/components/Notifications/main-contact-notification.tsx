'use client';

import { LOCALES, CompanyDetailsResponse } from '@whitbread-eos/api';
import { Alert, AlertTitle, AlertDescription, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { getPathForLocale, useTranslation } from '@whitbread-eos/utils/server';
import { Info } from 'lucide-react';

type Props = {
  locale: LOCALES;
  companyDetails: CompanyDetailsResponse;
  hasCompanyDetailsAccess: boolean;
};

export function MainContactNotification({
  locale,
  companyDetails,
  hasCompanyDetailsAccess,
}: Props) {
  const { t } = useTranslation(['notifications']);
  const mainEmployee = companyDetails?.requestedCompany?.companyDetails?.mainEmployee;

  const isMissingMainEmployeeFields = !(
    mainEmployee?.title &&
    mainEmployee?.firstName &&
    mainEmployee?.lastName &&
    mainEmployee?.emailAddress &&
    mainEmployee?.address?.addressLine1 &&
    mainEmployee?.address?.postCode &&
    mainEmployee?.address?.country
  );

  if (hasCompanyDetailsAccess && isMissingMainEmployeeFields) {
    return (
      <Alert variant="red" className="mb-4" data-testid="Notifications-IncompleteMainEmployee">
        <Info className="w-4 h-4" />
        <AlertTitle className="text-sm">
          <SanitizedContent>
            {t('notifications.notification.mainContact.update.title')}
          </SanitizedContent>
        </AlertTitle>
        <AlertDescription>
          <SanitizedContent replacements={{ '{link}': getPathForLocale(locale, 'manage/company') }}>
            {t('notifications.notification.mainContact.update.subtitle')}
          </SanitizedContent>
        </AlertDescription>
      </Alert>
    );
  }

  return null;
}
