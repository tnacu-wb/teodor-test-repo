import { Box } from '@chakra-ui/react';
import { dehydrate, useQueryClient } from '@tanstack/react-query';
import { getStaticContent, SITE_LEISURE } from '@whitbread-eos/api';
import type { SearchRoomType } from '@whitbread-eos/api';
import { ErrorBoundary, Alert, Error, Info, Notification } from '@whitbread-eos/atoms';
import { HomeBanner, ConsentNotificationModal } from '@whitbread-eos/molecules';
import { PISearchContainer as Search } from '@whitbread-eos/organisms';
import {
  axiosRequest,
  decodeIdToken,
  getServerSideCustomLocale,
  getUnleashToggles,
  ID_TOKEN_COOKIE,
  useAuth0User,
  useAuthToken,
  useCustomLocale,
  useFeatureToggle,
  useQueryRequest,
  useRestQueryRequest,
  useUserData,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { ReactElement, useEffect, useMemo, useState } from 'react';

import { DefaultLayout } from '~components';
import ContactBannerNotification from '~components/ContactBannerNotification';
import {
  useWebPushNotification,
  PUSH_REQUEST_CLOSED_COUNT,
  PUSH_REQUEST_CLOSED_TIMESTAMP,
} from '~hooks/use-web-push-notification';
import { PAGE } from '~utils/pi-all-pages-constants';

import { getAuth0TokenAndEmail } from '../../lib/getAuth0Token';

function mapRoomRequirementsToDefaultRooms(
  roomRequirements:
    | { adults?: number; children?: number; cotRequired?: boolean; type?: string }
    | null
    | undefined
): SearchRoomType[] | undefined {
  if (!roomRequirements) return undefined;
  return [
    {
      adults: roomRequirements.adults ?? 1,
      children: roomRequirements.children ?? 0,
      shouldIncludeCot: !!roomRequirements.cotRequired,
      roomType: roomRequirements.type ?? 'DB',
    },
  ];
}

interface HeaderProps {
  featureToggles?: { [key: string]: boolean };
  pushRequestClosedCountCookie: number;
  pushRequestClosedTimestampCookie: string | null;
  shouldShowHomeBanner?: boolean;
  shouldHideSearchBar?: boolean;
  defaultRooms?: SearchRoomType[];
  [key: string]: unknown;
}

export default function Header(pageProps: HeaderProps) {
  const {
    featureToggles,
    pushRequestClosedCountCookie,
    pushRequestClosedTimestampCookie,
    shouldShowHomeBanner = false,
    shouldHideSearchBar = false,
    defaultRooms,
  } = pageProps;
  useFeatureToggle(featureToggles);

  const client = useQueryClient();
  const { language, country } = useCustomLocale();
  const { isLoggedIn } = useUserData();
  const { token, isAuth0Enabled } = useAuthToken();
  const { user: auth0User } = useAuth0User(isAuth0Enabled);

  const clientEmail = isAuth0Enabled
    ? (auth0User?.email ?? '')
    : (decodeIdToken(token ?? '')?.email ?? '');

  const { data: userProfile } = useRestQueryRequest(
    ['userBookingPreference', clientEmail],
    'GET',
    `${process.env.NEXT_PUBLIC_REST_API}/customers/hotels/${clientEmail}?business=false`,
    { Authorization: `Bearer ${token ?? ''}` },
    { enabled: isLoggedIn && !!token && !!clientEmail }
  );

  const clientDefaultRooms = useMemo(
    () => mapRoomRequirementsToDefaultRooms(userProfile?.bookingPreference?.roomRequirements),
    [userProfile]
  );

  const [isMounted, setIsMounted] = useState(false);
  const [promoId, setPromoId] = useState<string | null | undefined>(undefined);

  useEffect(() => {
    setIsMounted(true);
    const params = new URLSearchParams(window.location.search);
    setPromoId(params.get('promoCode') ?? null);
  }, []);

  const staticContentQuery = getStaticContent(false);
  const { data: staticContent, isLoading: isStaticContentLoading } = useQueryRequest(
    ['GetStaticContent', language, country],
    staticContentQuery,
    { country, language, site: SITE_LEISURE, businessBooker: false }
  );
  const headerAnnouncement =
    staticContent?.headerInformation?.announcement?.text || null || undefined;
  const headerAnnouncementType = staticContent?.headerInformation?.announcement?.type;
  const contactBanner = staticContent?.headerInformation?.contactBanner;

  // Web Push Notifications
  const { shouldShowNotificationModal, handleNotificationPermission } = useWebPushNotification({
    featureToggles,
    pushRequestClosedCountCookie,
    pushRequestClosedTimestampCookie,
  });

  // Check if the current page supports web push notifications
  const [isWebPushSupportedPage, setIsWebPushSupportedPage] = useState(false);

  useEffect(() => {
    // Check for the data-iswebpushenabled attribute on the body element
    if (typeof document !== 'undefined') {
      const hasAttribute = document.body.hasAttribute('data-iswebpushenabled');
      setIsWebPushSupportedPage(hasAttribute);
    }
  }, []);

  const searchContainerStyles = {
    maxWidth: {
      mobile: '100%',
      lg: 'var(--chakra-space-breakpoint-lg)',
      xl: 'var(--chakra-space-breakpoint-xl)',
    },
    px: {
      mobile: '1rem',
      sm: '1.25rem',
      md: '1.5rem',
      lg: '1.75rem',
      xl: '4.125rem',
    },
    paddingTop: 'var(--chakra-space-sm)',
    mx: 'auto',
    width: '100%',
  };

  const contactBannerContainerStyles = {
    maxWidth: searchContainerStyles.maxWidth,
    px: searchContainerStyles.px,
    mx: 'auto',
    marginBottom: 'md',
    marginTop: { mobile: '2xl', xs: '3xl', sm: 'md' },
    width: '100%',
    sx: { '&:empty': { display: 'none' } },
  };

  const containerStyles = {
    height: '100%',
    direction: 'column' as const,
    mx: 0,
    px: 0,
    maxWidth: '100% !important',
    minW: '100%',
  };

  const announcementIconMap: Record<string, JSX.Element> = {
    error: <Error />,
    alert: <Alert />,
  };

  const notificationContainerStyles = {
    maxWidth: {
      mobile: '100%',
      lg: 'var(--chakra-space-breakpoint-lg)',
      xl: 'var(--chakra-space-breakpoint-xl)',
    },
    px: {
      mobile: '1rem',
      sm: '1.25rem',
      md: '1.5rem',
      lg: '1.75rem',
      xl: '4.125rem',
    },
    margin: '8px auto 16px auto',
    width: '100%',
  };

  return (
    <>
      <DefaultLayout
        showPromoCode={true}
        showFooter={false}
        containerStyles={containerStyles}
        mainStyles={{ mt: shouldShowHomeBanner && !shouldHideSearchBar ? 0 : undefined }}
        wrapperStyles={{ minH: 'unset', height: 'unset' }}
        promoCodeFromUrl={promoId ?? null}
        useNextImageForLogo={true}
      >
        <ErrorBoundary>
          {headerAnnouncement && (
            <Box {...notificationContainerStyles}>
              <Notification
                variant={headerAnnouncementType || 'info'}
                status={'info'}
                description={headerAnnouncement}
                svg={announcementIconMap[headerAnnouncementType ?? ''] ?? <Info />}
                isInnerHTML
              />
            </Box>
          )}
          {!shouldHideSearchBar &&
            (shouldShowHomeBanner ? (
              <HomeBanner
                searchComponent={
                  <Box
                    as="form"
                    autoComplete="off"
                    visibility={!isMounted || isStaticContentLoading ? 'hidden' : 'visible'}
                  >
                    <Search
                      queryClient={client}
                      defaultRooms={clientDefaultRooms ?? defaultRooms ?? undefined}
                      disableFlip
                    />
                  </Box>
                }
              />
            ) : (
              <Box
                as="form"
                autoComplete="off"
                visibility={!isMounted || isStaticContentLoading ? 'hidden' : 'visible'}
                {...searchContainerStyles}
              >
                <Search
                  queryClient={client}
                  defaultRooms={clientDefaultRooms ?? defaultRooms ?? undefined}
                  marginBottom={{ mobile: '5rem', xs: '7rem', sm: '0', md: '0', lg: '0', xl: '0' }}
                  disableFlip={true}
                />
              </Box>
            ))}
          {contactBanner && (
            <Box {...contactBannerContainerStyles}>
              <ContactBannerNotification contactBanner={contactBanner} />
            </Box>
          )}
        </ErrorBoundary>
      </DefaultLayout>
      {shouldShowNotificationModal && isWebPushSupportedPage && (
        <ConsentNotificationModal handleNotificationPermission={handleNotificationPermission} />
      )}
    </>
  );
}

Header.getLayout = function getLayout(page: ReactElement) {
  return <ErrorBoundary>{page}</ErrorBoundary>;
};

export async function getServerSideProps(props: GetServerSidePropsContext) {
  const { locale = 'gb', query } = props;
  const { logger, graphQLRequest, getI18nLabels } = await import('@whitbread-eos/utils');

  const queryKey = (name: string) =>
    Object.keys(query).find((key) => key.toLowerCase() === name.toLowerCase());

  const shouldShowHomeBanner = query.banner === 'true';
  const shouldHideSearchBar = query[queryKey('hideSearchBar') ?? 'hideSearchBar'] === 'true';

  const {
    getStaticContent,
    SITE_LEISURE,
    GET_SEARCH_RULES_QUERY,
    Channel,
    FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
    FT_PI_AUTH0_LOGIN,
    FT_PI_REDIS_RQ_CACHE,
    CountryCode,
  } = await import('@whitbread-eos/api');

  const cookies = new Cookies(props.req, props.res);
  const sessionId = cookies.get(WB_SESSION_ID);

  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles = await getUnleashToggles(
    props,
    PAGE.OPERA_SHARED.HEADER.featureToggles.appPage,
    PAGE.OPERA_SHARED.HEADER.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.OPERA_SHARED.HEADER.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({ language, queryClient });

  // Determine if barrier-free label should be enabled (only for DE locale) - based on existing logic
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? featureToggles[FT_PI_BB_CCUI_BARRIER_FREE_LABEL] : false;

  const staticContentQuery = getStaticContent(isBarrierFreeLabelEnabled);

  // Fetch static content and extract header announcement
  const staticContentPromise = graphQLRequest(staticContentQuery, {
    country,
    language,
    site: SITE_LEISURE,
    businessBooker: false,
  });

  const isAuth0Enabled = featureToggles[FT_PI_AUTH0_LOGIN] ?? false;
  let authToken: string | null = null;
  let userEmail: string | null = null;

  if (isAuth0Enabled) {
    const { accessToken, email } = await getAuth0TokenAndEmail(props.req);
    authToken = accessToken;
    userEmail = email;
  } else {
    const idTokenCookie = cookies.get(ID_TOKEN_COOKIE) ?? null;
    authToken = idTokenCookie;
    userEmail = idTokenCookie ? (decodeIdToken(idTokenCookie)?.email ?? null) : null;
  }

  let defaultRooms: SearchRoomType[] | undefined;

  if (authToken && userEmail) {
    try {
      const userDetails = await axiosRequest({
        method: 'GET',
        url: `${process.env.NEXT_PUBLIC_REST_API}/customers/hotels/${userEmail}?business=false`,
        headers: {
          Authorization: `Bearer ${authToken}`,
          [WB_SESSION_ID]: sessionId || '',
        },
      });
      defaultRooms = mapRoomRequirementsToDefaultRooms(
        userDetails?.bookingPreference?.roomRequirements
      );
    } catch (error) {
      logger.warn({
        label: 'PI:Opera-Shared:Header',
        message: 'Failed to fetch user booking preference',
        error,
      });
    }
  }

  await Promise.all([
    queryClient.prefetchQuery({
      queryKey: ['GetStaticContent', language, country],
      queryFn: () => staticContentPromise,
    }),
    queryClient.prefetchQuery({
      queryKey: ['getSearchRules', Channel.Pi],
      queryFn: () =>
        graphQLRequest(GET_SEARCH_RULES_QUERY, {
          channel: Channel.Pi,
        }),
    }),
  ]);

  const pushRequestClosedCountCookie = Number(cookies.get(PUSH_REQUEST_CLOSED_COUNT) ?? 0);
  const pushRequestClosedTimestampCookie = cookies.get(PUSH_REQUEST_CLOSED_TIMESTAMP) ?? null;

  logger.info({
    label: 'PI:Opera-Shared:Header',
    message: 'PageLoad',
  });

  return {
    props: {
      pushRequestClosedCountCookie,
      pushRequestClosedTimestampCookie,
      shouldShowHomeBanner,
      shouldHideSearchBar,
      ...(defaultRooms ? { defaultRooms } : {}),
      featureToggles,
      dehydratedState: dehydrate(queryClient),
      ...labels,
    },
  };
}
