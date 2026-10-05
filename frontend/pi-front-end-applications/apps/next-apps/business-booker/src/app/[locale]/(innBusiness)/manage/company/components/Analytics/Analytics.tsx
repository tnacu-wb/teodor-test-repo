'use client';

import { analytics } from '@whitbread-eos/utils';
import { useEffect } from 'react';

type Props = {
  pageName: string;
  companyDetails: any;
};

export function Analytics({ pageName, companyDetails }: Props) {
  useEffect(() => {
    if (window?.analyticsData?.validation) {
      analytics.remove(['validation']);
    }

    const employeeCount = companyDetails?.requestedCompany?.companyDetails?.numberOfEmployees;
    const getEmployeeCategory = (count: number): string => {
      if (count <= 9) return '1-9 Employees';
      if (count <= 49) return '10-49 Employees';
      if (count <= 99) return '50-99 Employees';
      if (count <= 249) return '100-249 Employees';
      if (count <= 499) return '250-499 Employees';
      return '500+ Employees';
    };

    const details = companyDetails?.requestedCompany?.companyDetails;
    const isProfileComplete = !!(
      details?.companyName?.trim() &&
      details?.companyAddress?.addressLine1?.trim() &&
      details?.companyAddress?.postCode?.trim() &&
      details?.mainEmployee?.emailAddress?.trim() &&
      details?.mainEmployee?.firstName?.trim() &&
      details?.mainEmployee?.lastName?.trim()
    );

    const currentData = window?.analyticsData ?? {};
    const existingInnBusiness = currentData.innBusiness ?? {};

    analytics.update({
      ...currentData,
      pageName,
      innBusiness: {
        ...existingInnBusiness,
        manageEmployees: employeeCount,
        numOfEmployees: employeeCount ? getEmployeeCategory(employeeCount) : undefined,
        profileCompletion: isProfileComplete,
        bookings: 0,
        manageCards: 0,
        numOfStays: 0,
      } as any,
    });
  }, [pageName, companyDetails]);

  return null;
}
