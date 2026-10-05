import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';

import AmendBookingConfirmationPageBb from './page.bb';

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

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('Amend Booking Confirmation Page - BB', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
  });
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      asPath: '/booking-confirmation?bookingReference=213',
      query: { searchLocation: 'test', ARRdd: '', ARRmm: '', ARRyyyy: '', NIGHTS: '', ROOMS: '' },
    });
  });
  it('should render page bb', async () => {
    const { getByTestId } = render(<AmendBookingConfirmationPageBb {...mockProps} />);

    expect(getByTestId('amend-booking-confirmation-container')).toBeInTheDocument();
  });
});
