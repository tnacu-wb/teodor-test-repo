import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Channel } from '@whitbread-eos/api';
import { graphQLRequest, isSameDate } from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { GraphQLClient } from 'graphql-request';

import {
  getGuestsPlaceholderString,
  getPromotionsInformation,
  getAmendPromotionsInfo,
  handleCheckDateIsSame,
  renderPromoNotification,
} from './helpers';
import { mockedRoomAvailabilityLabels } from './mockResponse';

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  PromotionsNotification: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  graphQLRequest: jest.fn(),
  isSameDate: jest.fn(),
}));

describe('getGuestsPlaceholderString', () => {
  it('should not display the children placeholder if the childrenNumber is equal to 0', () => {
    expect(getGuestsPlaceholderString(2, 0, mockedRoomAvailabilityLabels)).toBe('2 adults,');
  });
  it('should display the correct placeholder if the childrenNumber is equal to 1', () => {
    expect(getGuestsPlaceholderString(1, 1, mockedRoomAvailabilityLabels)).toBe(
      '1 adult, 1 child,'
    );
  });
  it('should display the correct placeholder if the childrenNumber is equal to 2', () => {
    expect(getGuestsPlaceholderString(1, 2, mockedRoomAvailabilityLabels)).toBe(
      '1 adult, 2 children,'
    );
  });
});

describe('getPromotionsInformation', () => {
  const mockQueryClient = {
    fetchQuery: jest.fn(),
  } as unknown as QueryClient;

  const mockGraphQLClient = {} as GraphQLClient;
  const mockPromoResponse = {
    promotionsInformation: {
      promoId: 'PROMO123',
      description: 'Special Discount',
    },
  };

  const mockParams = {
    arrival: '2025-10-20',
    departure: '2025-10-25',
    country: 'gb',
    language: 'en',
    brand: 'PID',
    channel: 'PI',
    basketReference: 'BASKET123',
    queryClient: mockQueryClient,
    client: mockGraphQLClient,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return null if isPromoCodeLandingPageEnabled is false', async () => {
    const result = await getPromotionsInformation(
      mockParams.arrival,
      mockParams.departure,
      mockParams.country,
      mockParams.language,
      mockParams.brand,
      mockParams.channel as Channel,
      mockParams.basketReference,
      mockParams.queryClient,
      mockParams.client,
      false
    );
    expect(result).toBeNull();
    expect(mockQueryClient.fetchQuery).not.toHaveBeenCalled();
  });

  it('should call fetchQuery with correct queryKey and return promotionsInformation when enabled', async () => {
    (graphQLRequest as jest.Mock).mockResolvedValue(mockPromoResponse);
    (mockQueryClient.fetchQuery as jest.Mock).mockImplementation(({ queryFn }) => queryFn());

    const promotionCode = 'PROMO123';
    const isPromoBox = true;
    const rateName = 'FLEX';
    const roomClass = 'ST';

    const result = await getPromotionsInformation(
      mockParams.arrival,
      mockParams.departure,
      mockParams.country,
      mockParams.language,
      mockParams.brand,
      mockParams.channel as Channel,
      mockParams.basketReference,
      mockParams.queryClient,
      mockParams.client,
      true,
      promotionCode,
      isPromoBox,
      rateName,
      roomClass
    );

    const formattedStart = format(new Date(mockParams.arrival), 'yyyy-MM-dd');
    const formattedEnd = format(new Date(mockParams.departure), 'yyyy-MM-dd');

    expect(mockQueryClient.fetchQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        queryKey: [
          'promotionsInformation',
          mockParams.country,
          mockParams.language,
          mockParams.brand,
          mockParams.channel,
          formattedStart,
          formattedEnd,
          mockParams.basketReference,
          promotionCode,
          isPromoBox,
          rateName,
          roomClass,
        ],
        queryFn: expect.any(Function),
      })
    );

    expect(graphQLRequest).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({
        country: mockParams.country,
        language: mockParams.language,
        brand: mockParams.brand,
        channel: mockParams.channel,
        stayStartDate: formattedStart,
        stayEndDate: formattedEnd,
        basketReference: mockParams.basketReference,
        promotionCode: promotionCode,
        isPromoBox: isPromoBox,
        rateName: rateName,
        roomClass: roomClass,
      }),
      undefined,
      undefined,
      mockParams.client
    );

    expect(result).toEqual(mockPromoResponse.promotionsInformation);
  });

  it('should throw an error if fetchQuery throws an error', async () => {
    (mockQueryClient.fetchQuery as jest.Mock).mockRejectedValue(new Error('Network error'));

    await expect(
      getPromotionsInformation(
        mockParams.arrival,
        mockParams.departure,
        mockParams.country,
        mockParams.language,
        mockParams.brand,
        mockParams.channel as Channel,
        mockParams.basketReference,
        mockParams.queryClient,
        mockParams.client,
        true
      )
    ).rejects.toThrow('Network error');
  });

  it('should use default values when optional params are not provided', async () => {
    (graphQLRequest as jest.Mock).mockResolvedValue(mockPromoResponse);
    (mockQueryClient.fetchQuery as jest.Mock).mockImplementation(({ queryFn }) => queryFn());

    await getPromotionsInformation(
      mockParams.arrival,
      mockParams.departure,
      mockParams.country,
      mockParams.language,
      mockParams.brand,
      mockParams.channel as Channel,
      mockParams.basketReference,
      mockParams.queryClient,
      mockParams.client,
      true
    );

    expect(graphQLRequest).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({
        promotionCode: '',
        isPromoBox: false,
        rateName: undefined,
        roomClass: undefined,
      }),
      undefined,
      undefined,
      mockParams.client
    );
  });
});

describe('handleCheckDateIsSame', () => {
  const mockIsSameDate = isSameDate as jest.Mock;

  const originalArrival = new Date('2024-01-01');
  const originalDeparture = new Date('2024-01-05');
  const newArrival = new Date('2024-01-02');
  const newDeparture = new Date('2024-01-06');

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call isSameDate twice with correct arguments', () => {
    mockIsSameDate.mockReturnValue(false);

    handleCheckDateIsSame(originalArrival, originalDeparture, newArrival, newDeparture);

    expect(mockIsSameDate).toHaveBeenCalledTimes(2);
    expect(mockIsSameDate).toHaveBeenNthCalledWith(1, originalArrival, newArrival);
    expect(mockIsSameDate).toHaveBeenNthCalledWith(2, originalDeparture, newDeparture);
  });

  it('should return undefined when arrival or departure dates differ', () => {
    mockIsSameDate.mockReturnValue(false);

    const result = handleCheckDateIsSame(
      originalArrival,
      originalDeparture,
      newArrival,
      newDeparture
    );

    expect(result).toBeUndefined();
  });

  it('should return undefined when both arrival and departure are the same (implicit return)', () => {
    mockIsSameDate.mockReturnValue(true);

    const result = handleCheckDateIsSame(
      originalArrival,
      originalDeparture,
      originalArrival,
      originalDeparture
    );

    expect(result).toBeUndefined();
    expect(mockIsSameDate).toHaveBeenCalledTimes(2);
  });
});

describe('renderPromoNotification', () => {
  const mockPromoData = {
    showPromo: true,
    promoId: 'PROMO123',
    description: 'Special Discount',
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return null if promoRoomsData is undefined', () => {
    const result = renderPromoNotification(undefined);
    expect(result).toBeNull();
  });

  it('should return null if promoRoomsData is null', () => {
    const result = renderPromoNotification(null);
    expect(result).toBeNull();
  });

  it('should return null if showPromo is false', () => {
    const result = renderPromoNotification({
      ...mockPromoData,
      showPromo: false,
    });
    expect(result).toBeNull();
  });

  it('should return a React element if showPromo is true', () => {
    const result = renderPromoNotification(mockPromoData);
    expect(result).not.toBeNull();
    expect(result?.type).toBeDefined();
  });
});

describe('getAmendPromotionsInfo', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should use legacy promotions information flow when feature flag is disabled', async () => {
    const mockQueryClient = {
      fetchQuery: jest.fn().mockResolvedValue({
        promotionsInformation: {
          showPromo: true,
        },
      }),
    };

    const result = await getAmendPromotionsInfo({
      isPromotionsInHotelAvailabilityEnabled: false,
      isPromoCodeLandingPageEnabled: true,
      channel: Channel.Pi,
      hotelId: 'HOTEL1',
      arrival: '2025-01-01',
      departure: '2025-01-02',
      country: 'GB',
      language: 'en',
      brand: 'PI',
      rooms: [],
      originalBasketReference: 'ABC123',
      queryClient: mockQueryClient as any,
      client: {} as any,
    });

    expect(mockQueryClient.fetchQuery).toHaveBeenCalled();

    expect(result).toEqual({
      showPromo: true,
    });
  });

  it('should return promotions information from hotel availability response when feature flag is enabled', async () => {
    (graphQLRequest as jest.Mock).mockResolvedValue({
      hotelAvailability: {
        promotionsInformation: {
          showPromo: true,
          promoBookingInfo: {
            promotionCode: 'SAVE10',
          },
        },
      },
    });

    const result = await getAmendPromotionsInfo({
      isPromotionsInHotelAvailabilityEnabled: true,
      isPromoCodeLandingPageEnabled: true,
      channel: Channel.Pi,
      hotelId: 'HOTEL1',
      arrival: '2025-01-01',
      departure: '2025-01-02',
      country: 'GB',
      language: 'en',
      brand: 'PI',
      rooms: [
        {
          adultsNumber: 2,
          childrenNumber: 0,
          roomType: 'DB',
        },
      ],
      originalBasketReference: 'ABC123',
      queryClient: {} as any,
      client: {} as any,
    });

    expect(graphQLRequest).toHaveBeenCalled();

    expect(result).toEqual({
      showPromo: true,
      promoBookingInfo: {
        promotionCode: 'SAVE10',
      },
    });
  });

  it('should use default room payload when rooms array is empty', async () => {
    (graphQLRequest as jest.Mock).mockResolvedValue({
      hotelAvailability: {
        promotionsInformation: null,
      },
    });

    await getAmendPromotionsInfo({
      isPromotionsInHotelAvailabilityEnabled: true,
      isPromoCodeLandingPageEnabled: true,
      channel: Channel.Pi,
      hotelId: 'HOTEL1',
      arrival: '2025-01-01',
      departure: '2025-01-02',
      country: 'GB',
      language: 'en',
      brand: 'PI',
      rooms: [],
      originalBasketReference: '',
      queryClient: {} as any,
      client: {} as any,
    });

    expect(graphQLRequest).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({
        rooms: [
          {
            adultsNumber: 1,
            childrenNumber: 0,
            cotRequired: false,
            roomType: '',
          },
        ],
      }),
      undefined,
      undefined,
      expect.anything()
    );
  });

  it('should return null when hotel availability request fails', async () => {
    const errorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

    (graphQLRequest as jest.Mock).mockRejectedValue(new Error('request failed'));

    const result = await getAmendPromotionsInfo({
      isPromotionsInHotelAvailabilityEnabled: true,
      isPromoCodeLandingPageEnabled: true,
      channel: Channel.Pi,
      hotelId: 'HOTEL1',
      arrival: '2025-01-01',
      departure: '2025-01-02',
      country: 'GB',
      language: 'en',
      brand: 'PI',
      rooms: [],
      originalBasketReference: '',
      queryClient: {} as any,
      client: {} as any,
    });

    expect(result).toBeNull();

    expect(errorSpy).toHaveBeenCalledWith(
      '[getAmendPromotionsInfo] hotelAvailability call failed:',
      expect.any(Error)
    );

    errorSpy.mockRestore();
  });
});
