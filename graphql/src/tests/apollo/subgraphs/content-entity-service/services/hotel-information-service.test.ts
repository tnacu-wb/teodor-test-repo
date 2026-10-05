import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import {
  fetchHotelInformationForBooking,
  getAllHotelsShortInformation,
  retrieveHotelsInformation,
  getHotelInformation,
  getMultiHotelInformation
} from '../../../../../apollo/subgraphs/content-entity-service/services/hotel-information-service';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/booking-information-pipeline/actions/ActionContextKeys';
import { beforeAll } from 'jest-circus';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveHotelsInformation', () => {
  beforeAll(() => {
    jest.resetAllMocks();
  });

  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const hotelIds = ['HOTEL1', 'HOTEL2', 'HOTEL3'];
  const country = 'gb';
  const language = 'en';

  it('should call the get function with correct parameters when retrieving hotels information', async () => {
    await retrieveHotelsInformation({ hotelIds, country, language }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.GET_HOTELS_INFORMATION,
      retrieveHotelsInformation,
      {
        hotelIds: 'HOTEL1,HOTEL2,HOTEL3',
        country: 'gb',
        language: 'en'
      },
      context
    );
  });

  it('should include stay dates when provided for hotels information query', async () => {
    await retrieveHotelsInformation(
      {
        hotelIds,
        country,
        language,
        stayStartDate: '2026-09-01',
        stayEndDate: '2026-09-03'
      },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.GET_HOTELS_INFORMATION,
      retrieveHotelsInformation,
      {
        hotelIds: 'HOTEL1,HOTEL2,HOTEL3',
        country: 'gb',
        language: 'en',
        stayStartDate: '2026-09-01',
        stayEndDate: '2026-09-03'
      },
      context
    );
  });

  it('should handle errors gracefully when retrieving hotels information', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      retrieveHotelsInformation({ hotelIds, country, language }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('fetchHotelInformationForBooking', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const args = {
    language: 'en',
    country: 'gb'
  };

  const pipelineContext = new PipelineContext();
  jest.spyOn(pipelineContext, 'get').mockImplementation((key: string) => {
    if (key === ActionContextKeys.BOOKING_INFORMATION_BY_BASKET) {
      return {
        hotelId: 'HOTEL_1'
      };
    }
  });

  it('should call the get function with correct parameters when fetching hotel information for booking', async () => {
    await fetchHotelInformationForBooking(args, context, pipelineContext);

    const serviceEndpoint = {
      ...endpoints.HOTEL_INFORMATION_FOR_BOOKING,
      endpoint: '/v1/content/hotels/HOTEL_1/information'
    };
    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      fetchHotelInformationForBooking,
      {
        language: 'en',
        country: 'gb'
      },
      context
    );
  });

  it('should handle errors gracefully when fetching hotel information for booking', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(fetchHotelInformationForBooking(args, context, pipelineContext)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getAllHotelsShortInformation', () => {
  beforeAll(() => {
    jest.resetAllMocks();
  });

  const getAllHotelsShortInformationEndPoint = {
    endpoint: '/v1/content/allhotels/gb/en',
    flowCode: 'DIGITAL_CON_014',
    axiosClient: expect.any(Function)
  };

  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const country = 'gb';
  const language = 'en';

  it('should call the get function with correct parameters when retrieving hotels information', async () => {
    await getAllHotelsShortInformation({ country, language }, context);

    expect(get).toHaveBeenCalledWith(
      getAllHotelsShortInformationEndPoint,
      getAllHotelsShortInformation,
      {
        country: 'gb',
        language: 'en'
      },
      context
    );
  });

  it('should handle errors gracefully when retrieving all hotels short information', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getAllHotelsShortInformation({ country, language }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getHotelInformation', () => {
  beforeAll(() => {
    jest.resetAllMocks();
  });

  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const hotelInformationRequest = {
    language: 'en',
    country: 'gb',
    hotelId: 'BIRPLI',
    bookingChannel: {
      channel: 'PI',
      subchannel: 'PI',
      language: 'en'
    }
  } as any; // cast to any to avoid Channel enum typing in tests

  it('should call get with replaced service endpoint and flat query params', async () => {
    await getHotelInformation(hotelInformationRequest, context);

    const serviceEndpoint = {
      ...endpoints.HOTELS_INFORMATION,
      endpoint: endpoints.HOTELS_INFORMATION.endpoint.replace(
        '{hotel}',
        hotelInformationRequest.hotelId
      )
    };

    const expectedQueryParams = {
      language: 'en',
      country: 'gb',
      channel: 'PI',
      subchannel: 'PI'
    };

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      getHotelInformation,
      expectedQueryParams,
      context
    );
  });

  it('should propagate errors from get', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getHotelInformation(hotelInformationRequest, context)).rejects.toThrow(
      'Test error'
    );
  });

  it('should pass through ancillaryCloseout with noMealsHeading and noMealsMessage from the REST response', async () => {
    const mockResponse = {
      brand: 'PI',
      name: 'Test Hotel',
      hotelId: 'BIRPLI',
      ancillaryCloseout: {
        noMealsHeading: 'No meals available',
        noMealsMessage: 'Sorry, meals are not available for your selected dates',
        items: [
          {
            startDate: '30/10/2025',
            endDate: '19/11/2025',
            serviceCode: 'All Packages',
            upsellCodes: 'SE'
          }
        ]
      }
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const result = await getHotelInformation(hotelInformationRequest, context);

    expect(result.ancillaryCloseout).toBeDefined();
    expect(result.ancillaryCloseout.noMealsHeading).toBe('No meals available');
    expect(result.ancillaryCloseout.noMealsMessage).toBe(
      'Sorry, meals are not available for your selected dates'
    );
    expect(result.ancillaryCloseout.items).toHaveLength(1);
    expect(result.ancillaryCloseout.items[0].serviceCode).toBe('All Packages');
  });

  it('should pass through hotelFlags from the REST response', async () => {
    const mockResponse = {
      brand: 'PI',
      name: 'Test Hotel',
      hotelId: 'BIRPLI',
      messagingFlag: { text: 'Flag text', description: 'desc', color: 'blue' },
      hotelFlags: {
        isEnabled: true,
        flagOverlay: {
          text: 'Overlay text',
          textColour: '#511E62',
          backgroundColour: '#0007',
          backgroundImage: '/path/img.png'
        },
        flagBanner: {
          text: 'Banner text',
          textColour: '#FFFFFF',
          backgroundColour: '#BDD500',
          backgroundImage: '/path/img.jpg'
        }
      }
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const result = await getHotelInformation(hotelInformationRequest, context);

    expect(result.hotelFlags).toBeDefined();
    expect(result.hotelFlags.isEnabled).toBe(true);
    expect(result.hotelFlags.flagOverlay.text).toBe('Overlay text');
    expect(result.hotelFlags.flagOverlay.textColour).toBe('#511E62');
    expect(result.hotelFlags.flagBanner.text).toBe('Banner text');
    expect(result.hotelFlags.flagBanner.backgroundColour).toBe('#BDD500');
  });
});

describe('getMultiHotelInformation', () => {
  beforeAll(() => {
    jest.resetAllMocks();
  });

  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const args = {
    availabilitiesSearchCriteria: {
      country: 'gb',
      language: 'en',
      startDate: '2026-09-01',
      endDate: '2026-09-03'
    }
  };

  const hotelAvailabilities = [{ hotelId: 'LONEUS' }, { hotelId: 'LONKIN' }];

  it('should call get with correct endpoint and hotel IDs', async () => {
    const mockResponse = [
      {
        hotelId: 'LONEUS',
        name: 'London Euston',
        hotelFlags: {
          isEnabled: true,
          flagOverlay: {
            text: 'New rooms',
            textColour: '#FFF',
            backgroundColour: '#000',
            backgroundImage: '/img.png'
          },
          flagBanner: null
        }
      },
      {
        hotelId: 'LONKIN',
        name: 'London Kings Cross',
        hotelFlags: { isEnabled: false, flagOverlay: null, flagBanner: null }
      }
    ];
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const result = await getMultiHotelInformation(args, hotelAvailabilities, context);

    expect(get).toHaveBeenCalled();
    const endpoint = (get as jest.Mock).mock.calls[0][0];
    expect(endpoint.endpoint).toContain('stayStartDate=2026-09-01');
    expect(endpoint.endpoint).toContain('stayEndDate=2026-09-03');
    expect(result).toHaveLength(2);
    expect(result[0].hotelFlags.isEnabled).toBe(true);
    expect(result[0].hotelFlags.flagOverlay.text).toBe('New rooms');
    expect(result[1].hotelFlags.isEnabled).toBe(false);
    expect(result[1].hotelFlags.flagBanner).toBeNull();
  });

  it('should propagate errors from get', async () => {
    const error = new Error('Multi hotel error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getMultiHotelInformation(args, hotelAvailabilities, context)).rejects.toThrow(
      'Multi hotel error'
    );
  });
});
