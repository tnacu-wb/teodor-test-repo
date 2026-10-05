import { Channel } from '../../../../../apollo/subgraphs/hotel-entity-service/models/channel';
import { post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-entity-service/services/base-service';
import { getHotelAvailabilitiesByIdsV3 } from '../../../../../apollo/subgraphs/hotel-entity-service/services/hotel-availability-by-ids-v3-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelAvailabilitiesByIdsV3', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const context = {};
  const availabilityByIdsV3SearchCriteria = {
    hotelIds: ['WIECIT', 'LONEUS'],
    arrivalDate: '2025-05-24',
    departureDate: '2025-05-25',
    rooms: [
      {
        adults: 1,
        children: 0,
        tag: 'DB',
        numberOfRooms: 1,
        pmsRoomType: 'PPLDBL'
      },
      {
        adults: 2,
        children: 0,
        tag: 'DB',
        numberOfRooms: 1,
        pmsRoomType: 'PPLDBL'
      }
    ],
    rates: {
      corporateRates: [
        {
          corporateId: '15017452',
          ratePlanSets: ['NEG']
        },
        {
          corporateId: '15017451',
          ratePlanSets: ['NEG']
        }
      ],
      ratePlanCodes: ['FLEXRATE']
    },
    bookingChannel: {
      channel: Channel.DISTR,
      subchannel: 'AGENCY',
      language: 'en'
    },
    isOta: true,
    vatNotRequired: true
  };

  it('should handle missing optional fields when fetching hotel availabilities by ids v3', async () => {
    await getHotelAvailabilitiesByIdsV3({ availabilityByIdsV3SearchCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.HOTEL_AVAILABILITY_BY_IDS_V3,
      getHotelAvailabilitiesByIdsV3,
      {
        hotelIds: ['WIECIT', 'LONEUS'],
        arrivalDate: '2025-05-24',
        departureDate: '2025-05-25',
        rooms: [
          {
            adults: 1,
            children: 0,
            tag: 'DB',
            numberOfRooms: 1,
            pmsRoomType: 'PPLDBL'
          },
          {
            adults: 2,
            children: 0,
            tag: 'DB',
            numberOfRooms: 1,
            pmsRoomType: 'PPLDBL'
          }
        ],
        rates: {
          corporateRates: [
            {
              corporateId: '15017452',
              ratePlanSets: ['NEG']
            },
            {
              corporateId: '15017451',
              ratePlanSets: ['NEG']
            }
          ],
          ratePlanCodes: ['FLEXRATE']
        },
        bookingChannel: {
          channel: Channel.DISTR,
          subchannel: 'AGENCY',
          language: 'en'
        },
        isOTA: true,
        vatNotRequired: true
      },
      context
    );
  });

  it('should handle errors gracefully when fetching hotel availabilities by ids v3', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getHotelAvailabilitiesByIdsV3({ availabilityByIdsV3SearchCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});
