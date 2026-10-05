import {
  Currency,
  LanguageEnum,
  PurposeOfStay,
  BCReservationListItem,
  HotelBrand,
} from '@whitbread-eos/api';

import {
  getCityTaxMessages,
  getDefaultDataFromBooking,
  mapBookingInformationForReuseBooking,
  checkIsBookingForSomeoneElse,
} from './guest-details';

const t = (key: string) => {
  switch (key) {
    case 'booking.reason.citytax.leisure.message.noExempt':
      return 'booking.reason.citytax.leisure.message.noExempt';
    case 'booking.confirmation.cityTaxExemptNotification':
      return 'booking.confirmation.cityTaxExemptNotification';
    case 'booking.reason.citytax.notification.price.included':
      return 'booking.reason.citytax.notification.price.included';
    case 'booking.overview.includeCityTax':
      return 'booking.overview.includeCityTax';
    case 'booking.reason.citytax.all.message':
      return 'booking.reason.citytax.all.message';
    case 'booking.reason.citytax.notification.message.noExempt':
      return 'booking.reason.citytax.notification.message.noExempt';
    default:
      return 'default';
  }
};

const singleRoomBookerAsGuestReservation = [
  {
    reservationId: '123',
    reservationGuestList: [
      {
        email: null,
        givenName: 'Catalin',
        isAccompanyingGuest: false,
        nameTitle: 'Mr',
        surName: 'Iosif',
      },
    ],
    createDateTime: '2023-04-25T08:48:24.891438173Z',
    roomStay: {
      checkInTime: '15:00',
      checkOutTime: '12:00',
      roomPrice: 100,
      ratesPerNight: null,
      cot: false,
      roomType: 'Double',
      adultsNumber: 1,
      childrenNumber: 0,
      arrivalDate: '2022-09-27',
      departureDate: '2022-09-28',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: null,
      roomExtraInfo: null,
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    additionalGuestInfo: {
      purposeOfStay: 'LEI',
    },
    reservationPackageList: null,
    depositPolicies: null,
    billing: {
      address: {
        addressLine1: 'Dental Surgery',
        addressLine2: 'addressLine2',
        addressLine3: 'addressLine3',
        addressLine4: 'addressLine4',
        country: 'en',
        postalCode: 'GU16 7HF',
        companyName: 'BUSINESS',
      },
      title: 'Mr',
      telephone: '',
      firstName: 'Catalin',
      lastName: 'Iosif',
      email: 'catalin.iosif@mailinator.com',
    },
    paymentCard: {
      cardNumberMasked: 'XXXXXXXXXXXX0017',
    },
    reservationOverridden: 'false',
    guaranteeCode: 'CC',
    reservationStatus: 'CONFIRMED',
  },
] as BCReservationListItem[];

const multiRoomBookerAsGuestReservation = [
  {
    reservationId: '123',
    reservationGuestList: [
      {
        email: null,
        givenName: 'Isco',
        isAccompanyingGuest: false,
        nameTitle: 'Mr',
        surName: 'Catalin',
      },
    ],
    createDateTime: '2023-04-25T08:48:24.891438173Z',
    roomStay: {
      checkInTime: '15:00',
      checkOutTime: '12:00',
      roomPrice: 100,
      ratesPerNight: null,
      cot: false,
      roomType: 'Double',
      adultsNumber: 1,
      childrenNumber: 0,
      arrivalDate: '2022-09-27',
      departureDate: '2022-09-28',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: null,
      roomExtraInfo: null,
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    additionalGuestInfo: {
      purposeOfStay: 'LEI',
    },
    reservationPackageList: null,
    depositPolicies: null,
    billing: {
      address: {
        addressLine1: 'Dental Surgery',
        addressLine2: 'addressLine2',
        addressLine3: 'addressLine3',
        addressLine4: 'addressLine4',
        country: 'en',
        postalCode: 'GU16 7HF',
        companyName: 'BUSINESS',
      },
      title: 'Mr',
      telephone: '',
      firstName: 'Catalin',
      lastName: 'Iosif',
      email: 'catalin.iosif@mailinator.com',
    },
    paymentCard: {
      cardNumberMasked: 'XXXXXXXXXXXX0017',
    },
    reservationOverridden: 'false',
    guaranteeCode: 'CC',
    reservationStatus: 'CONFIRMED',
  },
  {
    reservationId: '234',
    reservationGuestList: [
      {
        email: null,
        givenName: 'Catalin',
        isAccompanyingGuest: false,
        nameTitle: 'Mr',
        surName: 'Iosif',
      },
    ],
    createDateTime: '2023-04-25T08:48:24.891438173Z',
    roomStay: {
      checkInTime: '15:00',
      checkOutTime: '12:00',
      roomPrice: 100,
      ratesPerNight: null,
      cot: false,
      roomType: 'Double',
      adultsNumber: 1,
      childrenNumber: 0,
      arrivalDate: '2022-09-27',
      departureDate: '2022-09-28',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: null,
      roomExtraInfo: null,
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    additionalGuestInfo: {
      purposeOfStay: 'LEI',
    },
    reservationPackageList: null,
    depositPolicies: null,
    billing: {
      address: {
        addressLine1: 'Dental Surgery',
        addressLine2: 'addressLine2',
        addressLine3: 'addressLine3',
        addressLine4: 'addressLine4',
        country: 'en',
        postalCode: 'GU16 7HF',
        companyName: 'BUSINESS',
      },
      title: 'Mr',
      telephone: '',
      firstName: 'Catalin',
      lastName: 'Iosif',
      email: 'catalin.iosif@mailinator.com',
    },
    paymentCard: {
      cardNumberMasked: 'XXXXXXXXXXXX0017',
    },
    reservationOverridden: 'false',
    guaranteeCode: 'CC',
    reservationStatus: 'CONFIRMED',
  },
] as BCReservationListItem[];

const multiRoomBookerNotAsGuestReservation = [
  {
    reservationId: '123',
    reservationGuestList: [
      {
        email: null,
        givenName: 'Isco',
        isAccompanyingGuest: false,
        nameTitle: 'Mr',
        surName: 'Catalin',
      },
      {
        email: null,
        givenName: 'Isco',
        isAccompanyingGuest: true,
        nameTitle: 'Mrs',
        surName: 'Catalin',
      },
    ],
    createDateTime: '2023-04-25T08:48:24.891438173Z',
    roomStay: {
      checkInTime: '15:00',
      checkOutTime: '12:00',
      roomPrice: 100,
      ratesPerNight: null,
      cot: false,
      roomType: 'Double',
      adultsNumber: 1,
      childrenNumber: 0,
      arrivalDate: '2022-09-27',
      departureDate: '2022-09-28',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: null,
      roomExtraInfo: null,
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    additionalGuestInfo: {
      purposeOfStay: 'LEI',
    },
    reservationPackageList: null,
    depositPolicies: null,
    billing: {
      address: {
        addressLine1: 'Dental Surgery',
        addressLine2: 'addressLine2',
        addressLine3: 'addressLine3',
        addressLine4: 'addressLine4',
        country: 'en',
        postalCode: 'GU16 7HF',
        companyName: 'BUSINESS',
      },
      title: 'Mr',
      telephone: '',
      firstName: 'Catalin',
      lastName: 'Iosif',
      email: 'catalin.iosif@mailinator.com',
    },
    paymentCard: {
      cardNumberMasked: 'XXXXXXXXXXXX0017',
    },
    reservationOverridden: 'false',
    guaranteeCode: 'CC',
    reservationStatus: 'CONFIRMED',
  },
  {
    reservationId: '234',
    reservationGuestList: [
      {
        email: null,
        givenName: 'Catalin',
        isAccompanyingGuest: false,
        nameTitle: 'Mrs',
        surName: 'Iosif',
      },
    ],
    createDateTime: '2023-04-25T08:48:24.891438173Z',
    roomStay: {
      checkInTime: '15:00',
      checkOutTime: '12:00',
      roomPrice: 100,
      ratesPerNight: null,
      cot: false,
      roomType: 'Double',
      adultsNumber: 1,
      childrenNumber: 0,
      arrivalDate: '2022-09-27',
      departureDate: '2022-09-28',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: null,
      roomExtraInfo: null,
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
    additionalGuestInfo: {
      purposeOfStay: 'LEI',
    },
    reservationPackageList: null,
    depositPolicies: null,
    billing: {
      address: {
        addressLine1: 'Dental Surgery',
        addressLine2: 'addressLine2',
        addressLine3: 'addressLine3',
        addressLine4: 'addressLine4',
        country: 'en',
        postalCode: 'GU16 7HF',
        companyName: 'BUSINESS',
      },
      title: 'Mr',
      telephone: '',
      firstName: 'Catalin',
      lastName: 'Iosif',
      email: 'catalin.iosif@mailinator.com',
    },
    paymentCard: {
      cardNumberMasked: 'XXXXXXXXXXXX0017',
    },
    reservationOverridden: 'false',
    guaranteeCode: 'CC',
    reservationStatus: 'CONFIRMED',
  },
] as BCReservationListItem[];

const formBookingProps: {
  reservations: BCReservationListItem[];
  brand: HotelBrand;
  currentLang: string;
  basketReference: string;
  formDetails: Record<string, unknown>;
} = {
  reservations: [
    {
      reservationId: '123',
      reservationGuestList: [
        {
          address: {
            addressLine1: null,
            addressLine2: null,
            addressLine3: null,
            addressLine4: null,
            addressType: null,
            cityName: null,
            countryCode: null,
            postalCode: null,
          },
          email: null,
          givenName: 'Catalin',
          isAccompanyingGuest: false,
          nameTitle: 'Mrs.',
          surName: 'Iosif',
        },
      ],
      createDateTime: '2023-04-25T08:48:24.891438173Z',
      roomStay: {
        checkInTime: '15:00',
        checkOutTime: '12:00',
        roomPrice: 100,
        ratesPerNight: null,
        cot: false,
        roomType: 'Double',
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2022-09-27',
        departureDate: '2022-09-28',
        ratePlanCode: 'FLEXRATE',
        rateExtraInfo: null,
        roomExtraInfo: null,
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 1262',
        },
      },
      additionalGuestInfo: {
        purposeOfStay: 'LEI',
      },
      reservationPackageList: null,
      depositPolicies: null,
      billing: {
        address: {
          addressLine1: 'Dental Surgery',
          addressLine2: 'addressLine2',
          addressLine3: 'addressLine3',
          addressLine4: 'addressLine4',
          country: 'en',
          postalCode: 'GU16 7HF',
          companyName: 'BUSINESS',
        },
        title: 'Mr',
        telephone: '',
        firstName: 'Catalin',
        lastName: 'Iosif',
        email: 'catalin.iosif@mailinator.com',
      },
      paymentCard: {
        cardNumberMasked: 'XXXXXXXXXXXX0017',
      },
      reservationOverridden: 'false',
      guaranteeCode: 'CC',
      reservationStatus: 'CONFIRMED',
    },
  ],
  brand: 'PI' as HotelBrand,
  currentLang: 'en',
  basketReference: '1234',
  formDetails: {
    reasonForStay: 'LEI',
    companyName: '',
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    postalCode: '',
    cityName: '',
    postcodeAddress: '',
    addressSelection: '',
    billingAddressSelection: 'CurrentAddress',
    countryCode: 'en',
    leadGuest: [
      {
        firstName: 'Catalin',
      },
    ],
  },
};

describe('getCityTaxMessages Method', () => {
  it('should return default messages if there is no city tax for both leisure and business', () => {
    const messages = getCityTaxMessages(
      false,
      false,
      PurposeOfStay.LEISURE,
      t,
      Currency.GBP,
      LanguageEnum.ENGLISH,
      100
    );
    const expectedOutput = {
      mainBanner: '',
      secondaryBanner: '',
      summaryText: '',
      confPageBusinessNotif: '',
    };
    expect(messages).toEqual(expectedOutput);
  });

  it('should return messages according to business purpose of stay and no city tax for business', () => {
    const messages = getCityTaxMessages(
      true,
      false,
      PurposeOfStay.BUSINESS,
      t,
      Currency.GBP,
      LanguageEnum.ENGLISH,
      100
    );
    const expectedOutput = {
      mainBanner: 'booking.reason.citytax.leisure.message.noExempt',
      secondaryBanner: '',
      summaryText: '',
      confPageBusinessNotif: 'booking.confirmation.cityTaxExemptNotification',
    };
    expect(messages).toEqual(expectedOutput);
  });

  it('should return messages according to leisure purpose of stay and no city tax for business', () => {
    const messages = getCityTaxMessages(
      true,
      false,
      PurposeOfStay.LEISURE,
      t,
      Currency.GBP,
      LanguageEnum.ENGLISH,
      100
    );
    const expectedOutput = {
      mainBanner: 'booking.reason.citytax.leisure.message.noExempt',
      secondaryBanner: 'booking.reason.citytax.notification.price.included 100.00',
      summaryText: 'booking.overview.includeCityTax',
      confPageBusinessNotif: 'booking.confirmation.cityTaxExemptNotification',
    };
    expect(messages).toEqual(expectedOutput);
  });

  it('should return messages according to city tax for both leisure and business', () => {
    const messages = getCityTaxMessages(
      true,
      true,
      PurposeOfStay.LEISURE,
      t,
      Currency.GBP,
      LanguageEnum.ENGLISH,
      100
    );
    const expectedOutput = {
      mainBanner: 'booking.reason.citytax.all.message',
      secondaryBanner: '',
      summaryText: 'booking.overview.includeCityTax',
      confPageBusinessNotif: 'booking.confirmation.cityTaxExemptNotification',
    };
    expect(messages).toEqual(expectedOutput);
  });

  it('should return messages according to leisure purpose of stay and no city tax for leisure', () => {
    const messages = getCityTaxMessages(
      false,
      true,
      PurposeOfStay.LEISURE,
      t,
      Currency.GBP,
      LanguageEnum.ENGLISH,
      100
    );
    const expectedOutput = {
      mainBanner: 'booking.reason.citytax.notification.message.noExempt',
      secondaryBanner: '',
      summaryText: '',
      confPageBusinessNotif: '',
    };
    expect(messages).toEqual(expectedOutput);
  });

  it('should return messages according to business purpose of stay and no city tax for leisure', () => {
    const messages = getCityTaxMessages(
      false,
      true,
      PurposeOfStay.BUSINESS,
      t,
      Currency.GBP,
      LanguageEnum.ENGLISH,
      100
    );
    const expectedOutput = {
      mainBanner: 'booking.reason.citytax.notification.message.noExempt',
      secondaryBanner: 'booking.reason.citytax.notification.price.included 100.00',
      summaryText: 'booking.overview.includeCityTax',
      confPageBusinessNotif: '',
    };
    expect(messages).toEqual(expectedOutput);
  });
});

describe('getDefaultDataFromBooking Method', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should return data already completed', () => {
    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: 'Dental Surgery',
      addressLine2: 'addressLine2',
      addressLine3: 'addressLine3',
      addressLine4: 'addressLine4',
      addressSelection: 'BUSINESS',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_cityName: undefined,
      billing_companyName: '',
      billing_countryCode: 'GB',
      billing_postalCode: '',
      basketReferenceId: '1234',
      bookingForSomeoneElse: true,
      cityName: 'addressLine4',
      companyName: 'BUSINESS',
      countryCode: 'en',
      email: 'catalin.iosif@mailinator.com',
      firstName: 'Catalin',
      landline: '',
      lastName: 'Iosif',
      leadGuest: [
        {
          firstName: 'Catalin',
        },
      ],
      manualAddressToggle: '',
      phone: '',
      postalCode: 'GU16 7HF',
      postcodeAddress: 'GU16 7HF',
      reasonForStay: 'LEI',
      title: 'Mr',
      whoBookerIsTabs: 'MYSELF',
    };
    expect(data).toEqual(expectedOutput);
  });

  it('should fallback cityName to addressLine4 when billing cityName is missing', () => {
    const reservations = JSON.parse(JSON.stringify(formBookingProps.reservations));
    const formDetails = {
      ...formBookingProps.formDetails,
      cityName: '',
      addressLine4: '',
    };

    reservations[0].billing.address.cityName = '';
    reservations[0].billing.address.addressLine4 = 'Berlin';

    const data = getDefaultDataFromBooking(
      reservations,
      formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand
    );

    expect(data.cityName).toBe('Berlin');
  });

  it('should return data in DE', () => {
    formBookingProps.brand = 'PID';
    formBookingProps.currentLang = 'de';
    formBookingProps.reservations[0].billing.address.addressLine1 = '';
    formBookingProps.reservations[0].billing.address.addressLine2 = '';
    formBookingProps.reservations[0].billing.address.addressLine3 = '';
    formBookingProps.reservations[0].billing.address.addressLine4 = '';
    formBookingProps.reservations[0].billing.address.postalCode = '';

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressSelection: 'BUSINESS',
      basketReferenceId: '1234',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      bookingForSomeoneElse: true,
      billing_cityName: undefined,
      cityName: '',
      companyName: 'BUSINESS',
      countryCode: 'en',
      email: 'catalin.iosif@mailinator.com',
      firstName: 'Catalin',
      landline: '',
      lastName: 'Iosif',
      leadGuest: [
        {
          firstName: 'Catalin',
        },
      ],
      manualAddressToggle: '',
      phone: '',
      postalCode: '',
      postcodeAddress: '',
      reasonForStay: 'LEI',
      title: 'Mr',
      whoBookerIsTabs: 'MYSELF',
    };
    expect(data).toEqual(expectedOutput);
  });

  it('should return data in DE with HOME', () => {
    formBookingProps.formDetails.countryCode = '';
    formBookingProps.reservations[0].billing.address.companyName = '';
    formBookingProps.reservations[0].billing.address.country = '';
    formBookingProps.reservations[0].billing.telephone = '';

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      'de',
      formBookingProps.brand as any
    );

    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressSelection: 'HOME',
      basketReferenceId: '1234',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      billing_cityName: undefined,
      bookingForSomeoneElse: true,
      cityName: '',
      companyName: '',
      countryCode: '',
      email: 'catalin.iosif@mailinator.com',
      firstName: 'Catalin',
      landline: '',
      lastName: 'Iosif',
      leadGuest: [
        {
          firstName: 'Catalin',
        },
      ],
      manualAddressToggle: '',
      phone: '',
      postalCode: '',
      postcodeAddress: '',
      reasonForStay: 'LEI',
      title: 'Mr',
      whoBookerIsTabs: 'MYSELF',
    };

    expect(data).toEqual(expectedOutput);
  });

  it('should return data with correct leadGuest details', () => {
    formBookingProps.formDetails.leadGuest = [];

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressSelection: 'HOME',
      basketReferenceId: '1234',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      billing_cityName: undefined,
      bookingForSomeoneElse: true,
      cityName: '',
      companyName: '',
      countryCode: '',
      email: 'catalin.iosif@mailinator.com',
      firstName: 'Catalin',
      landline: '',
      lastName: 'Iosif',
      leadGuest: [
        {
          firstName: 'Catalin',
          lastName: 'Iosif',
          title: 'Mrs.',
        },
      ],
      manualAddressToggle: '',
      phone: '',
      postalCode: '',
      postcodeAddress: '',
      reasonForStay: 'LEI',
      title: 'Mr',
      whoBookerIsTabs: 'MYSELF',
    };

    expect(data).toEqual(expectedOutput);
  });

  it('should return data with no reason to stay', () => {
    formBookingProps.formDetails.reasonForStay = '';

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressSelection: 'HOME',
      basketReferenceId: '1234',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      billing_cityName: undefined,
      bookingForSomeoneElse: true,
      cityName: '',
      companyName: '',
      countryCode: '',
      email: 'catalin.iosif@mailinator.com',
      firstName: 'Catalin',
      landline: '',
      lastName: 'Iosif',
      leadGuest: [
        {
          firstName: 'Catalin',
          lastName: 'Iosif',
          title: 'Mrs.',
        },
      ],
      manualAddressToggle: '',
      phone: '',
      postalCode: '',
      postcodeAddress: '',
      reasonForStay: 'LEI',
      title: 'Mr',
      whoBookerIsTabs: 'MYSELF',
    };
    expect(data).toEqual(expectedOutput);
  });

  it('should return default data when not completed booking (no givenName)', () => {
    formBookingProps.reservations = [
      {
        ...formBookingProps.reservations[0],
        reservationGuestList: [
          {
            address: {
              addressLine1: null,
              addressLine2: null,
              addressLine3: null,
              addressLine4: null,
              addressType: null,
              cityName: null,
              countryCode: null,
              postalCode: null,
            },
            email: null,
            givenName: '',
            isAccompanyingGuest: false,
            nameTitle: 'Mrs.',
            surName: 'Iosif',
          },
        ],
      },
    ];

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressSelection: '',
      basketReferenceId: '',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_cityName: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      bookingForSomeoneElse: false,
      cityName: '',
      companyName: '',
      countryCode: 'DE',
      email: '',
      firstName: '',
      landline: '',
      lastName: '',
      leadGuest: [],
      manualAddressToggle: 'manualAddress',
      phone: '',
      postalCode: '',
      postcodeAddress: '',
      reasonForStay: '',
      title: '',
      whoBookerIsTabs: 'MYSELF',
    };
    expect(data).toEqual(expectedOutput);
  });

  it('should return default data when not completed booking (no surName)', () => {
    formBookingProps.reservations = [
      {
        ...formBookingProps.reservations[0],
        reservationGuestList: [
          {
            address: {
              addressLine1: null,
              addressLine2: null,
              addressLine3: null,
              addressLine4: null,
              addressType: null,
              cityName: null,
              countryCode: null,
              postalCode: null,
            },
            email: null,
            givenName: 'Catalina',
            isAccompanyingGuest: false,
            nameTitle: 'Mrs.',
            surName: '',
          },
        ],
      },
    ];

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressSelection: '',
      basketReferenceId: '',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_cityName: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      bookingForSomeoneElse: false,
      cityName: '',
      companyName: '',
      countryCode: 'DE',
      email: '',
      firstName: '',
      landline: '',
      lastName: '',
      leadGuest: [],
      manualAddressToggle: 'manualAddress',
      phone: '',
      postalCode: '',
      postcodeAddress: '',
      reasonForStay: '',
      title: '',
      whoBookerIsTabs: 'MYSELF',
    };
    expect(data).toEqual(expectedOutput);
  });

  it('should return default data when not completed booking (no nameTitle)', () => {
    formBookingProps.reservations = [
      {
        ...formBookingProps.reservations[0],
        reservationGuestList: [
          {
            address: {
              addressLine1: null,
              addressLine2: null,
              addressLine3: null,
              addressLine4: null,
              addressType: null,
              cityName: null,
              countryCode: null,
              postalCode: null,
            },
            email: null,
            givenName: 'Catalina',
            isAccompanyingGuest: false,
            nameTitle: '',
            surName: 'Iosif',
          },
        ],
      },
    ];

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressSelection: '',
      basketReferenceId: '',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_cityName: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      bookingForSomeoneElse: false,
      cityName: '',
      companyName: '',
      countryCode: 'DE',
      email: '',
      firstName: '',
      landline: '',
      lastName: '',
      leadGuest: [],
      manualAddressToggle: 'manualAddress',
      phone: '',
      postalCode: '',
      postcodeAddress: '',
      reasonForStay: '',
      title: '',
      whoBookerIsTabs: 'MYSELF',
    };
    expect(data).toEqual(expectedOutput);
  });

  it('should return data with correct leadGuest details for single room booking with booker is guest', () => {
    formBookingProps.reservations = singleRoomBookerAsGuestReservation;

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: 'Dental Surgery',
      addressLine2: 'addressLine2',
      addressLine3: 'addressLine3',
      addressLine4: 'addressLine4',
      addressSelection: 'BUSINESS',
      basketReferenceId: '1234',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      billing_cityName: undefined,
      bookingForSomeoneElse: false,
      cityName: 'addressLine4',
      companyName: 'BUSINESS',
      countryCode: 'en',
      email: 'catalin.iosif@mailinator.com',
      firstName: 'Catalin',
      landline: '',
      lastName: 'Iosif',
      leadGuest: [],
      manualAddressToggle: '',
      phone: '',
      postalCode: 'GU16 7HF',
      postcodeAddress: 'GU16 7HF',
      reasonForStay: 'LEI',
      title: 'Mr',
      whoBookerIsTabs: 'MYSELF',
    };

    expect(data).toEqual(expectedOutput);
  });

  it('should return data with correct leadGuest details for multi room booking with booker as guest', () => {
    formBookingProps.reservations = multiRoomBookerAsGuestReservation;

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: 'Dental Surgery',
      addressLine2: 'addressLine2',
      addressLine3: 'addressLine3',
      addressLine4: 'addressLine4',
      addressSelection: 'BUSINESS',
      basketReferenceId: '1234',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      billing_cityName: undefined,
      bookingForSomeoneElse: false,
      cityName: 'addressLine4',
      companyName: 'BUSINESS',
      countryCode: 'en',
      email: 'catalin.iosif@mailinator.com',
      firstName: 'Catalin',
      landline: '',
      lastName: 'Iosif',
      leadGuest: [
        {
          firstName: 'Isco',
          lastName: 'Catalin',
          title: 'Mr',
          stayInThisRoom: false,
        },
        {
          stayInThisRoom: true,
        },
      ],
      manualAddressToggle: '',
      phone: '',
      postalCode: 'GU16 7HF',
      postcodeAddress: 'GU16 7HF',
      reasonForStay: 'LEI',
      title: 'Mr',
      whoBookerIsTabs: 'MYSELF',
    };

    expect(data).toEqual(expectedOutput);
  });

  it('should return data with correct leadGuest details for multi room booking with booker not as guest', () => {
    formBookingProps.reservations = multiRoomBookerNotAsGuestReservation;

    const data = getDefaultDataFromBooking(
      formBookingProps.reservations,
      formBookingProps.formDetails,
      formBookingProps.basketReference,
      formBookingProps.currentLang,
      formBookingProps.brand as any
    );
    const expectedOutput = {
      acceptFutureMailing: false,
      addressLine1: 'Dental Surgery',
      addressLine2: 'addressLine2',
      addressLine3: 'addressLine3',
      addressLine4: 'addressLine4',
      addressSelection: 'BUSINESS',
      basketReferenceId: '1234',
      billing_addressLine1: '',
      billing_addressLine2: '',
      billing_addressLine3: '',
      billing_addressLine4: '',
      billing_companyName: '',
      billing_countryCode: 'DE',
      billing_postalCode: '',
      billing_cityName: undefined,
      bookingForSomeoneElse: false,
      cityName: 'addressLine4',
      companyName: 'BUSINESS',
      countryCode: 'en',
      email: 'catalin.iosif@mailinator.com',
      firstName: 'Catalin',
      landline: '',
      lastName: 'Iosif',
      leadGuest: [
        {
          firstName: 'Isco',
          lastName: 'Catalin',
          title: 'Mr',
          stayInThisRoom: false,
          accompanyingfirstName: 'Isco',
          accompanyinglastName: 'Catalin',
          accompanyingtitle: 'Mrs',
        },
        {
          firstName: 'Catalin',
          lastName: 'Iosif',
          title: 'Mrs',
          stayInThisRoom: false,
        },
      ],
      manualAddressToggle: '',
      phone: '',
      postalCode: 'GU16 7HF',
      postcodeAddress: 'GU16 7HF',
      reasonForStay: 'LEI',
      title: 'Mr',
      whoBookerIsTabs: 'MYSELF',
    };

    expect(data).toEqual(expectedOutput);
  });

  it('should return stored formDetails when booking is not completed and formDetails has updated flag with matching basketReferenceId', () => {
    const notCompletedReservations = [
      {
        ...formBookingProps.reservations[0],
        reservationGuestList: [
          {
            ...formBookingProps.reservations[0].reservationGuestList[0],
            givenName: null,
            surName: null,
          },
        ],
      },
    ];

    const reusedFormDetails = {
      updated: true,
      basketReferenceId: '1234',
      firstName: 'John',
      lastName: 'Doe',
      title: 'Mr',
      email: 'john.doe@test.com',
      phone: '07777777777',
      leadGuest: undefined,
    };

    const data = getDefaultDataFromBooking(
      notCompletedReservations as any,
      reusedFormDetails,
      '1234',
      'en',
      'PI' as any
    );

    expect(data.firstName).toBe('John');
    expect(data.lastName).toBe('Doe');
    expect(data.email).toBe('john.doe@test.com');
    expect(data.basketReferenceId).toBe('1234');
    // leadGuest should fall back to defaultData.leadGuest when formDetails.leadGuest is undefined
    expect(data.leadGuest).toEqual([]);
  });
});

describe('mapBookingInformationForReuseBooking Method', () => {
  it('should map booking information with business address selection', () => {
    const basketReference = 'basket-ref-123';
    const reservations = [
      {
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        billing: {
          address: {
            addressLine1: 'Line 1',
            addressLine2: 'Line 2',
            addressLine3: 'Line 3',
            addressLine4: 'Line 4',
            cityName: 'London',
            companyName: 'Whitbread',
            country: 'GB',
            postalCode: 'SW1A 1AA',
          },
          email: 'test@test.com',
          firstName: 'Jane',
          landline: '02000000000',
          lastName: 'Doe',
          telephone: '07123456789',
          title: 'Ms',
        },
      },
    ] as BCReservationListItem[];

    const data = mapBookingInformationForReuseBooking(basketReference, reservations, 'en');

    expect(data).toEqual({
      reasonForStay: 'LEI',
      acceptFutureMailing: false,
      manualAddressToggle: 'manualAddress',
      addressSelection: 'BUSINESS',
      whoBookerIsTabs: 'MYSELF',
      addressLine1: 'Line 1',
      addressLine2: 'Line 2',
      addressLine3: 'Line 3',
      addressLine4: 'Line 4',
      postcodeAddress: 'SW1A 1AA',
      cityName: 'London',
      companyName: 'Whitbread',
      countryCode: 'GB',
      email: 'test@test.com',
      firstName: 'Jane',
      landline: '02000000000',
      lastName: 'Doe',
      postalCode: 'SW1A 1AA',
      phone: '07123456789',
      title: 'Ms',
      basketReferenceId: basketReference,
    });
  });

  it('should set manualAddressToggle for de language when multi room redesign is disabled', () => {
    const reservations = [
      {
        billing: {
          address: {
            companyName: '',
            addressLine4: 'Berlin',
          },
        },
      },
    ] as BCReservationListItem[];

    const data = mapBookingInformationForReuseBooking('basket-ref-123', reservations, 'de', false);

    expect(data.manualAddressToggle).toBe('manualAddress');
    expect(data.addressSelection).toBe('HOME');
  });

  it('should keep manualAddressToggle empty for de language when multi room redesign is enabled', () => {
    const reservations = [
      {
        billing: {
          address: {
            cityName: '',
            addressLine4: 'Munich',
          },
        },
      },
    ] as BCReservationListItem[];

    const data = mapBookingInformationForReuseBooking('basket-ref-123', reservations, 'de', true);

    expect(data.manualAddressToggle).toBe('');
    expect(data.cityName).toBe('Munich');
  });

  it('should return safe defaults when billing details are missing', () => {
    const reservations = [{}] as BCReservationListItem[];

    const data = mapBookingInformationForReuseBooking('basket-ref-123', reservations, 'en');

    expect(data).toEqual({
      reasonForStay: '',
      acceptFutureMailing: false,
      manualAddressToggle: '',
      addressSelection: 'HOME',
      whoBookerIsTabs: 'MYSELF',
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      postcodeAddress: '',
      cityName: '',
      companyName: '',
      countryCode: '',
      email: '',
      firstName: '',
      landline: '',
      lastName: '',
      postalCode: '',
      phone: '',
      title: '',
      basketReferenceId: 'basket-ref-123',
    });
  });
});

describe('checkIsBookingForSomeoneElse Method', () => {
  it('should return false for single room booking when booker is the guest', () => {
    const reservations = singleRoomBookerAsGuestReservation;
    const result = checkIsBookingForSomeoneElse(reservations);
    expect(result).toBe(false);
  });

  it('should return true for single room booking when booker is NOT the guest', () => {
    const reservations = [
      {
        reservationId: '123',
        reservationGuestList: [
          {
            email: null,
            givenName: 'John',
            isAccompanyingGuest: false,
            nameTitle: 'Mr',
            surName: 'Doe',
          },
        ],
        createDateTime: '2023-04-25T08:48:24.891438173Z',
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          roomPrice: 100,
          ratesPerNight: null,
          cot: false,
          roomType: 'Double',
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-09-27',
          departureDate: '2022-09-28',
          ratePlanCode: 'FLEXRATE',
          rateExtraInfo: null,
          roomExtraInfo: null,
          accessibleRoom: {
            isAccessible: false,
            phoneNumber: '0333 321 1262',
          },
        },
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        reservationPackageList: null,
        depositPolicies: null,
        billing: {
          address: {
            addressLine1: 'Dental Surgery',
            addressLine2: 'addressLine2',
            addressLine3: 'addressLine3',
            addressLine4: 'addressLine4',
            country: 'en',
            postalCode: 'GU16 7HF',
            companyName: 'BUSINESS',
          },
          title: 'Mr',
          telephone: '',
          firstName: 'Jane',
          lastName: 'Smith',
          email: 'jane.smith@mailinator.com',
        },
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverridden: 'false',
        guaranteeCode: 'CC',
        reservationStatus: 'CONFIRMED',
      },
    ] as BCReservationListItem[];

    const result = checkIsBookingForSomeoneElse(reservations);
    expect(result).toBe(true);
  });

  it('should return false for multi-room booking regardless of guest match', () => {
    const reservations = multiRoomBookerNotAsGuestReservation;
    const result = checkIsBookingForSomeoneElse(reservations);
    expect(result).toBe(false);
  });

  it('should return false when single room has no lead guest', () => {
    const reservations = [
      {
        reservationId: '123',
        reservationGuestList: [
          {
            email: null,
            givenName: 'Jane',
            isAccompanyingGuest: true,
            nameTitle: 'Mrs',
            surName: 'Doe',
          },
        ],
        createDateTime: '2023-04-25T08:48:24.891438173Z',
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          roomPrice: 100,
          ratesPerNight: null,
          cot: false,
          roomType: 'Double',
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-09-27',
          departureDate: '2022-09-28',
          ratePlanCode: 'FLEXRATE',
          rateExtraInfo: null,
          roomExtraInfo: null,
          accessibleRoom: {
            isAccessible: false,
            phoneNumber: '0333 321 1262',
          },
        },
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        reservationPackageList: null,
        depositPolicies: null,
        billing: {
          address: {
            addressLine1: 'Dental Surgery',
            addressLine2: 'addressLine2',
            addressLine3: 'addressLine3',
            addressLine4: 'addressLine4',
            country: 'en',
            postalCode: 'GU16 7HF',
            companyName: 'BUSINESS',
          },
          title: 'Mr',
          telephone: '',
          firstName: 'John',
          lastName: 'Booker',
          email: 'john.booker@mailinator.com',
        },
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverridden: 'false',
        guaranteeCode: 'CC',
        reservationStatus: 'CONFIRMED',
      },
    ] as BCReservationListItem[];

    const result = checkIsBookingForSomeoneElse(reservations);
    expect(result).toBe(false);
  });

  it('should handle missing billing data gracefully', () => {
    const reservations = [
      {
        reservationId: '123',
        reservationGuestList: [
          {
            email: null,
            givenName: 'John',
            isAccompanyingGuest: false,
            nameTitle: 'Mr',
            surName: 'Doe',
          },
        ],
        createDateTime: '2023-04-25T08:48:24.891438173Z',
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          roomPrice: 100,
          ratesPerNight: null,
          cot: false,
          roomType: 'Double',
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-09-27',
          departureDate: '2022-09-28',
          ratePlanCode: 'FLEXRATE',
          rateExtraInfo: null,
          roomExtraInfo: null,
          accessibleRoom: {
            isAccessible: false,
            phoneNumber: '0333 321 1262',
          },
        },
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        reservationPackageList: null,
        depositPolicies: null,
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverridden: 'false',
        guaranteeCode: 'CC',
        reservationStatus: 'CONFIRMED',
      },
    ] as BCReservationListItem[];

    const result = checkIsBookingForSomeoneElse(reservations);
    // Should return true because guest John Doe doesn't match missing booker data (defaults to '')
    expect(result).toBe(true);
  });

  it('should compare all three name fields (firstName, lastName, title) for match', () => {
    const reservations = [
      {
        reservationId: '123',
        reservationGuestList: [
          {
            email: null,
            givenName: 'John',
            isAccompanyingGuest: false,
            nameTitle: 'Mr',
            surName: 'Doe',
          },
        ],
        createDateTime: '2023-04-25T08:48:24.891438173Z',
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          roomPrice: 100,
          ratesPerNight: null,
          cot: false,
          roomType: 'Double',
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-09-27',
          departureDate: '2022-09-28',
          ratePlanCode: 'FLEXRATE',
          rateExtraInfo: null,
          roomExtraInfo: null,
          accessibleRoom: {
            isAccessible: false,
            phoneNumber: '0333 321 1262',
          },
        },
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        reservationPackageList: null,
        depositPolicies: null,
        billing: {
          address: {
            addressLine1: 'Dental Surgery',
            addressLine2: 'addressLine2',
            addressLine3: 'addressLine3',
            addressLine4: 'addressLine4',
            country: 'en',
            postalCode: 'GU16 7HF',
            companyName: 'BUSINESS',
          },
          title: 'Mr',
          telephone: '',
          firstName: 'John',
          lastName: 'Doe',
          // Different title should make it return true (not same booker)
          email: 'john.doe@mailinator.com',
        },
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverridden: 'false',
        guaranteeCode: 'CC',
        reservationStatus: 'CONFIRMED',
      },
    ] as BCReservationListItem[];

    const result = checkIsBookingForSomeoneElse(reservations);
    // Should return false because all three fields match (guest is the booker)
    expect(result).toBe(false);
  });

  it('should return false when guest lastName is empty and lastName does not match', () => {
    const reservations = [
      {
        reservationId: '123',
        reservationGuestList: [
          {
            email: null,
            givenName: 'John',
            isAccompanyingGuest: false,
            nameTitle: 'Mr',
            surName: '',
          },
        ],
        createDateTime: '2023-04-25T08:48:24.891438173Z',
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          roomPrice: 100,
          ratesPerNight: null,
          cot: false,
          roomType: 'Double',
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-09-27',
          departureDate: '2022-09-28',
          ratePlanCode: 'FLEXRATE',
          rateExtraInfo: null,
          roomExtraInfo: null,
          accessibleRoom: {
            isAccessible: false,
            phoneNumber: '0333 321 1262',
          },
        },
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        reservationPackageList: null,
        depositPolicies: null,
        billing: {
          address: {
            addressLine1: 'Dental Surgery',
            addressLine2: 'addressLine2',
            addressLine3: 'addressLine3',
            addressLine4: 'addressLine4',
            country: 'en',
            postalCode: 'GU16 7HF',
            companyName: 'BUSINESS',
          },
          title: 'Mr',
          telephone: '',
          firstName: 'John',
          lastName: 'Smith',
          email: 'john.smith@mailinator.com',
        },
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverridden: 'false',
        guaranteeCode: 'CC',
        reservationStatus: 'CONFIRMED',
      },
    ] as BCReservationListItem[];

    const result = checkIsBookingForSomeoneElse(reservations);
    // Should return true because surnames don't match
    expect(result).toBe(true);
  });
});
