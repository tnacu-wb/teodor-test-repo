import { CustomerAccountDetails, LOCALES, PayAccountStatus } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import { getAccountInfo, getAccountRegistrationRoleDetails } from '@whitbread-eos/utils/server';
import React from 'react';

import SuspendedNotification from '~components/innBusiness/SuspendedNotification';

import Analytics from '../Analytics/analytics';

export interface SuspendedNotificationsWrapperProps {
  locale: LOCALES;
  account: CustomerAccountDetails;
  isAccountSuspended?: boolean;
}

export const getAccountSuspensionStatus = async (account: CustomerAccountDetails | null) => {
  const { isOnlyCardHolder } = getAccountRegistrationRoleDetails(account);
  if (!account || isOnlyCardHolder) {
    return false;
  }

  const accountInfo = await getAccountInfo(account.scheme, account.tetheredGuid);

  return [PayAccountStatus.Suspended, PayAccountStatus.SuspendedHold].includes(
    (accountInfo?.status?.toLowerCase() as PayAccountStatus) ?? ''
  );
};

export default async function SuspendedNotificationsWrapper({
  account,
  locale,
  isAccountSuspended,
}: SuspendedNotificationsWrapperProps) {
  const resolvedSuspension =
    typeof isAccountSuspended === 'boolean'
      ? isAccountSuspended
      : await getAccountSuspensionStatus(account);

  return (
    <>
      <SuspendedNotification account={account} locale={locale} isShown={resolvedSuspension} />
      <Analytics isSuspended={resolvedSuspension} />
    </>
  );
}

export function SuspendedNotificationsWrapperSkeleton() {
  return <Skeleton className="h-[6.125rem] w-full" />;
}
