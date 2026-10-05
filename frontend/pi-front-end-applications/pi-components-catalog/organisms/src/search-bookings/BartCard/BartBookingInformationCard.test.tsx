import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { ROOM_TYPE } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import BartBookingInformationCard from './BartBookingInformationCard.container';

const mockProps = {
  bookingReference: '12',
  bookerLastName: 'Test',
  arrivalDate: '30.01.2023',
  basketReference: 'AWM6248757',
  sourcePms: 'Bart',
  t: jest.fn(),
};

const mockHotelDetails = {
  data: {
    hotelInformation: {
      address: {
        addressLine1: '19 Angel Street',
        addressLine2: 'Sheffield',
        addressLine3: 'South Yorkshire',
        postalCode: 'S3 8LN',
        country: 'United Kingdom (the)',
      },
      name: 'Sheffield City Centre (Angel Street)',
      brand: 'PI',
      announcement: {
        endDate: '21/07/2022',
        showAnnouncement: 'true',
        startDate: '08/01/2021',
        text: 'Get all the latest updates on our response to&nbsp;<a href="/gb/en/covid-19.html" target="_blank"><u>COVID-19</u></a>&nbsp;and see how we’re keeping guests safe with our&nbsp;<a href="/gb/en/why/cleanliness.html" target="_blank"><u>Premier Inn CleanProtect<sup>TM</sup></u></a>&nbsp;promise.&nbsp;<br>\r\n',
        title: '',
        type: 'info',
      },
      importantInfo: null,
    },
  },
  isLoading: false,
  isError: false,
  error: { message: '' },
};

const mockRoomTypeInfo = {
  data: {
    roomTypeInformation: {
      roomTypes: [
        {
          roomTypeCode: 'PB',
          roomCategory: 'Standard Extra',
          roomLabel: 'Standard Extra room',
          roomDescription:
            'Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/standardextra/standard-extra-1.jpg',
          groupId: null,
        },
        {
          roomTypeCode: 'DIS',
          roomCategory: 'Accessible room',
          roomLabel: 'Accessible Room',
          roomDescription:
            'Spacious and well-designed. Most wheelchair seats measure 480mm in height, so we’ve designed our accessible beds and baths with that in mind.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
          groupId: null,
        },
        {
          roomTypeCode: 'RB',
          roomCategory: 'Premier Plus',
          roomLabel: 'Premier Plus Room',
          roomDescription:
            'Not all stays are standard, so neither are our rooms. Whether it’s an important business trip or a special occasion, book one of our Premier Plus double rooms and get more from your stay.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
          groupId: null,
        },
        {
          roomTypeCode: 'FAM',
          roomCategory: 'Family',
          roomLabel: 'Family Room',
          roomDescription:
            'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
          groupId: null,
        },
        {
          roomTypeCode: 'RB',
          roomCategory: 'Premier Plus',
          roomLabel: 'Premier Plus Room',
          roomDescription:
            'Not all stays are standard, so neither are our rooms. Whether it’s an important business trip or a special occasion, book one of our Premier Plus double rooms and get more from your stay.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
          groupId: null,
        },
        {
          roomTypeCode: 'SB',
          roomCategory: 'Standard',
          roomLabel: 'Standard Room',
          roomDescription:
            'A super-comfy Hypnos bed, a power shower and free Wi-Fi – our Standard rooms have everything you’ll need for a great night’s sleep.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg',
          groupId: null,
        },
        {
          roomTypeCode: 'TWIN',
          roomCategory: 'Twin',
          roomLabel: 'Twin Room',
          roomDescription:
            'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-twin-room.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.LOWERED_TWIN,
          roomCategory: 'Accessible room',
          roomLabel: 'Accessible twin bedroom with a lowered bath',
          roomDescription:
            'Accessible twin bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.WET_TWIN,
          roomCategory: 'Accessible room',
          roomLabel: 'Accessible twin bedroom with level access shower room',
          roomDescription:
            'Accessible twin bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.WET_DOUBLE,
          roomCategory: 'Accessible room',
          roomLabel: 'Accessible double bedroom with level access shower room',
          roomDescription:
            'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.LOWERED_DOUBLE,
          roomCategory: 'Accessible room',
          roomLabel: 'Accessible double bedroom with a lowered bath',
          roomDescription:
            'Accessible double bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.FAMILY_QUAD,
          roomCategory: 'Family',
          roomLabel: 'Family Room',
          roomDescription:
            'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
          groupId: null,
        },
        {
          roomTypeCode: 'TWINRM',
          roomCategory: 'Twin',
          roomLabel: 'Twin Room',
          roomDescription:
            'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-twin-room.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.SINGLE,
          roomCategory: 'Standard',
          roomLabel: 'Standard Room',
          roomDescription:
            'A super-comfy Hypnos bed, a power shower and free Wi-Fi – our Standard rooms have everything you’ll need for a great night’s sleep.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.PREMIER_PLUS,
          roomCategory: 'Premier Plus',
          roomLabel: 'Premier Plus Room',
          roomDescription:
            'Not all stays are standard, so neither are our rooms. Whether it’s an important business trip or a special occasion, book one of our Premier Plus double rooms and get more from your stay.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.STANDARD_EXTRA,
          roomCategory: 'Standard Extra',
          roomLabel: 'Standard Extra room',
          roomDescription:
            'Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/standardextra/standard-extra-1.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.FAMILY_TRIPLE,
          roomCategory: 'Family',
          roomLabel: 'Family Room',
          roomDescription:
            'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
          groupId: null,
        },
        {
          roomTypeCode: ROOM_TYPE.DOUBLE,
          roomCategory: 'Double',
          roomLabel: 'Double Room',
          roomDescription:
            'A super-comfy Hypnos bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
          groupId: null,
        },
        {
          roomTypeCode: 'ZPLDBL',
          roomCategory: 'Double',
          roomLabel: 'Double Room',
          roomDescription:
            'A super-comfy Hypnos bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
          groupId: null,
        },
      ],
    },
    isLoading: false,
    isError: false,
    error: { message: '' },
  },
};

const mockBartBookingInfo = {
  data: {
    bartBookingInformation: {
      balanceOutstanding: { amount: 0, currencyCode: 'EUR' },
      bookingType: 'LEISURE',
      donation: { amount: 2, currencyCode: 'EUR' },
      hasCityTax: false,
      paymentOption: 'PAY_ON_ARRIVAL',
      prepaidAmount: null,
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
      rateName: 'Flex',
      rooms: [
        {
          adultsNumber: 1,
          arrivalDate: '2023-10-27',
          childrenNumber: 0,
          departureDate: '2023-10-28',
          packages: [
            {
              description:
                '<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n',
              id: '11',
              imageSrc:
                '/content/dam/global/restaurants/Global/pi-breakfast-build-your-own-booking.jpg',
              name: 'Premier Inn Breakfast',
              postingDate: '2023-10-28',
              quantity: 1,
              unitCost: {
                amount: 10.99,
                currencyCode: 'GBP',
              },
            },
          ],
          reservationGuest: { givenName: 'Andrei', surName: 'Popescu' },
          roomType: 'DB',
          totalRoomCost: { amount: 170, currencyCode: 'EUR' },
        },
      ],
      totalCost: { amount: 171, currencyCode: 'EUR' },
    },
  },

  isLoading: false,
  isError: false,
  error: { message: '' },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  removeHtmlTags: jest.fn(),
}));

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
    useQuery: jest
      .fn()
      .mockImplementation((args: { queryKey: string | string[]; queryFn: () => void }) => {
        const queryKey = args.queryKey;
        let queryKeyValue = queryKey;
        if (Array.isArray(queryKey)) {
          queryKeyValue = queryKey[0];
        }

        switch (queryKeyValue) {
          case 'GetHotelDetails':
            return mockHotelDetails;
          case 'GetRoomTypeInformation':
            return mockRoomTypeInfo;
          case 'GetBartBookingInformation':
            return mockBartBookingInfo;
          default:
            return {};
        }
      }),
  };
});

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('BartBookingInformationCard', () => {
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render a <BartBookingInformationCard> with default props', function () {
    const { getByTestId } = render(<BartBookingInformationCard {...mockProps} />);

    expect(getByTestId('BartCard-Container')).toBeInTheDocument();
  });

  it('should display room information', function () {
    const { getByTestId } = render(<BartBookingInformationCard {...mockProps} />);

    expect(getByTestId('BartCard-Container-BartBookingsDetailsRoom-0')).toBeInTheDocument();
  });

  it('should display meal information', function () {
    const { getByText } = render(<BartBookingInformationCard {...mockProps} />);

    expect(getByText('Premier Inn Breakfast')).toBeInTheDocument();
  });
});
