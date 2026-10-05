import { CustomerAccountDetails, LOCALES, PayAccountStatus } from '@whitbread-eos/api';
import {
  Alert,
  AlertTitle,
  AlertDescription,
  SanitizedContent,
  Skeleton,
} from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  formatAccountNumber,
  getNotificationsV2,
  getAccountInfo,
  getDetailsFromToken,
  getAccountRegistrationRoleDetails,
  getCompanyDetails,
} from '@whitbread-eos/utils/server';
import { Info } from 'lucide-react';
import { Suspense } from 'react';

import SuspendedNotification from '~components/innBusiness/SuspendedNotification';

import Analytics from '../Analytics/analytics';
import { EmployeeRequestsNotificationWrapper } from './employee-requests-notification-wrapper';
import { MainContactNotification } from './main-contact-notification';
import { ProfileNotification } from './profile-notification';

type Props = {
  account: CustomerAccountDetails | null;
  locale: LOCALES;
  token: string;
};
const LOG_PAGE_NAME = 'homepage';

export async function Notifications({ account, locale, token }: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  if (!token || token.trim() === '') {
    return <></>;
  }

  const { companyId, employeeId, isTravelManager, isBusinessPayManager } =
    getDetailsFromToken(token);
  const { isOnlyCardHolder } = getAccountRegistrationRoleDetails(account);

  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const getAccountInfoPromise =
    !account || isOnlyCardHolder
      ? Promise.resolve(null)
      : getAccountInfo(account.scheme, account.tetheredGuid);

  const [{ t }, notifications, accountInfo, companyDetails] = await Promise.all([
    getTranslations(language, ['notifications']),
    getNotificationsV2(token),
    getAccountInfoPromise,
    getCompanyDetails(companyId, token, true, logContext),
  ]);

  const isAccountSuspended = [PayAccountStatus.Suspended, PayAccountStatus.SuspendedHold].includes(
    (accountInfo?.status?.toLowerCase() as PayAccountStatus) ?? ''
  );
  const isAccountClosed = accountInfo?.status?.toLowerCase() === PayAccountStatus.Closed;
  const profileUpdateRequired = notifications?.profileUpdateRequired ?? false;

  const closedDescription = t('notifications.notification.account.closed.subtitle')
    .replace('{account_name}', `<strong>${account?.accountName ?? ''}</strong>`)
    .replace(
      '{account_number}',
      `<strong>${formatAccountNumber(account?.accountNumber ?? '')}</strong>`
    );
  const hasAdHocNotification = t('notifications.notification.adhoc.isVisible') === 'true';
  const adHocNotificationType = t('notifications.notification.adhoc.type');
  const hasCompanyDetailsAccess = isTravelManager || isBusinessPayManager;

  return (
    <div data-testid="Notifications" className="flex flex-col">
      <SuspendedNotification account={account} locale={locale} isShown={isAccountSuspended} />
      {isAccountClosed && (
        <Alert variant="red" className="mb-4" data-testid="Notifications-AccountClosed">
          <Info className="w-4 h-4" />
          <AlertTitle className="text-sm font-semibold">
            {t('notifications.notification.account.closed.title')}
          </AlertTitle>
          <AlertDescription>
            <SanitizedContent>{closedDescription}</SanitizedContent>
          </AlertDescription>
        </Alert>
      )}
      {hasAdHocNotification && adHocNotificationType === 'error' && (
        <Alert variant="red" className="mb-4" data-testid="Notifications-AdHocNotification">
          <Info className="w-4 h-4" />
          <AlertTitle className="text-sm font-semibold">
            {t('notifications.notification.adhoc.title')}
          </AlertTitle>
          <AlertDescription>
            <SanitizedContent>{t('notifications.notification.adhoc.subtitle')}</SanitizedContent>
          </AlertDescription>
        </Alert>
      )}
      <MainContactNotification
        locale={locale}
        companyDetails={companyDetails}
        hasCompanyDetailsAccess={hasCompanyDetailsAccess}
      />
      {profileUpdateRequired && (
        <ProfileNotification
          locale={locale}
          title={t('notifications.notification.account.details.update.title')}
          subtitle={t('notifications.notification.contact.preferences.update.subtitle')}
          linkLabel={t('notifications.notification.contact.preferences.update.link')}
          isVisible={profileUpdateRequired}
        />
      )}
      {hasCompanyDetailsAccess && (
        <Suspense fallback={<Skeleton className="h-12 w-full mb-4" />}>
          <EmployeeRequestsNotificationWrapper locale={locale} />
        </Suspense>
      )}
      {hasAdHocNotification && adHocNotificationType === 'alert' && (
        <Alert variant="amber" className="mb-4" data-testid="Notifications-AdHocNotification">
          <Info className="w-4 h-4" />
          <AlertTitle className="text-sm font-semibold">
            {t('notifications.notification.adhoc.title')}
          </AlertTitle>
          <AlertDescription>
            <SanitizedContent>{t('notifications.notification.adhoc.subtitle')}</SanitizedContent>
          </AlertDescription>
        </Alert>
      )}
      {hasAdHocNotification && adHocNotificationType === 'info' && (
        <Alert variant="blue" className="mb-4" data-testid="Notifications-AdHocNotification">
          <Info className="w-4 h-4" />
          <AlertTitle className="text-sm font-semibold">
            {t('notifications.notification.adhoc.title')}
          </AlertTitle>
          <AlertDescription>
            <SanitizedContent>{t('notifications.notification.adhoc.subtitle')}</SanitizedContent>
          </AlertDescription>
        </Alert>
      )}
      <Analytics
        notifications={{
          isAccountClosed,
          isAccountSuspended,
          profileUpdateRequired,
          hasAdHocNotification,
        }}
      />
    </div>
  );
}
