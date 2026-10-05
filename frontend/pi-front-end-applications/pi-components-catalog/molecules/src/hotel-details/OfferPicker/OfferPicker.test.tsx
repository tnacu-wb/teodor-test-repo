import { render, screen, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import OfferPicker from './OfferPicker.component';

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

const baseProps = {
  data: mockData,
  activeRate: 'FLEXRATE',
  handleRateClick: jest.fn(),
  brand: 'pi',
};

describe('OfferPicker', () => {
  it('renders all rates with correct data-testid attributes', () => {
    render(<OfferPicker {...baseProps} />);
    expect(screen.getByTestId('OfferPicker-container')).toBeInTheDocument();
    expect(screen.getByTestId('OfferPicker-heading')).toBeInTheDocument();
    expect(screen.getByTestId('OfferPicker-rates-list')).toBeInTheDocument();
    expect(screen.getByTestId('OfferPicker-rate-STANDARD')).toBeInTheDocument();
    expect(screen.getByTestId('OfferPicker-rate-FLEXRATE')).toBeInTheDocument();
    expect(screen.getByTestId('OfferPicker-rate-NONFLEX')).toBeInTheDocument();
  });

  it('applies responsive styles correctly', async () => {
    render(<OfferPicker {...baseProps} />);
    const rateElement = screen.getByTestId('OfferPicker-rate-STANDARD');
    expect(rateElement).toHaveStyle('padding: var(--chakra-space-xs) 0');

    await act(async () => {
      userEvent.click(rateElement);
    });
  });

  it('should switch between rates when a rate is clicked', async () => {
    const handleRateClick = jest.fn();
    const { getAllByRole } = render(
      <OfferPicker {...baseProps} handleRateClick={handleRateClick} />
    );
    const radioBtns = getAllByRole('radio');
    expect(radioBtns.length).toBe(3);

    expect(radioBtns[0]).toBeChecked();
    expect(radioBtns[1]).not.toBeChecked();

    await act(async () => {
      userEvent.click(radioBtns[1]);
    });

    // Verify the handler was called with the correct rate
    expect(handleRateClick).toHaveBeenCalledWith('STANDARD');
  });

  it('applies responsive styles correctly with no rates', () => {
    render(<OfferPicker />);
    const rateElement = screen.getByTestId('OfferPicker-container');
    expect(rateElement).toBeInTheDocument();
  });

  it('renders hub in the rateName if hub brand is provided', () => {
    render(<OfferPicker {...baseProps} brand="hub" />);
    expect(screen.getByText('booking.rates.hub.prefix Flex')).toBeInTheDocument();
  });
});
