import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';

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

jest.mock('./page.ccui', () => ({
  __esModule: true,
  default: jest.fn(
    ({ confirmationInput, amendBookingStatus, bookingSpinnerConfig, tempBookingReference }) => (
      <div data-testid="amend-booking-confirmation-ccui">
        <div data-testid="booking-ref">{confirmationInput.bookingReference}</div>
        <div data-testid="status">{amendBookingStatus}</div>
        <div data-testid="spinner-config">{JSON.stringify(bookingSpinnerConfig)}</div>
        <div data-testid="temp-ref">{tempBookingReference}</div>
      </div>
    )
  ),
}));

describe('CCUI Amend Booking Confirmation Wrapper', () => {
  const mockQueryClient = new QueryClient();
  const mockConfirmationInput = {
    bookingReference: 'CCUI123',
    basketReference: 'BASKET789',
    token: 'token456',
    country: 'GB',
    language: 'en',
  };
  const mockBookingSpinnerConfig = [
    { order: 1, seconds: 3, text: 'Processing...' },
    { order: 2, seconds: 5, text: 'Almost done...' },
  ];

  it('should render AmendBookingConfirmationPageCcui component', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        queryClient={mockQueryClient}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('amend-booking-confirmation-ccui')).toBeInTheDocument();
  });

  it('should pass confirmationInput prop to component', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        queryClient={mockQueryClient}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('booking-ref')).toHaveTextContent('CCUI123');
  });

  it('should render AmendBookingConfirmationPageCcui inside wrapper', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        queryClient={mockQueryClient}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('amend-booking-confirmation-ccui')).toBeInTheDocument();
  });

  it('should pass amendBookingStatus when provided', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
        queryClient={mockQueryClient}
        amendBookingStatus={CONFIRM_AMEND_STATUS.success}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('status')).toHaveTextContent(CONFIRM_AMEND_STATUS.success);
  });

  it('should pass bookingSpinnerConfig to child component', () => {
    const { getByTestId } = render(
      <BookingConfirmationPage
        confirmationInput={mockConfirmationInput}
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
        queryClient={mockQueryClient}
        bookingSpinnerConfig={mockBookingSpinnerConfig}
        tempBookingReference="TEMP123"
      />
    );

    expect(getByTestId('temp-ref')).toHaveTextContent('TEMP123');
  });
});
