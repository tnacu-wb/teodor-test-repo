'use client';

import { Customer, HashType } from '@whitbread-eos/api';
import {
  analytics,
  useGetCountryLanguage,
  GLOBALS,
  hashString,
  setPageAnalytics,
} from '@whitbread-eos/utils';
import { format } from 'date-fns';
import Script from 'next/script';
import { useEffect } from 'react';

type Props = {
  userDetails?: Customer;
  pathName?: string;
  searchParams?: URLSearchParams;
};

export function Analytics({ userDetails, pathName, searchParams }: Props) {
  const { language } = useGetCountryLanguage();
  const dtScript =
    language === GLOBALS.language.EN
      ? process.env.NEXT_PUBLIC_DYNATRACE_GB
      : process.env.NEXT_PUBLIC_DYNATRACE_DE;

  useEffect(() => {
    async function updateAnalytics() {
      const currentData = window?.analyticsData ?? {};
      const [userMobile, userTelephone, userFN, userLN, userHashedEA] = await Promise.all([
        userDetails?.contactDetail?.mobile
          ? hashString(userDetails.contactDetail.mobile, HashType.SHA256)
          : Promise.resolve(undefined),
        userDetails?.contactDetail?.telephone
          ? hashString(userDetails.contactDetail.telephone, HashType.SHA256)
          : Promise.resolve(undefined),
        userDetails?.contactDetail?.firstName
          ? hashString(userDetails.contactDetail.firstName, HashType.SHA256)
          : Promise.resolve(undefined),
        userDetails?.contactDetail?.lastName
          ? hashString(userDetails.contactDetail.lastName, HashType.SHA256)
          : Promise.resolve(undefined),
        userDetails?.contactDetail?.email
          ? hashString(userDetails.contactDetail.email, HashType.SHA256)
          : Promise.resolve(undefined),
      ]);
      const userCountry = userDetails?.contactDetail?.address?.countryCode ?? undefined;
      analytics.update({
        language,
        browserTimeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
        environment: process.env.NODE_ENV === 'development' ? 'development' : 'production',
        companyID: userDetails?.companyId ?? '',
        userID: userDetails?.business?.employeeId ?? undefined,
        userLevel: userDetails?.business?.accessLevel ?? '',
        userLoggedIn: userDetails ? 'Logged In' : 'Not Logged In',
        currentTime: format(new Date(), 'HH:mm'),
        currencyCode: language === 'en' ? 'gbp' : 'eur',
        pageName: currentData.pageName || 'InnBusiness',
        pageType: 'look to book',
        pageURL: window.location.href,
        userCountry: userCountry,
        userFN: userFN,
        userLN: userLN,
        userMobile: userMobile,
        userTelephone: userTelephone,
        userHashedEA: userHashedEA,
      });
    }
    updateAnalytics();
  }, []);

  useEffect(() => {
    if (!pathName) return;

    const queryParams = Object.fromEntries(searchParams?.entries() ?? []);

    setPageAnalytics(pathName, 'PIB', queryParams, language);
  }, [pathName, language]);

  return (
    <>
      <Script
        src="//assets.adobedtm.com/launch-EN1f330bc46c5949b29c22bbf3f0573f75.min.js"
        strategy="beforeInteractive"
        data-testid="adobe-script"
      />
      {dtScript && (
        <Script
          src={dtScript}
          data-testid="dynatrace-script"
          strategy="beforeInteractive"
          defer={false}
        />
      )}
    </>
  );
}
