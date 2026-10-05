import { dehydrate, QueryClient, useQueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  QueriesLogger,
  axiosRequest,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import type { GetServerSidePropsContext } from 'next';
import type { ReactElement } from 'react';

import { SecondaryHDPLayout } from '~components';
import { Page as GroupBookings } from '~page-helper/group-bookings';
import { PAGE } from '~utils/pi-all-pages-constants';

export default function GroupBookingsPage() {
  const queryClient = useQueryClient();
  return (
    <QueryClientProvider client={queryClient}>
      <GroupBookings />
    </QueryClientProvider>
  );
}

GroupBookingsPage.getLayout = function GetLayout(page: ReactElement) {
  return (
    <SecondaryHDPLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </SecondaryHDPLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const queryClient = new QueryClient();
  const { language, country } = getServerSideCustomLocale(locale);
  const cookies = new Cookies(props.req, props.res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    props.req,
    props.res,
    props.query,
    'PI | GBFP | Group Booking Form Page'
  );

  const axiosProps = {
    method: 'GET',
    url: `${process.env.NEXT_PUBLIC_HOTELS_API_URL}/hotels?country=${country}&language=${language}`,
    headers: {
      [WB_SESSION_ID]: sessionId || '',
    },
  };

  await prefetchQuery(['groupBookingHotels', language, country], () => axiosRequest(axiosProps));

  const [labels, featureToggles] = await Promise.all([
    getI18nLabels({
      language,
      queryClient,
    }),
    getUnleashToggles(
      props,
      PAGE.GROUP_BOOKING_FORM.featureToogles.appPage,
      PAGE.GROUP_BOOKING_FORM.featureToogles.flagsWithFallback
    ),
  ]);
  logQueries(performance.now());
  return { props: { ...labels, dehydratedState: dehydrate(queryClient), featureToggles } };
}
