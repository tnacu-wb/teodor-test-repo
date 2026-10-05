import { get } from '../../../../../../src/apollo/client/rest-client';
import { getHotelAvailabilities } from '../../../../../../src/apollo/subgraphs/hotel-entity-service/services/hotel-availability-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelAvailabilities', () => {
  const context = {};
  const singleAvailabilityEndPoint = {
    endpoint: '/v1/hotels/GATGAT/availabilities',
    flowCode: 'DIGITAL_AVA_001',
    axiosClient: expect.any(Function)
  };

  const availabilitySearchCriteriaNonOptional = {
    rooms: [
      {
        roomType: 'SB',
        adultsNumber: 1,
        childrenNumber: 0
      },
      {
        roomType: 'DB',
        adultsNumber: 2,
        childrenNumber: 1
      }
    ],
    hotel: { identifier: 'GATGAT' },
    arrival: '2025-12-01',
    departure: '2025-12-10'
  };

  it('should handle missing optional fields when fetching hotel availabilities', async () => {
    await getHotelAvailabilities(
      { availabilitySearchCriteria: availabilitySearchCriteriaNonOptional },
      context
    );

    expect(get).toHaveBeenCalledWith(
      singleAvailabilityEndPoint,
      getHotelAvailabilities,
      expect.objectContaining({
        hotelId: 'GATGAT',
        arrivalDate: '2025-12-01',
        departureDate: '2025-12-10',
        roomTypes: 'SB,DB',
        adultsNumber: '1,2',
        childrenNumber: '0,1'
      }),
      context
    );
  });

  const availabilitySearchCriteriaFull = {
    rooms: [
      {
        roomType: 'SB',
        adultsNumber: 1,
        childrenNumber: 0,
        cotRequired: false
      },
      {
        roomType: 'DB',
        adultsNumber: 2,
        childrenNumber: 1,
        cotRequired: true
      }
    ],
    hotel: { identifier: 'GATGAT' },
    arrival: '2025-12-01',
    departure: '2025-12-10',
    companyId: '10002634',
    country: 'gb',
    ratePlanCodes: ['FLEXBEDB', 'FLEXRATE'],
    bookingChannel: { channel: 'DISTR', subchannel: 'AGENCY', language: 'en' },
    promotionCode: 'PROMO'
  };

  it('should call the get function with correct parameters when fetching hotel availabilities', async () => {
    await getHotelAvailabilities(
      { availabilitySearchCriteria: availabilitySearchCriteriaFull },
      context
    );

    expect(get).toHaveBeenCalledWith(
      singleAvailabilityEndPoint,
      getHotelAvailabilities,
      {
        hotelId: 'GATGAT',
        arrivalDate: '2025-12-01',
        departureDate: '2025-12-10',
        roomTypes: 'SB,DB',
        adultsNumber: '1,2',
        childrenNumber: '0,1',
        cotsRequired: 'false,true',
        ratePlanCodes: 'FLEXBEDB,FLEXRATE',
        channel: 'DISTR',
        subchannel: 'AGENCY',
        language: 'en',
        companyId: '10002634',
        promotionCode: 'PROMO'
      },
      context
    );
  });

  it('should handle errors gracefully when fetching hotel availabilities', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getHotelAvailabilities(
        { availabilitySearchCriteria: availabilitySearchCriteriaFull },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
