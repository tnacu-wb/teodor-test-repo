import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import * as UtilsServer from '@whitbread-eos/utils/server';

import { InnBusinessPaySection } from './inn-business-pay-section';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  getWorldlineUserPreferences: jest.fn(),
  formatIBAssetsUrl: (url: string) => url || '',
}));

jest.mock('./inn-business-pay-preferences', () => ({
  InnBusinessPayPreferences: jest.fn(
    ({
      accountName,
      preferences,
    }: {
      accountName: string;
      preferences: Array<{ id: string; translationKey: string }>;
    }) => (
      <div data-testid={`prefs-${accountName}`}>
        {preferences.map((p) => (
          <div key={p.id} data-testid={`pref-${p.id}`}>
            {p.translationKey}
          </div>
        ))}
      </div>
    )
  ),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Notification: jest.fn(({ type = 'info', icon, message, className }) => (
    <div
      data-testid="notification"
      data-type={type}
      className={`w-[420px] mobile:w-full p-4 rounded border border-solid flex items-start gap-2 ${
        className || ''
      }`}
    >
      {icon && <img src={icon} alt={`${type} icon`} data-testid="notification-icon" />}
      <div data-testid="notification-message">{message}</div>
    </div>
  )),
}));

describe('InnBusinessPaySection', () => {
  const mockToken = 'test-token';
  const mockIcons = {
    'icon.notification.info': '/icons/info.svg',
    infoIcon: '/icons/info.svg',
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders notification and empty state when no token provided', () => {
    render(<InnBusinessPaySection icons={mockIcons} />);

    const notification = screen.getByTestId('notification');
    expect(notification).toBeInTheDocument();
    expect(notification).toHaveAttribute('data-type', 'info');
    expect(screen.getByTestId('notification-message')).toHaveTextContent(
      'innBusinessPay.preferences.notification'
    );
    expect(screen.getByTestId('notification-icon')).toHaveAttribute('src', '/icons/info.svg');
  });

  test('renders ACCOUNT_HOLDER preferences correctly', async () => {
    const mockData = [
      {
        tetheredUserGuid: 'guid2',
        accountName: 'Test Account',
        accountNumber: '12345',
        registrationRoles: ['ACCOUNT_HOLDER'],
        settings: [
          { smsType: 'SMS001', isSmsSelected: true },
          { smsType: 'SMS002', isSmsSelected: false },
          { smsType: 'SMS003', isSmsSelected: true },
          { smsType: 'SMS004', isSmsSelected: false },
        ],
        preferenceDetails: {
          showSmsStopsToCardholder: true,
          sendCardsToCardholder: false,
        },
      },
    ];

    (UtilsServer.getWorldlineUserPreferences as jest.Mock).mockResolvedValue(mockData);

    render(<InnBusinessPaySection token={mockToken} icons={mockIcons} />);

    await waitFor(() => {
      expect(screen.getByTestId('prefs-Test Account')).toBeInTheDocument();
    });

    const notification = screen.getByTestId('notification');
    expect(notification).toBeInTheDocument();
    expect(notification).toHaveAttribute('data-type', 'info');

    expect(screen.getByText('innBusinessPay.preferences.text.alerts.title')).toBeInTheDocument();
    expect(
      screen.getByText('innBusinessPay.preferences.replacement.cards.title')
    ).toBeInTheDocument();

    expect(screen.getByTestId('pref-nearingLimit')).toBeInTheDocument();
    expect(screen.getByTestId('pref-onOffStop')).toBeInTheDocument();
    expect(screen.getByTestId('pref-cardLimitReached')).toBeInTheDocument();
    expect(screen.getByTestId('pref-invoiceReady')).toBeInTheDocument();
  });

  test('does not render anything if account scheme is DE (isPIBAEuro)', () => {
    const mockAccount = { scheme: 'DE' } as any;
    render(<InnBusinessPaySection account={mockAccount} icons={{}} />);
    expect(screen.queryByTestId('notification')).not.toBeInTheDocument();
  });

  test('renders nothing if getWorldlineUserPreferences returns empty array', async () => {
    (UtilsServer.getWorldlineUserPreferences as jest.Mock).mockResolvedValue([]);
    render(<InnBusinessPaySection token="token" icons={{}} />);
    await waitFor(() => {
      expect(screen.getByTestId('notification')).toBeInTheDocument();
    });
    // No preferences rendered
    expect(screen.queryByTestId(/^prefs-/)).not.toBeInTheDocument();
  });

  test('handles error from getWorldlineUserPreferences gracefully', async () => {
    const errorSpy = jest.spyOn(console, 'error').mockImplementation(() => false);
    (UtilsServer.getWorldlineUserPreferences as jest.Mock).mockRejectedValue(new Error('fail'));
    render(<InnBusinessPaySection token="token" icons={{}} />);
    await waitFor(() => {
      expect(screen.getByTestId('notification')).toBeInTheDocument();
    });
    errorSpy.mockRestore();
  });

  test('renders FINANCE_USER preferences correctly', async () => {
    const mockData = [
      {
        tetheredUserGuid: 'guid3',
        accountName: 'Finance Account',
        accountNumber: '54321',
        registrationRoles: ['FINANCE_USER'],
        settings: [
          { smsType: 'SMS001', isSmsSelected: true },
          { smsType: 'SMS002', isSmsSelected: false },
          { smsType: 'SMS004', isSmsSelected: true },
        ],
        preferenceDetails: {},
      },
    ];
    (UtilsServer.getWorldlineUserPreferences as jest.Mock).mockResolvedValue(mockData);

    render(
      <InnBusinessPaySection
        token="token"
        icons={{ 'icon.notification.info': '/icons/info.svg' }}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('prefs-Finance Account')).toBeInTheDocument();
    });

    expect(screen.getByTestId('pref-nearingLimit')).toBeInTheDocument();
    expect(screen.getByTestId('pref-onOffStop')).toBeInTheDocument();
    expect(screen.getByTestId('pref-invoiceReady')).toBeInTheDocument();
    // Should not render cardLimitReached for FINANCE_USER
    expect(screen.queryByTestId('pref-cardLimitReached')).not.toBeInTheDocument();
  });

  test('renders CARD_HOLDER only preferences correctly', async () => {
    const mockData = [
      {
        tetheredUserGuid: 'guid4',
        accountName: 'Card Holder Account',
        accountNumber: '99999',
        registrationRoles: ['CARD_HOLDER'],
        settings: [
          { smsType: 'SMS005', isSmsSelected: true },
          { smsType: 'SMS006', isSmsSelected: false },
        ],
        preferenceDetails: {},
      },
    ];
    (UtilsServer.getWorldlineUserPreferences as jest.Mock).mockResolvedValue(mockData);

    render(
      <InnBusinessPaySection
        token="token"
        icons={{ 'icon.notification.info': '/icons/info.svg' }}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('prefs-Card Holder Account')).toBeInTheDocument();
    });

    expect(screen.getByTestId('pref-cardNearingLimit')).toBeInTheDocument();
    expect(screen.getByTestId('pref-onOffStop')).toBeInTheDocument();
    // Should not render nearingLimit for CARD_HOLDER only
    expect(screen.queryByTestId('pref-nearingLimit')).not.toBeInTheDocument();
  });

  test('renders FINANCE_USER and CARD_HOLDER preferences correctly', async () => {
    const mockData = [
      {
        tetheredUserGuid: 'guid5',
        accountName: 'Finance Card Account',
        accountNumber: '88888',
        registrationRoles: ['FINANCE_USER', 'CARD_HOLDER'],
        settings: [
          { smsType: 'SMS005', isSmsSelected: true },
          { smsType: 'SMS006', isSmsSelected: true },
        ],
        preferenceDetails: {},
      },
    ];
    (UtilsServer.getWorldlineUserPreferences as jest.Mock).mockResolvedValue(mockData);

    render(
      <InnBusinessPaySection
        token="token"
        icons={{ 'icon.notification.info': '/icons/info.svg' }}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('prefs-Finance Card Account')).toBeInTheDocument();
    });

    expect(screen.getByTestId('pref-cardNearingLimit')).toBeInTheDocument();
    expect(screen.getByTestId('pref-onOffStop')).toBeInTheDocument();
    // Should not render nearingLimit for FINANCE_USER and CARD_HOLDER
    expect(screen.queryByTestId('pref-nearingLimit')).not.toBeInTheDocument();
  });

  test('renders multiple accounts', async () => {
    const mockData = [
      {
        tetheredUserGuid: 'guid6',
        accountName: 'Account 1',
        accountNumber: '11111',
        registrationRoles: ['ACCOUNT_HOLDER'],
        settings: [],
        preferenceDetails: { showSmsStopsToCardholder: true, sendCardsToCardholder: true },
      },
      {
        tetheredUserGuid: 'guid7',
        accountName: 'Account 2',
        accountNumber: '22222',
        registrationRoles: ['FINANCE_USER'],
        settings: [],
        preferenceDetails: {},
      },
    ];
    (UtilsServer.getWorldlineUserPreferences as jest.Mock).mockResolvedValue(mockData);

    render(
      <InnBusinessPaySection
        token="token"
        icons={{ 'icon.notification.info': '/icons/info.svg' }}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('prefs-Account 1')).toBeInTheDocument();
      expect(screen.getByTestId('prefs-Account 2')).toBeInTheDocument();
    });
  });
});
