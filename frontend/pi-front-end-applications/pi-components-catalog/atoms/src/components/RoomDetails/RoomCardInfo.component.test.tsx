import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import RoomCardInfo from './RoomCardInfo.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => ({
    locale: 'en',
  }),
}));

const mockData = {
  roomReservationStartDate: '15 Apr 2022',
  roomReservationEndDate: '16 Apr 2022',
  leadGuestTitle: 'Mr.',
  leadGuestName: 'Sergio Ponte',
  roomType: 'Twin',
  roomTypeDescription: 'double bed and sofa bed Room',
  rateType: 'Flex',
  rateTypeDescription:
    "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
  roomGroup: '1 Adult, 1 Child',
  ratesPerNight: [{ pricePerNight: 123, startDate: '2022-09-09', cityTaxPerNight: 2 }],
  roomTotalPrice: 118,
  adultMealDescription: ['1 Adult Premier Inn Breakfast'],
  childrenMealDescription: ['1 Child Free breakfast for kids'],
  mealPrice: 18,
  currency: 'GBP',
  roomNumber: 1,
  currentLang: 'en',
  taxesMessage: 'Price includes taxes and fees',
  extrasRoomSelection: [
    {
      packagesSelection: [
        {
          id: 'HSCKIN',
          noOfSelections: 1,
        },
        {
          id: 'HSCOU2',
          noOfSelections: 1,
        },
      ],
      price: 0,
      reservationId: '2038132',
    },
    {
      packagesSelection: [],
      price: 0,
      reservationId: '2038131',
    },
    {
      packagesSelection: [
        {
          id: 'HSCOU2',
          noOfSelections: 1,
        },
      ],
      price: 0,
      reservationId: '2038133',
    },
  ],
  packagesExtrasItems: [
    {
      currency: 'GBP',
      description: 'Check in any time from 11am (normal check-in time is 3pm).',
      id: 'HSCKIN',
      imageSrc: '/content/dam/global/extras/early-check-in.png',
      name: 'Early check-in',
      order: 1,
      price: 10,
      available: 9,
    },
    {
      currency: 'GBP',
      description: 'Check out any time until 2pm (normal check-out time is 12pm).',
      id: 'HSCOU2',
      imageSrc: '/content/dam/global/extras/late-checkout.png',
      name: 'Late check-out',
      order: 2,
      price: 10,
      available: 8,
    },
  ],
  t: (key: string) => {
    switch (key) {
      case 'booking.confirmation.leadGuest':
        return 'Lead Guest';
      case 'booking.confirmation.yourRoom':
        return 'Your Room';
      case 'booking.confirmation.yourRate':
        return 'Your Rate';
      case 'booking.confirmation.yourGroup':
        return 'Your Group';
      case 'booking.hotel.summary.roomTotal':
        return 'Room total:';
      case 'booking.hotel.summary.arriving':
        return 'Arriving';
      case 'booking.confirmation.checkinTime':
        return 'Check-in before 15.00 PM';
      case 'booking.confirmation.checkoutTime':
        return 'Check out before 12 pm';
      case 'ancillaries.booking.summary.checkin':
        return 'Check in after 11am';
      case 'ancillaries.booking.summary.checkout':
        return 'Check out before 2pm';
      case 'booking.hotel.summary.checkout':
        return 'Leaving';
      case 'booking.hotel.summary.meals':
        return 'Meals';
      case 'booking.confirmation.roomTotalMD':
        return 'Room [roomNumber] total price:';
      case 'booking.confirmation.room':
        return 'Room [roomNumber]';
      case 'convertCurrencySymb':
        return '0';
      default:
        return 'RoomCard default';
    }
  },
};

describe('RoomCardInfo', () => {
  it('should render RoomCardInfo corectly', function () {
    mockData.currentLang = 'enGB';
    const { getByText, getByTestId } = render(<RoomCardInfo {...mockData} />);

    expect(getByTestId('RoomCardInfo-room1')).toBeInTheDocument();
    expect(getByText('Lead Guest')).toBeInTheDocument();
    expect(getByText('Mr. Sergio Ponte')).toBeInTheDocument();
    expect(getByText('Your Room')).toBeInTheDocument();
    expect(getByText('Twin')).toBeInTheDocument();
    expect(getByText('- double bed and sofa bed Room')).toBeInTheDocument();
    expect(getByText('Your Rate')).toBeInTheDocument();
    expect(getByText('Flex')).toBeInTheDocument();
    expect(
      getByText(
        "- You can amend or cancel your booking any time up to 1pm on the day you're due to arrive"
      )
    ).toBeInTheDocument();
    expect(getByText('Your Group')).toBeInTheDocument();
    expect(getByText('1 Adult, 1 Child')).toBeInTheDocument();
    expect(getByText('Arriving')).toBeInTheDocument();
    expect(getByText('Fri 09 Sep 2022')).toBeInTheDocument();
    expect(getByText('Leaving')).toBeInTheDocument();
    expect(getByText('Saturday')).toBeInTheDocument();
    expect(getByText('Check in after 11am')).toBeInTheDocument();
    expect(getByText('Check out before 2pm')).toBeInTheDocument();
    expect(getByText('Meals')).toBeInTheDocument();
    expect(getByText('1 Adult Premier Inn Breakfast')).toBeInTheDocument();
    expect(getByText('1 Child Free breakfast for kids')).toBeInTheDocument();
    expect(getByText('Room 1 total price:')).toBeInTheDocument();
    expect(getByText('Price includes taxes and fees')).toBeInTheDocument();
  });

  it('should formatDates if lang is de', function () {
    mockData.currentLang = 'de';
    const { getByText } = render(<RoomCardInfo {...mockData} />);

    expect(getByText('Fr. 09 Sep. 2022')).toBeInTheDocument();
    expect(getByText('Samstag')).toBeInTheDocument();
    expect(getByText('Sa. 16 Apr. 2022')).toBeInTheDocument();
  });

  it('should render mealDescription if childrenMeal are selected', function () {
    mockData.adultMealDescription = [];
    const { getByTestId } = render(<RoomCardInfo {...mockData} />);

    expect(getByTestId('RoomCardInfo-room1-RightColumn-Meals-Label')).toBeInTheDocument();
  });

  it('should render standard room labels with no extras packages', function () {
    mockData.packagesExtrasItems = [];
    mockData.extrasRoomSelection = [];
    const { getByText } = render(<RoomCardInfo {...mockData} />);

    expect(getByText('Check-in before 15.00 PM')).toBeInTheDocument();
    expect(getByText('Check out before 12 pm')).toBeInTheDocument();
  });
});
