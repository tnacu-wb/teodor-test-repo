import { ROOM_TYPE } from '@whitbread-eos/api';

export const getBookingInformationData = {
  isLoading: false,
  isError: false,
  error: {
    message: 'error booking information',
  },
  data: {
    bookingInformation: {
      hotelId: 'MANOLD',
      totalCost: 999,
      currencyCode: 'GBP',
      bookingFlowId: 'booking-a1',
      infoMessages: [
        '<p>Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival</p>\n',
      ],
      reservationByIdList: [
        {
          roomStay: {
            adultsNumber: 2,
            childrenNumber: 0,
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: {
              rateName: 'Flex',
            },
            roomExtraInfo: {
              roomType: ROOM_TYPE.PREMIER_PLUS,
              roomName: 'Premier Plus Room',
            },
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
          },
        },
        {
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 0,
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: {
              rateName: 'Flex',
            },
            roomExtraInfo: {
              roomType: ROOM_TYPE.PREMIER_PLUS,
              roomName: 'Premier Plus Room',
            },
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
          },
        },
      ],
      upgradeToFlex: {
        amount: null,
        currency: null,
        flexRateCode: null,
      },
    },
  },
};

export const getBookingInformationDataForGermanHotel = {
  isLoading: false,
  isError: false,
  error: {
    message: 'error booking information',
  },
  data: {
    bookingInformation: {
      hotelId: 'FRAMTI',
      totalCost: 999,
      currencyCode: 'GBP',
      bookingFlowId: 'booking-a1',
      infoMessages: [
        '<p>Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival</p>\n',
      ],
      reservationByIdList: [
        {
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 0,
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: {
              rateName: 'Flex',
            },
            roomExtraInfo: {
              roomType: ROOM_TYPE.PREMIER_PLUS,
              roomName: 'Premier Plus Room',
            },
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
          },
          reservationId: '111111',
        },
        {
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 0,
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: {
              rateName: 'Flex',
            },
            roomExtraInfo: {
              roomType: ROOM_TYPE.PREMIER_PLUS,
              roomName: 'Premier Plus Room',
            },
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
          },
          reservationId: '111112',
        },
      ],
      upgradeToFlex: {
        amount: null,
        currency: null,
        flexRateCode: null,
      },
    },
  },
};

export const singleBookingInformationData = {
  reservationByIdList: [
    {
      roomStay: {
        adultsNumber: 2,
        childrenNumber: 0,
        arrivalDate: '2023-03-23',
        departureDate: '2023-03-24',
        ratePlanCode: 'FLEXRATE',
        rateExtraInfo: {
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomType: ROOM_TYPE.PREMIER_PLUS,
          roomName: 'Premier Plus Room',
        },
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 1315',
        },
      },
    },
  ],
};

export const getHotelInformation = {
  data: {
    hotelInformation: {
      address: {
        addressLine1: 'Sir Alex Ferguson Way',
        addressLine2: 'Trafford Park',
        addressLine3: 'Manchester',
        postalCode: 'M17 1WS',
        country: 'United Kingdom (the)',
      },
      name: 'Manchester Old Trafford',
      brand: 'PID',
      announcement: {
        endDate: '21/07/2022',
        showAnnouncement: 'true',
        startDate: '08/01/2021',
        text: 'Get all the latest updates on our response to&nbsp;<a href="/gb/en/covid-19.html" target="_blank"><u>COVID-19</u></a>&nbsp;and see how we\'re keeping guests safe with our&nbsp;<a href="/gb/en/why/cleanliness.html" target="_blank"><u>Premier Inn CleanProtect<sup>TM</sup></u></a>&nbsp;promise.&nbsp;<br>\r\n',
        title: '',
        type: 'info',
      },
      importantInfo: {
        title: 'Important Information',
        infoItems: [
          {
            text: 'The bathrooms in this hotel have showers only.',
            priority: '1',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
          {
            text: 'Another important information message with highest priority.',
            priority: '10',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
          {
            text: 'Parking is not going to be available during this period of time due to some works on the street.',
            priority: '3',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
        ],
      },
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error hotel information',
  },
};

export const gbHotelInfo = {
  ...getHotelInformation,
  data: {
    ...getHotelInformation.data,
    hotelInformation: {
      ...getHotelInformation.data.hotelInformation,
      brand: 'PI',
    },
  },
};

export const getCountriesData = {
  data: {
    countries: {
      countries: [
        {
          countryCode: 'AT',
          countryCodeLegacy: 'A',
          countryName: 'Austria',
          dialingCode: '+43',
          flagSrc: '',
          passportRequired: true,
          nationality: 'Austrian',
        },
        {
          countryCode: 'GB',
          countryCodeLegacy: 'GB',
          countryName: 'United Kingdom (the)',
          dialingCode: '+44',
          flagSrc: '',
          passportRequired: true,
          nationality: 'British, UK',
        },
        {
          countryCode: 'DE',
          countryCodeLegacy: 'D',
          countryName: 'Germany',
          dialingCode: '+49',
          flagSrc: '',
          passportRequired: true,
          nationality: 'German',
        },
        {
          countryCode: 'RO',
          countryCodeLegacy: 'RO',
          countryName: 'Romania',
          dialingCode: '+40',
          flagSrc: '',
          passportRequired: false,
          nationality: 'Romanian',
        },
      ],
    },
  },
  isLoading: false,
  isError: false,
  isSuccess: true,
  error: {
    message: 'error country selection',
  },
};

export const getPostCodeAddresesData = {
  data: {
    partialAddress: [
      {
        addressText:
          'Prime Minister & First Lord of the Treasury, 10 Downing Street, LONDON SW1A 2AA',
        id: 'GBB|926690f9-91c9-4e57-a1fd-a04b84d923b0|7.730lOGBBEAbnBwAAAAABAwEAAAABsApUEgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAAc3cxYSAyYWEAAAAAAA--$8',
      },
      {
        addressText: 'Star Commerce Partners Ltd, 48 Downing Street, LONDON SW1A 2AA',
        id: 'GBB|926690f9-91c9-4e57-a1fd-a04b84d923b0|7.7309OGBBEAbnBwAAAAABAwEAAAACdp1sEgAhEAYRAKEAAgAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAAc3cxYSAyYWEAAAAAAA--$8',
      },
    ],
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'Error Address',
  },
};

export const getPostCodeAddresesInfoData = {
  data: {
    formattedAddress: {
      addressLine1: '10 Downing Street',
      addressLine2: null,
      addressLine3: null,
      addressLine4: 'London',
      addressLine5: null,
      companyName: 'Prime Minister & First Lord of the Treasury',
      country: 'GB',
      label: 'Prime Minister & First Lord of the Treasury, 10 Downing Street, LONDON, SW1A 2AA',
      postalCode: 'SW1A 2AA',
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'Error formattedAddress',
  },
};

export const getRoomSelectionData = {
  data: {
    packages: {
      packages: {
        roomSelection: [
          {
            packagesSelection: [],
          },
        ],
      },
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error room selection',
  },
};

export const defaultValuesWithGuests = {
  reasonForStay: 'LEI',
  title: 'Mr',
  firstName: 'Chris',
  lastName: 'Smith',
  email: 'test@email.com',
  phone: '+4432443223432',
  landline: '',
  companyName: 'D Young & Company',
  addressLine1: '120 Holborn',
  addressLine2: '',
  addressLine3: '',
  addressLine4: 'LONDON',
  postalCode: 'EC1N 2TD',
  manualAddressToggle: 'manualAddress',
  cityName: 'LONDON',
  postcodeAddress: 'EC1N 2TD',
  addressSelection: 'BUSINESS',
  countryCode: 'GB',
  acceptFutureMailing: 'en',
  whoBookerIsTabs: 'MYSELF',
  billingAddressCheckbox: false,
  dateOfBirth: '12/12/1993',
  leadGuest: [
    {
      firstName: 'Stephan',
      lastName: 'Smith',
      stayInThisRoom: undefined,
      title: 'Mr',
    },
    {
      firstName: 'Henry',
      lastName: 'Smith',
      stayInThisRoom: undefined,
      title: 'Mr',
    },
  ],
};

export const defaultValues = {
  reasonForStay: '',
  title: '',
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  landline: '',
  companyName: '',
  addressLine1: '',
  addressLine2: '',
  addressLine3: '',
  addressLine4: '',
  postalCode: '',
  manualAddressToggle: '',
  cityName: '',
  postcodeAddress: '',
  addressSelection: '',
  countryCode: 'GB',
  acceptFutureMailing: 'en',
  whoBookerIsTabs: 'MYSELF',
  billingAddressCheckbox: false,
};
