import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';

import AmendBookingConfirmationPagePi from './page.pi';

const mockProps = {
  confirmationInput: {
    bookingReference: 'AKU8410577',
    basketReference: 'AKU8410577',
    token: 'test',
    country: 'GB',
    language: 'en',
  },
  queryClient: new ReactQuery.QueryClient(),
  amendBookingStatus: CONFIRM_AMEND_STATUS.success,
};

jest.mock('@whitbread-eos/organisms', () => ({
  AmendBookingConfirmationContainer: () => (
    <div data-testid="amend-booking-confirmation-container" />
  ),
}));

jest.mock('@whitbread-eos/utils', () => ({
  cn: (...classes: any[]) => classes.filter(Boolean).join(' '),
  updateAmendPageAnalytics: jest.fn(),
  useSessionStorage: () => [null, jest.fn()],
}));

const mockCustomLocale = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    asPath: '/',
    query: { searchLocation: '' },
  }),
}));

describe('Amend Booking Confirmation Page - PI', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should render page pi', async () => {
    const { getByTestId } = render(<AmendBookingConfirmationPagePi {...mockProps} />);

    expect(getByTestId('amend-booking-confirmation-container')).toBeInTheDocument();
  });
});
