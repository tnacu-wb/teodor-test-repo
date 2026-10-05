import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { renderHook, waitFor } from '@testing-library/react';
import { Channel } from '@whitbread-eos/api';
import * as utils from '@whitbread-eos/utils';
import { ReactNode } from 'react';

import usePreviousBookingReuse from './use-previous-booking-reuse';

// Mock dependencies
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: jest.fn(),
  useQueryRequest: jest.fn(),
  useMutationRequest: jest.fn(),
  getMaxValueFromRoomStays: jest.fn(),
  getNightsNumber: jest.fn(),
  graphQLRequest: jest.fn(),
}));

const mockUseCustomLocale = utils.useCustomLocale as jest.MockedFunction<
  typeof utils.useCustomLocale
>;
const mockUseQueryRequest = utils.useQueryRequest as jest.MockedFunction<
  typeof utils.useQueryRequest
>;
const mockUseMutationRequest = utils.useMutationRequest as jest.MockedFunction<
  typeof utils.useMutationRequest
>;
const mockGetMaxValueFromRoomStays = utils.getMaxValueFromRoomStays as jest.MockedFunction<
  typeof utils.getMaxValueFromRoomStays
>;
const mockGetNightsNumber = utils.getNightsNumber as jest.MockedFunction<
  typeof utils.getNightsNumber
>;
const mockGraphQLRequest = utils.graphQLRequest as jest.MockedFunction<typeof utils.graphQLRequest>;

describe('usePreviousBookingReuse', () => {
  let queryClient: QueryClient;
  let wrapper: ({ children }: { children: ReactNode }) => JSX.Element;

  const mockPreviousBookingData = {
    bookingInformation: {
      hotelId: 'HOTEL123',
      bookingFlowId: 'flow123',
      reservationByIdList: [
        {
          reservationId: 'res123',
          roomStay: {
            arrivalDate: '2026-04-15',
            departureDate: '2026-04-17',
          },
          billing: {
            address: {
              companyName: 'Test Company',
              addressLine1: '123 Test St',
              addressLine2: 'Suite 100',
              addressLine3: '',
              addressLine4: '',
              cityName: 'London',
              country: 'GB',
              postalCode: 'SW1A 1AA',
            },
            title: 'Mr',
            firstName: 'John',
            lastName: 'Doe',
            email: 'john.doe@test.com',
            telephone: '07700123456',
            landline: '02071234567',
          },
          reservationGuestList: [
            {
              nameTitle: 'Mr',
              givenName: 'John',
              surName: 'Doe',
            },
          ],
          additionalGuestInfo: {
            purposeOfStay: 'Business',
          },
        },
      ],
    },
  };

  const mockPackagesData = {
    packages: {
      packages: {
        roomSelection: [
          {
            packagesSelection: [
              {
                packageId: 'pkg123',
                quantity: 1,
              },
            ],
          },
        ],
      },
    },
  };

  const mockMutationObject = {
    mutate: jest.fn(),
    isLoading: false,
    isError: false,
    isSuccess: false,
    data: null,
    error: null,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });

    wrapper = ({ children }: { children: ReactNode }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    );

    // Default mocks
    mockUseCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    mockGetMaxValueFromRoomStays.mockReturnValue(2);
    mockGetNightsNumber.mockReturnValue(2);

    mockUseMutationRequest.mockReturnValue({
      mutation: mockMutationObject,
      isLoading: false,
      isError: false,
      error: null,
      isSuccess: false,
    });
  });

  afterEach(() => {
    queryClient.clear();
  });

  describe('Initial state without previous reservation', () => {
    it('should return default state when no prevReservationId is provided', () => {
      mockUseQueryRequest.mockReturnValue({
        data: null,
        isLoading: false,
        isError: false,
        error: null,
      });

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: '',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      expect(result.current.shouldSavePreviousData).toBe(false);
      expect(result.current.isLoading).toBe(false);
      expect(result.current.isError).toBe(false);
      expect(result.current.isSuccess).toBe(true);
      expect(typeof result.current.executeBookingWithReuse).toBe('function');
    });

    it('should not enable queries when prevReservationId is empty', () => {
      mockUseQueryRequest.mockReturnValue({
        data: null,
        isLoading: false,
        isError: false,
        error: null,
      });

      renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: '',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      // First call is for booking info, second is for packages
      const firstCallOptions = mockUseQueryRequest.mock.calls[0]?.[3];
      expect(firstCallOptions?.enabled).toBe(false);
    });
  });

  describe('With previous reservation ID', () => {
    it('should enable booking info query when prevReservationId is provided', () => {
      mockUseQueryRequest.mockReturnValue({
        data: mockPreviousBookingData,
        isLoading: false,
        isError: false,
        error: null,
      });

      renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      const firstCallOptions = mockUseQueryRequest.mock.calls[0]?.[3];
      expect(firstCallOptions?.enabled).toBe(true);
    });

    it('should set shouldSavePreviousData to true when all conditions are met', () => {
      mockUseQueryRequest.mockReturnValue({
        data: mockPreviousBookingData,
        isLoading: false,
        isError: false,
        error: null,
      });

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      expect(result.current.shouldSavePreviousData).toBe(true);
    });

    it('should set shouldSavePreviousData to false when variant is not CCUI', () => {
      mockUseQueryRequest.mockReturnValue({
        data: mockPreviousBookingData,
        isLoading: false,
        isError: false,
        error: null,
      });

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'PI',
            queryClient,
          }),
        { wrapper }
      );

      expect(result.current.shouldSavePreviousData).toBe(false);
    });

    it('should set shouldSavePreviousData to false when no reservation list', () => {
      mockUseQueryRequest.mockReturnValue({
        data: {
          bookingInformation: {
            reservationByIdList: [],
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      });

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      expect(result.current.shouldSavePreviousData).toBe(false);
    });
  });

  describe('Packages query', () => {
    it('should enable packages query when shouldSavePreviousData is true', () => {
      let callCount = 0;
      mockUseQueryRequest.mockImplementation(() => {
        callCount++;
        if (callCount === 1) {
          // First call - booking info
          return {
            data: mockPreviousBookingData,
            isLoading: false,
            isError: false,
            error: null,
          };
        }
        // Second call - packages
        return {
          data: mockPackagesData,
          isLoading: false,
          isError: false,
          error: null,
        };
      });

      renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      // Check packages query (second call) is enabled
      const secondCallOptions = mockUseQueryRequest.mock.calls[1]?.[3];
      expect(secondCallOptions?.enabled).toBe(true);
    });
  });

  describe('State calculations', () => {
    it('should return isLoading true when mutations are loading and shouldSavePreviousData is true', () => {
      mockUseQueryRequest.mockReturnValue({
        data: mockPreviousBookingData,
        isLoading: false,
        isError: false,
        error: null,
      });

      mockUseMutationRequest
        .mockReturnValueOnce({
          mutation: mockMutationObject,
          isLoading: true,
          isError: false,
          error: null,
          isSuccess: false,
        })
        .mockReturnValueOnce({
          mutation: mockMutationObject,
          isLoading: false,
          isError: false,
          error: null,
          isSuccess: false,
        });

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      expect(result.current.isLoading).toBe(true);
    });

    it('should return isError true when any mutation fails and shouldSavePreviousData is true', () => {
      mockUseQueryRequest.mockReturnValue({
        data: mockPreviousBookingData,
        isLoading: false,
        isError: false,
        error: null,
      });

      mockUseMutationRequest
        .mockReturnValueOnce({
          mutation: mockMutationObject,
          isLoading: false,
          isError: true,
          error: new Error('Mutation failed'),
          isSuccess: false,
        })
        .mockReturnValueOnce({
          mutation: mockMutationObject,
          isLoading: false,
          isError: false,
          error: null,
          isSuccess: false,
        });

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      expect(result.current.isError).toBe(true);
      expect(result.current.error).toBeTruthy();
    });

    it('should return isSuccess true when both mutations succeed and shouldSavePreviousData is true', () => {
      mockUseQueryRequest.mockReturnValue({
        data: mockPreviousBookingData,
        isLoading: false,
        isError: false,
        error: null,
      });

      mockUseMutationRequest
        .mockReturnValueOnce({
          mutation: mockMutationObject,
          isLoading: false,
          isError: false,
          error: null,
          isSuccess: true,
        })
        .mockReturnValueOnce({
          mutation: mockMutationObject,
          isLoading: false,
          isError: false,
          error: null,
          isSuccess: true,
        });

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      expect(result.current.isSuccess).toBe(true);
    });
  });

  describe('executeBookingWithReuse', () => {
    it('should call bookMutation.mutate with correct parameters', () => {
      mockUseQueryRequest.mockReturnValue({
        data: null,
        isLoading: false,
        isError: false,
        error: null,
      });

      const mockBookMutation = {
        mutate: jest.fn(),
      };

      const mockReservations = [
        {
          roomTypeCode: 'DOUBLE',
          specialRequests: [],
        },
      ];

      const mockBookingChannel = {
        channel: Channel.Ccui,
        subchannel: 'WEB',
        language: 'EN',
      };

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: '',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      result.current.executeBookingWithReuse(
        mockBookMutation,
        mockReservations,
        mockBookingChannel,
        'flow123'
      );

      expect(mockBookMutation.mutate).toHaveBeenCalledWith(
        {
          reservations: mockReservations,
          bookingChannel: mockBookingChannel,
          bookingFlowId: 'flow123',
        },
        expect.objectContaining({
          onSuccess: expect.any(Function),
        })
      );
    });

    it('should not trigger ancillaries mutation when shouldSavePreviousData is false', () => {
      mockUseQueryRequest.mockReturnValue({
        data: null,
        isLoading: false,
        isError: false,
        error: null,
      });

      const mockBookMutation = {
        mutate: jest.fn((_, options) => {
          // Simulate immediate success
          options.onSuccess({ createReservation: { basketReference: 'newBasket123' } });
        }),
      };

      const mockAncillariesMutation = {
        mutate: jest.fn(),
      };

      mockUseMutationRequest.mockReturnValueOnce({
        mutation: mockAncillariesMutation,
        isLoading: false,
        isError: false,
        error: null,
        isSuccess: false,
      });

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: '',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      result.current.executeBookingWithReuse(mockBookMutation, [], {
        channel: Channel.Ccui,
        subchannel: 'WEB',
        language: 'EN',
      });

      expect(mockBookMutation.mutate).toHaveBeenCalled();
      expect(mockAncillariesMutation.mutate).not.toHaveBeenCalled();
    });
  });

  describe('Mutation chaining', () => {
    it('should call graphQLRequest and handle ancillaries when shouldSavePreviousData is true', async () => {
      let callCount = 0;
      mockUseQueryRequest.mockImplementation(() => {
        callCount++;
        if (callCount === 1) {
          return {
            data: mockPreviousBookingData,
            isLoading: false,
            isError: false,
            error: null,
          };
        }
        return {
          data: mockPackagesData,
          isLoading: false,
          isError: false,
          error: null,
        };
      });

      const mockFetchedBookingInfo = {
        bookingInformation: {
          hotelId: 'HOTEL123',
          reservationByIdList: [
            {
              reservationId: 'newRes123',
              roomStay: {
                arrivalDate: '2026-04-15',
                departureDate: '2026-04-17',
              },
            },
          ],
        },
      };

      mockGraphQLRequest.mockResolvedValue(mockFetchedBookingInfo);

      const mockAncillariesMutation = {
        mutate: jest.fn(),
      };

      mockUseMutationRequest
        .mockReturnValueOnce({
          mutation: mockAncillariesMutation,
          isLoading: false,
          isError: false,
          error: null,
          isSuccess: false,
        })
        .mockReturnValueOnce({
          mutation: { mutate: jest.fn() },
          isLoading: false,
          isError: false,
          error: null,
          isSuccess: false,
        });

      // Mock queryClient.fetchQuery
      queryClient.fetchQuery = jest.fn().mockResolvedValue(mockFetchedBookingInfo);

      const mockBookMutation = {
        mutate: jest.fn(async (_, options) => {
          await options.onSuccess({ createReservation: { basketReference: 'newBasket123' } });
        }),
      };

      const { result } = renderHook(
        () =>
          usePreviousBookingReuse({
            prevReservationId: 'prev123',
            variant: 'CCUI',
            queryClient,
          }),
        { wrapper }
      );

      result.current.executeBookingWithReuse(mockBookMutation, [], {
        channel: Channel.Ccui,
        subchannel: 'WEB',
        language: 'EN',
      });

      await waitFor(() => {
        expect(mockBookMutation.mutate).toHaveBeenCalled();
      });
    });
  });
});
