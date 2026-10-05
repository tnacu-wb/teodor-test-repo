import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';

import { render } from '../utils/test-utils';
import AmendBookingConfirmationPageCCUI from './page.ccui';

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
  AgentMemo: () => <div data-testid="agent-memo" />,
}));

jest.mock('@whitbread-eos/utils', () => ({
  cn: (...classes: any[]) => classes.filter(Boolean).join(' '),
  formatDataTestId: (...ids: string[]) => ids.filter(Boolean).join('-'),
  getNoOfDaysInYear: () => 365,
  getAuthCookie: () => null,
  getCookie: () => null,
  useCustomLocale: () => ({ language: 'gb', country: 'gb' }),
  useSessionStorage: () => [null, jest.fn()],
  useAgentMemo: () => ({
    isAgentMemoOpen: false,
    openAgentMemo: jest.fn(),
    closeAgentMemo: jest.fn(),
    setAgentMemoReservationId: jest.fn(),
    agentMemoState: { reservationId: '', variant: '' },
    agentMemoData: null,
    agentMemoCount: 0,
  }),
  updateAmendPageAnalytics: jest.fn(),
}));

const mockCustomLocale = jest.fn();

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('Amend Booking Confirmation Page - CCUI', () => {
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
  it('should render page ccui', async () => {
    const { getByTestId } = render(<AmendBookingConfirmationPageCCUI {...mockProps} />);

    expect(getByTestId('amend-booking-confirmation-container')).toBeInTheDocument();
  });
});
