'use client';

import { analytics } from '@whitbread-eos/utils';
import React, { useEffect } from 'react';

type Props = {
  pageName?: string;
  stays?: number;
  bookings?: number;
  applications?: number;
  notifications?: {
    isAccountSuspended: boolean;
    isAccountClosed: boolean;
    profileUpdateRequired: boolean;
    hasAdHocNotification: boolean;
    employeeRequests?: number;
  };
};

enum AnalyticsNotifications {
  AccountSuspended = 'PIBA Account suspended,PIBA account exceeds limit',
  AccountClosed = 'PIBA Account closed',
  ProfileUpdateRequired = 'BB Profile questions',
  EmployeeRequests = 'Employee requests',
  AdHocNotification = 'Ad hoc BB notification',
}

const Analytics: React.FC<Props> = ({
  pageName,
  stays,
  bookings,
  applications,
  notifications,
}: Props) => {
  useEffect(() => {
    if (stays === undefined || bookings === undefined) {
      return;
    }

    const currentData = window?.analyticsData?.innBusiness ?? {};
    analytics.update({
      innBusiness: {
        ...currentData,
        bookings: bookings,
        stays: stays,
      },
    });
  }, [bookings, stays]);

  useEffect(() => {
    if (applications === undefined) {
      return;
    }

    const currentData = window?.analyticsData?.innBusiness ?? {};
    analytics.update({
      innBusiness: {
        ...currentData,
        applications: applications,
      },
    });
  }, [applications]);

  useEffect(() => {
    if (notifications === undefined) {
      return;
    }

    const validations = [];
    if (notifications.isAccountSuspended) {
      validations.push(AnalyticsNotifications.AccountSuspended);
    }
    if (notifications.isAccountClosed) {
      validations.push(AnalyticsNotifications.AccountClosed);
    }
    if (notifications.profileUpdateRequired) {
      validations.push(AnalyticsNotifications.ProfileUpdateRequired);
    }
    if (notifications.hasAdHocNotification) {
      validations.push(AnalyticsNotifications.AdHocNotification);
    }
    if (!!notifications?.employeeRequests) {
      validations.push(
        `${notifications?.employeeRequests} ${AnalyticsNotifications.EmployeeRequests}`
      );
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
  }, [applications]);

  useEffect(() => {
    if (pageName === undefined) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const currentValidation = window?.analyticsData?.validation;
    analytics.update({
      ...currentData,
      pageName: pageName,
    });

    if (currentValidation) {
      analytics.remove(['validation']);
    }
  }, [pageName]);

  return <></>;
};

export default Analytics;
