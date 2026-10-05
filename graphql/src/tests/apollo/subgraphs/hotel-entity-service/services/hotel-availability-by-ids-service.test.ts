import { endpoints } from '../../../../../../src/apollo/subgraphs/hotel-entity-service/services/base-service';
import { get } from '../../../../../../src/apollo/client/rest-client';
import { getHotelAvailabilitiesByIds } from '../../../../../../src/apollo/subgraphs/hotel-entity-service/services/hotel-availability-by-ids-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelAvailabilitiesByIds', () => {
  const context = {};
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const availabilityByIdsSearchCriteriaNonOptional = {
    rooms: [
      {
        roomType: 'SB',
        pmsRoomType: 'DOUBLE',
        adultsNumber: 1,
        childrenNumber: 0
      },
      {
        roomType: 'DB',
        pmsRoomType: 'PPLDBL',
        adultsNumber: 2,
        childrenNumber: 1
      }
    ],
    hotels: [
      {
        identifier: 'GATGAT,HEAPTI'
      }
    ],
    arrival: '2025-12-01',
    departure: '2025-12-10'
  };

  it('should handle missing optional fields when fetching hotel availabilities by ids', async () => {
    await getHotelAvailabilitiesByIds(
      { availabilityByIdsSearchCriteria: availabilityByIdsSearchCriteriaNonOptional },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.HOTEL_AVAILABILITY_BY_IDS,
      getHotelAvailabilitiesByIds,
      {
        hotelIds: 'GATGAT,HEAPTI',
        arrivalDate: '2025-12-01',
        departureDate: '2025-12-10',
        roomTypes: 'SB,DB',
        pmsRoomTypes: 'DOUBLE,PPLDBL',
        adultsNumber: '1,2',
        childrenNumber: '0,1',
        vatNotRequired: false,
        isOTA: false
      },
      context
    );
  });

  const availabilityByIdsSearchCriteriaFull = {
    rooms: [
      {
        roomType: 'SB',
        pmsRoomType: 'DOUBLE',
        adultsNumber: 1,
        childrenNumber: 0,
        cotRequired: false
      },
      {
        roomType: 'DB',
        pmsRoomType: 'PPLDBL',
        adultsNumber: 2,
        childrenNumber: 1,
        cotRequired: true
      }
    ],
    hotels: [
      {
        identifier: 'GATGAT,HEAPTI'
      }
    ],
    ratePlanCodes: ['FLEXBEDB', 'FLEXRATE'],
    arrival: '2025-12-01',
    departure: '2025-12-10',
    bookingChannel: { channel: 'DISTR', subchannel: 'AGENCY', language: 'en' },
    vatNotRequired: true,
    isOta: false,
    negotiatedRates: {
      globalCompanyId: 'company1',
      rateDisplaySets: ['set1', 'set2']
    }
  };

  it('should call the get function with correct parameters when fetching hotel availabilities by ids', async () => {
    await getHotelAvailabilitiesByIds(
      { availabilityByIdsSearchCriteria: availabilityByIdsSearchCriteriaFull },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.HOTEL_AVAILABILITY_BY_IDS,
      getHotelAvailabilitiesByIds,
      {
        hotelIds: 'GATGAT,HEAPTI',
        arrivalDate: '2025-12-01',
        departureDate: '2025-12-10',
        roomTypes: 'SB,DB',
        pmsRoomTypes: 'DOUBLE,PPLDBL',
        adultsNumber: '1,2',
        childrenNumber: '0,1',
        cotsRequired: 'false,true',
        ratePlanCodes: 'FLEXBEDB,FLEXRATE',
        channel: 'DISTR',
        subchannel: 'AGENCY',
        language: 'en',
        vatNotRequired: true,
        isOTA: false,
        globalCompanyId: 'company1',
        negotiatedRateDisplaySets: 'set1,set2'
      },
      context
    );
  });

  it('should handle errors gracefully when fetching hotel availabilities by ids', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getHotelAvailabilitiesByIds(
        { availabilityByIdsSearchCriteria: availabilityByIdsSearchCriteriaFull },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
