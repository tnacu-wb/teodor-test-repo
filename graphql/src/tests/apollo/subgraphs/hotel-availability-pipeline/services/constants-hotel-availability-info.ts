import { PipelineAvailabilityInfoCriteria } from '../../../../../apollo/subgraphs/hotel-availabilities-pipeline/models/availability-info-criteria';

export const criteria: PipelineAvailabilityInfoCriteria = {
  availabilityCriteria: {
    hotel: {
      identifier: 'HEAPTI'
    },
    arrival: '2025-09-21',
    departure: '2025-09-25',
    ratePlanCodes: ['FLEXRATE'],
    companyId: 'COMP123',
    rooms: [
      {
        adultsNumber: 2,
        childrenNumber: 1,
        roomType: 'DB',
        cotRequired: false
      }
    ],
    bookingChannel: {
      channel: 'PI',
      subchannel: 'MOBILE',
      language: 'en'
    },
    promotionCode: 'PROMO123'
  },
  brand: 'pi',
  language: 'en',
  country: 'GB',
  hotelId: 'HEAPTI',
  channel: 'PI',
  ratePlans: ['FLEXRATE']
};

export const availabilityMock = {
  available: true,
  endDate: '2025-09-25',
  hotelId: 'HEAPTI',
  limitedAvailability: false,
  mlos: false,
  roomRates: [
    {
      ratePlanCode: 'FLEXRATE',
      rateDisplaySet: null,
      cellCode: null,
      twinRoomTypeAvailability: false,
      globalCompanyId: null,
      roomTypes: [
        {
          roomType: 'DB',
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              pmsRoomType: 'PPLDBL',
              silentSubstitution: true,
              isSubstitution: true,
              substitution: 'PPLDBL',
              roomClass: 'PP',
              cotAvailable: false,
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 90,
                totalGrossAmount: 75,
                totalTaxAmount: 15,
                packageAmount: null,
                baseRateAmount: null,
                packageCode: null,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2025-09-24',
                    netPrice: 90
                  }
                ]
              },
              mealsIncluded: null,
              numberOfRoomsAvailable: 16
            }
          ]
        }
      ],
      promotionCode: null
    },
    {
      ratePlanCode: 'xxxx',
      rateDisplaySet: null,
      cellCode: null,
      twinRoomTypeAvailability: false,
      globalCompanyId: null,
      roomTypes: [
        {
          pmsRoomType: 'xxx',
          roomType: 'DB',
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              silentSubstitution: true,
              isSubstitution: false,
              substitution: null,
              roomClass: 'xx',
              cotAvailable: false,
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 90,
                totalGrossAmount: 75,
                totalTaxAmount: 15,
                packageAmount: null,
                baseRateAmount: null,
                packageCode: null,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2025-09-24',
                    netPrice: 90
                  }
                ]
              },
              mealsIncluded: null,
              numberOfRoomsAvailable: 16
            }
          ]
        }
      ],
      promotionCode: null
    },
    {
      rateDisplaySet: null,
      cellCode: null,
      twinRoomTypeAvailability: false,
      globalCompanyId: null,
      roomTypes: [
        {
          roomType: 'DB',
          adults: 1,
          children: 0,
          cotRequested: false,
          rooms: [
            {
              silentSubstitution: true,
              isSubstitution: true,
              substitution: 'SUPERIOR',
              cotAvailable: false,
              specialRequests: ['SING'],
              roomPriceBreakdown: {
                totalNetAmount: 90,
                totalGrossAmount: 75,
                totalTaxAmount: 15,
                packageAmount: null,
                baseRateAmount: null,
                packageCode: null,
                currencyCode: 'GBP',
                dailyPrices: [
                  {
                    date: '2025-09-24',
                    netPrice: 90
                  }
                ]
              },
              mealsIncluded: null,
              numberOfRoomsAvailable: 16
            }
          ]
        }
      ],
      promotionCode: null
    }
  ]
};

export const ratesInfoMock = {
  rateClassifications: [
    {
      rateClassification: 'FLEXRATE',
      ratePlanCode: 'FLEXRATE',
      rateOrder: '10',
      rateName: 'Flex',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
      rateLongDescription: '',
      additionalDescription: '',
      rateNotes: '<p>Flex: Amend or cancel up to 1pm on arrival day</p>\n',
      rateCategory: 'A',
      rateTags: []
    }
  ]
};

export const roomTypeInfoMock = {
  roomTypes: [
    {
      roomTypeCode: ['PPLDBL'],
      roomCategory: 'Premier Plus',
      roomLabel: 'Premier Plus room',
      roomDescription:
        'Our enhanced room design. Includes Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
      groupId: 'double'
    }
  ]
};

export const rateClassCategMock = {
  roomClassConfig: [
    {
      code: 'PP',
      order: 1,
      availableUpgrades: ['PV']
    }
  ]
};
