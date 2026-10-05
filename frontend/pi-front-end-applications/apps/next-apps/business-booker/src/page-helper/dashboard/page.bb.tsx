import { StyleProps, useMediaQuery } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import {
  AMEND_3CP_VISITED_INITIAL_VALUE,
  AMEND_3CP_VISITED_KEY,
  Area,
  BookingHistoryRequest,
  GET_BOOKING_HISTORY,
  PageName,
  ScreenSizeValues,
  UserAccessLevels,
  BookingChannelCriteria,
  Channel,
  Query,
  FT_PI_BB_BOOKING_HISTORY_REDESIGN,
} from '@whitbread-eos/api';
import { SEO as Seo } from '@whitbread-eos/molecules';
import {
  BBSearchContainer as Search,
  BookingsHistory,
  getTableConfig,
  getTableRedesignDesktopConfig,
  getDashboardRedesignTabletConfig,
  getDashboardRedesignMobileConfig,
} from '@whitbread-eos/organisms';
import {
  getAuthCookie,
  getLoggedInUserInfo,
  graphQLRequest,
  useCustomLocale,
  useUserData,
  useSessionStorage,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { useEffect } from 'react';

interface Props {
  screenSize: ScreenSizeValues;
  showSearch?: boolean;
}

const IS_BUSINESS_USER = true;
const SORT_ORDER = 'DEFAULT';
const PAGE_SIZE = 10;

export const MyDashboardPageBb = ({ screenSize, showSearch = true }: Props) => {
  const router = useRouter();
  const client = useQueryClient();
  const { t } = useTranslation();
  const { country, language } = useCustomLocale();
  const { isLoggedIn } = useUserData();
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  const bookingChannel: BookingChannelCriteria = {
    channel: Channel.Bb,
    subchannel: 'WEB',
    language: language.toUpperCase() as 'EN' | 'DE',
  };

  const { [FT_PI_BB_BOOKING_HISTORY_REDESIGN]: isBookingHistoryRedesignPIAndBBEnabled = false } =
    useFeatureToggle();

  const idTokenCookie = getAuthCookie();
  const { accessLevel } = getLoggedInUserInfo(idTokenCookie);

  const [amend3cpVisited] = useSessionStorage<string>(
    AMEND_3CP_VISITED_KEY,
    AMEND_3CP_VISITED_INITIAL_VALUE
  );

  const [isLessThanSm, isLessThanLg] = useMediaQuery(['(max-width: 575px)', '(max-width: 1279px)']);

  const getTableConfigForScreen = () => {
    if (!isBookingHistoryRedesignPIAndBBEnabled) {
      return getTableConfig(t, language, IS_BUSINESS_USER);
    }
    if (isLessThanSm) {
      return getDashboardRedesignMobileConfig(t, language, IS_BUSINESS_USER);
    }
    if (isLessThanLg) {
      return getDashboardRedesignTabletConfig(t, language, IS_BUSINESS_USER);
    }
    return getTableRedesignDesktopConfig(t, language, IS_BUSINESS_USER);
  };

  const tableConfig = getTableConfigForScreen();

  useEffect(() => {
    if (amend3cpVisited) {
      sessionStorage.removeItem(AMEND_3CP_VISITED_KEY);
      router.replace(
        `/${country}/${language}/business-booker/amend/details?bookingReference=${amend3cpVisited}`
      );
    }
  }, [amend3cpVisited]);

  const handleFetch = (params: BookingHistoryRequest): Promise<Query> => {
    return client.fetchQuery({
      queryKey: [
        'getBookingHistory',
        IS_BUSINESS_USER,
        SORT_ORDER,
        PAGE_SIZE,
        ...Object.values(params),
        bookingChannel,
      ],
      queryFn: () =>
        graphQLRequest(
          GET_BOOKING_HISTORY,
          {
            business: IS_BUSINESS_USER,
            sortOrder: SORT_ORDER,
            pageSize: PAGE_SIZE,
            ...params,
            bookingChannel,
          },
          idTokenCookie
        ),
    });
  };

  const renderSearch = () => {
    return (
      <Search
        queryClient={client}
        searchLocation={searchLocation?.toString()}
        ARRdd={Number(ARRdd)}
        ARRmm={Number(ARRmm)}
        ARRyyyy={Number(ARRyyyy)}
        NIGHTS={Number(NIGHTS)}
        ROOMS={Number(ROOMS)}
        isSummaryActive={false}
        variant="bb"
      />
    );
  };
  const Heading = dynamic(
    async () => {
      const { Heading } = await import('@chakra-ui/react');
      return { default: Heading };
    },
    {
      ssr: false,
    }
  );

  const renderHeading = () => {
    const headingStyle = getHeadingStyle(showSearch);
    return (
      <Heading as="h1" {...headingStyle}>
        {t('dashboard.bookings.bookingListHeading')}
      </Heading>
    );
  };

  if (!isLoggedIn) {
    return renderSearch();
  }

  return (
    <>
      <Seo page={PageName.DASHBOARD} />
      {accessLevel !== UserAccessLevels.STAYER && showSearch && renderSearch()}
      {renderHeading()}
      <BookingsHistory
        tableConfig={tableConfig ?? []}
        screenSize={screenSize}
        onFetch={handleFetch}
        pageSize={PAGE_SIZE}
        bookingChannel={bookingChannel}
        area={Area.BB}
        rowStyles={getRowStyles(isBookingHistoryRedesignPIAndBBEnabled)}
        isBookingHistoryRedesignPIAndBBEnabled={isBookingHistoryRedesignPIAndBBEnabled}
      />
    </>
  );
};

const getHeadingStyle = (showSearch: boolean) => ({
  fontWeight: 'semibold',
  lineHeight: {
    mobile: '4',
    sm: '5',
  },
  fontSize: {
    mobile: '3xl',
    sm: '3xxl',
  },
  color: 'baseBlack',
  mb: 'md',
  mt: {
    mobile: showSearch ? '-4xl' : '1rem',
    xs: showSearch ? '-1.25rem' : '1rem',
    sm: '3xl',
    lg: '5xl',
  },
});

const getRowStyles = (isRedesignEnabled: boolean): StyleProps => {
  if (isRedesignEnabled) {
    return {
      padding: {
        mobile: 'var(--chakra-space-3) var(--chakra-space-4)',
        lg: 'var(--chakra-space-6) var(--chakra-space-3) var(--chakra-space-6)',
      },
    };
  }
  return {
    padding: {
      xl: 'var(--chakra-space-10) var(--chakra-space-5) var(--chakra-space-4)',
      lg: 'var(--chakra-space-6) var(--chakra-space-4) var(--chakra-space-4)',
      md: 'var(--chakra-space-6) var(--chakra-space-3) var(--chakra-space-4)',
      sm: 'var(--chakra-space-6) var(--chakra-space-1) var(--chakra-space-4)',
      xs: 'var(--chakra-space-6) var(--chakra-space-1) var(--chakra-space-4)',
      mobile: 'var(--chakra-space-6) var(--chakra-space-1) var(--chakra-space-4)',
    },
  };
};

export default MyDashboardPageBb;
