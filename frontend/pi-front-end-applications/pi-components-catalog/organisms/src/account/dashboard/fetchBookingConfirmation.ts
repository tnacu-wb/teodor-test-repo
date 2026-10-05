import type { QueryClient } from '@tanstack/react-query';
import {
  Area,
  BCAuthResponse,
  BCResponse,
  BookingConfirmation,
  GET_DASHBOARD_BOOKING_CONFIRMATION,
  GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
  GET_HOTEL_INFORMATION,
} from '@whitbread-eos/api';
import { graphQLRequest } from '@whitbread-eos/utils';

interface FetchBookingConfirmationParams {
  queryClient: QueryClient;
  loggedOrCCUI: boolean;
  bookingReference?: string;
  basketReference: string | null;
  language: string;
  country: string;
  area?: Area;
  token?: string;
  queryOptions?: Record<string, unknown>;
}

export async function fetchBookingConfirmation({
  queryClient,
  loggedOrCCUI,
  bookingReference,
  basketReference,
  language,
  country,
  area,
  token,
  queryOptions,
}: FetchBookingConfirmationParams): Promise<BookingConfirmation> {
  const data = await queryClient.fetchQuery({
    queryKey: [
      loggedOrCCUI ? 'getBookingConfirmationAuthenticated' : 'getBookingConfirmation',
      loggedOrCCUI ? bookingReference : basketReference,
      language,
      country,
    ],
    queryFn: () =>
      graphQLRequest(
        loggedOrCCUI
          ? GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED
          : GET_DASHBOARD_BOOKING_CONFIRMATION,
        loggedOrCCUI
          ? {
              bookingReference,
              language,
              country,
              bookingChannel: area?.toUpperCase(),
            }
          : {
              basketReference,
              language,
              country,
              bookingChannel: area?.toUpperCase(),
            },
        loggedOrCCUI ? token : undefined
      ),
    ...queryOptions,
  });

  return loggedOrCCUI
    ? (data as BCAuthResponse)?.bookingConfirmationAuthenticated
    : (data as BCResponse)?.bookingConfirmation;
}

export function fetchHotelInformation(
  queryClient: QueryClient,
  hotelId: string,
  language: string,
  country: string
) {
  return queryClient.fetchQuery({
    queryKey: ['GetHotelInformation', hotelId, country, language],
    queryFn: () => graphQLRequest(GET_HOTEL_INFORMATION, { hotelId, language, country }),
  });
}
