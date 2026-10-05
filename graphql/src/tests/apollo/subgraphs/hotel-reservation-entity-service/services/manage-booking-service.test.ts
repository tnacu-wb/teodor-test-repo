import { endpoints } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/base-service';
import { get, put } from '../../../../../apollo/client/rest-client';
import { Channel } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/channel';
import {
  getFindBooking,
  getFindBookingForKiosk,
  getManageBookingInformation,
  getSearchBookings,
  updateUdfc20
} from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/manage-booking-service';
import { CancelInformationCriteria } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/cancel-information-criteria';
import { FindBookingCriteria } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/find-booking-criteria';
import { SearchBookingsCriteria } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/search-bookings-criteria';
import { FindBookingForKioskCriteria } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/find-booking-for-kiosk-criteria';
import { resolvers } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/subgraph-resolvers';

jest.mock('../../../../../apollo/client/rest-client');

describe('getManageBooking', () => {
  const context = {};
  afterEach(() => {
    jest.resetAllMocks();
  });

  const cancelInformationCriteria: CancelInformationCriteria = {
    basketReference: 'AKU-632a712a-973a-4f96-9189-33a068a83393',
    hotelId: 'LONEUS',
    userDateTime: '2024-03-26T11:29:20+02:00',
    bookingChannel: {
      channel: Channel.PI,
      subchannel: 'WEB',
      language: 'en'
    },
    token: 'token123'
  };

  it('should call get when getManageBookingInformation is called with correct parameters', async () => {
    await getManageBookingInformation(
      { cancelInformationCriteria: cancelInformationCriteria },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.MANAGE_BOOKING,
      getManageBookingInformation,
      {
        hotelId: 'LONEUS',
        basketReference: 'AKU-632a712a-973a-4f96-9189-33a068a83393',
        userDateTime: '2024-03-26T11:29:20+02:00',
        token: 'token123',
        channel: Channel.PI,
        subchannel: 'WEB',
        language: 'en'
      },
      context
    );
  });

  it('should call get when getManageBookingInformation is called with empty parameters with no errors', async () => {
    const criteria = {
      ...cancelInformationCriteria,
      bookingChannel: {
        channel: Channel.PI,
        subchannel: 'WEB'
      }
    };
    await getManageBookingInformation({ cancelInformationCriteria: criteria }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.MANAGE_BOOKING,
      getManageBookingInformation,
      {
        hotelId: 'LONEUS',
        basketReference: 'AKU-632a712a-973a-4f96-9189-33a068a83393',
        userDateTime: '2024-03-26T11:29:20+02:00',
        token: 'token123',
        channel: Channel.PI,
        subchannel: 'WEB'
      },
      context
    );
  });

  it('should handle errors when getManageBookingInformation fails correctly', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getManageBookingInformation({ cancelInformationCriteria: cancelInformationCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getFindBooking', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const findBookingCriteria: FindBookingCriteria = {
    resNo: '123456',
    lastName: 'Doe',
    arrivalDate: '2025-05-24',
    country: 'US',
    bookingChannel: {
      channel: Channel.PI,
      language: 'en',
      subchannel: 'MOBILE'
    }
  };

  it('should handle all fields when getFindBooking is called', async () => {
    await getFindBooking({ findBookingCriteria: findBookingCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.FIND_BOOKING,
      getFindBooking,
      {
        resNo: '123456',
        lastName: 'Doe',
        arrivalDate: '2025-05-24',
        country: 'US',
        channel: Channel.PI,
        subchannel: 'MOBILE',
        language: 'en'
      },
      context
    );
  });

  it('should handle missing optional fields when getFindBooking is called', async () => {
    const criteria = { ...findBookingCriteria, bookingChannel: undefined, country: undefined };
    await getFindBooking({ findBookingCriteria: criteria }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.FIND_BOOKING,
      getFindBooking,
      {
        resNo: '123456',
        lastName: 'Doe',
        arrivalDate: '2025-05-24',
        channel: Channel.PI,
        subchannel: 'WEB'
      },
      context
    );
  });

  it('should handle errors when getFindBooking fails correctly', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getFindBooking({ findBookingCriteria: findBookingCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getFindBookingForKiosk', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const findBookingForKioskCriteria: FindBookingForKioskCriteria = {
    resNo: '123456'
  };
  it('should handle all fields when getFindBooking is called', async () => {
    await getFindBookingForKiosk(
      { findBookingForKioskCriteria: findBookingForKioskCriteria },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.FIND_BOOKING_FOR_KIOSK,
      getFindBookingForKiosk,
      {
        resNo: '123456'
      },
      context
    );
  });

  it('should handle missing optional fields when getFindBooking is called', async () => {
    const criteria = {
      ...findBookingForKioskCriteria,
      bookingChannel: undefined,
      country: undefined
    };
    await getFindBookingForKiosk({ findBookingForKioskCriteria: criteria }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.FIND_BOOKING_FOR_KIOSK,
      getFindBookingForKiosk,
      {
        resNo: '123456'
      },
      context
    );
  });

  it('should handle errors when getFindBooking fails correctly', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getFindBookingForKiosk({ findBookingForKioskCriteria: findBookingForKioskCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getSearchBookings', () => {
  const context = {};
  afterEach(() => {
    jest.resetAllMocks();
  });

  const searchBookingsCriteria: SearchBookingsCriteria = new SearchBookingsCriteria({
    bookingReference: 'BR123',
    bookerLastName: 'Doe',
    guestLastName: 'Smith',
    arrivalDate: '2023-12-01',
    bookerPostcode: '12345',
    hotelId: 'H123',
    bookerEmail: 'john.doe@example.com',
    bookerPhone: '1234567890',
    cancellationDate: '2023-12-02',
    companyName: 'Company',
    thirdPartyBookingReferenceNumber: 'TPBR123',
    channel: Channel.PI,
    offset: 0,
    limit: 10
  });

  it('should call get when getSearchBookings is called with correct parameters', async () => {
    await getSearchBookings({ searchBookingsCriteria: searchBookingsCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.SEARCH_BOOKINGS,
      getSearchBookings,
      {
        bookingReference: 'BR123',
        bookerLastName: 'Doe',
        guestLastName: 'Smith',
        arrivalDate: '2023-12-01',
        bookerPostcode: '12345',
        hotelId: 'H123',
        bookerEmail: 'john.doe@example.com',
        bookerPhone: '1234567890',
        cancellationDate: '2023-12-02',
        companyName: 'Company',
        thirdPartyBookingReferenceNumber: 'TPBR123',
        channel: Channel.PI,
        offset: 0,
        limit: 10
      },
      context
    );
  });

  it('should call get when getSearchBookings is called with empty parameters with no errors', async () => {
    const criteria = {
      bookingReference: '',
      bookerLastName: '',
      guestLastName: '',
      arrivalDate: ''
    };
    await getSearchBookings({ searchBookingsCriteria: criteria }, context);

    expect(get).toHaveBeenCalledWith(endpoints.SEARCH_BOOKINGS, getSearchBookings, {}, context);
  });

  it('should handle errors when getSearchBookings fails correctly', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getSearchBookings({ searchBookingsCriteria: searchBookingsCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('FindBooking.isThirdPartyBooking (strict)', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const resolverFn = resolvers.FindBooking.isThirdPartyBooking;

  it('should return true when idContext is exactly "3rd Party"', () => {
    expect(resolverFn({ idContext: '3rd Party' })).toBe(true);
  });

  it('should return false for any value other than exact "3rd Party"', () => {
    const cases = [
      { idContext: '3RD PARTY' },
      { idContext: ' 3rd Party ' },
      { idContext: '3rd party' },
      { idContext: 'DIRECT' },
      { idContext: '' },
      {} as any,
      { idContext: null } as any,
      { idContext: undefined } as any
    ];

    cases.forEach((testCase) => {
      expect(resolverFn(testCase)).toBe(false);
    });
  });
});

describe('updateUdfc20', () => {
  const context = {};

  afterEach(() => {
    jest.resetAllMocks();
  });

  const updateUdfc20Criteria = {
    reservationIds: ['RES1', 'RES2'],
    hotelId: 'H123',
    ciolStatus: 'DK_ISSUED' as const
  };

  it('should call put when updateUdfc20 is called with correct parameters', async () => {
    const mockResponse = { status: 'OK' };
    (put as jest.Mock).mockResolvedValue(mockResponse);

    const result = await updateUdfc20({ updateUdfc20Criteria }, context);

    expect(put).toHaveBeenCalledWith(
      endpoints.UPDATE_UDFC_20,
      updateUdfc20,
      updateUdfc20Criteria,
      context
    );
    expect(result).toEqual(mockResponse);
  });

  it('should handle errors when updateUdfc20 fails correctly', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);

    await expect(updateUdfc20({ updateUdfc20Criteria }, context)).rejects.toThrow('Test error');
  });
});
