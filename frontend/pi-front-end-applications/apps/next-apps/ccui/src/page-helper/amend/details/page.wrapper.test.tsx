import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

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

jest.mock('./page.ccui', () => ({
  __esModule: true,
  default: jest.fn(({ confirmationInput }) => (
    <div data-testid="amend-page-ccui">
      <div data-testid="booking-ref">{confirmationInput.bookingReference}</div>
    </div>
  )),
}));

describe('CCUI Amend Details Wrapper', () => {
  const mockQueryClient = new QueryClient();
  const mockConfirmationInput = {
    bookingReference: 'CCUI123',
    basketReference: 'BASKET789',
    token: 'token456',
    country: 'GB',
    language: 'en',
  };

  it('should render AmendPageCcui component', () => {
    const { getByTestId } = render(
      <DetailsPage confirmationInput={mockConfirmationInput} queryClient={mockQueryClient} />
    );

    expect(getByTestId('amend-page-ccui')).toBeInTheDocument();
  });

  it('should pass confirmationInput from wrapper to AmendPageCcui', () => {
    const { getByTestId } = render(
      <DetailsPage confirmationInput={mockConfirmationInput} queryClient={mockQueryClient} />
    );

    expect(getByTestId('booking-ref')).toHaveTextContent('CCUI123');
  });
});
