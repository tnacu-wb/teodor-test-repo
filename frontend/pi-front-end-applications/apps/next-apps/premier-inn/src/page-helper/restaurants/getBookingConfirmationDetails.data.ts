import { QueryClient, dehydrate } from '@tanstack/react-query';
import { GET_ENQUIRY_INFO_BY_ID, GET_EVENT_INFO_BY_ID } from '@whitbread-eos/api';
import {
  instrumentQueryClient,
  getDefaultSessionTracing,
  logger,
  graphQLRequestRestaurants,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface GetBookingConfirmationDetailsDataParams extends GetServerSidePropsContext {
  queryClient: QueryClient;
  eventId: string;
  enquiryId: string;
}

export async function getBookingConfirmationDetailsDatFn({
  queryClient,
  eventId,
  enquiryId,
  query,
  req,
  res,
}: GetBookingConfirmationDetailsDataParams) {
  const cookies = new Cookies(req, res);
  const sessionTracing = getDefaultSessionTracing(cookies);

  const queryKey = eventId ? 'GetEventById' : 'GetEnquiryById';
  const queryFn = eventId ? GET_EVENT_INFO_BY_ID : GET_ENQUIRY_INFO_BY_ID;
  const queryVariables = eventId ? { id: eventId } : { id: enquiryId };
  const { prefetchQuery } = instrumentQueryClient(queryClient);
  logger.info({
    label: 'AEM Data for TB:START_DATA_FETCHING',
    msg: {
      query,
      ...sessionTracing,
    },
  });
  try {
    await prefetchQuery([queryKey], () => graphQLRequestRestaurants(queryFn, queryVariables));
    logger.info({
      label: 'TB:BookingConfirmationDetails:booking confirmation details',
      msg: {
        eventId,
        enquiryId,
      },
    });
    return {
      dehydratedState: dehydrate(queryClient),
    };
  } catch (error) {
    // Log any errors
    logger.info({
      label: 'TB:BookingConfirmationDetails:booking confirmation error',
      msg: {
        error,
      },
    });

    // Handle the error or return an error state if needed
    return {
      dehydratedState: dehydrate(queryClient),
    };
  }
}
