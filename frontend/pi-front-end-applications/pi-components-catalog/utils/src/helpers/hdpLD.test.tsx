import { containsPlaceLD, getPriceRange } from './hdpLD';

jest.mock('@whitbread-eos/utils', () => ({
  formatCurrency: jest.fn((currency: string) => {
    const map: Record<string, string> = {
      GBP: '£',
      EUR: '€',
    };

    return map[currency] || currency;
  }),
}));

jest.mock('@whitbread-eos/api', () => {
  const actual = jest.requireActual('@whitbread-eos/api');

  return {
    ...actual,
    RateClassificationNames: {
      ...actual.RateClassificationNames,
      FLEX: 'Flexible Rate',
    },
  };
});

describe('containsPlaceLD', () => {
  const mockRoomTypeInformation: any = {
    roomTypeInformation: {
      roomTypes: [
        {
          roomTypeCode: ['DBL'],
          roomLabel: 'Double Room',
          roomDescription: 'A comfortable double room',
          roomImage: '/images/double-room.jpg',
        },
      ],
    },
  };

  const mockAvailability: any = {
    hotelAvailability: {
      startDate: '2026-05-20',
      endDate: '2026-05-22',
      available: true,
      roomRates: [
        {
          ratePlanCode: 'FLEX',
          roomTypes: [
            {
              adults: 2,
              children: 1,
              rooms: [
                {
                  pmsRoomType: 'DBL',
                  numberOfRoomsAvailable: 3,
                  roomPriceBreakdown: {
                    totalNetAmount: 250,
                    currencyCode: 'GBP',
                  },
                },
              ],
            },
          ],
        },
      ],
    },
  };

  it('should transform hotel availability data correctly', () => {
    const result = containsPlaceLD(mockAvailability, mockRoomTypeInformation, 'PI');

    expect(result).toHaveLength(1);

    expect(result[0]).toEqual({
      '@type': ['HotelRoom', 'Product'],
      name: 'Double Room',
      description: 'A comfortable double room',
      image: 'https://www.premierinn.com/images/double-room.jpg',
      inventoryLevel: {
        '@type': 'QuantitativeValue',
        value: 3,
        description: 'Only 3 room(s) left for your search',
      },
      occupancy: {
        '@type': 'QuantitativeValue',
        value: 3,
      },
      offers: [
        {
          '@type': ['Offer', 'LodgingReservation'],
          name: 'Flexible Rate',
          checkinTime: '2026-05-20T14:00:00',
          checkoutTime: '2026-05-22T12:00:00',
          price: 250,
          priceCurrency: 'GBP',
          availability: 'https://schema.org/InStock',
          priceSpecification: {
            '@type': 'CompoundPriceSpecification',
            price: 250,
            priceCurrency: 'GBP',
            unitCode: 'DAY',
          },
        },
      ],
    });
  });

  it('should return OutOfStock when hotel is unavailable', () => {
    const unavailableData = {
      ...mockAvailability,
      hotelAvailability: {
        ...mockAvailability.hotelAvailability,
        available: false,
      },
    };

    const result = containsPlaceLD(unavailableData, mockRoomTypeInformation, 'PID');

    expect(result[0].offers[0].availability).toBe('https://schema.org/OutOfStock');
  });

  it('should fallback to ratePlanCode when classification name is not found', () => {
    const data = {
      ...mockAvailability,
      hotelAvailability: {
        ...mockAvailability.hotelAvailability,
        roomRates: [
          {
            ratePlanCode: 'UNKNOWN_RATE',
            roomTypes: mockAvailability.hotelAvailability.roomRates[0].roomTypes,
          },
        ],
      },
    };

    const result = containsPlaceLD(data, mockRoomTypeInformation, 'PI');

    expect(result[0].offers[0].name).toBe('UNKNOWN_RATE');
  });

  it('should group multiple offers under the same room type', () => {
    const multiRateData = {
      hotelAvailability: {
        ...mockAvailability.hotelAvailability,
        roomRates: [
          ...mockAvailability.hotelAvailability.roomRates,
          {
            ratePlanCode: 'FLEX',
            roomTypes: [
              {
                adults: 2,
                children: 0,
                rooms: [
                  {
                    pmsRoomType: 'DBL',
                    numberOfRoomsAvailable: 2,
                    roomPriceBreakdown: {
                      totalNetAmount: 300,
                      currencyCode: 'GBP',
                    },
                  },
                ],
              },
            ],
          },
        ],
      },
    };

    const result = containsPlaceLD(multiRateData as any, mockRoomTypeInformation, 'PI');

    expect(result).toHaveLength(1);
    expect(result[0].offers).toHaveLength(2);
  });

  it('should set price and currency fields to null when roomPriceBreakdown is missing', () => {
    const dataWithMissingPriceBreakdown = {
      hotelAvailability: {
        startDate: '2026-05-20',
        endDate: '2026-05-22',
        available: true,
        roomRates: [
          {
            ratePlanCode: 'FLEX',
            roomTypes: [
              {
                adults: 2,
                children: 0,
                rooms: [
                  {
                    pmsRoomType: 'DBL',
                    numberOfRoomsAvailable: null,
                    roomPriceBreakdown: null,
                  },
                ],
              },
            ],
          },
        ],
      },
    };

    const result = containsPlaceLD(
      dataWithMissingPriceBreakdown as any,
      mockRoomTypeInformation,
      'PI'
    );

    expect(result[0].offers[0]).toMatchObject({
      price: null,
      priceCurrency: null,
      priceSpecification: {
        '@type': 'CompoundPriceSpecification',
        price: null,
        priceCurrency: null,
        unitCode: 'DAY',
      },
    });
  });
});

describe('getPriceRange', () => {
  it('should return formatted min and max price range', () => {
    const mockData: any = {
      hotelAvailability: {
        roomRates: [
          {
            roomTypes: [
              {
                rooms: [
                  {
                    roomPriceBreakdown: {
                      totalNetAmount: 250,
                      currencyCode: 'GBP',
                    },
                  },
                  {
                    roomPriceBreakdown: {
                      totalNetAmount: 300,
                      currencyCode: 'GBP',
                    },
                  },
                ],
              },
            ],
          },
          {
            roomTypes: [
              {
                rooms: [
                  {
                    roomPriceBreakdown: {
                      totalNetAmount: 180,
                      currencyCode: 'GBP',
                    },
                  },
                ],
              },
            ],
          },
        ],
      },
    };

    const result = getPriceRange(mockData);

    expect(result).toBe('£180 - £300');
  });

  it('should return null when no valid prices exist', () => {
    const mockData: any = {
      hotelAvailability: {
        roomRates: [
          {
            roomTypes: [
              {
                rooms: [
                  {
                    roomPriceBreakdown: null,
                  },
                ],
              },
            ],
          },
        ],
      },
    };

    const result = getPriceRange(mockData);

    expect(result).toBeNull();
  });

  it('should ignore non-number prices', () => {
    const mockData: any = {
      hotelAvailability: {
        roomRates: [
          {
            roomTypes: [
              {
                rooms: [
                  {
                    roomPriceBreakdown: {
                      totalNetAmount: '250',
                      currencyCode: 'GBP',
                    },
                  },
                  {
                    roomPriceBreakdown: {
                      totalNetAmount: 400,
                      currencyCode: 'GBP',
                    },
                  },
                ],
              },
            ],
          },
        ],
      },
    };

    const result = getPriceRange(mockData);

    expect(result).toBe('£400 - £400');
  });

  it('should handle missing hotel availability gracefully', () => {
    const result = getPriceRange({} as any);

    expect(result).toBeNull();
  });

  it('should handle empty roomRates array', () => {
    const mockData: any = {
      hotelAvailability: {
        roomRates: [],
      },
    };

    const result = getPriceRange(mockData);

    expect(result).toBeNull();
  });

  it('should work with a single price', () => {
    const mockData: any = {
      hotelAvailability: {
        roomRates: [
          {
            roomTypes: [
              {
                rooms: [
                  {
                    roomPriceBreakdown: {
                      totalNetAmount: 220,
                      currencyCode: 'GBP',
                    },
                  },
                ],
              },
            ],
          },
        ],
      },
    };

    const result = getPriceRange(mockData);

    expect(result).toBe('£220 - £220');
  });
});
