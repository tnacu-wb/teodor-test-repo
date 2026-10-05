import { Spacer, useMediaQuery } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import {
  BookingHistoryRequest,
  GET_BOOKING_HISTORY,
  PageName,
  ScreenSizeValues,
  AMEND_3CP_VISITED_KEY,
  AMEND_3CP_VISITED_INITIAL_VALUE,
  BookingChannelCriteria,
  Channel,
  Query,
  FT_PI_BB_BOOKING_HISTORY_REDESIGN,
} from '@whitbread-eos/api';
import { SEO as Seo } from '@whitbread-eos/molecules';
import {
  BookingsHistory,
  PISearchContainer as Search,
  getTableConfig,
  getTableRedesignDesktopConfig,
  getDashboardRedesignTabletConfig,
  getDashboardRedesignMobileConfig,
} from '@whitbread-eos/organisms';
import {
  useAuthToken,
  graphQLRequest,
  useCustomLocale,
  useUserData,
  useSessionStorage,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

interface Props {
  screenSize: ScreenSizeValues;
}

const IS_BUSINESS_USER = false;
const SORT_ORDER = 'DEFAULT';
const PAGE_SIZE = 10;

export const MyDashboardPagePi = ({ screenSize }: Props) => {
  const router = useRouter();
  const client = useQueryClient();
  const { t } = useTranslation();
  const { country, language } = useCustomLocale();
  const { isLoggedIn } = useUserData();
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  const bookingChannel: BookingChannelCriteria = {
    channel: Channel.Pi,
    subchannel: 'WEB',
    language: language.toUpperCase() as 'EN' | 'DE',
  };

  const { [FT_PI_BB_BOOKING_HISTORY_REDESIGN]: isBookingHistoryRedesignPIAndBBEnabled = false } =
    useFeatureToggle();

  const { token, isLoading: isAuthTokenLoading } = useAuthToken();
  const [firstTimeAccessLoggedIn] = useState(isLoggedIn);

  const [amend3cpVisited] = useSessionStorage<string>(
    AMEND_3CP_VISITED_KEY,
    AMEND_3CP_VISITED_INITIAL_VALUE
  );

  const [isLessThanSm, isLessThanLg] = useMediaQuery(['(max-width: 575px)', '(max-width: 1279px)']);

  const getTableConfigForScreen = () => {
    if (!isBookingHistoryRedesignPIAndBBEnabled) {
      return getTableConfig(t, language);
    }
    if (isLessThanSm) {
      return getDashboardRedesignMobileConfig(t, language);
    }
    if (isLessThanLg) {
      return getDashboardRedesignTabletConfig(t, language);
    }
    return getTableRedesignDesktopConfig(t, language);
  };

  const tableConfig = getTableConfigForScreen();

  useEffect(() => {
    if (firstTimeAccessLoggedIn && !isLoggedIn) {
      window.location.href = `${window.location.origin}/${country}/${language}/home.html`;
    }
  }, [isLoggedIn]);

  useEffect(() => {
    if (amend3cpVisited) {
      sessionStorage.removeItem(AMEND_3CP_VISITED_KEY);
      router.replace(`/${country}/${language}/amend/details?bookingReference=${amend3cpVisited}`);
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
          token
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
        variant="pi"
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
      <Seo page={PageName.DASHBOARD} noIndexNoFollow={true} />
      {renderSearch()}
      {renderHeading()}

      {!isAuthTokenLoading && (
        <BookingsHistory
          tableConfig={tableConfig ?? []}
          screenSize={screenSize}
          onFetch={handleFetch}
          pageSize={PAGE_SIZE}
          bookingChannel={bookingChannel}
          isBookingHistoryRedesignPIAndBBEnabled={isBookingHistoryRedesignPIAndBBEnabled}
        />
      )}
      <Spacer h="16" />
    </>
  );
};

const headingStyle = {
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
    mobile: '-4xl',
    xs: '-1.25rem',
    sm: '3xl',
    lg: '5xl',
  },
};

export default MyDashboardPagePi;
