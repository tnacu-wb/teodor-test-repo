import '@testing-library/jest-dom';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { Currency, GlobalInnB } from '@whitbread-eos/api';
import { updateBookingAlerts } from '@whitbread-eos/utils/server';
import { getCookie } from 'cookies-next';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { AlertsContent } from './AlertsContent';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string, defaultValue?: any) => defaultValue || key,
  }),
  updateBookingAlerts: jest.fn(),
  ID_TOKEN_COOKIE: 'mock_id_token_cookie',
}));

jest.mock('cookies-next', () => ({
  getCookie: jest.fn(),
}));

const mockRouterRefresh = jest.fn();
const mockRouterPush = jest.fn();
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: mockRouterPush,
    refresh: mockRouterRefresh,
  }),
}));

const mockToastFn = jest.fn();
jest.mock('@whitbread-eos/atoms/ui', () => ({
  useToast: () => ({
    toast: mockToastFn,
  }),
  Button: jest.fn(({ children, onClick, type, disabled, className, ...props }) => (
    <button
      data-testid={props['data-testid']}
      onClick={onClick}
      type={type}
      disabled={disabled}
      className={className}
    >
      {children}
    </button>
  )),
}));

jest.mock('~components/innBusiness/forms/AlertsForms/AlertToggles', () => ({
  AlertToggles: jest.fn(() => <div data-testid="mock-alert-toggles">AlertToggles</div>),
}));
jest.mock('~components/innBusiness/forms/AlertsForms/HotelAlertsSelector', () => ({
  HotelAlertsSelector: jest.fn(() => (
    <div data-testid="mock-hotel-alerts-selector">HotelAlertsSelector</div>
  )),
}));
jest.mock('~components/innBusiness/forms/AlertsForms/PriceAlertsFormWrapper', () => ({
  PriceAlertsFormWrapper: jest.fn(() => (
    <div data-testid="mock-price-alerts-form-wrapper">PriceAlertsFormWrapper</div>
  )),
}));
jest.mock('~components/innBusiness/forms/AlertsForms/AlertRecipients', () => ({
  AlertRecipients: jest.fn(() => <div data-testid="mock-alert-recipients">AlertRecipients</div>),
}));
jest.mock('~components/innBusiness/forms/AlertsForms/FrequencyRadios', () => ({
  FrequencyRadios: jest.fn(() => <div data-testid="mock-frequency-radios">FrequencyRadios</div>),
}));
jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: jest.fn(() => <div data-testid="mock-review-changes">ReviewChanges</div>),
}));

const mockDefaultProps: React.ComponentProps<typeof AlertsContent> = {
  icons: { testIcon: 'test.svg' },
  globalLabels: {} as unknown as GlobalInnB,
  locationIcon: 'location.svg',
  companyId: 'test-company-123',
  language: 'en',
  bookingAlerts: {
    dayOfArrival: false,
    weekendArrival: true,
    passThroughWeekend: false,
    rateCaps: {
      uKWide: { amount: 100, currency: Currency.GBP_NAME },
      greaterLondon: { amount: 150, currency: Currency.GBP_NAME },
      ireland: { amount: 120, currency: Currency.EUR_NAME },
    },
    frequency: 'D',
    recipientEmailAddresses: ['test1@example.com', 'test2@example.com'],
    bookingAlertHotels: ['HOTEL_CODE_1', 'HOTEL_CODE_2'],
  },
};

describe('AlertsContent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (getCookie as jest.Mock).mockReturnValue('mock-jwt-token');
  });

  it('Test 1: initializes form and renders child components', () => {
    render(<AlertsContent {...mockDefaultProps} />);

    expect(screen.getByTestId('mock-alert-toggles')).toBeInTheDocument();
    expect(screen.getByTestId('mock-hotel-alerts-selector')).toBeInTheDocument();
    expect(screen.getByTestId('mock-price-alerts-form-wrapper')).toBeInTheDocument();
    expect(screen.getByTestId('mock-alert-recipients')).toBeInTheDocument();
    expect(screen.getByTestId('mock-frequency-radios')).toBeInTheDocument();

    expect(screen.getByTestId('AlertsPage-save-button')).toBeInTheDocument();
    expect(screen.getByText('company.coMngt.saveUpdatesButton')).toBeInTheDocument();
  });

  it('Test 2: handles successful form submission', async () => {
    (updateBookingAlerts as jest.Mock).mockResolvedValue({ status: 'success' });

    render(<AlertsContent {...mockDefaultProps} />);

    const saveButton = screen.getByTestId('AlertsPage-save-button');
    await act(async () => {
      fireEvent.click(saveButton);
    });

    expect(updateBookingAlerts).toHaveBeenCalledTimes(1);
    const expectedTransformedData = {
      dayOfArrival: mockDefaultProps.bookingAlerts?.dayOfArrival,
      weekendArrival: mockDefaultProps.bookingAlerts?.weekendArrival,
      passThroughWeekend: mockDefaultProps.bookingAlerts?.passThroughWeekend,
      rateCaps: {
        uKWide: {
          amount: mockDefaultProps.bookingAlerts?.rateCaps?.uKWide?.amount || 0,
          currency: Currency.GBP_NAME,
        },
        greaterLondon: {
          amount: mockDefaultProps.bookingAlerts?.rateCaps?.greaterLondon?.amount || 0,
          currency: Currency.GBP_NAME,
        },
        ireland: {
          amount: mockDefaultProps.bookingAlerts?.rateCaps?.ireland?.amount || 0,
          currency: Currency.GBP_NAME,
        },
      },
      frequency: 'D',
      bookingAlertHotels: [],
      recipientEmailAddresses:
        mockDefaultProps.bookingAlerts?.recipientEmailAddresses?.map((r) => r) || [],
    };
    expect(updateBookingAlerts).toHaveBeenCalledWith(
      mockDefaultProps.companyId,
      expect.objectContaining({
        dayOfArrival: expectedTransformedData.dayOfArrival,
        weekendArrival: expectedTransformedData.weekendArrival,
        passThroughWeekend: expectedTransformedData.passThroughWeekend,
        frequency: expectedTransformedData.frequency,
        bookingAlertHotels: expect.any(Array),
        recipientEmailAddresses: expect.any(Array),
      }),
      'mock-jwt-token'
    );

    expect(mockToastFn).toHaveBeenCalledWith({
      content: 'company.notification.message.save',
    });

    expect(mockRouterRefresh).toHaveBeenCalledTimes(1);
  });

  it('Test 3: handles discard changes flow', async () => {
    render(<AlertsContent {...mockDefaultProps} />);

    const discardButton = screen.getByTestId('AlertsPage-discard-button');
    await act(async () => {
      fireEvent.click(discardButton);
    });

    expect(screen.getByTestId('mock-review-changes')).toBeInTheDocument();
    expect(ReviewChanges).toHaveBeenLastCalledWith(
      expect.objectContaining({
        isOpen: true,
        onContinue: expect.any(Function),
        onDiscard: expect.any(Function),
      }),
      undefined
    );
  });

  test('Test 3: handles API error on form submission', async () => {
    const apiErrorMessage = 'company.notification.message.error';
    (updateBookingAlerts as jest.Mock).mockResolvedValue({
      status: 'error',
      error: apiErrorMessage,
    });

    render(<AlertsContent {...mockDefaultProps} />);

    const saveButton = screen.getByTestId('AlertsPage-save-button');
    await act(async () => {
      fireEvent.click(saveButton);
    });

    expect(updateBookingAlerts).toHaveBeenCalledTimes(1);

    expect(mockToastFn).toHaveBeenCalledWith({
      content: apiErrorMessage,
      variant: 'error',
    });

    expect(mockRouterRefresh).not.toHaveBeenCalled();
  });
});
