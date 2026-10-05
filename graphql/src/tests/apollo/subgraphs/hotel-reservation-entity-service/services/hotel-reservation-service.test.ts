import {
  addNewRoom,
  amendDistribution,
  attachFileToReservation,
  cancelOnHoldReservation,
  cancelReservation,
  createMemo,
  createReservation,
  getBookingAllowances,
  getCancellationPolicies,
  getMemos,
  getPmsBookingInformation,
  getSearchBookingsCcui,
  saveCharityPackage,
  updateCnp,
  updateRateCode,
  updateReasonForStay,
  updateReservationOverrideReasons,
  updateReservationPackageScheduled,
  updateReservationPreferences,
  updateEmail,
  saveReservationAncillaries,
  createReservationGuest,
  updateReservationPackagesByReservation,
  amendEditRoom,
  removeRoom,
  changeBookingDates,
  getBookingInformationAuthenticated,
  getBookingInformationAuthenticatedWithToken
} from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/hotel-reservation-service';
import { get, post, put } from '../../../../../apollo/client/rest-client';
import { CancellationPolicies } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/cancellation-policies-criteria';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/base-service';
import { SearchBookingsCcuiCriteria } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/search-bookings-ccui-criteria';
import { replaceServiceEndpoint } from '../../../../../apollo/utils/base-utils';

jest.mock('../../../../../apollo/client/rest-client');
beforeAll(() => {
  jest.clearAllMocks();
});

describe('getBookingAllowances', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const bookingAllowancesEndPoint = {
    endpoint: '/v1/reservations/basket/basketReference/allowances',
    flowCode: 'DIGITAL_PAY_009',
    axiosClient: expect.any(Function)
  };

  it('should call the get function when getBookingAllowances is called with correct parameters', async () => {
    await getBookingAllowances({ basketReference: 'basketReference' }, context);
    expect(get).toHaveBeenCalledWith(
      bookingAllowancesEndPoint,
      getBookingAllowances,
      null,
      context
    );
  });

  it('should handle errors when getBookingAllowances fails gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getBookingAllowances({ basketReference: 'basketReference' }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getCancellationPolicies', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const cancellationCriteria: CancellationPolicies = {
    hotelId: '123',
    arrivalDate: '2023-10-10',
    basketRef: 'basket123',
    ratePlanCode: 'rate123'
  };

  it('should handle all fields when getCancellationPolicies is called', async () => {
    await getCancellationPolicies({ cancellationPolicies: cancellationCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.CANCELLATION_POLICIES,
      getCancellationPolicies,
      {
        hotelId: '123',
        arrivalDate: '2023-10-10',
        basketReference: 'basket123',
        ratePlanCode: 'rate123'
      },
      context
    );
  });

  it('should handle missing optional fields when getCancellationPolicies is called', async () => {
    const emptyCancellationCriteria: CancellationPolicies = {
      hotelId: '123',
      arrivalDate: '',
      basketRef: '',
      ratePlanCode: ''
    };
    await getCancellationPolicies({ cancellationPolicies: emptyCancellationCriteria }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.CANCELLATION_POLICIES,
      getCancellationPolicies,
      {
        hotelId: '123',
        basketReference: '',
        arrivalDate: '',
        ratePlanCode: ''
      },
      context
    );
  });

  it('should handle missing basketRef field when getCancellationPolicies is called', async () => {
    const emptyCancellationCriteria: CancellationPolicies = {
      hotelId: '123',
      arrivalDate: '',
      ratePlanCode: ''
    };
    await getCancellationPolicies({ cancellationPolicies: emptyCancellationCriteria }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.CANCELLATION_POLICIES,
      getCancellationPolicies,
      {
        hotelId: '123',
        basketReference: '',
        arrivalDate: '',
        ratePlanCode: ''
      },
      context
    );
  });

  it('should handle errors when getCancellationPolicies fails correctly', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getCancellationPolicies({ cancellationPolicies: cancellationCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getMemos', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const memosEndPoint = {
    endpoint: '/v1/reservations/basket/basketReference/memos',
    flowCode: 'DIGITAL_CRE_004',
    axiosClient: expect.any(Function)
  };

  it('should call the get function when getMemos is called with correct parameters', async () => {
    await getMemos({ basketReference: 'basketReference' }, context);
    expect(get).toHaveBeenCalledWith(memosEndPoint, getMemos, null, context);
  }, 1000);

  it('should handle errors when getMemos fails gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getMemos({ basketReference: 'basketReference' }, context)).rejects.toThrow(
      'Test error'
    );
  }, 1000);
});

describe('getPmsBookingInformation', () => {
  const context = {};
  const basketReference = 'testBasketReference';
  const priceBreakdownNeeded = 'true';
  afterEach(() => {
    jest.resetAllMocks();
  });

  it('should call get when getPmsBookingInformation is called with correct parameters', async () => {
    const pmsBookingInformationPath = endpoints.PMS_BOOKING_INFORMATION.endpoint.replace(
      '{basketReference}',
      basketReference
    );
    const pmsBookingInformationEndPoint = `${pmsBookingInformationPath}`;
    const newEndPoint = {
      endpoint: pmsBookingInformationEndPoint,
      flowCode: endpoints.PMS_BOOKING_INFORMATION.flowCode,
      axiosClient: expect.any(Function)
    };

    await getPmsBookingInformation({ basketReference, priceBreakdownNeeded }, context);

    expect(get).toHaveBeenCalledWith(
      newEndPoint,
      getPmsBookingInformation,
      { priceBreakdownNeeded },
      context
    );
  }, 1000);

  it('should handle errors when getPmsBookingInformation fails correctly', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);
    await expect(
      getPmsBookingInformation({ basketReference, priceBreakdownNeeded }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getSearchBookingsCcui', () => {
  const context = {};
  afterEach(() => {
    jest.resetAllMocks();
  });

  const searchBookingsCcuiCriteria: SearchBookingsCcuiCriteria = {
    bookingReference: 'BR123',
    bookingsDatabaseSearch: true,
    bookerLastName: 'Doe',
    guestLastName: 'Smith',
    bookerPostcode: '12345',
    hotelId: 'H123',
    bookerEmail: 'dummy@email.com',
    bookerPhone: '1234567890',
    arrivalDateFrom: '2023-12-01',
    arrivalDateTo: '2023-12-02',
    cancellationDate: '2023-12-03',
    companyName: 'Company',
    pageSize: 0,
    pageNumber: 10,
    continuationToken: 'CT123',
    thirdPartyBookingReferenceNumber: 'TPBR123'
  };

  it('should call get when getSearchBookingsCcui is called with correct parameters', async () => {
    await getSearchBookingsCcui(
      { searchBookingsCcuiCriteria: searchBookingsCcuiCriteria },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.SEARCH_BOOKINGS_CCUI,
      getSearchBookingsCcui,
      {
        bookingReference: 'BR123',
        bookingsDatabaseSearch: true,
        bookerLastName: 'Doe',
        guestLastName: 'Smith',
        bookerPostcode: '12345',
        hotelId: 'H123',
        bookerEmail: 'dummy@email.com',
        bookerPhone: '1234567890',
        arrivalDateFrom: '2023-12-01',
        arrivalDateTo: '2023-12-02',
        cancellationDate: '2023-12-03',
        companyName: 'Company',
        pageSize: 0,
        pageNumber: 10,
        continuationToken: 'CT123',
        thirdPartyBookingReferenceNumber: 'TPBR123'
      },
      context
    );
  });

  it('should call get when getSearchBookingsCcui is called with some parameters', async () => {
    const criteria: SearchBookingsCcuiCriteria = {
      bookingReference: 'BR123'
    };
    await getSearchBookingsCcui({ searchBookingsCcuiCriteria: criteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.SEARCH_BOOKINGS_CCUI,
      getSearchBookingsCcui,
      {
        bookingReference: 'BR123',
        bookingsDatabaseSearch: false,
        pageNumber: 0,
        pageSize: 0
      },
      context
    );
  });

  it('should handle errors when getSearchBookingsCcui fails correctly', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getSearchBookingsCcui({ searchBookingsCcuiCriteria: searchBookingsCcuiCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('cancelOnHoldReservation', () => {
  const context = {};
  const basketReference = 'testBasketReference';
  const hotelId = 'my-hotelId';

  it('should call method when cancelOnHoldReservation is called with correct parameters', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    await cancelOnHoldReservation({ basketReference, hotelId }, context);

    expect(put).toHaveBeenCalledWith(
      endpoints.CANCEL_ON_HOLD_RESERVATION,
      cancelOnHoldReservation,
      { basketReference, hotelId },
      context
    );
  });

  it('should handle errors when cancelOnHoldReservation fails correctly', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(cancelOnHoldReservation({ basketReference, hotelId }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('updateReasonForStay', () => {
  const context = {};
  const updateReasonForStayRequest = {
    basketReference: 'BR123',
    hotelId: 'H123',
    reasonForStay: 'BUS'
  };

  it('should call method when updateReasonForStay is called with correct parameters', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });
    await updateReasonForStay({ updateReasonForStayRequest: updateReasonForStayRequest }, context);

    expect(put).toHaveBeenCalledWith(
      endpoints.REASON_FOR_STAY,
      updateReasonForStay,
      updateReasonForStayRequest,
      context
    );
  });

  it('should handle errors when updateReasonForStay fails correctly', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(
      updateReasonForStay({ updateReasonForStayRequest: updateReasonForStayRequest }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('updateRateCode', () => {
  const context = {};
  const rateCodeCriteria = {
    basketReference: 'BR123',
    hotelId: 'H123',
    rateCode: 'EGDTSG'
  };

  it('should call method when updateRateCode is called with correct parameters', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    await updateRateCode({ rateCodeCriteria: rateCodeCriteria }, context);

    expect(put).toHaveBeenCalledWith(
      endpoints.RATE_CODE,
      updateRateCode,
      rateCodeCriteria,
      context
    );
  });

  it('should handle errors when updateRateCode fails correctly', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(updateRateCode({ rateCodeCriteria: rateCodeCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('amendDistribution', () => {
  const context = {};
  const basketReference = 'BR123';
  const amendDistributionCriteria = {
    hotelId: 'H123',
    rateCode: 'EGDTSG'
  };

  const amendDistributionEndpoint = endpoints.AMEND_DISTRIBUTION.endpoint.replace(
    '{basketReference}',
    basketReference
  );
  const serviceEndpoint = {
    ...endpoints.AMEND_DISTRIBUTION,
    endpoint: amendDistributionEndpoint
  };

  it('should call method when amendDistribution is called with correct parameters', async () => {
    await amendDistribution(
      { basketReference: basketReference, amendDistributionCriteria: amendDistributionCriteria },
      context
    );

    expect(post).toHaveBeenCalledWith(
      serviceEndpoint,
      amendDistribution,
      amendDistributionCriteria,
      context
    );
  });

  it('should handle errors when amendDistribution fails correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(
      amendDistribution(
        { basketReference: basketReference, amendDistributionCriteria: amendDistributionCriteria },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('updateReservationOverrideReasons', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const updateReservationOverrideReasonsCriteria = {
    basketReference: 'BR123',
    hotelId: 'H123',
    rateCode: 'EGDTSG'
  };

  it('should call method when updateReservationOverrideReasons is called with correct parameters', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    await updateReservationOverrideReasons(
      { updateReservationOverrideReasonsCriteria: updateReservationOverrideReasonsCriteria },
      context
    );

    expect(put).toHaveBeenCalledWith(
      endpoints.RESERVATION_OVERRIDE_REASONS,
      updateReservationOverrideReasons,
      updateReservationOverrideReasonsCriteria,
      context
    );
  });

  it('should handle errors when updateReservationOverrideReasons fails correctly', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(
      updateReservationOverrideReasons(
        { updateReservationOverrideReasonsCriteria: updateReservationOverrideReasonsCriteria },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('saveCharityPackage', () => {
  const context = {};

  afterEach(() => {
    jest.resetAllMocks();
  });

  const basketReference = 'AQN-e8692e41-f65f-472d-84cc-3eb1751b7767';
  const createPaymentCriteria = {
    booking: {
      businessSite: {
        identifier: 'HEAPTI',
        name: 'London Heathrow Airport (M4/J4)',
        type: 'HOTEL',
        location: 'HEAPTI'
      },
      channel: 'PI',
      journey: 'BOOKING',
      language: 'en',
      rooms: [
        {
          adultsNumber: 1,
          rate: 'FLEXRATE',
          type: 'DOUBLE'
        },
        {
          adultsNumber: 1,
          rate: 'FLEXRATE',
          type: 'DOUBLE'
        }
      ],
      arrivalDate: '2025-04-25',
      departureDate: '2025-04-27',
      type: 'PAY_ON_ARRIVAL'
    },
    payment: {
      billing: {
        address: {
          addressLine1: '1 Middlesex Street',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'LONDON',
          postalCode: 'E1 7AA',
          country: 'GB',
          addressType: 'BUSINESS'
        },
        email: 'alinna.gore@gmail.com',
        firstName: 'Alina',
        lastName: 'Gore',
        telephone: '+440771496994',
        title: 'Mr',
        differentBillingAddress: false,
        bookerIsNotGuest: false
      },
      environment: 'https://www.dit.premierinn.digital',
      subType: 'ECOMM',
      type: 'CARD',
      pibaCardPresent: true
    },
    charityPackageCode: 'ZCHRY1',
    hotelId: 'HEAPTI',
    requestId: '3c58f258-516b-40e1-822f-e03284db5945'
  };

  const args = { basketReference, createPaymentCriteria };

  it('should call the get function when saveCharityPackage is called with correct parameters', async () => {
    await saveCharityPackage(args, context);
    expect(put).toHaveBeenCalledWith(
      endpoints.SAVE_CHARITY_PACKAGES,
      saveCharityPackage,
      {
        basketReferenceId: 'AQN-e8692e41-f65f-472d-84cc-3eb1751b7767',
        hotelId: 'HEAPTI',
        arrivalDate: '2025-04-25',
        departureDate: '2025-04-27',
        roomsSelections: [
          {
            packagesSelection: [
              {
                id: 'ZCHRY1',
                noOfSelections: 1
              }
            ]
          },
          {
            packagesSelection: []
          }
        ]
      },
      context
    );
  });

  it('should handle errors when saveCharityPackage fails gracefully', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);
    await expect(saveCharityPackage(args, context)).rejects.toThrow('Test error');
  });
});

describe('updateCnp', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const basketReference = 'basket-123';
  const updateCnpCriteria = {
    language: 'en',
    countryCode: 'GB',
    businessAccount: 'account'
  };

  it('should call method when updateCnp is called with correct parameters', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    const updateCnpEndPoint = replaceServiceEndpoint(
      endpoints.UPDATE_CNP,
      '{basketReference}',
      basketReference
    );

    await updateCnp(
      { basketReference: basketReference, updateCnpCriteria: updateCnpCriteria },
      context
    );

    expect(put).toHaveBeenCalledWith(updateCnpEndPoint, updateCnp, updateCnpCriteria, context);
  });

  it('should handle errors when updateCnp fails correctly', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(
      updateCnp({ basketReference: basketReference, updateCnpCriteria: updateCnpCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('createMemo', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const createMemoCriteria = {
    basketReference: 'basket-123',
    description: 'description',
    bookingChannel: 'PI'
  };

  it('should call method with correct parameters', async () => {
    await createMemo({ createMemoCriteria: createMemoCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.CREATE_MEMO,
      createMemo,
      createMemoCriteria,
      context
    );
  });

  it('should handle errors correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(createMemo({ createMemoCriteria: createMemoCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('createReservation', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const createReservationCriteria = {
    reservations: [{ hotelId: 'HEAPTI' }],
    bookingChannel: {
      channel: 'DISTR',
      subchannel: 'AGENCY',
      language: 'EN'
    },
    getReservationsByIds: true,
    isOta: true
  };
  const args = { createReservationCriteria };

  it('should call method when createReservation is called with correct parameters', async () => {
    await createReservation(args, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.CREATE_RESERVATION,
      createReservation,
      createReservationCriteria,
      context
    );
  });

  it('should handle errors when createReservation fails correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(createReservation(args, context)).rejects.toThrow('Test error');
  });
});

describe('cancelReservation', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const cancellationCriteria = {
    basketReference: 'AQN-4e050a29-3591-4e65-bd08-2ee12382f76e',
    hotelId: 'HEAPTI',
    token: 'token123'
  };
  const args = { cancellationCriteria };

  it('should call method with correct parameters', async () => {
    await cancelReservation(args, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.CANCEL_RESERVATION,
      cancelReservation,
      cancellationCriteria,
      context
    );
  });

  it('should handle errors correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(cancelReservation(args, context)).rejects.toThrow('Test error');
  });
});

describe('attachFileToReservation', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const fileAttachmentCriteria = {
    fileName: 'file.txt',
    reservationId: 'reservation123',
    overwriteExistingFile: true,
    description: 'description',
    hotelId: 'LONEUS',
    global: true,
    fileAttachment: 'base64file'
  };

  it('should call method with correct parameters when attachFileToReservation is called', async () => {
    await attachFileToReservation({ fileAttachmentCriteria: fileAttachmentCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.ATTACH_TO_RESERVATION,
      attachFileToReservation,
      fileAttachmentCriteria,
      context
    );
  });

  it('should handle errors correctly when attachFileToReservation throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(
      attachFileToReservation({ fileAttachmentCriteria: fileAttachmentCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('updateReservationPackageScheduled', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const updateReservationPackagesScheduledRequest = {
    hotelId: 'LONEUS',
    reservations: 'reservation123'
  };
  it('should call method with correct parameters when updateReservationPackageScheduled is called', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    await updateReservationPackageScheduled(
      { updateReservationPackagesScheduledRequest: updateReservationPackagesScheduledRequest },
      context
    );

    expect(put).toHaveBeenCalledWith(
      endpoints.UPDATE_RESERVATION_PACKAGES_SCHEDULED,
      updateReservationPackageScheduled,
      updateReservationPackagesScheduledRequest,
      context
    );
  });

  it('should handle errors when updateReservationPackageScheduled throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(
      updateReservationPackageScheduled(
        { updateReservationPackagesScheduledRequest: updateReservationPackagesScheduledRequest },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('addNewRoom', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const data = {
    addNewRoomCriteria: {
      tempBookingRef: 'AJK-367922f0',
      token: 'token123'
    }
  };
  it('should call method with correct parameters when addNewRoom is called', async () => {
    (post as jest.Mock).mockResolvedValue({
      data: {}
    });

    await addNewRoom({ addNewRoomCriteria: data.addNewRoomCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.ADD_NEW_ROOM,
      addNewRoom,
      data.addNewRoomCriteria,
      context
    );
  });

  it('should handle errors when addNewRoom throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(addNewRoom(data.addNewRoomCriteria, context)).rejects.toThrow('Test error');
  });
});

describe('removeRoom', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const args: any = {
    tempBookingRef: 'tempRef123',
    reservationId: 'resId123',
    bookingChannel: {
      channel: 'WEB',
      subchannel: 'DIRECT',
      language: 'EN'
    },
    token: 'token123'
  };
  const url =
    '/v1/reservations/rooms/delete?tempBookingRef=tempRef123&reservationId=resId123&channel=WEB&subchannel=DIRECT&token=token123&language=EN';
  const newEndpoint = {
    endpoint: url,
    flowCode: endpoints.REMOVE_ROOM.flowCode,
    axiosClient: expect.any(Function)
  };

  it('should call the post function when removeRoom is called with correct parameters', async () => {
    (post as jest.Mock).mockResolvedValue({ data: {} });
    await removeRoom(args, context);

    expect(post).toHaveBeenCalledWith(newEndpoint, removeRoom, null, context);
  });

  it('should handle errors gracefully when when removeRoom is called and throws errors', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);

    await expect(removeRoom(args, context)).rejects.toThrow('Test error');
  });
});

describe('updateReservationPreferences', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const updateReservationPreferencesRequest = {
    hotelId: 'LONEUS',
    reservationsIds: ['reservation123'],
    preferencesCollections: 'ANNV'
  };
  it('should call method with correct parameters when updateReservationPreferences is called', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    await updateReservationPreferences(
      { updateReservationPreferencesRequest: updateReservationPreferencesRequest },
      context
    );

    expect(put).toHaveBeenCalledWith(
      endpoints.UPDATE_RESERVATION_PREFERENCES,
      updateReservationPreferences,
      updateReservationPreferencesRequest,
      context
    );
  });

  it('should handle errors when updateReservationPreferencesRequest throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(
      updateReservationPreferences(
        { updateReservationPreferencesRequest: updateReservationPreferencesRequest },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('updateEmail', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const basketReference = 'BAS_123_456';
  const updateEmailCriteria = {
    email: 'test.apollo@email.com'
  };

  it('should call method with correct parameters when updating booking email', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });
    await updateEmail({ basketReference, updateEmailCriteria }, context);
    const serviceEndpoint = {
      ...endpoints.UPDATE_EMAIL,
      endpoint: '/v1/reservations/email/BAS_123_456'
    };

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updateEmail,
      {
        email: 'test.apollo@email.com'
      },
      context
    );
  });

  it('should handle errors when updating booking email fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(updateEmail({ basketReference, updateEmailCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('saveReservationAncillaries', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const ancillariesCriteria = {
    basketReference: 'BAS_123_456',
    hotelId: 'HOTEL_1',
    arrivalDate: '2025-04-18',
    departureDate: '2025-04-20',
    roomsSelections: [
      {
        packagesSelection: [
          {
            id: 'BFST',
            noOfSelections: 1
          }
        ]
      }
    ]
  };

  it('should call method with correct parameters when saving reservation ancillaries', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: { basketReference: 'BAS_123_456' }
    });
    const response = await saveReservationAncillaries({ ancillariesCriteria }, context);

    expect(put).toHaveBeenCalledWith(
      endpoints.SAVE_RESERVATION,
      saveReservationAncillaries,
      {
        basketReference: 'BAS_123_456',
        hotelId: 'HOTEL_1',
        arrivalDate: '2025-04-18',
        departureDate: '2025-04-20',
        roomsSelections: [
          {
            packagesSelection: [
              {
                id: 'BFST',
                noOfSelections: 1
              }
            ]
          }
        ]
      },
      context
    );

    expect(response).toEqual('{basketReference=BAS_123_456}');
  });

  it('should handle errors when saving reservation ancillaries fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(saveReservationAncillaries({ ancillariesCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('createReservationGuest', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const createReservationGuestCriteria = {
    hotelId: 'hotelId',
    reasonForStay: 'LEI',
    addressLine4: 'LONDON',
    addressType: 'HOME',
    cityName: 'LONDON',
    countryCode: 'GB',
    postalCode: 'E1 6AN',
    title: 'Mr',
    firstName: 'Adam',
    lastName: 'Smith',
    emailAddress: 'email@email.com',
    mobile: '+4432000032'
  };

  it('should call method with correct parameters when createReservationGuest is called', async () => {
    await createReservationGuest(
      { createReservationGuestCriteria: createReservationGuestCriteria },
      context
    );

    expect(post).toHaveBeenCalledWith(
      endpoints.CREATE_RESERVATION_GUEST,
      createReservationGuest,
      createReservationGuestCriteria,
      context
    );
  });

  it('should handle errors when createReservationGuest throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(
      createReservationGuest(
        { createReservationGuestCriteria: createReservationGuestCriteria },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('updateReservationPackagesByReservation', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const updateReservationPackagesRequest = {
    hotelId: 'LONEUS',
    basketReferenceId: 'basket-123',
    roomsSelections: 'room1'
  };
  it('should call method with correct parameters when updateReservationPackagesByReservation is called', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    await updateReservationPackagesByReservation(
      { updateReservationPackagesRequest: updateReservationPackagesRequest },
      context
    );

    expect(put).toHaveBeenCalledWith(
      endpoints.UPDATE_RESERVATION_PACKAGES_BY_RESERVATION,
      updateReservationPackagesByReservation,
      updateReservationPackagesRequest,
      context
    );
  });

  it('should handle errors when updateReservationPackagesByReservation throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(
      updateReservationPackagesByReservation(
        { updateReservationPackagesRequest: updateReservationPackagesRequest },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('amendEditRoom', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });
  const context = {};
  const editRoomCriteria = {
    reservationId: 'reservation1',
    tempBookingRef: 'basket-123',
    roomsSelections: 'room1',
    requestId: 'request1',
    roomOccupancy: 'occupancy1',
    leadGuest: 'guest1'
  };
  it('should call method with correct parameters when amendEditRoom is called', async () => {
    (put as jest.Mock).mockResolvedValue({
      data: {}
    });

    await amendEditRoom({ editRoomCriteria: editRoomCriteria }, context);

    expect(put).toHaveBeenCalledWith(endpoints.EDIT_ROOM, amendEditRoom, editRoomCriteria, context);
  });

  it('should handle errors when amendEditRoom throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValue(error);
    await expect(amendEditRoom({ editRoomCriteria: editRoomCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('changeBookingDates', () => {
  const context = {};
  const amendStayDatesCriteria = {
    hotelId: 'H123',
    rateCode: 'EGDTSG'
  };

  it('should call method when amendDistribution is called with correct parameters', async () => {
    await changeBookingDates({ amendStayDatesCriteria: amendStayDatesCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.CHANGE_BOOKING_DATES,
      changeBookingDates,
      amendStayDatesCriteria,
      context
    );
  });

  it('should handle errors when amendDistribution fails correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(
      changeBookingDates({ amendStayDatesCriteria: amendStayDatesCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getBookingInformationAuthenticated', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const bookingReference = 'BOOK123';

  it('should call get with correct parameters when getBookingInformationAuthenticated is called', async () => {
    await getBookingInformationAuthenticated({ bookingReference }, context);

    const serviceEndpoint = {
      ...endpoints.BOOKING_INFORMATION_AUTHENTICATED,
      endpoint: '/v1/reservations/basket/BOOK123/authenticated'
    };
    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      getBookingInformationAuthenticated,
      {},
      context
    );
  });

  it('should handle errors when getBookingInformationAuthenticated is called', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);
    await expect(getBookingInformationAuthenticated({ bookingReference }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getBookingInformationAuthenticatedWithToken', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const bookingReference = 'BOOK123';
  const token = 'TOK_123';

  it('should call get with correct parameters when getBookingInformationAuthenticatedWithToken is called', async () => {
    await getBookingInformationAuthenticatedWithToken({ bookingReference, token }, context);

    const serviceEndpoint = {
      ...endpoints.BOOKING_INFORMATION_AUTHENTICATED_WITH_TOKEN,
      endpoint: '/v1/reservations/basket/BOOK123/authenticatedWithToken'
    };
    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      getBookingInformationAuthenticatedWithToken,
      {
        token: 'TOK_123'
      },
      context
    );
  });

  it('should handle errors when getBookingInformationAuthenticatedWithToken is called', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);
    await expect(
      getBookingInformationAuthenticatedWithToken({ bookingReference, token }, context)
    ).rejects.toThrow('Test error');
  });
});
