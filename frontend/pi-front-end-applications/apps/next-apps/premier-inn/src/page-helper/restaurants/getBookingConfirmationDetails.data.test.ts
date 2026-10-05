import { QueryClient } from '@tanstack/react-query';
import { GET_ENQUIRY_INFO_BY_ID, GET_EVENT_INFO_BY_ID } from '@whitbread-eos/api';

import { getBookingConfirmationDetailsDatFn } from './getBookingConfirmationDetails.data';

const queryClient = new QueryClient();

jest.mock('@whitbread-eos/utils', () => ({
  instrumentQueryClient: jest.fn(),
  logger: {
    info: jest.fn(),
  },
  getDefaultSessionTracing: jest.fn(() => ({
    'WB-SESSION-ID': 'test-session-id',
  })),
  graphQLRequestRestaurants: jest.fn(),
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: jest.fn(),
    set: jest.fn(),
  }));
});

describe('getBookingConfirmationDetailsDatFn', () => {
  const mockReq = {} as never;
  const mockRes = {} as never;
  const mockQuery = { test: 'query' };

  let mockGraphQLRequest: jest.Mock;
  let mockInstrumentQueryClient: jest.Mock;
  let mockLogger: { info: jest.Mock };
  let mockGetDefaultSessionTracing: jest.Mock;

  beforeEach(() => {
    jest.clearAllMocks();

    const utils = jest.requireMock('@whitbread-eos/utils');
    mockGraphQLRequest = utils.graphQLRequestRestaurants as jest.Mock;
    mockInstrumentQueryClient = utils.instrumentQueryClient as jest.Mock;
    mockLogger = utils.logger as { info: jest.Mock };
    mockGetDefaultSessionTracing = utils.getDefaultSessionTracing as jest.Mock;

    mockInstrumentQueryClient.mockReturnValue({
      prefetchQuery: jest.fn(async (_queryKey: unknown[], queryFn: () => unknown) => {
        return queryFn();
      }),
    });
  });

  describe('with eventId', () => {
    it('should successfully fetch event details when eventId is provided', async () => {
      const mockEventData = { event: { id: 'event-123', status: 'confirmed' } };
      mockGraphQLRequest.mockResolvedValue(mockEventData);

      const result = await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: 'event-123',
        enquiryId: '',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(mockInstrumentQueryClient).toHaveBeenCalledWith(queryClient);
      expect(mockGraphQLRequest).toHaveBeenCalledWith(GET_EVENT_INFO_BY_ID, { id: 'event-123' });
      expect(mockLogger.info).toHaveBeenCalledWith({
        label: 'AEM Data for TB:START_DATA_FETCHING',
        msg: expect.objectContaining({
          query: mockQuery,
          'WB-SESSION-ID': 'test-session-id',
        }),
      });
      expect(mockLogger.info).toHaveBeenCalledWith({
        label: 'TB:BookingConfirmationDetails:booking confirmation details',
        msg: {
          eventId: 'event-123',
          enquiryId: '',
        },
      });
      expect(result).toHaveProperty('dehydratedState');
      expect(result.dehydratedState).toHaveProperty('queries');
      expect(result.dehydratedState).toHaveProperty('mutations');
    });

    it('should use GetEventById query key when eventId is provided', async () => {
      const mockPrefetchQuery = jest.fn(async (_queryKey: unknown[], queryFn: () => unknown) => {
        return queryFn();
      });
      mockInstrumentQueryClient.mockReturnValue({
        prefetchQuery: mockPrefetchQuery,
      });
      mockGraphQLRequest.mockResolvedValue({ event: {} });

      await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: 'event-123',
        enquiryId: '',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(mockPrefetchQuery).toHaveBeenCalledWith(['GetEventById'], expect.any(Function));
    });

    it('should handle errors when fetching event details', async () => {
      const mockError = new Error('Failed to fetch event');
      mockGraphQLRequest.mockRejectedValue(mockError);

      const result = await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: 'event-123',
        enquiryId: '',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(mockLogger.info).toHaveBeenCalledWith({
        label: 'TB:BookingConfirmationDetails:booking confirmation error',
        msg: {
          error: mockError,
        },
      });
      expect(result).toHaveProperty('dehydratedState');
    });
  });

  describe('with enquiryId', () => {
    it('should successfully fetch enquiry details when enquiryId is provided', async () => {
      const mockEnquiryData = { enquiry: { id: 'enquiry-456', status: 'pending' } };
      mockGraphQLRequest.mockResolvedValue(mockEnquiryData);

      const result = await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: '',
        enquiryId: 'enquiry-456',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(mockGraphQLRequest).toHaveBeenCalledWith(GET_ENQUIRY_INFO_BY_ID, {
        id: 'enquiry-456',
      });
      expect(mockLogger.info).toHaveBeenCalledWith({
        label: 'TB:BookingConfirmationDetails:booking confirmation details',
        msg: {
          eventId: '',
          enquiryId: 'enquiry-456',
        },
      });
      expect(result).toHaveProperty('dehydratedState');
    });

    it('should use GetEnquiryById query key when enquiryId is provided', async () => {
      const mockPrefetchQuery = jest.fn(async (_queryKey: unknown[], queryFn: () => unknown) => {
        return queryFn();
      });
      mockInstrumentQueryClient.mockReturnValue({
        prefetchQuery: mockPrefetchQuery,
      });
      mockGraphQLRequest.mockResolvedValue({ enquiry: {} });

      await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: '',
        enquiryId: 'enquiry-456',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(mockPrefetchQuery).toHaveBeenCalledWith(['GetEnquiryById'], expect.any(Function));
    });

    it('should handle errors when fetching enquiry details', async () => {
      const mockError = new Error('Failed to fetch enquiry');
      mockGraphQLRequest.mockRejectedValue(mockError);

      const result = await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: '',
        enquiryId: 'enquiry-456',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(mockLogger.info).toHaveBeenCalledWith({
        label: 'TB:BookingConfirmationDetails:booking confirmation error',
        msg: {
          error: mockError,
        },
      });
      expect(result).toHaveProperty('dehydratedState');
    });
  });

  describe('session tracking', () => {
    it('should call getDefaultSessionTracing with cookies', async () => {
      mockGraphQLRequest.mockResolvedValue({});

      await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: 'event-123',
        enquiryId: '',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(mockGetDefaultSessionTracing).toHaveBeenCalled();
    });

    it('should log session tracing data in start message', async () => {
      mockGraphQLRequest.mockResolvedValue({});
      mockGetDefaultSessionTracing.mockReturnValue({
        'WB-SESSION-ID': 'custom-session-123',
      });

      await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: 'event-123',
        enquiryId: '',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(mockLogger.info).toHaveBeenCalledWith({
        label: 'AEM Data for TB:START_DATA_FETCHING',
        msg: expect.objectContaining({
          'WB-SESSION-ID': 'custom-session-123',
        }),
      });
    });
  });

  describe('query client dehydration', () => {
    it('should return dehydrated state on success', async () => {
      mockGraphQLRequest.mockResolvedValue({ event: { id: 'test' } });

      const result = await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: 'event-123',
        enquiryId: '',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(result).toEqual({
        dehydratedState: expect.objectContaining({
          queries: expect.any(Array),
          mutations: expect.any(Array),
        }),
      });
    });

    it('should return dehydrated state on error', async () => {
      mockGraphQLRequest.mockRejectedValue(new Error('API Error'));

      const result = await getBookingConfirmationDetailsDatFn({
        queryClient,
        eventId: 'event-123',
        enquiryId: '',
        query: mockQuery,
        req: mockReq,
        res: mockRes,
        resolvedUrl: '/booking-confirmation',
      });

      expect(result).toEqual({
        dehydratedState: expect.objectContaining({
          queries: expect.any(Array),
          mutations: expect.any(Array),
        }),
      });
    });
  });
});
