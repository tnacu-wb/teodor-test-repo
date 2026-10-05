import { useQueryClient } from '@tanstack/react-query';
import {
  type QueryBookingInformationArgs,
  type QueryHotelInformationArgs,
  type PackagesCriteria,
  type InnBusinessServerSideProps,
  FT_PI_BB_CANCELLATION_POLICY,
  FT_PI_BB_CCUI_SHOW_MEALS_FREE,
  LOCALES,
  FT_BB_MARKETING_EMAIL_OPTIN,
  FT_BB_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_SITE_WIDE,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  FT_BB_FREE_FNB_AND_EXTRAS,
  FT_PI_REDIS_RQ_CACHE,
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  FT_ONE_TRUST_COOKIE_CONSENT,
  FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION,
  PageName,
  UnleashChannel,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { BusinessStepType } from '@whitbread-eos/layout';
import {
  getAuthCookie,
  getLoggedInUserInfo,
  getServerSideCustomLocale,
  isGuestDetailsPageAllowed,
  useCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  ID_TOKEN_COOKIE,
  isInnBusinessApp,
  GLOBALS,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement, useEffect, useState } from 'react';

import { GuestDetailsLayout } from '~components';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import {
  createGuestDetailsBBDataLoaderFn,
  Page as GuestDetailsPageBB,
} from '~page-helper/guest-details';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../search';

interface DynamicObject {
  [key: string]: boolean;
}

interface Props {
  pcksQueryInput: PackagesCriteria;
  hiQueryInput: QueryHotelInformationArgs;
  biQueryInput: QueryBookingInformationArgs;
  featureToggles: DynamicObject;
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
}

export default function GuestDetailsBBPage({
  hiQueryInput,
  pcksQueryInput,
  biQueryInput,
  featureToggles,
  innBusiness,
}: Readonly<Props>) {
  const router = useRouter();
  const queryClient = useQueryClient();

  useFeatureToggle(featureToggles);

  const { language, country } = useCustomLocale();
  const [origin, setOrigin] = useState('');
  const idTokenCookie = getAuthCookie();
  const isWindowDefined = typeof window !== 'undefined';
  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  useEffect(() => {
    const { accessLevel } = getLoggedInUserInfo(idTokenCookie);

    if (!isGuestDetailsPageAllowed(accessLevel)) {
      window.location.href = `${origin}/${country}/${language}/business-booker`;
    }
  }, []);

  return (
    <GuestDetailsPageBB
      router={router}
      queryClient={queryClient}
      biQueryInput={biQueryInput}
      pcksQueryInput={pcksQueryInput}
      hiQueryInput={hiQueryInput}
      userDetails={innBusiness?.userDetails}
      companyDetails={innBusiness?.companyDetails}
    />
  );
}

GuestDetailsBBPage.getLayout = function getLayout(page: ReactElement<any>) {
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        serverSideProps={page.props.innBusiness}
        showSidebar={false}
        isBusinessBookerPage
        businessStepType={BusinessStepType.GUEST_DETAILS_PAGE}
        featureToggle={page.props.featureToggles}
        secureUrl={secureUrl}
      >
        <ErrorBoundary>{page}</ErrorBoundary>
      </InnBusinessLayout>
    );
  }

  return (
    <GuestDetailsLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </GuestDetailsLayout>
  );
};

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const { language, country } = getServerSideCustomLocale(locale);

  const flagsWithFallback = {
    release_bb_ultimate_wifi: false,
    release_bb_ancillaries_extras_display: false,
    release_bb_accompanying_guest_details: false,
    [FT_PI_BB_CANCELLATION_POLICY]: false,
    [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: false,
    [FT_BB_MARKETING_EMAIL_OPTIN]: false,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: false,
    [FT_BB_PROMO_CODE_SITE_WIDE]: false,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: false,
    [FT_BB_FREE_FNB_AND_EXTRAS]: false,
    [FT_PI_REDIS_RQ_CACHE]: false,
    [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
    [FT_ONE_TRUST_COOKIE_CONSENT]: false,
    [FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION]: false,
  };

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    'BB | GDP | Guest Details Page',
    flagsWithFallback,
    {
      country: country || GLOBALS.locale.GB,
      channel: UnleashChannel.PIB,
      pageName: PageName.GUEST_DETAILS.toUpperCase(),
    }
  );

  const queryClient = getPersistentQueryClient({
    page: 'BB | GDP | Guest Details Page',
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isInnBusinessAppPage = !!isInnBusinessApp(props?.req?.headers?.host ?? '');
  const shouldRedirectToInnBusinessLogin = !idTokenCookie && isInnBusinessAppPage;
  if (shouldRedirectToInnBusinessLogin) {
    return innBusinessLoginRedirect(locale);
  }

  let loadedData;
  try {
    loadedData = await createGuestDetailsBBDataLoaderFn({
      queryClient: queryClient,
      language: language,
      country: country,
      isInnBusinessAppPage: isInnBusinessAppPage,
      ...props,
    });
  } catch (error) {
    if (isInnBusinessAppPage) {
      const ibLocale = locale.toLowerCase() === 'de' ? LOCALES.DE : LOCALES.EN;
      return {
        redirect: {
          destination: `/${ibLocale}/error`,
          permanent: false,
        },
      };
    } else {
      return {
        notFound: true,
      };
    }
  }

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  return {
    props: {
      ...loadedData,
      ...labels,
      featureToggles,
      isInnBusinessAppPage,
    },
  };
});
