import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { RepeatBookingPage } from './index';

const bkngData = {
  bookingConfirmation: {
    balanceOutstanding: 0,
    bookingFlowId: 'booking-hub',
    currencyCode: 'GBP',
    hotelId: 'LONKIN',
    infoMessages: ['<p>Free cancellation up to 1pm on the day of arrival</p>\n'],
    newTotal: 6051,
    policyCode: 'D1',
    totalCost: 6051,
    bookingReference: 'MAH7346157',
    bookingSpinnerConfig: [
      {
        order: '1',
        seconds: '10',
        text: 'One moment...',
      },
      {
        order: '2',
        seconds: '20',
        text: 'Hold tight we’re processing your order',
      },
      {
        order: '3',
        seconds: '30',
        text: 'Sorry for the delay - please bear with us',
      },
    ],
    reservationByIdList: [
      {
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        reservationPackageList: [
          {
            description: 'Premier Inn Breakfast',
            unitPrice: 9.5,
            totalQuantity: 1,
            computedPrice: 9.5,
          },
        ],
        depositPolicies: [
          {
            amountDue: {
              amount: 0,
              currencyCode: null,
            },
            amountPaid: {
              amount: 2017,
              currencyCode: null,
            },
            policyCode: 'D1',
          },
        ],
        reservationGuestList: [
          {
            givenName: 'tester',
            surName: 'testerqa',
            nameTitle: 'Mrs',
          },
        ],
        roomStay: {
          roomPrice: 1998,
          adultsNumber: 1,
          arrivalDate: '2023-09-13',
          childrenNumber: 0,
          departureDate: '2023-09-15',
          roomType: 'BIGWIN',
          cot: false,
          ratePlanCode: 'FLEXRATE',
          ratesPerNight: [
            {
              startDate: '2023-09-13',
              pricePerNight: 999,
              cityTaxPerNight: 0,
            },
            {
              startDate: '2023-09-14',
              pricePerNight: 999,
              cityTaxPerNight: 0,
            },
          ],
          rateExtraInfo: {
            rateDescription:
              'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
            rateLongDescription: '',
            rateName: 'Flex Rate',
          },
          roomExtraInfo: {
            roomDescription:
              'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
            roomType: 'BIGWIN',
            roomName: 'Bigger Room',
          },
        },
        billing: {
          address: {
            addressLine1: '33 Poplar Grove',
            addressLine2: 'Ramsbottom',
            addressLine3: '',
            addressLine4: 'BURY',
            country: 'GB',
            postalCode: 'BL0 0BE',
          },
          title: 'Mrs',
          telephone: '+4456576786797',
          lastName: 'testerqa',
          firstName: 'tester',
          email: 'lorena.fruntisanu@whitbread.com',
        },
      },
      {
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        reservationPackageList: [
          {
            description: 'Premier Inn Breakfast',
            unitPrice: 9.5,
            totalQuantity: 1,
            computedPrice: 9.5,
          },
        ],
        depositPolicies: [
          {
            amountDue: {
              amount: 0,
              currencyCode: null,
            },
            amountPaid: {
              amount: 2017,
              currencyCode: null,
            },
            policyCode: 'D1',
          },
        ],
        reservationGuestList: [
          {
            givenName: 'gyytd',
            surName: 'room',
            nameTitle: 'Prof',
          },
        ],
        roomStay: {
          roomPrice: 1998,
          adultsNumber: 1,
          arrivalDate: '2023-09-13',
          childrenNumber: 0,
          departureDate: '2023-09-15',
          roomType: 'BIGWIN',
          cot: false,
          ratePlanCode: 'FLEXRATE',
          ratesPerNight: [
            {
              startDate: '2023-09-13',
              pricePerNight: 999,
              cityTaxPerNight: 0,
            },
            {
              startDate: '2023-09-14',
              pricePerNight: 999,
              cityTaxPerNight: 0,
            },
          ],
          rateExtraInfo: {
            rateDescription:
              'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
            rateLongDescription: '',
            rateName: 'Flex Rate',
          },
          roomExtraInfo: {
            roomDescription:
              'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
            roomType: 'BIGWIN',
            roomName: 'Bigger Room',
          },
        },
        billing: {
          address: {
            addressLine1: '33 Poplar Grove',
            addressLine2: 'Ramsbottom',
            addressLine3: '',
            addressLine4: 'BURY',
            country: 'GB',
            postalCode: 'BL0 0BE',
          },
          title: 'Mrs',
          telephone: '+4456576786797',
          lastName: 'testerqa',
          firstName: 'tester',
          email: 'lorena.fruntisanu@whitbread.com',
        },
      },
      {
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
        reservationPackageList: [
          {
            description: 'Premier Inn Breakfast',
            unitPrice: 9.5,
            totalQuantity: 1,
            computedPrice: 9.5,
          },
        ],
        depositPolicies: [
          {
            amountDue: {
              amount: 0,
              currencyCode: null,
            },
            amountPaid: {
              amount: 2017,
              currencyCode: null,
            },
            policyCode: 'D1',
          },
        ],
        reservationGuestList: [
          {
            givenName: 'testing',
            surName: 'rooms',
            nameTitle: 'Lady',
          },
        ],
        roomStay: {
          roomPrice: 1998,
          adultsNumber: 1,
          arrivalDate: '2023-09-13',
          childrenNumber: 0,
          departureDate: '2023-09-15',
          roomType: 'BIGWIN',
          cot: false,
          ratePlanCode: 'FLEXRATE',
          ratesPerNight: [
            {
              startDate: '2023-09-13',
              pricePerNight: 999,
              cityTaxPerNight: 0,
            },
            {
              startDate: '2023-09-14',
              pricePerNight: 999,
              cityTaxPerNight: 0,
            },
          ],
          rateExtraInfo: {
            rateDescription:
              'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
            rateLongDescription: '',
            rateName: 'Flex Rate',
          },
          roomExtraInfo: {
            roomDescription:
              'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
            roomType: 'BIGWIN',
            roomName: 'Bigger Room',
          },
        },
        billing: {
          address: {
            addressLine1: '33 Poplar Grove',
            addressLine2: 'Ramsbottom',
            addressLine3: '',
            addressLine4: 'BURY',
            country: 'GB',
            postalCode: 'BL0 0BE',
          },
          title: 'Mrs',
          telephone: '+4456576786797',
          lastName: 'testerqa',
          firstName: 'tester',
          email: 'lorena.fruntisanu@whitbread.com',
        },
      },
    ],
  },
};

const mockHotelInformationData = {
  hotelInformation: {
    address: {
      addressLine1: '50 Wharfdale Road',
      addressLine2: 'London',
      addressLine3: '',
      addressLine4: null,
      postalCode: 'N1 9FA',
      country: 'United Kingdom (the)',
    },
    hotelId: 'LONKIN',
    hotelOpeningDate: '',
    name: 'hub London Kings Cross',
    brand: 'HUB',
    parkingDescription:
      "<p>hub by Premier Inn does not have a car park. We're so central we would encourage you to leave your car at home and arrive by public transport.</p>",
    directions:
      '<p>When leaving Kings Cross St Pancras Station, turn left onto York Way. Continue to follow York Way. The hotel will be on your right. The closest tube station is Kings Cross St Pancras.</p>\n',
    county: 'greater-london',
    contactDetails: {
      phone: '0871 527 9225',
      hotelNationalPhone: '0333 321 3104',
      email: '',
    },
    coordinates: {
      latitude: 51.533674,
      longitude: -0.122153,
    },
    links: {
      detailsPage: '/england/greater-london/london/hub-london-kings-cross',
    },
    galleryImages: [
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-lounge2.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONSTM/3.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-reception_1.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONSTM/2.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/generic/hub-bigger.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONSTM/4.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/generic/hub-accessible-bedroom.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/generic/hub-accessible-wet-room.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-lounge1.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-lounge-5.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-lounge-6.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-lounge-4.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-lounge-7.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-meeting-room-3.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-meeting-room-2.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-meeting-room-1.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-meeting-room-4.jpg',
      },
      {
        alt: '',
        thumbnailSrc: '/content/dam/hub/hotelimages/LONKIN/Kings-Cross-1.jpg',
      },
    ],
    announcement: {
      endDate: '21/07/2034',
      showAnnouncement: 'true',
      startDate: '08/01/2021',
      text: 'Get all the latest updates on our response to&nbsp;<a href="/gb/en/covid-19.html" target="_blank"><u>COVID-19</u></a>&nbsp;and see how we’re keeping guests safe with our&nbsp;<a href="/gb/en/why/cleanliness.html" target="_blank"><u>Premier Inn CleanProtect<sup>TM</sup></u></a>&nbsp;promise.&nbsp;<br>\r\n',
      title: '',
      type: 'info',
    },
    importantInfo: {
      title: 'Important Information',
      infoItems: [
        {
          text: 'Here at hub by Premier Inn our terms and conditions are a little different – so please make sure you check them out before booking.',
          priority: '1',
          startDate: '20/06/2023',
          endDate: '20/06/2043',
        },
      ],
    },
  },
};

const mockSearchRulesData = {
  data: {
    maxNightsLimitation: {
      maxNights: 364,
    },
    globalConfig: {
      maxRoomsLim: {
        maxRooms: 9,
      },
    },
    maxArrivalDateLimitation: {
      maxArrivalDate: 364,
    },
    roomOccupancyLimitations: {
      roomOccupancies: [
        {
          adultsNumber: 2,
          childrenNumber: 2,
          acceptedRoomTypes: ['FAM'],
        },
        {
          adultsNumber: 2,
          childrenNumber: 1,
          acceptedRoomTypes: ['DIS', 'FAM'],
        },
        {
          adultsNumber: 2,
          childrenNumber: 0,
          acceptedRoomTypes: ['DB', 'TWIN', 'DIS', 'FAM'],
        },
        {
          adultsNumber: 1,
          childrenNumber: 2,
          acceptedRoomTypes: ['DIS', 'FAM'],
        },
        {
          adultsNumber: 1,
          childrenNumber: 1,
          acceptedRoomTypes: ['DB', 'TWIN', 'DIS', 'FAM'],
        },
        {
          adultsNumber: 1,
          childrenNumber: 0,
          acceptedRoomTypes: ['SB', 'DB', 'TWIN', 'DIS', 'FAM'],
        },
      ],
    },
  },
};

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    invalidateQueries: jest.fn(),
  }),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      switch (key) {
        case 'ccui.manageBooking.options.repeatBooking':
          return 'Repeat booking';
      }
    },
  }),
}));

const mockBookingInfoRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: bkngData,
};

const mockHotelInfoRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: mockHotelInformationData,
};

const mockSearchRulesRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: mockSearchRulesData.data,
};

const mockStaticContentRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    headerInformation: {
      config: {
        roomCodes: {
          family: 'FAM',
          accessible: 'DIS',
          single: 'SB',
          twin: 'TWIN',
          double: 'DB',
        },
      },
    },
  },
};

const mockRoomTypeInfoRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    roomTypeInformation: {
      roomTypes: [
        {
          roomTypeCode: ['LOWTWN'],
          roomCategory: 'Accessible room',
          roomLabel: 'Accessible twin bedroom with a lowered bath',
          roomDescription:
            'Accessible twin bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
          groupId: 'accessible',
        },
      ],
    },
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
    isSuccess: false,
  }),
  useQueryRequest: (queryKey: any) => {
    const key = queryKey[0];
    switch (key) {
      case 'GetBookingConfirmation':
        return mockBookingInfoRequest;
      case 'GetHotelInformation':
        return mockHotelInfoRequest;
      case 'getSearchRules':
        return mockSearchRulesRequest;
      case 'GetStaticContent':
        return mockStaticContentRequest;
      case 'getRoomTypeInformation':
        return mockRoomTypeInfoRequest;
    }
  },
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

mockUseRouter.mockReturnValue({
  locale: 'en',
  asPath: 'ancillaries?reservationId=LONEUS0936286',
  query: {
    searchLocation: 'London',
    ARRdd: Number(23),
    ARRmm: Number(10),
    ARRyyyy: Number(2024),
    NIGHTS: Number(5),
    ROOMS: Number(1),
    ADULT1: Number(1),
    CHILD1: Number(0),
    locale: 'en',
  },
});

jest.mock('@whitbread-eos/organisms', () => ({
  CCUISearchContainer: () => <div />,
  BookingHistoryInfoCardContainer: () => <div>Repeat Booking Card</div>,
}));
const queryClient = new QueryClient();
describe('Repeat booking page', () => {
  it('should render Repeat booking page and find page title', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <RepeatBookingPage />
      </QueryClientProvider>
    );
    expect(getByTestId('repeat-booking-page-title')).toBeInTheDocument();
  });
  it('should render Repeat booking card component', async () => {
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <RepeatBookingPage />
      </QueryClientProvider>
    );
    expect(getByText('Repeat Booking Card')).toBeInTheDocument();
  });
  it('should find the covid notification on Repeat booking page', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <RepeatBookingPage />
      </QueryClientProvider>
    );
    expect(getByTestId('AlertTitle')).toBeInTheDocument();
  });
  it('should show loading message on Repeat booking page', async () => {
    mockBookingInfoRequest.isLoading = true;
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <RepeatBookingPage />
      </QueryClientProvider>
    );
    expect(getByTestId('Loading-RepeatBookingPage')).toBeInTheDocument();
  });
});
