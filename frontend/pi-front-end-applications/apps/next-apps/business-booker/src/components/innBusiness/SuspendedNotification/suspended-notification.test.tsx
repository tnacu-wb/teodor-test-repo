import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES, Scheme } from '@whitbread-eos/api';

import { SuspendedNotification } from './suspended-notification';

const mockWordlineButton = jest.fn((props: any) => (
  <button data-testid={`${props.baseDataTestId}-Button`} />
));

jest.mock('next/headers', () => ({
  headers: jest.fn(() => ({
    get: jest.fn(() => 'http://example.com'),
  })),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getWorldlineReturnUrl: jest.fn(() => ''),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: jest.fn(() => ({
      t: jest.fn((key: string) => key),
    })),
    useTranslation: jest.fn(() => ({
      t: (key: string) => key,
    })),
    formatAccountNumber: jest.fn((accountNumber: string) => accountNumber),
  };
});

jest.mock('../WordlineButton', () => ({
  WordlineButton: (props: any) => mockWordlineButton(props),
}));

describe('SuspendedNotification', () => {
  beforeEach(() => {
    mockWordlineButton.mockClear();
  });

  it('renders correctly when isShown is true', async () => {
    const { getByTestId } = render(
      await SuspendedNotification({
        locale: LOCALES.EN,
        account: {
          accountName: 'Test Account',
          accountNumber: '1234567890',
          scheme: 'GB' as Scheme.Gb,
          tetheredGuid: 'test-guid',
        },
        isShown: true,
      })
    );

    expect(getByTestId('Notifications-AccountSuspended')).toBeInTheDocument();
    expect(
      getByTestId('Notifications-AccountSuspended-Inline-Make-a-payment-Button')
    ).toBeInTheDocument();

    const makePaymentCall = mockWordlineButton.mock.calls.find(
      ([props]) => props.baseDataTestId === 'Notifications-AccountSuspended-Inline-Make-a-payment'
    );
    expect(makePaymentCall?.[0].page).toBe('CardPayment.aspx');
  });

  it('renders correctly when isShown is false', async () => {
    const { container } = render(
      await SuspendedNotification({
        locale: LOCALES.EN,
        account: {
          accountName: 'Test Account',
          accountNumber: '1234567890',
          scheme: 'GB' as Scheme.Gb,
          tetheredGuid: 'test-guid',
        },
        isShown: false,
      })
    );

    expect(container).toBeEmptyDOMElement();
  });

  it('renders interim payment link for DE schemes', async () => {
    render(
      await SuspendedNotification({
        locale: LOCALES.DE,
        account: {
          accountName: 'Test Account',
          accountNumber: '1234567890',
          scheme: 'DE' as Scheme.De,
          tetheredGuid: 'test-guid',
        },
        isShown: true,
      })
    );

    const makePaymentCall = mockWordlineButton.mock.calls.find(
      ([props]) => props.baseDataTestId === 'Notifications-AccountSuspended-Inline-Make-a-payment'
    );
    expect(makePaymentCall?.[0].page).toBe('InterimPayment.aspx');
  });
});
