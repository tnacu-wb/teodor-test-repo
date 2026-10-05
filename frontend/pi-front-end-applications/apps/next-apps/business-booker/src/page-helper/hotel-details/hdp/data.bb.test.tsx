import { GET_PROMO_INFORMATION, HOTEL_AVAILABILITY_BB_QUERY } from '@whitbread-eos/api';

describe('createHDPBbDataLoaderFn', () => {
  let mockQueryClient: any;
  let mockReq: any;
  let mockRes: any;
  let mockCookies: any;
  let mockQueriesLogger: any;
  let mockGraphQLRequest: jest.Mock;
  let mockGetInnBusinessServerSideProps: jest.Mock;
  let mockGetStaticContent: jest.Mock;
  let mockGetAvailabilityParamsFromUrl: jest.Mock;
  let mockDecodeIdToken: jest.Mock;
  let mockAxiosRequest: jest.Mock;
  let mockGetLoggedInUserInfo: jest.Mock;
  let mockDehydrate: jest.Mock;

  const defaultProps = {
    queryClient: {},
    language: 'en',
    country: 'GB',
    query: {},
    params: { slug: ['london', 'hotel'] },
    req: {},
    res: {},
    featureToggles: { FT_PI_BB_CCUI_BARRIER_FREE_LABEL: true },
    isInnBusinessAppPage: false,
    getGQLClient: jest.fn(() => ({
      request: jest.fn(),
    })),
    resolvedUrl: '/hotels/london/hotel?arrival=2024-01-01&departure=2024-01-02&rooms=1',
  };

  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks();

    mockQueryClient = {};
    mockReq = {
      url: '/hotels/london/hotel?arrival=2024-01-01&departure=2024-01-02&rooms=1',
      headers: {},
    };
    mockRes = {};
    mockCookies = { get: jest.fn() };
    mockQueriesLogger = jest.fn().mockImplementation(() => ({
      prefetchQuery: jest.fn((key, fn) => Promise.resolve(fn())),
      fetchQuery: jest.fn((key, fn) => Promise.resolve(fn())),
      logQueries: jest.fn(),
    }));
    mockGraphQLRequest = jest.fn();
    mockGetInnBusinessServerSideProps = jest.fn();
    mockGetStaticContent = jest.fn();
    mockGetAvailabilityParamsFromUrl = jest.fn();
    mockDecodeIdToken = jest.fn();
    mockAxiosRequest = jest.fn();
    mockGetLoggedInUserInfo = jest.fn();
    mockDehydrate = jest.fn().mockReturnValue('dehydratedState');

    jest.doMock('cookies', () => {
      return jest.fn(() => mockCookies);
    });
    jest.doMock('@whitbread-eos/utils', () => ({
      ...jest.requireActual('@whitbread-eos/utils'),
      QueriesLogger: mockQueriesLogger,
      graphQLRequest: mockGraphQLRequest,
      getInnBusinessServerSideProps: mockGetInnBusinessServerSideProps,
      getStaticContent: mockGetStaticContent,
      getAvailabilityParamsFromUrl: mockGetAvailabilityParamsFromUrl,
      decodeIdToken: mockDecodeIdToken,
      axiosRequest: mockAxiosRequest,
      getLoggedInUserInfo: mockGetLoggedInUserInfo,
      ID_TOKEN_COOKIE: 'id_token',
      getGQLClient: jest.fn(),
    }));
    jest.doMock('@tanstack/react-query', () => ({
      dehydrate: mockDehydrate,
      QueryClient: jest.fn(),
    }));

    process.env.NEXT_PUBLIC_ACCOUNT_SERVICE = 'http://account-service';
  });

  afterEach(() => {
    jest.resetModules();
  });

  it('should load static hotel data and static content, return dehydrated state and innBusiness undefined if not inn business app page', async () => {
    mockReq.url =
      '/hotels/london/hotel?ARRyyyy=2024&ARRmm=01&ARRdd=01&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0';
    mockCookies.get.mockReturnValue(undefined);
    mockGraphQLRequest.mockResolvedValueOnce({
      hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' },
    });
    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
      isInnBusinessAppPage: false,
      resolvedUrl: '/hotels/london/hotel?arrival=2024-01-01&departure=2024-01-02&rooms=1',
    });

    expect(mockGraphQLRequest).toHaveBeenCalled();
    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({
        slug: '/hotels/london/hotel',
        language: 'en',
        country: 'GB',
        stayStartDate: '2024-01-01',
        stayEndDate: '2024-01-02',
      }),
      undefined,
      undefined,
      undefined
    );
    expect(result.dehydratedState).toBe('dehydratedState');
    expect(result.innBusiness).toBeUndefined();
  });

  it('should include arrival and departure in static hotel cache key', async () => {
    mockReq.url =
      '/hotels/london/hotel?ARRyyyy=2024&ARRmm=01&ARRdd=01&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0';
    const fetchQueryMock = jest.fn((key, fn) => Promise.resolve(fn()));
    mockQueriesLogger.mockImplementation(() => ({
      prefetchQuery: jest.fn((key, fn) => Promise.resolve(fn())),
      fetchQuery: fetchQueryMock,
      logQueries: jest.fn(),
    }));

    mockCookies.get.mockReturnValue(undefined);
    mockGraphQLRequest.mockResolvedValue({
      hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' },
    });
    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    await createHDPBbDataLoaderFn({
      ...defaultProps,
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    } as any);

    expect(fetchQueryMock).toHaveBeenCalledWith(
      ['staticHotelInformation', 'en', 'GB', '/hotels/london/hotel', '2024-01-01', '2024-01-02'],
      expect.any(Function)
    );
  });

  it('should handle staticHotelQuery rejection gracefully', async () => {
    mockCookies.get.mockReturnValue(undefined);
    mockGraphQLRequest.mockRejectedValueOnce(new Error('fail'));
    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    } as any);

    expect(result.dehydratedState).toBe('dehydratedState');
    expect(result.innBusiness).toBeUndefined();
  });

  it('should fetch user details, hotel availability, rates info, and SEO info if idTokenCookie exists', async () => {
    mockCookies.get.mockReturnValue('token');
    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotelQuery
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({ hotelAvailability: { roomRates: [{ ratePlanCode: 'RP1' }] } }) // hotelAvailability
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({});
    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
    });
    mockDecodeIdToken.mockReturnValue({ email: 'user@email.com' });
    mockAxiosRequest.mockResolvedValue({});
    mockGetLoggedInUserInfo.mockReturnValue({ operaCompanyId: 'cid' });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    } as any);

    expect(mockAxiosRequest).toHaveBeenCalled();
    expect(mockGraphQLRequest).toHaveBeenCalledTimes(5);
    expect(result.dehydratedState).toBe('dehydratedState');
  });

  it('should not fetch hotel availability and rates info if numberOfNights is 0', async () => {
    mockCookies.get.mockReturnValue('token');
    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotelQuery
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({});
    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 0,
    });
    mockDecodeIdToken.mockReturnValue({ email: 'user@email.com' });
    mockAxiosRequest.mockResolvedValue({});
    mockGetLoggedInUserInfo.mockReturnValue({ operaCompanyId: 'cid' });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    } as any);

    expect(mockGraphQLRequest).toHaveBeenCalledTimes(3);
    expect(result.dehydratedState).toBe('dehydratedState');
  });

  it('should log queries at the end', async () => {
    const logQueries = jest.fn();
    mockQueriesLogger.mockImplementation(() => ({
      prefetchQuery: jest.fn((key, fn) => Promise.resolve(fn())),
      fetchQuery: jest.fn((key, fn) => Promise.resolve(fn())),
      logQueries,
    }));
    mockCookies.get.mockReturnValue(undefined);
    mockGraphQLRequest.mockResolvedValueOnce({
      hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' },
    });
    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    await createHDPBbDataLoaderFn({
      ...defaultProps,
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    } as any);

    expect(logQueries).toHaveBeenCalled();
  });
  it('should call promo information API when promo landing page is enabled and promoId exists', async () => {
    mockCookies.get.mockReturnValue(undefined);

    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotel
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({ promotionsInformation: {} });

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: 'PROMO123',
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_bb_promo_code_landing_page: true,
        release_bb_promo_code_site_wide: false,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).toHaveBeenCalled();
  });
  it('should call promo information API when site-wide FT is enabled even without promoId', async () => {
    mockCookies.get.mockReturnValue(undefined);

    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } })
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({ promotionsInformation: {} });

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: undefined,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_bb_promo_code_landing_page: true,
        release_bb_promo_code_site_wide: true,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).toHaveBeenCalled();
  });

  it('should use cookie-based promo code when isPromoBoxAppliedCodeCookieEnabled is true and cookie exists', async () => {
    mockCookies.get.mockImplementation((cookieName: string) => {
      if (cookieName === 'appliedPromoBoxCode') return 'COOKIE_PROMO123';
      return undefined;
    });

    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotel
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({ promotionsInformation: {} }); // promo API

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: undefined,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_pi_ccui_bb_promotion_box_code_cookie: true,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      GET_PROMO_INFORMATION,
      expect.objectContaining({
        promotionCode: 'COOKIE_PROMO123',
        isPromoBox: true,
      }),
      undefined,
      undefined,
      undefined
    );
    expect(result.dehydratedState).toBe('dehydratedState');
  });

  it('should fallback to empty string when isPromoBoxAppliedCodeCookieEnabled is true but cookie is absent', async () => {
    mockCookies.get.mockImplementation((cookieName: string) => {
      if (cookieName === 'appliedPromoBoxCode') return undefined;
      return undefined;
    });

    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotel
      .mockResolvedValueOnce({});

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: undefined,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_pi_ccui_bb_promotion_box_code_cookie: true,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).not.toHaveBeenCalledWith(
      GET_PROMO_INFORMATION,
      expect.anything(),
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
    expect(result.dehydratedState).toBe('dehydratedState');
  });

  it('should fallback to empty string when isPromoBoxAppliedCodeCookieEnabled is false even if cookie exists', async () => {
    mockCookies.get.mockImplementation((cookieName: string) => {
      if (cookieName === 'appliedPromoBoxCode') return 'COOKIE_PROMO123';
      return undefined;
    });

    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotel
      .mockResolvedValueOnce({});

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: undefined,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_pi_ccui_bb_promotion_box_code_cookie: false,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).not.toHaveBeenCalledWith(
      GET_PROMO_INFORMATION,
      expect.anything(),
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
    expect(result.dehydratedState).toBe('dehydratedState');
  });

  it('should set landingPagePromoCode to promoId when isPromoCodeLandingPageEnabled is true and promoId exists', async () => {
    mockCookies.get.mockReturnValue(undefined);

    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotel
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({ promotionsInformation: {} }); // promo API

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: 'PROMO123',
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_bb_promo_code_landing_page: true,
        release_bb_promo_code_site_wide: false,
        release_pi_ccui_bb_promotion_box_code_cookie: false,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      GET_PROMO_INFORMATION,
      expect.objectContaining({
        promotionCode: 'PROMO123',
        isPromoBox: false,
      }),
      undefined,
      undefined,
      undefined
    );
    expect(result.dehydratedState).toBe('dehydratedState');
  });

  it('should set landingPagePromoCode to empty string when isPromoCodeLandingPageEnabled is true but promoId is undefined', async () => {
    mockCookies.get.mockReturnValue(undefined);

    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotel
      .mockResolvedValueOnce({});

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: undefined,
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_bb_promo_code_landing_page: true,
        release_bb_promo_code_site_wide: false,
        release_pi_ccui_bb_promotion_box_code_cookie: false,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).not.toHaveBeenCalledWith(
      GET_PROMO_INFORMATION,
      expect.anything(),
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
    expect(result.dehydratedState).toBe('dehydratedState');
  });

  it('should set landingPagePromoCode to empty string when isPromoCodeLandingPageEnabled is false even if promoId exists', async () => {
    mockCookies.get.mockReturnValue(undefined);

    mockGraphQLRequest
      .mockResolvedValueOnce({ hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' } }) // staticHotel
      .mockResolvedValueOnce({});

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: 'PROMO123',
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    const result = await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_bb_promo_code_landing_page: false,
        release_bb_promo_code_site_wide: false,
        release_pi_ccui_bb_promotion_box_code_cookie: false,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).not.toHaveBeenCalledWith(
      GET_PROMO_INFORMATION,
      expect.anything(),
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
    expect(result.dehydratedState).toBe('dehydratedState');
  });

  it('should not call promotions information API when promotions in hotel availability FT is enabled', async () => {
    mockCookies.get.mockReturnValue(undefined);

    mockGraphQLRequest
      .mockResolvedValueOnce({
        hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' },
      })
      .mockResolvedValueOnce({});

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: 'PROMO123',
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_bb_promo_code_landing_page: true,
        release_bb_promo_code_site_wide: false,
        release_promotions_in_hotelavailability: true,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    expect(mockGraphQLRequest).not.toHaveBeenCalledWith(
      GET_PROMO_INFORMATION,
      expect.anything(),
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });
  it('should send isPromoBox to hotel availability API when promotions in hotel availability FT is enabled', async () => {
    mockCookies.get.mockImplementation((name: string) => {
      if (name === 'id_token') return 'token';
      if (name === 'appliedPromoBoxCode') return 'PROMO123';
      return undefined;
    });

    mockGraphQLRequest
      .mockResolvedValueOnce({
        hotelInformationBySlug: { hotelId: 'h1', brand: 'premierinn' },
      })
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({
        hotelAvailability: {
          roomRates: [{ ratePlanCode: 'RP1' }],
        },
      })
      .mockResolvedValueOnce({})
      .mockResolvedValueOnce({});

    mockGetAvailabilityParamsFromUrl.mockReturnValue({
      arrival: '2024-01-01',
      departure: '2024-01-02',
      rooms: [{ adults: 1 }],
      numberOfNights: 1,
      promoId: undefined,
    });

    mockDecodeIdToken.mockReturnValue({
      email: 'user@email.com',
    });

    mockAxiosRequest.mockResolvedValue({});

    mockGetLoggedInUserInfo.mockReturnValue({
      operaCompanyId: 'cid',
    });

    const createHDPBbDataLoaderFn = (await import('./data.bb')).default;

    await createHDPBbDataLoaderFn({
      ...defaultProps,
      featureToggles: {
        release_pi_ccui_bb_promotion_box_code_cookie: true,
        release_promotions_in_hotelavailability: true,
      },
      req: mockReq,
      res: mockRes,
      queryClient: mockQueryClient,
    });

    const availabilityCall = mockGraphQLRequest.mock.calls.find(
      ([query]) => query === HOTEL_AVAILABILITY_BB_QUERY
    );
    expect(availabilityCall?.[1]?.isPromoBox).toBeUndefined();
  });
});
