'use client';

import {
  AnalyticsData,
  AnalyticsDataCartConfirmation,
  HashType,
  FT_PI_ANALYTICS_DEBOUNCE,
} from '@whitbread-eos/api';
import getConfig from 'next/config';
import { ReactNode, useEffect, useRef } from 'react';

import { decodeIdToken, getAuthCookie, getLoggedInUserInfo } from '../../getters/auth';
import { hashString } from '../../helpers/hashing';
import { useRestQueryRequest, useUserDetails, useFeatureToggle } from '../../hooks';
import { useAuth0User } from '../../hooks/useAuth0User';
import { useAuthToken } from '../../hooks/useAuthToken';
import { isInnBusinessApp } from '../../server/validators';
import { useUserData } from '../../store/UserContext';
import analytics, { configureAnalytics } from './analytics';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    rdata: string[];
    analyticsDataCartConfirmation: AnalyticsDataCartConfirmation;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

interface Props {
  children: ReactNode;
  initialAnalyticsData?: AnalyticsData | null;
  isBusinessBooker?: boolean;
}

const defaultAnalyticsData: AnalyticsData = {};

export function AnalyticsProviderComponent({
  children,
  initialAnalyticsData = defaultAnalyticsData,
  isBusinessBooker = false,
}: Readonly<Props>) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { isLoggedIn } = useUserData();
  const featureToggles = useFeatureToggle();
  const isAnalyticsDebounceEnabled = featureToggles?.[FT_PI_ANALYTICS_DEBOUNCE] ?? false;

  let isInnBusiness;

  const userData = useUserDetails(isBusinessBooker, isLoggedIn);

  if (typeof window !== 'undefined') {
    defaultAnalyticsData.browserTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    defaultAnalyticsData.environment =
      process.env.NODE_ENV === 'development' ? 'development' : 'production';
    isInnBusiness = isInnBusinessApp(window?.location?.host ?? '');
  }

  const { token, isAuth0Enabled: isAuth0TokenEnabled } = useAuthToken();
  const { user: auth0User } = useAuth0User(isAuth0TokenEnabled);

  const legacyCookie = isLoggedIn ? getAuthCookie() : '';
  const email =
    isAuth0TokenEnabled && auth0User?.email
      ? auth0User.email
      : (decodeIdToken(legacyCookie).email ?? '');

  const { isSuccess, isError } = useRestQueryRequest(
    ['userDetails', token, email],
    'GET',
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/customers/hotels/${email}?business=${isBusinessBooker}`,
    { Authorization: `Bearer ${token}` },
    { enabled: isLoggedIn && !!email && !isInnBusiness }
  );

  const isUserLoggedSuccess = isInnBusiness ? true : isSuccess;
  const isUserLoggedFailure = isError || !isLoggedIn;

  const { cdhEmployeeId, cdhCustomerId } = getLoggedInUserInfo(legacyCookie);

  // auth0.ts's onCallback hook stamps these params on the redirect only when a
  // sign-in JUST completed. Stash them here instead of tracking immediately -
  // the actual auth_sign_in_success event fires once the user-details fetch
  // below confirms the profile loaded, not just that the session cookie is set.
  const pendingSignInRef = useRef<{ authRedirectPageType?: string } | null>(null);

  useEffect(() => {
    if (typeof window !== 'undefined') {
      window.analyticsData = {
        ...window.analyticsData,
        ...initialAnalyticsData,
      };
    }
  }, [initialAnalyticsData]);

  useEffect(() => {
    configureAnalytics({ isAnalyticsDebounceEnabled });
  }, [isAnalyticsDebounceEnabled]);

  useEffect(() => {
    if (typeof window === 'undefined') return;

    const url = new URL(window.location.href);
    if (url.searchParams.get('authSignInSuccess') === '1') {
      pendingSignInRef.current = {
        authRedirectPageType: url.searchParams.get('authRedirectPageType') ?? undefined,
      };
      url.searchParams.delete('authSignInSuccess');
      url.searchParams.delete('authRedirectPageType');
      window.history.replaceState(window.history.state, '', url.toString());
    }
  }, []);

  useEffect(() => {
    const updateAnalyticsAsync = async () => {
      if (isUserLoggedSuccess) {
        const userID = (isBusinessBooker ? cdhEmployeeId : cdhCustomerId) || undefined;
        const [userMobile, userTelephone, userFN, userLN] = await Promise.all([
          userData?.contactDetail?.mobile
            ? hashString(userData.contactDetail.mobile, HashType.SHA256)
            : Promise.resolve(undefined),
          userData?.contactDetail?.telephone
            ? hashString(userData.contactDetail.telephone, HashType.SHA256)
            : Promise.resolve(undefined),
          userData?.contactDetail?.firstName
            ? hashString(userData.contactDetail.firstName, HashType.SHA256)
            : Promise.resolve(undefined),
          userData?.contactDetail?.lastName
            ? hashString(userData.contactDetail.lastName, HashType.SHA256)
            : Promise.resolve(undefined),
        ]);
        const userCountry = userData?.contactDetail?.address?.countryCode ?? undefined;
        analytics.update({
          userID,
          userHashedEA: await hashString(email, HashType.SHA256),
          userLoggedIn: 'Logged In',
          userMobile: userMobile,
          userTelephone: userTelephone,
          userFN: userFN,
          userLN: userLN,
          userCountry: userCountry,
          selectedSearch: undefined,
        });

        if (pendingSignInRef.current) {
          analytics.track('auth_sign_in_success', {
            authStep: 'success',
            authRedirectPageType: pendingSignInRef.current.authRedirectPageType,
          });
          pendingSignInRef.current = null;
        }
      }
      if (isUserLoggedFailure) {
        analytics.update({
          userID: undefined,
          userHashedEA: undefined,
          userLoggedIn: 'Not Logged In',
          userMobile: undefined,
          userTelephone: undefined,
          userFN: undefined,
          userLN: undefined,
          userCountry: undefined,
          selectedSearch: undefined,
        });
      }
    };

    updateAnalyticsAsync();
  }, [
    isLoggedIn,
    isUserLoggedSuccess,
    isUserLoggedFailure,
    userData?.customerAccountId,
    userData?.contactDetail?.firstName,
    userData?.contactDetail?.lastName,
    userData?.contactDetail?.address?.countryCode,
    userData?.contactDetail?.mobile,
    userData?.contactDetail?.telephone,
  ]);

  return <>{children}</>;
}
