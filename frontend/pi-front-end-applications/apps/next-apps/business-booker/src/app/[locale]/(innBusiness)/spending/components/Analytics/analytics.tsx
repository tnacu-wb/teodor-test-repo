'use client';

import { analytics } from '@whitbread-eos/utils';
import React, { useEffect } from 'react';

type Props = {
  pageName?: string;
  notifications?: {
    isAccountSuspended: boolean;
    hasUpcomingSpending?: boolean;
  };
  validations?: string[];
};

enum AnalyticsNotifications {
  AccountSuspended = 'PIBA Account suspended,PIBA account exceeds limit',
  UpcomingMessage = 'You have other transactions planned this month that have not been charged yet',
}

const Analytics: React.FC<Props> = ({ pageName, notifications, validations }: Props) => {
  useEffect(() => {
    if (pageName === undefined) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const currentValidation = window?.analyticsData?.validation;
    analytics.update({
      ...currentData,
      pageName: pageName,
      innBusiness: {},
    });

    if (currentValidation) {
      analytics.remove(['validation']);
    }
  }, [pageName]);

  useEffect(() => {
    if (notifications === undefined) {
      return;
    }

    const validations = [];
    if (notifications.isAccountSuspended) {
      validations.push(AnalyticsNotifications.AccountSuspended);
    }
    if (notifications?.hasUpcomingSpending) {
      validations.push(AnalyticsNotifications.UpcomingMessage);
    }
    if (validations.length === 0) {
      return;
    }

    window?._satellite?.track('error');

    const currentData = window?.analyticsData ?? {};
    analytics.update({
      ...currentData,
      validation: validations.join(','),
    });
  }, [notifications]);

  useEffect(() => {
    if (!validations || validations.length === 0) {
      return;
    }

    window?._satellite?.track('error');

    const currentData = window?.analyticsData ?? {};
    analytics.update({
      ...currentData,
      validation: validations.join(','),
    });
  }, [validations]);

  return <></>;
};

export default Analytics;
