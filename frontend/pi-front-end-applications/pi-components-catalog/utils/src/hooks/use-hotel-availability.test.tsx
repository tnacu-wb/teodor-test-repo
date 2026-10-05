import {
  useHotelAvailability,
  useHotelAvailabilityBB,
  useHotelAvailabilityCCUI,
  useHotelRatesInformationBB,
  useHotelRatesInformationCCUI,
  useHotelAvailabilityDiscountRate,
  useHotelRatesInformationDiscountRate,
} from './use-hotel-availability';

const mockCustomLocale = jest.fn();
jest.mock('./use-custom-locale', () => () => mockCustomLocale());

const mockQueryRequest = jest.fn();
jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useQueryRequest: (...args: any[]) => mockQueryRequest(...args),
}));

jest.mock('../getters/auth', () => ({
  ...jest.requireActual('../getters/auth'),
  getLoggedInUserInfo: () => jest.fn().mockReturnValue({ operaCompanyId: 'someId' }),
}));

jest.mock('../getters', () => ({
  ...jest.requireActual('../getters'),
  getHotelAvailabilityQueryKey: () =>
    jest
      .fn()
      .mockReturnValue([
        'hotelAvailability',
        'en',
        'gb',
        'pi',
        'MANOLD',
        '2023-05-14',
        '2023-05-15',
        '{"roomType":"DB"}',
      ]),
}));

const mockQueryRequestResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    hotelAvailability: {
      hotelId: 'MANOLD',
      startDate: '2023-05-14',
      endDate: '2023-05-15',
      available: true,
      roomRates: [
        {
          ratePlanCode: 'STANDARD',
          roomTypes: [
            {
              roomType: 'BUSIFLEX',
              adults: 2,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: 'DBLWIN',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  specialRequests: [],
                  roomPriceBreakdown: {
                    totalNetAmount: 200,
                    currencyCode: 'GBP',
                    dailyPrices: {
                      date: '2023-05-14',
                      netPrice: 200,
                    },
                  },
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'BUSIFLEX',
          roomTypes: [
            {
              roomType: 'DB',
              adults: 2,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: 'DBLWIN',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  specialRequests: [],
                  roomPriceBreakdown: {
                    totalNetAmount: 200,
                    currencyCode: 'GBP',
                    dailyPrices: {
                      date: '2023-05-14',
                      netPrice: 200,
                    },
                  },
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'FLEXRATE',
          roomTypes: [
            {
              roomType: 'DB',
              adults: 2,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: 'DBLWIN',
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  specialRequests: [],
                  roomPriceBreakdown: {
                    totalNetAmount: 200,
                    currencyCode: 'GBP',
                    dailyPrices: {
                      date: '2023-05-14',
                      netPrice: 200,
                    },
                  },
                },
              ],
            },
          ],
        },
      ],
    },
    ratesInformation: {
      rateClassifications: [
        {
          rateClassification: 'FLEX',
          rateDescription: 'Flex rate',
          rateName: 'Flex',
          rateOrder: 1,
          rateTags: [],
        },
      ],
    },
  },
};

describe('useHotelAvailability', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
  });

  afterEach(() => {
    mockQueryRequest.mockReset();
  });

  // TODO: replace with useLeisureHotelAvailability as part of DNRQ-46824
  describe('useHotelAvailability PI', () => {
    beforeEach(() => {
      mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
      mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
    });
    it('should return availability and rates information data, error obj, isLoading and isError flags', () => {
      const { isLoading, isError, error, data } = useHotelAvailability(
        'MANOLD',
        'PI',
        '2023-05-14',
        '2023-05-15',
        [],
        'PI'
      );

      expect(isLoading).toBe(false);
      expect(isError).toBe(false);
      expect(error).toEqual({ message: '' });
      expect(data).toEqual(mockQueryRequestResponse.data);
    });

    it('should call useQueryRequest when retrieving hotel availability', () => {
      useHotelAvailability('MANOLD', 'PI', '2023-05-14', '2023-05-15', [], 'PI');
      expect(mockQueryRequest).toHaveBeenCalledTimes(1);
    });
    it('should pass rateName, roomClass and isPromoBox to useQueryRequest', () => {
      useHotelAvailability(
        'MANOLD',
        'PI',
        '2023-05-14',
        '2023-05-15',
        [],
        'PI',
        [],
        undefined,
        'PROMO10',
        undefined,
        undefined,
        'Saver Rate',
        'DB',
        true
      );

      expect(mockQueryRequest).toHaveBeenCalledTimes(1);
    });
    it('should pass placeholderData when isPromoBox is true', () => {
      useHotelAvailability(
        'MANOLD',
        'PI',
        '2023-05-14',
        '2023-05-15',
        [],
        'PI',
        [],
        undefined,
        'PROMO10',
        undefined,
        undefined,
        'Saver Rate',
        'DB',
        true
      );

      const options = mockQueryRequest.mock.calls[0][3];

      expect(options.enabled).toBe(true);
      expect(options.placeholderData).toBeDefined();
      expect(typeof options.placeholderData).toBe('function');

      const previousData = { hotelId: 'MANOLD' };
      expect(options.placeholderData(previousData)).toBe(previousData);
    });
  });

  // TODO: replace with useHotelAvailability as part of DNRQ-46824
  describe('useHotelAvailability BB', () => {
    beforeEach(() => {
      mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
      mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
    });
    it('should return availability data, error obj, isLoading and isError flags', () => {
      mockQueryRequest.mockReturnValue({
        ...mockQueryRequestResponse,
        data: mockQueryRequestResponse.data.hotelAvailability,
      });
      const { isLoading, isError, error, data } = useHotelAvailabilityBB(
        'MANOLD',
        'PI',
        '2023-05-14',
        '2023-05-15',
        [],
        'PI',
        'accessToken'
      );

      expect(isLoading).toBe(false);
      expect(isError).toBe(false);
      expect(error).toEqual({ message: '' });
      expect(data).toEqual(mockQueryRequestResponse.data.hotelAvailability);
    });

    it('should return availability data with default companyId, if no accessToken is provided', () => {
      mockQueryRequest.mockReturnValue({
        ...mockQueryRequestResponse,
        data: mockQueryRequestResponse.data.hotelAvailability,
      });
      const { isLoading, isError, error, data } = useHotelAvailabilityBB(
        'MANOLD',
        'PI',
        '2023-05-14',
        '2023-05-15',
        [],
        'PI',
        ''
      );

      expect(isLoading).toBe(false);
      expect(isError).toBe(false);
      expect(error).toEqual({ message: '' });
      expect(data).toEqual(mockQueryRequestResponse.data.hotelAvailability);
    });

    it('should call useQueryRequest when retrieving hotel availability BB', () => {
      useHotelAvailabilityBB('MANOLD', 'PI', '2023-05-14', '2023-05-15', [], 'PI', 'accessToken');
      expect(mockQueryRequest).toHaveBeenCalledTimes(1);
    });
    it('should pass rateName, roomClass and isPromoBox for BB', () => {
      useHotelAvailabilityBB(
        'MANOLD',
        'PI',
        '2023-05-14',
        '2023-05-15',
        [],
        'BB',
        'accessToken',
        'PROMO10',
        undefined,
        'Saver Rate',
        'DB',
        true
      );

      expect(mockQueryRequest).toHaveBeenCalledTimes(1);
    });
    it('should pass placeholderData for BB when isPromoBox is true', () => {
      useHotelAvailabilityBB(
        'MANOLD',
        'PI',
        '2023-05-14',
        '2023-05-15',
        [],
        'BB',
        'accessToken',
        'PROMO10',
        undefined,
        'Saver Rate',
        'DB',
        true
      );

      const options = mockQueryRequest.mock.calls[0][3];

      expect(options.enabled).toBe(true);
    });
  });

  describe('useHotelAvailability CCUI', () => {
    beforeEach(() => {
      mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
      mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
    });
    it('should return availability data, error obj, isLoading and isError flags', () => {
      mockQueryRequest.mockReturnValue({
        ...mockQueryRequestResponse,
        data: mockQueryRequestResponse.data.hotelAvailability,
      });
      const { isLoading, isError, error, data } = useHotelAvailabilityCCUI(
        'MANOLD',
        'PI',
        '2023-10-14',
        '2023-10-15',
        [],
        'CCUI',
        true,
        '2569944'
      );

      expect(isLoading).toBe(false);
      expect(isError).toBe(false);
      expect(error).toEqual({ message: '' });
      expect(data).toEqual(mockQueryRequestResponse.data.hotelAvailability);
    });

    it('should return availability data without companyId, if no companyId is provided', () => {
      mockQueryRequest.mockReturnValue({
        ...mockQueryRequestResponse,
        data: mockQueryRequestResponse.data.hotelAvailability,
      });
      const { isLoading, isError, error, data } = useHotelAvailabilityCCUI(
        'MANOLD',
        'PI',
        '2023-10-14',
        '2023-10-15',
        [],
        'CCUI',
        true
      );

      expect(isLoading).toBe(false);
      expect(isError).toBe(false);
      expect(error).toEqual({ message: '' });
      expect(data).toEqual(mockQueryRequestResponse.data.hotelAvailability);
    });

    it('should call useQueryRequest when retrieving hotel availability CCUI', () => {
      useHotelAvailabilityCCUI('MANOLD', 'PI', '2023-10-14', '2023-10-15', [], 'CCUI', true);
      expect(mockQueryRequest).toHaveBeenCalledTimes(1);
    });
    it('should pass rateName, roomClass and isPromoBox for CCUI', () => {
      useHotelAvailabilityCCUI(
        'MANOLD',
        'PI',
        '2023-10-14',
        '2023-10-15',
        [],
        'CCUI',
        true,
        '2569944',
        'PROMO10',
        undefined,
        'Saver Rate',
        'DB',
        true
      );

      expect(mockQueryRequest).toHaveBeenCalledTimes(1);
    });

    it('should pass placeholderData for CCUI when isPromoBox is true', () => {
      useHotelAvailabilityCCUI(
        'MANOLD',
        'PI',
        '2023-10-14',
        '2023-10-15',
        [],
        'CCUI',
        true,
        '2569944',
        'PROMO10',
        undefined,
        'Saver Rate',
        'DB',
        true
      );

      const options = mockQueryRequest.mock.calls[0][3];

      expect(options.enabled).toBe(true);
    });
  });

  // TODO: replace with useHotelRatesInformation as part of DNRQ-46824
  describe('useHotelRatesInformation BB', () => {
    beforeEach(() => {
      mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
      mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
    });
    it('should return rates information data, error obj, isLoading and isError flags', () => {
      mockQueryRequest.mockReturnValue({
        ...mockQueryRequestResponse,
        data: mockQueryRequestResponse.data.ratesInformation,
      });
      const { isLoading, isError, error, data } = useHotelRatesInformationBB(
        'MANOLD',
        'PI',
        { hotelAvailability: mockQueryRequestResponse.data.hotelAvailability },
        'PI',
        'accessToken'
      );

      expect(isLoading).toBe(false);
      expect(isError).toBe(false);
      expect(error).toEqual({ message: '' });
      expect(data).toEqual({
        hotelAvailability: mockQueryRequestResponse.data.hotelAvailability,
        rateClassifications: mockQueryRequestResponse.data.ratesInformation.rateClassifications,
      });
    });

    it('should call useQueryRequest when retrieving rates information BB', () => {
      useHotelRatesInformationBB(
        'MANOLD',
        'PI',
        { hotelAvailability: mockQueryRequestResponse.data.hotelAvailability },
        'PI',
        'accessToken'
      );
      expect(mockQueryRequest).toHaveBeenCalledTimes(1);
    });
    it('should pass placeholderData for BB when isPromoBox is true', () => {
      useHotelAvailabilityBB(
        'MANOLD',
        'PI',
        '2023-05-14',
        '2023-05-15',
        [],
        'BB',
        'accessToken',
        'PROMO10',
        undefined,
        'Saver Rate',
        'DB',
        true
      );

      const options = mockQueryRequest.mock.calls[0][3];

      expect(options.enabled).toBe(true);
      expect(options.placeholderData).toBeDefined();
      expect(typeof options.placeholderData).toBe('function');

      const previousData = {
        hotelAvailability: {
          hotelId: 'MANOLD',
        },
      };
      expect(options.placeholderData(previousData)).toBe(previousData);
    });
  });

  describe('useHotelRatesInformation CCUI', () => {
    beforeEach(() => {
      mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
      mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
    });
    it('should return rates information data, error obj, isLoading and isError flags', () => {
      mockQueryRequest.mockReturnValue({
        ...mockQueryRequestResponse,
        data: mockQueryRequestResponse.data.ratesInformation,
      });
      const { isLoading, isError, error, data } = useHotelRatesInformationCCUI(
        'MANOLD',
        'PI',
        { hotelAvailability: mockQueryRequestResponse.data.hotelAvailability },
        'CCUI',
        true
      );

      expect(isLoading).toBe(false);
      expect(isError).toBe(false);
      expect(error).toEqual({ message: '' });
      expect(data).toEqual({
        hotelAvailability: mockQueryRequestResponse.data.hotelAvailability,
        rateClassifications: mockQueryRequestResponse.data.ratesInformation.rateClassifications,
      });
    });

    it('should call useQueryRequest when retrieving rates information CCUI', () => {
      useHotelRatesInformationCCUI(
        'MANOLD',
        'PI',
        { hotelAvailability: mockQueryRequestResponse.data.hotelAvailability },
        'CCUI',
        true
      );
      expect(mockQueryRequest).toHaveBeenCalledTimes(1);
    });
  });
  it('should pass placeholderData for CCUI when isPromoBox is true', () => {
    useHotelAvailabilityCCUI(
      'MANOLD',
      'PI',
      '2023-10-14',
      '2023-10-15',
      [],
      'CCUI',
      true,
      '2569944',
      'PROMO10',
      undefined,
      'Saver Rate',
      'DB',
      true
    );

    const options = mockQueryRequest.mock.calls[0][3];

    expect(options.enabled).toBe(true);
    expect(options.placeholderData).toBeDefined();
    expect(typeof options.placeholderData).toBe('function');

    const previousData = {
      hotelAvailability: {
        hotelId: 'MANOLD',
      },
    };
    expect(options.placeholderData(previousData)).toBe(previousData);
  });
});

describe('useHotelAvailabilityDiscountRate PI', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
  });
  it('should return availability data, error obj, isLoading and isError flags', () => {
    mockQueryRequest.mockReturnValue({
      ...mockQueryRequestResponse,
      data: mockQueryRequestResponse.data,
    });
    const { isLoading, isError, error, data } = useHotelAvailabilityDiscountRate({
      hotelId: 'MANOLD',
      hotelBrand: 'PI',
      arrival: '2023-10-14',
      departure: '2023-10-15',
      rooms: [],
      channel: 'PI',
      queryEnabled: true,
      companyId: '2569944',
    });

    expect(isLoading).toBe(false);
    expect(isError).toBe(false);
    expect(error).toEqual({ message: '' });
    expect(data).toEqual(mockQueryRequestResponse.data);
  });
});

describe('useHotelRatesInformationDiscountRate PI', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
  });
  it('should return rates information data, error obj, isLoading and isError flags', () => {
    mockQueryRequest.mockReturnValue({
      ...mockQueryRequestResponse,
      data: mockQueryRequestResponse.data.ratesInformation,
    });
    const { isLoading, isError, error, data } = useHotelRatesInformationDiscountRate(
      'MANOLD',
      'PI',
      { hotelAvailability: mockQueryRequestResponse.data.hotelAvailability },
      'PI',
      true
    );

    expect(isLoading).toBe(false);
    expect(isError).toBe(false);
    expect(error).toEqual({ message: '' });
    expect(data).toEqual({
      hotelAvailability: mockQueryRequestResponse.data.hotelAvailability,
      rateClassifications: mockQueryRequestResponse.data.ratesInformation.rateClassifications,
    });
  });
});
