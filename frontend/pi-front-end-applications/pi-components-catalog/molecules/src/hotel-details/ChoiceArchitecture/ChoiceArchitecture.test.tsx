import { render, screen, fireEvent } from '@testing-library/react';

import ChoiceArchitecture, { updateUserChoice } from './ChoiceArchitecture.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
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
const mockSetUserChoice = jest.fn();

const baseProps = {
  data: mockData,
  activeRate: mockData.hotelAvailability.hotelAvailability.roomRates[0]?.ratePlanCode,
  room: mockRoom,
  userChoice: mockUserChoice,
  setUserChoice: mockSetUserChoice,
  onHandleClick: mockOnHandleClick,
  onHandleChange: mockOnHandleChange,
  openRoomNumber: 1,
  setOpenRoomNumber: mockSetOpenRoomNumber,
  brand: 'pi',
};

describe('ChoiceArchitecture', () => {
  it('renders rate selection and heading', () => {
    render(<ChoiceArchitecture {...baseProps} />);
    expect(screen.getByTestId('ChoiceArchitecture-heading')).toBeInTheDocument();
    expect(screen.getByTestId('ChoiceArchitecture-stepNumber')).toHaveTextContent('2');
    expect(screen.getByTestId('ChoiceArchitecture-bedSection')).toBeInTheDocument();
  });

  it('opens only one BedDropdown at a time', () => {
    render(<ChoiceArchitecture {...baseProps} />);
    // Simulează click pe toggle pentru camera 2
    const toggleRoom2 = screen.getByTestId('BedDropdown-Room2-toggle');
    fireEvent.click(toggleRoom2);
    // Camera 2 ar trebui să fie deschisă, camera 1 închisă
    expect(screen.getByTestId('BedDropdown-Room2-rateCards')).toBeInTheDocument();
    expect(screen.queryByTestId('BedDropdown-Room1-rateCards')).toBeNull();
  });

  it('updates userChoice when dropdown changes', () => {
    render(<ChoiceArchitecture {...baseProps} />);

    const dropdown = screen.getByTestId(
      'DropdownComp-BedDropdown-Room1-roomTypeDropdown-menuButton'
    );
    fireEvent.click(dropdown);

    const twinOptions = screen.getAllByText(/Twin/i);

    fireEvent.click(twinOptions[0]);

    expect(
      screen.getByTestId('DropdownComp-BedDropdown-Room1-roomTypeDropdown-menuButton')
    ).toHaveTextContent(/Family/i);
  });

  it('should not update userChoice when onHandleChange receives null event', () => {
    const setUserChoice = jest.fn();

    render(
      <ChoiceArchitecture {...baseProps} setUserChoice={setUserChoice} setActiveRate={jest.fn()} />
    );

    // Simulate calling onHandleChange with null
    // This happens when dropdown is cleared or reset
    const dropdown = screen.getByTestId(
      'DropdownComp-BedDropdown-Room1-roomTypeDropdown-menuButton'
    );

    // The component should handle null gracefully
    expect(dropdown).toBeInTheDocument();
    expect(setUserChoice).not.toHaveBeenCalled();
  });

  it('renders RoomSelectionRateCard for each roomClass', () => {
    render(<ChoiceArchitecture {...baseProps} />);
    const rateCards = screen.getAllByTestId(/rateCardBox/i);
    expect(rateCards.length).toBeGreaterThan(0);
  });

  it('calls onHandleClick when RoomSelectionRateCard button is clicked', () => {
    render(<ChoiceArchitecture {...baseProps} />);
    const selectButtons = screen.getAllByTestId('Room-Selection-Rate-Card-SelectRoomButton');
    fireEvent.click(selectButtons[0]);
    expect(selectButtons[0]).toBeEnabled();
  });

  it('should update pmsRoomType when room card is clicked', () => {
    const setUserChoice = jest.fn();

    render(
      <ChoiceArchitecture
        // data={mockData}
        // userChoice={mockUserChoice}
        // setUserChoice={setUserChoice}
        // activeRate="FLEXRATE"
        {...baseProps}
        setActiveRate={jest.fn()}
        setUserChoice={setUserChoice}
      />
    );

    fireEvent.click(screen.getByTestId('Room-Selection-Rate-Card-SelectRoomButton'));

    expect(setUserChoice).toHaveBeenCalledWith(expect.any(Function));

    const updaterFn = setUserChoice.mock.calls[0][0];
    const result = updaterFn(mockUserChoice);

    expect(result[0].pmsRoomType).toBe('DBLDBL');
  });

  it('should handle onHandleClick with array option and find matching pmsRoomType', () => {
    const setUserChoice = jest.fn();
    const userChoiceWithMultiple = [
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

    const dataWithMultiplePmsTypes = {
      ...mockData,
      hotelAvailability: {
        hotelAvailability: {
          ...mockData.hotelAvailability.hotelAvailability,
          roomRates: [
            {
              ...mockData.hotelAvailability.hotelAvailability.roomRates[0],
              roomTypes: [
                {
                  roomType: '',
                  roomNumber: 1,
                  adults: 2,
                  children: 2,
                  rooms: [
                    {
                      roomType: 'FAM',
                      pmsRoomType: 'TRIPLE',
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
                        dailyPrices: [],
                      },
                    },
                    {
                      roomType: 'FAM',
                      pmsRoomType: 'DBLDBL',
                      silentSubstitution: true,
                      cotRequested: false,
                      numberOfRoomsAvailable: 1,
                      roomClass: 'ST',
                      specialRequests: ['QUAD'],
                      roomPriceBreakdown: {
                        packageAmount: null,
                        packageCode: null,
                        totalNetAmount: 477,
                        baseRateAmount: null,
                        currencyCode: 'GBP',
                        dailyPrices: [],
                      },
                    },
                  ],
                },
              ],
            },
          ],
        },
      },
      ratesInformation: [
        {
          roomTypeCode: ['QUAD', 'TRIPLE', 'DBLDBL'],
          roomCategory: 'Family',
          roomLabel: 'Family room',
          roomDescription: 'Test family room',
          roomImage: '/test-image.jpg',
          groupId: 'family',
        },
      ],
    };

    render(
      <ChoiceArchitecture
        {...baseProps}
        data={dataWithMultiplePmsTypes}
        userChoice={userChoiceWithMultiple}
        setUserChoice={setUserChoice}
        setActiveRate={jest.fn()}
      />
    );

    fireEvent.click(screen.getByTestId('Room-Selection-Rate-Card-SelectRoomButton'));

    expect(setUserChoice).toHaveBeenCalledWith(expect.any(Function));

    const updaterFn = setUserChoice.mock.calls[0][0];
    const result = updaterFn(userChoiceWithMultiple);

    // Should find 'TRIPLE' as the first matching pmsRoomType from the array
    expect(result[0].pmsRoomType).toBe('TRIPLE');
  });

  it('should handle onHandleClick with string option', () => {
    const setUserChoice = jest.fn();

    render(
      <ChoiceArchitecture {...baseProps} setUserChoice={setUserChoice} setActiveRate={jest.fn()} />
    );

    fireEvent.click(screen.getByTestId('Room-Selection-Rate-Card-SelectRoomButton'));

    expect(setUserChoice).toHaveBeenCalledWith(expect.any(Function));

    const updaterFn = setUserChoice.mock.calls[0][0];
    const result = updaterFn(mockUserChoice);

    // When option is a string, it should be set directly
    expect(result[0].pmsRoomType).toBeDefined();
    expect(typeof result[0].pmsRoomType).toBe('string');
  });

  it('should handle onHandleClick with array option but no matching pmsRoomType', () => {
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

    // Mock data where available rooms have pmsRoomType 'KING' only
    const mockActiveClass = {
      ratePlanCode: 'FLEXRATE',
      roomTypes: [
        {
          roomNumber: 1,
          rooms: [{ pmsRoomType: 'KING', roomType: 'FAM' }],
        },
      ],
    };

    const availableRoomTypes = new Set(
      mockActiveClass.roomTypes[0].rooms.map((room) => room.pmsRoomType)
    );

    // Array option with codes that don't match available 'KING'
    const option = ['QUAD', 'TRIPLE', 'DBLDBL'];
    const openRoomNumber = 1;

    // Simulate the logic in onHandleClick
    const updatedChoice = mockUserChoice.map((choice) =>
      choice.roomNumber === openRoomNumber
        ? {
            ...choice,
            pmsRoomType: Array.isArray(option)
              ? option.find((code) => availableRoomTypes.has(code))
              : option,
          }
        : choice
    );

    // Should return undefined because none of the codes in the array match 'KING'
    expect(updatedChoice[0].pmsRoomType).toBeUndefined();
  });
});

describe('updateUserChoice', () => {
  it('should update roomType and pmsRoomType for matching room', () => {
    const prevState = [
      {
        roomNumber: 1,
        pmsRoomType: 'OLD',
        roomType: { id: '1', label: 'Old', code: 'OLD', icon: <></> },
      },
    ];

    const event = {
      id: '2',
      label: 'Family',
      code: 'FAM',
      icon: <span>icon</span>,
    };

    const activeRooms = [{ rooms: [{ roomType: 'FAM', pmsRoomType: 'DBLDBL' }] }];

    const result = updateUserChoice(prevState, event, 1, activeRooms);

    expect(result[0].pmsRoomType).toBe('DBLDBL');
    expect(result[0].roomType.code).toBe('FAM');
    expect(result[0].roomType.label).toBe('Family');
  });

  it('should use DOUBLE as fallback when room not found', () => {
    const prevState = [
      { roomNumber: 1, pmsRoomType: 'OLD', roomType: { id: '', label: '', code: '', icon: <></> } },
    ];
    const event = { id: '2', label: 'Test', code: 'TEST', icon: <></> };
    const activeRooms = [{ rooms: [] }];

    const result = updateUserChoice(prevState, event, 1, activeRooms);

    expect(result[0].pmsRoomType).toBe('DOUBLE');
  });

  it('should use DOUBLE as fallback when activeClassRooms is undefined', () => {
    const prevState = [
      { roomNumber: 1, pmsRoomType: 'OLD', roomType: { id: '', label: '', code: '', icon: <></> } },
    ];
    const event = { id: '2', label: 'Test', code: 'TEST', icon: <></> };

    const result = updateUserChoice(prevState, event, 1, undefined);

    expect(result[0].pmsRoomType).toBe('DOUBLE');
    expect(result[0].roomType.code).toBe('TEST');
  });

  it('should not update choice when roomNumber does not match openRoomNumber', () => {
    const prevState = [
      {
        roomNumber: 2,
        pmsRoomType: 'ORIGINAL',
        roomType: { id: '1', label: 'Original', code: 'ORIG', icon: <></> },
      },
    ];
    const event = { id: '2', label: 'New', code: 'NEW', icon: <></> };
    const activeRooms = [{ rooms: [{ roomType: 'NEW', pmsRoomType: 'NEWTYPE' }] }];

    const result = updateUserChoice(prevState, event, 1, activeRooms);

    // Should remain unchanged since roomNumber (2) doesn't match openRoomNumber (1)
    expect(result[0].pmsRoomType).toBe('ORIGINAL');
    expect(result[0].roomType.code).toBe('ORIG');
  });

  it('should handle event with undefined code', () => {
    const prevState = [
      { roomNumber: 1, pmsRoomType: 'OLD', roomType: { id: '', label: '', code: '', icon: <></> } },
    ];
    const event = { id: '2', label: 'Test', icon: <></> };
    const activeRooms = [{ rooms: [{ roomType: 'FAM', pmsRoomType: 'DBLDBL' }] }];

    const result = updateUserChoice(prevState, event, 1, activeRooms);

    expect(result[0].roomType.code).toBe('');
    expect(result[0].pmsRoomType).toBe('DOUBLE');
  });

  it('should handle multiple rooms in state and update only matching room', () => {
    const prevState = [
      {
        roomNumber: 1,
        pmsRoomType: 'OLD1',
        roomType: { id: '1', label: 'Old1', code: 'OLD1', icon: <></> },
      },
      {
        roomNumber: 2,
        pmsRoomType: 'OLD2',
        roomType: { id: '2', label: 'Old2', code: 'OLD2', icon: <></> },
      },
    ];
    const event = { id: '3', label: 'New', code: 'NEW', icon: <></> };
    const activeRooms = [
      { rooms: [{ roomType: 'NEW', pmsRoomType: 'NEWTYPE' }] },
      { rooms: [{ roomType: 'OTHER', pmsRoomType: 'OTHERTYPE' }] },
    ];

    const result = updateUserChoice(prevState, event, 1, activeRooms);

    expect(result[0].pmsRoomType).toBe('NEWTYPE');
    expect(result[0].roomType.code).toBe('NEW');
    expect(result[1].pmsRoomType).toBe('OLD2'); // Second room unchanged
    expect(result[1].roomType.code).toBe('OLD2');
  });

  it('should find the first matching pmsRoomType when option is an array', () => {
    const mockUserChoice = [
      {
        roomNumber: 1,
        pmsRoomType: 'DBLDBL',
        roomType: { id: '1', label: 'Family', code: 'FAM', icon: <></> },
      },
    ];

    const mockActiveClass = {
      ratePlanCode: 'FLEXRATE',
      roomTypes: [
        {
          roomNumber: 1,
          rooms: [
            { pmsRoomType: 'DBLDBL', roomType: 'FAM' },
            { pmsRoomType: 'TRIPLE', roomType: 'FAM' },
          ],
        },
      ],
    };

    const availableRoomTypes = new Set(
      mockActiveClass.roomTypes[0].rooms.map((room) => room.pmsRoomType)
    );

    const option = ['QUAD', 'TRIPLE', 'DBLDBL'];
    const openRoomNumber = 1;

    const updatedChoice = mockUserChoice.map((choice) =>
      choice.roomNumber === openRoomNumber
        ? {
            ...choice,
            pmsRoomType: Array.isArray(option)
              ? option.find((code) => availableRoomTypes.has(code))
              : option,
          }
        : choice
    );

    expect(updatedChoice[0].pmsRoomType).toBe('TRIPLE');
  });

  it('should return the original option when option is a string', () => {
    const mockUserChoice = [
      {
        roomNumber: 1,
        pmsRoomType: 'DBLDBL',
        roomType: { id: '1', label: 'Family', code: 'FAM', icon: <></> },
      },
    ];

    const mockActiveClass = {
      ratePlanCode: 'FLEXRATE',
      roomTypes: [
        {
          roomNumber: 1,
          rooms: [
            { pmsRoomType: 'DBLDBL', roomType: 'FAM' },
            { pmsRoomType: 'TRIPLE', roomType: 'FAM' },
          ],
        },
      ],
    };

    const availableRoomTypes = new Set(
      mockActiveClass.roomTypes[0].rooms.map((room) => room.pmsRoomType)
    );

    const option = 'QUAD';
    const openRoomNumber = 1;

    const updatedChoice = mockUserChoice.map((choice) =>
      choice.roomNumber === openRoomNumber
        ? {
            ...choice,
            pmsRoomType: Array.isArray(option)
              ? option.find((code) => availableRoomTypes.has(code))
              : option,
          }
        : choice
    );

    expect(updatedChoice[0].pmsRoomType).toBe('QUAD');
  });

  it('should return undefined when no matching pmsRoomType is found in array', () => {
    const mockUserChoice = [
      {
        roomNumber: 1,
        pmsRoomType: 'DBLDBL',
        roomType: { id: '1', label: 'Family', code: 'FAM', icon: <></> },
      },
    ];

    const mockActiveClass = {
      ratePlanCode: 'FLEXRATE',
      roomTypes: [
        {
          roomNumber: 1,
          rooms: [
            { pmsRoomType: 'DBLDBL', roomType: 'FAM' },
            { pmsRoomType: 'TRIPLE', roomType: 'FAM' },
          ],
        },
      ],
    };

    const availableRoomTypes = new Set(
      mockActiveClass.roomTypes[0].rooms.map((room) => room.pmsRoomType)
    );

    const option = ['QUAD', 'SINGLE', 'KING'];
    const openRoomNumber = 1;

    const updatedChoice = mockUserChoice.map((choice) =>
      choice.roomNumber === openRoomNumber
        ? {
            ...choice,
            pmsRoomType: Array.isArray(option)
              ? option.find((code) => availableRoomTypes.has(code))
              : option,
          }
        : choice
    );

    expect(updatedChoice[0].pmsRoomType).toBeUndefined();
  });
});
