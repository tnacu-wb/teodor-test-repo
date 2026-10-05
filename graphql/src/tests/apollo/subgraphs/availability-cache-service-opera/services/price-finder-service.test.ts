import { get } from '../../../../../apollo/client/rest-client';
import {
  getLowestPricesByLocationForCalendar,
  getLowestRatesByHotel,
  getLowestRatesByLocationId
} from '../../../../../apollo/subgraphs/availability-cache-service-opera/services/price-finder-service';
import { endpoints } from '../../../../../apollo/subgraphs/availability-cache-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getLowestRatesByLocationId', () => {
  const context = {};

  const input = {
    criteria: {
      locationId: 'Timisoara',
      arrival: 'today-111',
      daysRange: 4,
      showMinimumNights: false,
      page: 1,
      initialPageSize: 10,
      lazyLoadPageSize: 10,
      country: 'gb',
      language: 'en'
    }
  };

  it('should call the get function with correct parameters when getLowestRatesByLocationId is called', async () => {
    await getLowestRatesByLocationId(input, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.PRICE_FINDER_BY_LOCATION,
      getLowestRatesByLocationId,
      input,
      context
    );
  });

  it('should handle errors gracefully when getLowestRatesByLocationId throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getLowestRatesByLocationId(input, context)).rejects.toThrow('Test error');
  });
});

describe('getLowestRatesByHotel', () => {
  const context = {};

  const input = {
    hotelCodes: ['hotel1', 'hotel2'],
    rooms: 2,
    arrival: 'today-111',
    language: 'en',
    country: 'uk',
    showMinimumNights: false
  };

  it('should call the get function with correct parameters when getLowestRatesByHotel is called', async () => {
    await getLowestRatesByHotel(input, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.PRICE_FINDER_BY_HOTEL,
      getLowestRatesByHotel,
      expect.objectContaining({
        hotelCodes: 'hotel1,hotel2',
        language: 'en',
        arrival: 'today-111',
        country: 'uk',
        showMinimumNights: false
      }),
      context
    );
  });

  it('should handle errors gracefully when getLowestRatesByLocation throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getLowestRatesByHotel(input, context)).rejects.toThrow('Test error');
  });
});

describe('getLowestPricesByLocationForCalendar', () => {
  const context = {};

  const input = {
    locationId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    milesRadius: 50,
    month: 11
  };

  it('should call the get function with correct parameters when getLowestPricesByLocationForCalendar is called', async () => {
    await getLowestPricesByLocationForCalendar(input, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.PRICE_FINDER_BY_LOCATION_FOR_CALENDAR,
      getLowestPricesByLocationForCalendar,
      expect.objectContaining({
        locationId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
        milesRadius: 50,
        month: 11
      }),
      context
    );
  });

  it('should handle errors gracefully when getLowestPricesByLocationForCalendar throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getLowestPricesByLocationForCalendar(input, context)).rejects.toThrow(
      'Test error'
    );
  });
});
