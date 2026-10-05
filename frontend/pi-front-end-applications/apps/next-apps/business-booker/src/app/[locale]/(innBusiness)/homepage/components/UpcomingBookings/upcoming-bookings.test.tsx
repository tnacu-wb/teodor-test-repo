import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { cookies } from 'next/headers';
import React from 'react';

import { UpcomingBookings, UpcomingBookingsSkeleton } from './upcoming-bookings';

const mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    tethered: false,
  },
};

const mockCookieData = {
  value: mockToken,
};

const mockCookieStore = {
  get: () => mockCookieData,
} as unknown as ReturnType<typeof cookies>;

jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({
    get: () => 'test',
  }),
}));

const mockGetCountryLanguage = jest.fn();
const mockUseTranslationServer = jest.fn();
const mockGetAccessLevel = jest.fn();
const mockGetUpcomingBookings = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  getCountryLanguageByLocale: (...args: unknown[]) => mockGetCountryLanguage(...args),
  formatIBAssetsUrl: () => '/',
  getCommonIcons: () => [],
  getTranslations: (...args: unknown[]) => mockUseTranslationServer(...args),
  getAccessLevel: (...args: unknown[]) => mockGetAccessLevel(...args),
  getUpcomingBookings: (...args: unknown[]) => mockGetUpcomingBookings(...args),
}));

describe('UpcomingBookings', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetCountryLanguage.mockReturnValue({ country: 'gb', language: 'en' });
    mockUseTranslationServer.mockResolvedValue({ t: (str: string) => str });
    mockGetAccessLevel.mockResolvedValue({ accessLevel: 'SUPER' });
    mockGetUpcomingBookings.mockResolvedValue({
      data: {
        getUpcomingBookings: {
          hotelName: 'Test Hotel',
          galleryImages: [{ alt: 'Test Hotel', imageSrc: '/test-image.jpg' }],
          arrivalDate: '2025-03-17',
          arrivalTime: '15:00:00',
          departureDate: '2025-03-18',
          departureTime: '12:00:00',
          bookingReference: '123456',
          brand: 'HUB',
          stays: 2,
          bookings: 18,
        },
      },
    });
  });

  it('renders the UpcomingBookings component', async () => {
    render(await UpcomingBookings({ locale: LOCALES.EN }));
    expect(await screen.findByTestId('UpcomingBookings-Container')).toBeInTheDocument();
    expect(
      await screen.findByText('homepage.home.innbusinessPay.upcomingBookings.heading')
    ).toBeInTheDocument();
    expect(
      await screen.findByText('homepage.home.innbusinessPay.upcomingBookings.viewAllBookings.link')
    ).toBeInTheDocument();
  });

  it('renders the booking card with correct data', async () => {
    render(await UpcomingBookings({ locale: LOCALES.EN }));
    expect(await screen.findByText('Test Hotel')).toBeInTheDocument();
    expect(await screen.findByText('Mon 17 Mar')).toBeInTheDocument();
    expect(await screen.findByText('Tue 18 Mar')).toBeInTheDocument();
  });

  it('renders the stays and bookings counters', async () => {
    render(await UpcomingBookings({ locale: LOCALES.EN }));
    expect(
      await screen.findByText('homepage.home.innbusinessPay.upcomingBookings.myStays')
    ).toBeInTheDocument();
    expect(
      await screen.findByText('homepage.home.innbusinessPay.upcomingBookings.bookings')
    ).toBeInTheDocument();
    expect(await screen.findByText('2')).toBeInTheDocument();
    expect(await screen.findByText('18')).toBeInTheDocument();
  });

  it('renders NoBookingCard when there is no upcoming booking', async () => {
    mockGetUpcomingBookings.mockResolvedValueOnce({
      data: { getUpcomingBookings: null },
    });

    render(await UpcomingBookings({ locale: LOCALES.EN }));
    expect(await screen.findByTestId('UpcomingBookings-BookingCardContainer')).toBeInTheDocument();
    expect(
      await screen.findByText('homepage.home.innbusinessPay.upcomingBookings.noBookings')
    ).toBeInTheDocument();
  });

  it('renders with missing stays and bookings values as 0', async () => {
    mockGetUpcomingBookings.mockResolvedValueOnce({
      data: {
        getUpcomingBookings: {
          hotelName: 'Test Hotel',
          stays: undefined,
          bookings: undefined,
        },
      },
    });

    render(await UpcomingBookings({ locale: LOCALES.EN }));
    const zeroElements = await screen.findAllByText('0');
    expect(zeroElements.length).toBeGreaterThanOrEqual(2);
  });

  it('renders correct link href for dashboard', async () => {
    render(await UpcomingBookings({ locale: LOCALES.EN }));
    const link = await screen.findByTestId('UpcomingBookings-ViewAllBookings');
    expect(link).toHaveAttribute('href', '/gb/en/business-booker/account/dashboard.html');
  });
  it('hides bookings counter for non-booker access levels', async () => {
    mockGetAccessLevel.mockResolvedValueOnce({ accessLevel: 'SELF' });

    render(await UpcomingBookings({ locale: LOCALES.EN }));

    expect(screen.queryByTestId('UpcomingBookings-BookingsContainer')).toBeNull();
    const staysContainer = screen.getByTestId('UpcomingBookings-StaysContainer');
    expect(staysContainer.className).toContain('items-left');
  });
});

describe('UpcomingBookingsSkeleton', () => {
  it('renders the UpcomingBookingsSkeleton component', () => {
    render(<UpcomingBookingsSkeleton t={(key: string) => key} country="GB" language="en" />);
    expect(screen.getByTestId('UpcomingBookings-Skeleton')).toBeInTheDocument();
    expect(
      screen.getByText('homepage.home.innbusinessPay.upcomingBookings.heading')
    ).toBeInTheDocument();
    expect(
      screen.getByText('homepage.home.innbusinessPay.upcomingBookings.viewAllBookings.link')
    ).toBeInTheDocument();
  });

  it('renders skeletons for booking card and counters', () => {
    render(<UpcomingBookingsSkeleton t={(key: string) => key} country="GB" language="en" />);
    expect(screen.getAllByText(/homepage.home.innbusinessPay.upcomingBookings/)).toHaveLength(4);
  });
});
