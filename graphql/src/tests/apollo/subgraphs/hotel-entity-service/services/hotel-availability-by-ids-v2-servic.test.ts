import { AvailabilityByIdsV2SearchCriteria } from '../../../../../apollo/subgraphs/hotel-entity-service/models/availability-by-ids-v2-search-criteria';
import { getHotelAvailabilitiesByIdsV2 } from '../../../../../apollo/subgraphs/hotel-entity-service/services/hotel-availability-by-ids-v2-service';
import { post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-entity-service/services/base-service';
import { Channel } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/channel';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelAvailabilitiesByIdsV2', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const context = {};
  const availabilityByIdsV2SearchCriteria: AvailabilityByIdsV2SearchCriteria = {
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
      ratePlanCodes: ['FLEXRATE']
    },
    bookingChannel: {
      channel: Channel.BB,
      subchannel: 'AGENCY',
      language: 'en'
    },
    isOta: true,
    vatNotRequired: true
  };

  it('should handle missing optional fields when fetching hotel availabilities by ids v2', async () => {
    await getHotelAvailabilitiesByIdsV2(
      { availabilityByIdsV2SearchCriteria: availabilityByIdsV2SearchCriteria },
      context
    );

    expect(post).toHaveBeenCalledWith(
      endpoints.HOTEL_AVAILABILITY_BY_IDS_V2,
      getHotelAvailabilitiesByIdsV2,
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

  it('should handle errors gracefully when fetching hotel availabilities by ids v2', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getHotelAvailabilitiesByIdsV2(
        { availabilityByIdsV2SearchCriteria: availabilityByIdsV2SearchCriteria },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
