import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Area } from '@whitbread-eos/api';

import DetailsPage from './page.wrapper';

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

jest.mock('./page.pi', () => ({
  __esModule: true,
  default: jest.fn(({ confirmationInput, pcksQueryInput }) => (
    <div data-testid="amend-page-pi">
      <div data-testid="booking-ref">{confirmationInput.bookingReference}</div>
      <div data-testid="packages">{JSON.stringify(pcksQueryInput)}</div>
    </div>
  )),
}));

describe('Premier Inn Amend Details Wrapper', () => {
  const mockQueryClient = new QueryClient();
  const mockConfirmationInput = {
    bookingReference: 'ABC123',
    basketReference: 'BASKET456',
    token: 'token789',
    country: 'GB',
    language: 'en',
  };
  const mockPcksQueryInput = {
    country: 'GB',
    language: 'en',
    site: 'SITE_LEISURE',
  };

  it('should render AmendPagePi component', () => {
    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.PI}
        queryClient={mockQueryClient}
      />
    );

    expect(getByTestId('amend-page-pi')).toBeInTheDocument();
  });

  it('should render AmendPagePi component inside wrapper', () => {
    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.PI}
        queryClient={mockQueryClient}
      />
    );

    expect(getByTestId('amend-page-pi')).toBeInTheDocument();
  });

  it('should pass confirmationInput from wrapper to AmendPagePi', () => {
    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.PI}
        queryClient={mockQueryClient}
      />
    );

    expect(getByTestId('booking-ref')).toHaveTextContent('ABC123');
  });

  it('should pass pcksQueryInput to AmendPagePi', () => {
    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.PI}
        queryClient={mockQueryClient}
      />
    );

    expect(getByTestId('packages')).toHaveTextContent(JSON.stringify(mockPcksQueryInput));
  });

  it('should handle optional userDetails prop', () => {
    const mockUserDetails = {
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
    };

    const { getByTestId } = render(
      <DetailsPage
        confirmationInput={mockConfirmationInput}
        pcksQueryInput={mockPcksQueryInput}
        variant={Area.PI}
        queryClient={mockQueryClient}
        userDetails={mockUserDetails}
      />
    );

    expect(getByTestId('amend-page-pi')).toBeInTheDocument();
  });
});
