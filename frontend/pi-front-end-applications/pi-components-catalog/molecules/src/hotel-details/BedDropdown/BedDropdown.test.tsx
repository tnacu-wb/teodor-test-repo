import { render, screen, fireEvent } from '@testing-library/react';
import { ROOM_CODES, StandardRoomType, RoomTypeLabels, SearchRoomCodes } from '@whitbread-eos/api';

import BedDropdown, { getRoomTypeIcon, getDropdownOptions } from './BedDropdown.component';

const baseDataTestId = 'BedDropdown-Room1';

jest.mock('next/image', () => {
  const MockImage = (props: any) => <img {...props} />;
  MockImage.displayName = 'Image';
  return MockImage;
});

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'search.adults': 'Adults',
        'search.adult': 'Adult',
        'search.children': 'Children',
        'search.child': 'Child',
        'search.room': 'Room',
        'pihotelinfo.night': 'Night',
        'pihotelinfo.nights': 'Nights',
      };
      return translations[key] || key;
    },
  }),
}));

const mockRoom = {
  roomType: '',
  roomNumber: 1,
  adults: 2,
  children: 2,
  rooms: [
    {
      roomType: 'FAM',
      pmsRoomType: 'DBLDBL',
      silentSubstitution: true,
      numberOfRoomsAvailable: 2,
      roomClass: 'ST',
      specialRequests: ['QUAD'],
      roomPriceBreakdown: {
        packageAmount: null,
        packageCode: null,
        totalNetAmount: 477,
        baseRateAmount: null,
        currencyCode: 'GBP',
        dailyPrices: [
          {
            date: '2025-12-19',
            netPrice: 208,
          },
          {
            date: '2025-12-20',
            netPrice: 269,
          },
        ],
      },
    },
  ],
};

const mockUserChoice = [
  {
    roomNumber: 1,
    cotRequested: false,
    roomType: {
      id: 'Family',
      icon: <span data-testid="icon-family" />,
      label: 'Family',
      code: 'FAM',
    },
    pmsRoomType: 'DBLDBL',
  },
];

const mockData = {
  hotelAvailability: {
    hotelAvailability: {
      hotelId: 'LONEUS',
      startDate: '2025-12-19',
      endDate: '2025-12-21',
      available: true,
      limitedAvailability: false,
      roomRates: [
        {
          ratePlanCode: 'FLEXRATE',
          promotionCode: null,
          cellCode: null,
          roomTypes: [
            {
              roomType: '',
              roomNumber: 1,
              adults: 2,
              children: 2,
              rooms: [
                {
                  roomType: 'FAM',
                  pmsRoomType: 'DBLDBL',
                  silentSubstitution: true,
                  cotRequested: false,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['QUAD'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 477,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 208,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 269,
                      },
                    ],
                  },
                },
              ],
            },
            {
              roomType: '',
              roomNumber: 2,
              adults: 1,
              children: 0,
              rooms: [
                {
                  roomType: 'SB',
                  pmsRoomType: 'DBLDBL',
                  silentSubstitution: true,
                  cotRequested: false,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['SING'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 477,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 208,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 269,
                      },
                    ],
                  },
                },
                {
                  roomType: 'DB',
                  pmsRoomType: 'DBLDBL',
                  silentSubstitution: true,
                  cotRequested: false,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['SING'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 477,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 208,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 269,
                      },
                    ],
                  },
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'STANDARD',
          promotionCode: null,
          cellCode: null,
          roomTypes: [
            {
              roomType: '',
              roomNumber: 1,
              adults: 2,
              children: 2,
              rooms: [
                {
                  roomType: 'FAM',
                  pmsRoomType: 'DBLDBL',
                  silentSubstitution: true,
                  cotRequested: false,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['QUAD'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 449,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 196,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 253,
                      },
                    ],
                  },
                },
              ],
            },
            {
              roomType: '',
              roomNumber: 2,
              adults: 1,
              children: 0,
              rooms: [
                {
                  roomType: 'SB',
                  pmsRoomType: 'DBLDBL',
                  cotRequested: false,
                  silentSubstitution: true,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['SING'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 449,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 196,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 253,
                      },
                    ],
                  },
                },
                {
                  roomType: 'DB',
                  pmsRoomType: 'DBLDBL',
                  cotRequested: false,
                  silentSubstitution: true,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['SING'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 449,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 196,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 253,
                      },
                    ],
                  },
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'NONFLEX',
          promotionCode: null,
          cellCode: null,
          roomTypes: [
            {
              roomType: '',
              roomNumber: 1,
              adults: 2,
              children: 2,
              rooms: [
                {
                  roomType: 'FAM',
                  pmsRoomType: 'DBLDBL',
                  cotRequested: false,
                  silentSubstitution: true,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['QUAD'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 431,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 188,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 243,
                      },
                    ],
                  },
                },
              ],
            },
            {
              roomType: '',
              roomNumber: 2,
              adults: 1,
              children: 0,
              rooms: [
                {
                  roomType: 'SB',
                  pmsRoomType: 'DBLDBL',
                  silentSubstitution: true,
                  cotRequested: false,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['SING'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 431,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 188,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 243,
                      },
                    ],
                  },
                },
                {
                  roomType: 'DB',
                  pmsRoomType: 'DBLDBL',
                  cotRequested: false,
                  silentSubstitution: true,
                  numberOfRoomsAvailable: 2,
                  roomClass: 'ST',
                  specialRequests: ['SING'],
                  roomPriceBreakdown: {
                    packageAmount: null,
                    packageCode: null,
                    totalNetAmount: 431,
                    baseRateAmount: null,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2025-12-19',
                        netPrice: 188,
                      },
                      {
                        date: '2025-12-20',
                        netPrice: 243,
                      },
                    ],
                  },
                },
              ],
            },
          ],
        },
      ],
    },
    ratesInformation: {},
  },
  ratesInformation: [
    {
      roomTypeCode: ['LOWTWN'],
      roomCategory: 'Accessible room',
      roomLabel: 'Accessible twin bedroom with a lowered bath',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-9-standard-accessible-bedroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['WETTWN'],
      roomCategory: 'Accessible room',
      roomLabel: 'Accessible twin bedroom with level access shower room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-9-standard-accessible-bedroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['WETDBL'],
      roomCategory: 'Accessible room',
      roomLabel: 'Accessible double bedroom with level access shower room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-9-standard-accessible-bedroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['LOWDBL'],
      roomCategory: 'Accessible room',
      roomLabel: 'Accessible double bedroom with a lowered bath',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-9-standard-accessible-bedroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['FMQUAD', 'FMFOUR'],
      roomCategory: 'Family',
      roomLabel: 'Family room',
      roomDescription:
        'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-6-family-standard-bedroom.jpg',
      groupId: 'family',
    },
    {
      roomTypeCode: ['TWINRM', 'DBLDBL'],
      roomCategory: 'Twin',
      roomLabel: 'Twin room',
      roomDescription:
        'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-4-improved-twin-standard-bedroom.jpg',
      groupId: 'twin',
    },
    {
      roomTypeCode: ['SINGLE'],
      roomCategory: 'Standard',
      roomLabel: 'Standard room',
      roomDescription:
        'A super-comfy bed, a power shower and free Wi-Fi – our Standard rooms have everything you’ll need for a great night’s sleep.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-1-double-standard-bedroom.jpg',
      groupId: 'single',
    },
    {
      roomTypeCode: ['PPLDBL'],
      roomCategory: 'Premier Plus',
      roomLabel: 'Premier Plus room',
      roomDescription:
        'Our enhanced room design. Includes Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
      groupId: 'double',
    },
    {
      roomTypeCode: ['FMTRPL', 'FMTHRE'],
      roomCategory: 'Family',
      roomLabel: 'Family room',
      roomDescription:
        'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-6-family-standard-bedroom.jpg',
      groupId: 'family',
    },
    {
      roomTypeCode: ['DOUBLE', 'ZPLDBL'],
      roomCategory: 'Double',
      roomLabel: 'Double room',
      roomDescription:
        'A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-1-double-standard-bedroom.jpg',
      groupId: 'double',
    },
    {
      roomTypeCode: ['PPDLOW'],
      roomCategory: 'Accessible room',
      roomLabel: 'Premier Plus Accessible Double Lowered Bath',
      roomDescription:
        'Accessible double bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID5_premier_plus_ua_bathroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['BRFDBL'],
      roomCategory: 'Barrier Free room',
      roomLabel: 'Barrier Free room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-9-standard-accessible-bedroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['BRFZPL'],
      roomCategory: 'Barrier Free room',
      roomLabel: 'Barrier Free room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-9-standard-accessible-bedroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['BRFTWN'],
      roomCategory: 'Barrier Free room',
      roomLabel: 'Barrier Free room',
      roomDescription:
        'Our accessible rooms offer more space, a double or kingsize bed between 480mm and 520mm in height, and wider entry bathrooms with a lowered bath or wet room. ',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID5/id5-9-standard-accessible-bedroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['VDOUBL'],
      roomCategory: 'Double',
      roomLabel: 'Double room with a view',
      roomDescription:
        "A great view, a super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you'll need for a great night's sleep. ",
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
      groupId: 'double',
    },
    {
      roomTypeCode: ['VPPDBL'],
      roomCategory: 'Premier Plus',
      roomLabel: 'Premier Plus room with a view',
      roomDescription:
        'Our enhanced room design with a great view, Ultimate Wi-Fi, coffee machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
      groupId: 'double',
    },
    {
      roomTypeCode: ['VFMTHR', 'VFMTRP'],
      roomCategory: 'Family',
      roomLabel: 'Family room with a view',
      roomDescription:
        "A great view, a super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you'll need for a great night's sleep. ",
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-4.jpg',
      groupId: 'family',
    },
    {
      roomTypeCode: ['VFMFOR', 'VFMQUD'],
      roomCategory: 'Family',
      roomLabel: 'Family room with a view',
      roomDescription:
        'Our Family rooms with a view include a great view, a double or kingsize bed plus a sofa bed and pull-out bed (depending on the number of guests). We also provide travel cots at no extra cost. Room size and set up can vary based on the hotel and the number',
      roomImage: '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-4.jpg',
      groupId: 'family',
    },
    {
      roomTypeCode: ['PPDWET'],
      roomCategory: 'Accessible room',
      roomLabel: 'Premier Plus Accessible double with level access shower room',
      roomDescription:
        'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
      roomImage:
        '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID5_premier_plus_ua_wetroom.jpg',
      groupId: 'accessible',
    },
    {
      roomTypeCode: ['BIGWIN'],
      roomCategory: 'Bigger Room',
      roomLabel: 'Bigger room',
      roomDescription:
        'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
      roomImage: '/content/dam/hub/hotelimages/generic/hub-bigger.jpg',
      groupId: 'double',
    },
  ],
  rateClassifications: [
    {
      rateClassification: 'FLEXRATE',
      rateOrder: '10',
      rateNotes: '<p>Flex: Amend or cancel up to 6pm on arrival day</p>\n',
      rateName: 'Flex',
      rateLongDescription: '',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 6pm on the day of arrival',
    },
    {
      rateClassification: 'SEMIFLEX',
      rateOrder: '20',
      rateNotes:
        '<p>Semi-Flex: Amend or cancel up to three full days before arrival date. Your arrival date can be amended up to 6pm on the day you’re due to arrive.</p>\n',
      rateName: 'Semi-Flex',
      rateLongDescription: '',
      rateDescription:
        'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
    },
    {
      rateClassification: 'ADVANCE',
      rateOrder: '30',
      rateNotes:
        '<p>Advance: Amend or cancel up to 28 days before arrival date. Your arrival date can be amended up to 6pm on the day you’re due to arrive.</p>\n',
      rateName: 'Advance',
      rateLongDescription: '',
      rateDescription:
        'Pay now, fully refundable with free cancellation up to 28 full days before arrival',
    },
    {
      rateClassification: 'STANDARD',
      rateOrder: '40',
      rateNotes:
        '<p>Standard: Your arrival date can be amended up to 6pm on the day you’re due to arrive.</p>\n',
      rateName: 'Standard',
      rateLongDescription: '',
      rateDescription:
        'Pay now, non-refundable. Amendable check in date at the same hotel up to 6pm on the day of arrival',
    },
    {
      rateClassification: 'NONFLEX',
      rateOrder: '50',
      rateNotes: '<p>Non-Flex: This booking cannot be amended or cancelled.</p>\n',
      rateName: 'Non-Flex',
      rateLongDescription: '',
      rateDescription: 'Pay now. No changes',
    },
    {
      rateClassification: 'EMPLOYEE',
      rateOrder: '1',
      rateNotes: '<p>Amend or cancel up to 6pm on arrival day</p>\n',
      rateName: 'Flex (Employee Discount Applied)',
      rateLongDescription: '',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 6pm on the day of arrival',
    },
    {
      rateClassification: 'BUSIFLEX',
      rateOrder: '1',
      rateNotes: '<p>Amend or cancel up to 6pm on arrival day</p>\n',
      rateName: 'Business Flex',
      rateLongDescription: '',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 6pm on the day of arrival',
    },
  ],
};

const mockOnHandleClick = jest.fn();
const mockOnHandleChange = jest.fn();
const mockSetOpenRoomNumber = jest.fn();

const baseProps = {
  data: mockData,
  activeRate: mockData.hotelAvailability.hotelAvailability.roomRates[0]?.ratePlanCode,
  room: mockRoom,
  userChoice: mockUserChoice,
  onHandleClick: mockOnHandleClick,
  onHandleChange: mockOnHandleChange,
  openRoomNumber: 1,
  setOpenRoomNumber: mockSetOpenRoomNumber,
};

describe('BedDropdown', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders wrapper and header', () => {
    render(<BedDropdown {...baseProps} />);
    expect(screen.getByTestId(`${baseDataTestId}-wrapper`)).toBeInTheDocument();
    expect(screen.getByTestId(`${baseDataTestId}-header`)).toBeInTheDocument();
  });

  it('renders room label and toggle', () => {
    render(<BedDropdown {...baseProps} />);
    expect(screen.getByTestId(`${baseDataTestId}-roomLabel`)).toHaveTextContent('Room 1');
    expect(screen.getByTestId(`${baseDataTestId}-toggle`)).toBeInTheDocument();
  });

  it('calls setOpenRoomNumber when toggle is clicked', () => {
    render(<BedDropdown {...baseProps} openRoomNumber={0} />);
    fireEvent.click(screen.getByTestId(`${baseDataTestId}-toggle`));
    expect(mockSetOpenRoomNumber).toHaveBeenCalled();
  });

  it('renders people icons and label', () => {
    render(<BedDropdown {...baseProps} />);
    expect(screen.getByTestId(`${baseDataTestId}-peopleIcons`)).toBeInTheDocument();
    expect(screen.getByTestId(`${baseDataTestId}-peopleLabel`)).toHaveTextContent(
      '2 Adults, 2 Children'
    );
  });

  it('renders correct label depending on children & adults number', () => {
    render(
      <BedDropdown
        {...baseProps}
        room={{
          ...baseProps.room,
          cotRequested: false,
          adults: 1,
          children: 1,
        }}
        // data={{
        //   ...mockData,
        //   hotelAvailability: {
        //     ...mockData.hotelAvailability,
        //     hotelAvailability: {
        //       ...mockData.hotelAvailability.hotelAvailability,
        //       roomRates: [
        //         {
        //           ...mockData.hotelAvailability.hotelAvailability.roomRates[0],
        //           roomTypes: [
        //             {
        //               ...mockData.hotelAvailability.hotelAvailability.roomRates[0].roomTypes[0],
        //               adults: 1,
        //               children: 1,
        //               cotRequested: false,
        //               rooms:
        //                 mockData.hotelAvailability.hotelAvailability.roomRates[0].roomTypes[0]
        //                   .rooms,
        //             },
        //           ],
        //         },
        //       ],
        //     },
        //   },
        // }}
      />
    );
    expect(screen.getByTestId(`${baseDataTestId}-peopleIcons`)).toBeInTheDocument();
    expect(screen.getByTestId(`${baseDataTestId}-peopleLabel`)).toHaveTextContent(
      '1 Adult, 1 Child'
    );
  });

  it('renders rate cards and calls onHandleClick', () => {
    render(<BedDropdown {...baseProps} />);
    const rateCards = screen.getByTestId(`${baseDataTestId}-rateCards`);
    expect(rateCards).toBeInTheDocument();
    const selectButtons = screen.getAllByTestId('Room-Selection-Rate-Card-SelectRoomButton');
    fireEvent.click(selectButtons[0]);
    expect(mockOnHandleClick).toHaveBeenCalled();
  });

  it('renders other bed text when open and Twin bed', () => {
    render(
      <BedDropdown
        {...baseProps}
        userChoice={[
          {
            roomType: {
              id: 'Twin',
              icon: <span data-testid="icon-twin" />,
              label: 'Twin',
              code: 'TWIN',
            },
            roomNumber: 1,
            pmsRoomType: '',
          },
        ]}
      />
    );
    expect(screen.getByTestId(`${baseDataTestId}-otherBedText`)).toBeInTheDocument();
  });

  it('renders dropdown menu button and list', () => {
    render(<BedDropdown {...baseProps} />);
    const dropdownMenuButton = screen.getByTestId(
      'DropdownComp-BedDropdown-Room1-roomTypeDropdown-menuButton'
    );
    const dropdownMenuList = screen.getByTestId(
      'DropdownComp-BedDropdown-Room1-roomTypeDropdown-entireList'
    );
    expect(dropdownMenuButton).toBeInTheDocument();
    expect(dropdownMenuList).toBeInTheDocument();
  });
});

describe('getDropdownOptions', () => {
  const LABELS: RoomTypeLabels = {
    single: StandardRoomType.SB,
    double: StandardRoomType.DB,
    accessible: StandardRoomType.DIS,
    twin: StandardRoomType.TWIN,
    family: StandardRoomType.FAM,
  };

  const CODES: Partial<SearchRoomCodes> = {
    SB: 'single',
    DB: 'double',
    DIS: 'accessible',
    TWIN: 'twin',
    FAM: 'family',
  };

  it('returns correct options for all codes', () => {
    const options = getDropdownOptions(
      [
        ROOM_CODES.single,
        ROOM_CODES.double,
        ROOM_CODES.accessible,
        ROOM_CODES.twin,
        ROOM_CODES.family,
      ],
      LABELS,
      CODES
    );
    expect(options).toHaveLength(5);
    expect(options[0]).toMatchObject({ id: 'Single', label: 'Single', code: ROOM_CODES.single });
    expect(options[1]).toMatchObject({ id: 'Double', label: 'Double', code: ROOM_CODES.double });
    expect(options[2]).toMatchObject({
      id: 'Accessible',
      label: 'Accessible',
      code: ROOM_CODES.accessible,
    });
    expect(options[3]).toMatchObject({ id: 'Twin', label: 'Twin', code: ROOM_CODES.twin });
    expect(options[4]).toMatchObject({ id: 'Family', label: 'Family', code: ROOM_CODES.family });
  });

  it('returns option with code as label if codeKey not found', () => {
    const options = getDropdownOptions(['Unknown'], LABELS, CODES);
    expect(options[0]).toMatchObject({ id: 'Unknown', label: 'Unknown', code: 'Unknown' });
  });

  it('returns correct icon for each option', () => {
    const options = getDropdownOptions(
      [
        ROOM_CODES.single,
        ROOM_CODES.double,
        ROOM_CODES.accessible,
        ROOM_CODES.twin,
        ROOM_CODES.family,
      ],
      LABELS,
      CODES
    );
    expect(options[0]?.icon?.type?.name).toBe('SvgSingleBed');
    expect(options[1]?.icon?.type?.name).toBe('SvgDoubleBed');
    expect(options[2]?.icon?.type?.name).toBe('SvgAccessible');
    expect(options[3]?.icon?.type?.name).toBe('SvgBedTwin');
    expect(options[4]?.icon?.type?.name).toBe('SvgFamilyRoom');
  });
});

describe('getRoomTypeIcon', () => {
  it('returns SingleBed for ROOM_CODES.single', () => {
    const icon = getRoomTypeIcon(ROOM_CODES.single);
    expect(icon.type?.name).toBe('SvgSingleBed');
  });

  it('returns Accessible for ROOM_CODES.accessible', () => {
    const icon = getRoomTypeIcon(ROOM_CODES.accessible);
    expect(icon.type?.name).toBe('SvgAccessible');
  });

  it('returns BedTwin for ROOM_CODES.twin', () => {
    const icon = getRoomTypeIcon(ROOM_CODES.twin);
    expect(icon.type?.name).toBe('SvgBedTwin');
  });

  it('returns FamilyRoom for ROOM_CODES.family', () => {
    const icon = getRoomTypeIcon(ROOM_CODES.family);
    expect(icon.type?.name).toBe('SvgFamilyRoom');
  });

  it('returns DoubleBed for ROOM_CODES.double', () => {
    const icon = getRoomTypeIcon(ROOM_CODES.double);
    expect(icon.type?.name).toBe('SvgDoubleBed');
  });

  it('returns DoubleBed for unknown code', () => {
    const icon = getRoomTypeIcon('UNKNOWN_CODE');
    expect(icon.type?.name).toBe('SvgDoubleBed');
  });
});
