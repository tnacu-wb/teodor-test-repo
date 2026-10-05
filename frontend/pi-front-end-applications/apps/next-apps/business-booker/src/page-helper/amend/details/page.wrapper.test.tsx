import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Area } from '@whitbread-eos/api';

import DetailsPage from './page.wrapper';

// Mock dependencies.
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
  default: jest.fn(({ confirmationInput, pcksQueryInput }) => (
    <div data-testid="amend-page-bb">
      <div data-testid="booking-ref">{confirmationInput.bookingReference}</div>
      <div data-testid="packages">{JSON.stringify(pcksQueryInput)}</div>
    </div>
  )),
}));

describe('Business Booker Amend Details Wrapper', () => {
  const mockQueryClient = new QueryClient();
  const mockConfirmationInput = {
    bookingReference: 'BB123',
    basketReference: 'BASKET456',
    token: 'token789',
    country: 'GB',
    language: 'en',
  };
  const mockPcksQueryInput = {
    country: 'GB',
    language: 'en',
    site: 'SITE_BB',
  };

  it('should render AmendPageBb component', () => {
    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
      />
    );

    expect(getByTestId('amend-page-bb')).toBeInTheDocument();
  });

  it('should render AmendPageBb component inside wrapper', () => {
    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
      />
    );

    expect(getByTestId('amend-page-bb')).toBeInTheDocument();
  });

  it('should pass confirmationInput from wrapper to AmendPageBb', () => {
    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
      />
    );

    expect(getByTestId('booking-ref')).toHaveTextContent('BB123');
  });

  it('should pass pcksQueryInput to AmendPageBb', () => {
    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
      />
    );

    expect(getByTestId('packages')).toHaveTextContent(JSON.stringify(mockPcksQueryInput));
  });

  it('should handle optional userDetails prop', () => {
    const mockUserDetails = {
      email: 'bb@example.com',
      firstName: 'Jane',
      lastName: 'Smith',
    };

    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.BB}
        queryClient={mockQueryClient}
        userDetails={mockUserDetails}
      />
    );

    expect(getByTestId('amend-page-bb')).toBeInTheDocument();
  });
});
