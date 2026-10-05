import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Area, CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';

import BookingConfirmationPage from './page.wrapper';

// Mock dependencies
jest.mock('@whitbread-eos/molecules', () => ({
  SEO: () => null,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: jest.fn(() => ({ country: 'GB', language: 'en' })),
  useAmendCookieValidation: jest.fn(() => ({
    isValid: true,
    token: 'token123',
    basketReference: 'BASKET123',
    bookingReference: 'TEST123',
    error: null,
  })),
}));

jest.mock('./page.bb', () => ({
  __esModule: true,
  default: jest.fn(
    ({
      confirmationInput,
      amendBookingStatus,
      email,
      bookingSpinnerConfig,
      tempBookingReference,
    }) => (
      <div data-testid="amend-booking-confirmation-bb">
        <div data-testid="booking-ref">{confirmationInput.bookingReference}</div>
        <div data-testid="status">{amendBookingStatus}</div>
        <div data-testid="email">{email}</div>
        <div data-testid="spinner-config">{JSON.stringify(bookingSpinnerConfig)}</div>
        <div data-testid="temp-ref">{tempBookingReference}</div>
      </div>
    )
  ),
}));

describe('Business Booker Amend Booking Confirmation Wrapper', () => {
  const mockQueryClient = new QueryClient();
  const mockConfirmationInput = {
    bookingReference: 'BB123',
    basketReference: 'BASKET456',
    token: 'token789',
    country: 'GB',
    language: 'en',
  };
  const mockBookingSpinnerConfig = [
    { order: 1, seconds: 3, text: 'Processing...' },
    { order: 2, seconds: 5, text: 'Almost done...' },
  ];

  it('should render AmendBookingConfirmationPageBb component', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('amend-booking-confirmation-bb')).toBeInTheDocument();
  });

  it('should render AmendBookingConfirmationPageBb inside wrapper', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('amend-booking-confirmation-bb')).toBeInTheDocument();
  });

  it('should pass amendBookingStatus when provided', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
        amendBookingStatus={CONFIRM_AMEND_STATUS.success}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('status')).toHaveTextContent(CONFIRM_AMEND_STATUS.success);
  });

  it('should pass email when provided', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
        email="bb@example.com"
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('email')).toHaveTextContent('bb@example.com');
  });

  it('should pass bookingSpinnerConfig to child component', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('spinner-config')).toHaveTextContent(
      JSON.stringify(mockBookingSpinnerConfig)
    );
  });

  it('should pass tempBookingReference to child component', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('temp-ref')).toHaveTextContent('TEMP123');
  });
});
