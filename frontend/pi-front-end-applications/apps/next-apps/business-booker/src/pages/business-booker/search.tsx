import { useQueryClient } from '@tanstack/react-query';
import {
  FT_IB_REDIRECT_AFTER_LOGIN,
  FT_PI_REDIS_RQ_CACHE,
  InnBusinessServerSideProps,
  LOCALES,
  Channel,
  UnleashChannel,
  PageName,
} from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import { getPromotionsInformation } from '@whitbread-eos/molecules';
import {
  getServerSideCustomLocale,
  useUserData,
  useUserDetails,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  ID_TOKEN_COOKIE,
  isInnBusinessApp,
  getHotelBrandFromSearchResults,
  extractMultiHotelAvailabilities,
  type PromotionsInformation,
  getGQLClient,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import { SearchPageBB, createSearchResultsBBDataLoader } from '~page-helper/search';
import { PAGE } from '~utils/bb-all-pages-constants';
import checkValidRedirect from '~utils/checkValidRedirect';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

interface Props {
  featureToggles: { [key: string]: boolean };
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
  fallbackSearchPlace?: { PLACEID?: string; 'searchModel.searchTerm'?: string };
  promotionBannerData?: PromotionsInformation;
}

export default function SearchPage(props: Readonly<Props>) {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { featureToggles, fallbackSearchPlace, promotionBannerData } = props;
  useFeatureToggle(featureToggles);

  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn, props?.innBusiness?.userDetails);
  const accessLevel = (userData as any)?.business?.accessLevel;

  return (
    <>
      {!!accessLevel && (
        <SearchPageBB
          router={router}
          queryClient={queryClient}
          variant="bb"
          accessLevel={accessLevel}
          innBusiness={props.innBusiness}
          fallbackSearchPlace={fallbackSearchPlace}
          promotionBannerData={promotionBannerData}
        />
      )}
    </>
  );
}

SearchPage.getLayout = function getLayout(page: ReactElement<any>) {
  const containerStyles = {
    mx: 0,
    px: 0,
    maxWidth: '100% !important',
    height: '100%',
  };

  const mainStyles = {
    mb: '-0.5rem !important',
  };

  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';

  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        showEditSearch={true}
        serverSideProps={page.props.innBusiness}
        isBusinessBookerPage
        featureToggle={page.props.featureToggles}
        secureUrl={secureUrl}
        collapsedSidebar={true}
        mainId="pib-main-container"
      >
        <Container containerStyles={containerStyles}>
          <ErrorBoundary>{page}</ErrorBoundary>
        </Container>
      </InnBusinessLayout>
    );
  }

  return (
    <DefaultLayout showFooter={false} containerStyles={containerStyles} mainStyles={mainStyles}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export function innBusinessLoginRedirect(
  locale: string,
  featureToggles?: { [key: string]: boolean },
  currentPath?: string
) {
  const ibLocale = locale.toLowerCase() === 'de' ? LOCALES.DE : LOCALES.EN;
  const bbLocale = locale.toLowerCase() === 'de' ? '/de/de' : '/gb/en';

  let destination = `/${ibLocale}/account/login`;
  const isRedirectAfterLoginEnabled = featureToggles?.[FT_IB_REDIRECT_AFTER_LOGIN] ?? false;

  let currentPathWithBBLocale = currentPath || '';
  if (!currentPathWithBBLocale.startsWith(bbLocale)) {
    currentPathWithBBLocale = `${bbLocale}${currentPathWithBBLocale}`;
  }

  const isValidRedirect = checkValidRedirect(currentPathWithBBLocale ?? '');

  if (
    isRedirectAfterLoginEnabled &&
    isValidRedirect &&
    currentPathWithBBLocale &&
    currentPath !== ''
  ) {
    let redirectUrl = currentPathWithBBLocale;

    if (!redirectUrl.includes('.html')) {
      // put back .html before ? as next js strips it out
      const queryIndex = redirectUrl.indexOf('?');
      if (queryIndex !== -1) {
        // Insert .html before the ?
        redirectUrl = `${redirectUrl.slice(0, queryIndex)}.html${redirectUrl.slice(queryIndex)}`;
      } else {
        // No query string, just add .html at the end
        redirectUrl = `${redirectUrl}.html`;
      }
    }
    // encodeURIComponent - so urls like SRP are treated as one long string
    destination += `?redirectURL=${encodeURIComponent(redirectUrl)}`;
  }
  return {
    redirect: {
      destination,
      permanent: false,
    },
  };
}

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.SRP.featureToggles.appPage,
    PAGE.SRP.featureToggles.flagsWithFallback,
    { channel: UnleashChannel.PIB, pageName: PageName.SRP }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.SRP.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isInnBusinessAppPage = !!isInnBusinessApp(props?.req?.headers?.host ?? '');
  const currentPath = props?.resolvedUrl || '';
  const shouldRedirectToInnBusinessLogin = !idTokenCookie && isInnBusinessAppPage;

  // Redirect to innBusiness login page with redirect param - so login will bring user to their intended page
  if (shouldRedirectToInnBusinessLogin) {
    return innBusinessLoginRedirect(locale, featureToggles, currentPath);
  }

  const loadedData = await createSearchResultsBBDataLoader({
    queryClient,
    language,
    country,
    featureToggles,
    isInnBusinessAppPage,
    ...props,
  });
  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const {
    arrival = '',
    departure = '',
    channel = '',
    promotionCode = '',
  } = loadedData?.promoInformationQuery ?? {};

  const hotelsList = extractMultiHotelAvailabilities(loadedData);
  const hotelBrand = getHotelBrandFromSearchResults(hotelsList);

  const isPromoEnabled =
    Boolean(hotelBrand) && (loadedData?.promoInformationQuery?.isPromoEnabled ?? false);

  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const promotionBannerData = await getPromotionsInformation(
    arrival as string,
    departure as string,
    country,
    language,
    hotelBrand as string,
    channel as Channel,
    '',
    queryClient,
    client,
    isPromoEnabled,
    promotionCode
  );

  return {
    props: {
      featureToggles,
      isInnBusinessAppPage,
      ...loadedData,
      ...labels,
      promotionBannerData,
    },
  };
});
