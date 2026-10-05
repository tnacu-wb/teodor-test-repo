import type { QueryClient } from '@tanstack/react-query';
import { graphQLRequest } from '@whitbread-eos/utils';

import { fetchBookingConfirmation, fetchHotelInformation } from './fetchBookingConfirmation';

const mockFetchQuery = jest.fn();
const mockQueryClient = {
  fetchQuery: mockFetchQuery,
} as unknown as QueryClient;

jest.mock('@whitbread-eos/utils', () => ({
  graphQLRequest: jest.fn(),
}));

const mockGraphQLRequest = graphQLRequest as jest.MockedFunction<typeof graphQLRequest>;

describe('fetchBookingConfirmation', () => {
  const baseParams = {
    queryClient: mockQueryClient,
    loggedOrCCUI: false as boolean,
    bookingReference: 'REF123',
    basketReference: 'BAS123',
    language: 'en',
    country: 'gb',
    area: 'PI' as any,
    token: 'samplevalue',
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('uses getBookingConfirmation key and returns bookingConfirmation when loggedOrCCUI is false', async () => {
    const bookingData = { hotelId: 'H1', reservationByIdList: [] };
    mockFetchQuery.mockResolvedValue({ bookingConfirmation: bookingData });

    const result = await fetchBookingConfirmation({ ...baseParams, loggedOrCCUI: false });

    expect(mockFetchQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        queryKey: ['getBookingConfirmation', 'BAS123', 'en', 'gb'],
      })
    );
    expect(result).toEqual(bookingData);
  });

  it('uses getBookingConfirmationAuthenticated key and returns bookingConfirmationAuthenticated when loggedOrCCUI is true', async () => {
    const bookingData = { hotelId: 'H2', reservationByIdList: [] };
    mockFetchQuery.mockResolvedValue({ bookingConfirmationAuthenticated: bookingData });

    const result = await fetchBookingConfirmation({ ...baseParams, loggedOrCCUI: true });

    expect(mockFetchQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        queryKey: ['getBookingConfirmationAuthenticated', 'REF123', 'en', 'gb'],
      })
    );
    expect(result).toEqual(bookingData);
  });

  it('passes token to graphQLRequest when loggedOrCCUI is true', async () => {
    const bookingData = { bookingConfirmationAuthenticated: { hotelId: 'H2' } };
    mockFetchQuery.mockImplementation(async (opts: any) => {
      await opts.queryFn();
      return bookingData;
    });
    mockGraphQLRequest.mockResolvedValue(bookingData);

    await fetchBookingConfirmation({ ...baseParams, loggedOrCCUI: true });

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({ bookingReference: 'REF123', bookingChannel: 'PI' }),
      'samplevalue'
    );
  });

  it('passes undefined token to graphQLRequest when loggedOrCCUI is false', async () => {
    const bookingData = { bookingConfirmation: { hotelId: 'H1' } };
    mockFetchQuery.mockImplementation(async (opts: any) => {
      await opts.queryFn();
      return bookingData;
    });
    mockGraphQLRequest.mockResolvedValue(bookingData);

    await fetchBookingConfirmation({ ...baseParams, loggedOrCCUI: false });

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({ basketReference: 'BAS123', bookingChannel: 'PI' }),
      undefined
    );
  });

  it('forwards queryOptions to fetchQuery', async () => {
    const bookingData = { bookingConfirmation: { hotelId: 'H1' } };
    mockFetchQuery.mockResolvedValue(bookingData);

    await fetchBookingConfirmation({
      ...baseParams,
      loggedOrCCUI: false,
      queryOptions: { staleTime: 0 },
    });

    expect(mockFetchQuery).toHaveBeenCalledWith(expect.objectContaining({ staleTime: 0 }));
  });
});

describe('fetchHotelInformation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('calls fetchQuery with correct queryKey', async () => {
    const hotelData = { hotelInformation: { name: 'Premier Inn London' } };
    mockFetchQuery.mockResolvedValue(hotelData);

    const result = await fetchHotelInformation(mockQueryClient, 'LONEUS', 'en', 'gb');

    expect(mockFetchQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        queryKey: ['GetHotelInformation', 'LONEUS', 'gb', 'en'],
      })
    );
    expect(result).toEqual(hotelData);
  });

  it('calls graphQLRequest with hotelId, language, and country', async () => {
    const hotelData = { hotelInformation: { name: 'Premier Inn London' } };
    mockFetchQuery.mockImplementation(async (opts: any) => opts.queryFn());
    mockGraphQLRequest.mockResolvedValue(hotelData);

    await fetchHotelInformation(mockQueryClient, 'LONEUS', 'en', 'gb');

    expect(mockGraphQLRequest).toHaveBeenCalledWith(expect.anything(), {
      hotelId: 'LONEUS',
      language: 'en',
      country: 'gb',
    });
  });

  it('returns the raw queryClient response', async () => {
    const hotelData = { hotelInformation: { name: 'Test Hotel', brand: 'PI' } };
    mockFetchQuery.mockResolvedValue(hotelData);

    const result = await fetchHotelInformation(mockQueryClient, 'TESTH', 'en', 'gb');

    expect(result).toEqual(hotelData);
  });
});
