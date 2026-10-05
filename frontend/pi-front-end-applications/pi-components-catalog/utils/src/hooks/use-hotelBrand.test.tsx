import * as ReactQuery from '@tanstack/react-query';
import { waitFor } from '@testing-library/react';
import { renderHook } from '@testing-library/react';
import { Channel } from '@whitbread-eos/api';

import useHotelBrands from './use-hotelBrand';

type bookingInformationData = {
  bookingInformation:
    | {
        hotelId: string;
        bookingFlowId: string;
        reservationByIdList: [
          {
            roomStay: {
              arrivalDate: string;
              departureDate: string;
            };
          },
        ];
      }
    | undefined;
};
const getBookingInformationData: bookingInformationData = {
  bookingInformation: {
    hotelId: 'LONKIN',
    bookingFlowId: 'booking-hub',
    reservationByIdList: [
      {
        roomStay: {
          arrivalDate: '2023-02-23',
          departureDate: '2023-02-24',
        },
      },
    ],
  },
};

type RouterResponse = { data: { query: { key: '' } } | undefined };
const mockUseRouterResponse: RouterResponse = {
  data: {
    query: { key: '' },
  },
};

jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  graphQLRequest: jest.fn(),
}));

const queryClient = new ReactQuery.QueryClient();

// Mock the queryClient methods directly
jest.spyOn(queryClient, 'fetchQuery').mockImplementation((options: any) => {
  const queryKey = options.queryKey || options;
  const key = Array.isArray(queryKey) ? queryKey[0] : queryKey;

  if (key === 'GetBookingInformation') {
    return Promise.resolve(getBookingInformationData);
  }
  if (key === 'GetBookingFlowInformation') {
    return Promise.resolve({});
  }
  // Return empty object for any other query to avoid undefined
  return Promise.resolve({});
});
jest.mock('next/router', () => ({
  useRouter() {
    return mockUseRouterResponse.data;
  },
}));

const params = {
  basketReference: '1234' as any,
  channel: 'PI' as Channel,
  queryClient: queryClient,
};

describe('useCompanyDetails', () => {
  it('should return company details with company name', async () => {
    const { result } = renderHook(() => useHotelBrands(params));
    await waitFor(() => {
      expect(result.current.brand).toBe(null);
    });
  });

  it('should return company details on channel BB', async () => {
    mockUseRouterResponse.data = undefined;
    params.channel = Channel.Bb;
    const { result } = renderHook(() => useHotelBrands(params));
    await waitFor(() => {
      expect(result.current.brand).toBe(null);
    });
  });

  it('should return company details without useRouter', async () => {
    mockUseRouterResponse.data = undefined;
    params.channel = Channel.Pi;
    params.basketReference = undefined;
    const { result } = renderHook(() => useHotelBrands(params));
    await waitFor(() => {
      expect(result.current.brand).toBe(null);
    });
  });

  it('should return company details without useRouter', async () => {
    params.basketReference = '123';
    getBookingInformationData.bookingInformation = null as any;
    params.channel = Channel.Ccui;
    const { result } = renderHook(() => useHotelBrands(params));
    await waitFor(() => {
      expect(result.current.brand).toBe(null);
    });
  });
});
