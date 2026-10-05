import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES, UpcomingBookingsResponse } from '@whitbread-eos/api';

import { BookingCard, BookingCardSkeleton } from './booking-card';

const mockBooking: UpcomingBookingsResponse = {
  hotelName: 'Test Hotel',
  galleryImages: [{ alt: 'Test Hotel', imageSrc: '/test-image.jpg' }],
  arrivalDate: '2025-03-17',
  arrivalTime: '15:00:00',
  departureDate: '2025-03-18',
  departureTime: '12:00:00',
  bookingReference: '123456',
  brand: 'HUB',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => [],
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

const mockT = (key: string) => key;

describe('BookingCard', () => {
  it('should render booking details correctly', () => {
    const { getByText, getByTestId } = render(
      <BookingCard
        locale={LOCALES.EN}
        booking={mockBooking}
        t={mockT}
        baseDataTestId="test"
        hubBadge=""
      />
    );

    expect(getByText('Test Hotel')).toBeInTheDocument();
    expect(getByTestId('test-Booking-image')).toBeInTheDocument();
    expect(getByText('homepage.home.innbusinessPay.upcomingBookings.arriving')).toBeInTheDocument();
    expect(getByText('Mon 17 Mar')).toBeInTheDocument();
    expect(getByText('homepage.home.innbusinessPay.upcomingBookings.leaving')).toBeInTheDocument();
    expect(getByText('Tue 18 Mar')).toBeInTheDocument();
    expect(
      getByText('homepage.home.innbusinessPay.upcomingBookings.viewBookingDetails.link')
    ).toBeInTheDocument();
  });

  it('should render the link with correct href', () => {
    const { getByText } = render(
      <BookingCard
        locale={LOCALES.EN}
        booking={mockBooking}
        t={mockT}
        baseDataTestId="test"
        hubBadge=""
      />
    );

    const link = getByText('homepage.home.innbusinessPay.upcomingBookings.viewBookingDetails.link');
    expect(link.closest('a')).toHaveAttribute(
      'href',
      '/gb/en/business-booker/account/dashboard.html?reference=123456'
    );
  });

  it('should render the link with correct href DE', () => {
    const { getByText } = render(
      <BookingCard
        locale={LOCALES.DE}
        booking={mockBooking}
        t={mockT}
        baseDataTestId="test"
        hubBadge=""
      />
    );

    const link = getByText('homepage.home.innbusinessPay.upcomingBookings.viewBookingDetails.link');
    expect(link.closest('a')).toHaveAttribute(
      'href',
      '/de/de/business-booker/account/dashboard.html?reference=123456'
    );
  });

  it('should render bookingCard with no booking', () => {
    const { getByText } = render(
      <BookingCard locale={LOCALES.DE} booking={{}} t={mockT} baseDataTestId="test" hubBadge="" />
    );

    const link = getByText('homepage.home.innbusinessPay.upcomingBookings.viewBookingDetails.link');
    expect(link.closest('a')).toHaveAttribute(
      'href',
      '/de/de/business-booker/account/dashboard.html?reference=undefined'
    );
  });
});

describe('BookingCardSkeleton', () => {
  it('should render skeleton component', () => {
    const { container } = render(<BookingCardSkeleton />);
    expect(container.firstChild).toBeInTheDocument();
  });
});
