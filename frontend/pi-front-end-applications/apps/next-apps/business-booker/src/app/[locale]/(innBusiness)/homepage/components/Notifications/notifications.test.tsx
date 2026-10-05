import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { CustomerAccountDetails, LOCALES } from '@whitbread-eos/api';
import { cookies } from 'next/headers';

import { Notifications } from './notifications';

const mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    tethered: false,
  },
};

const mockCookieData = {
  value: mockToken,
};

const mockCookieStore = {
  get: () => mockCookieData,
} as unknown as ReturnType<typeof cookies>;

jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({
    get: () => 'test',
  }),
}));

const mockGetTranslations = jest.fn(() => ({ t: (str: string) => str }));
const getNotificationsV2Mock = jest.fn(() => ({ profileUpdateRequired: false }));
const getAccountInfoMock = jest.fn(() => ({ status: 'Current' }));
const getCompanyDetailsMock = jest.fn(() => ({
  requestedCompany: {
    companyDetails: {
      mainEmployee: {
        title: '',
        firstName: 'John',
        lastName: 'Doe',
        emailAddress: 'test@test.com',
      },
    },
  },
}));
const getAccountRegistrationRoleDetailsMock = jest.fn(() => ({
  isOnlyCardHolder: false,
  isOnlyFinanceUser: false,
  isCardHolderAndFinanceUser: false,
}));
const getDetailsFromTokenMock = jest.fn(() => ({
  isTravelManager: true,
  isBusinessPayManager: false,
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: () => ({ language: 'en' }),
    getTranslations: () => mockGetTranslations(),
    useTranslation: jest.fn(() => ({
      t: jest.fn((key) => key),
    })),
    getCommonIcons: () => ({}),
    formatAccountNumber: (accountNumber: string) => accountNumber,
    getWorldlineReturnUrl: (str: string) => str,
    getUserDetails: jest.fn(() => {
      return {};
    }),
    getNotificationsV2: () => getNotificationsV2Mock(),
    getAccountInfo: () => getAccountInfoMock(),
    getCompanyDetails: () => getCompanyDetailsMock(),
    getAccountRegistrationRoleDetails: () => getAccountRegistrationRoleDetailsMock(),
    getDetailsFromToken: () => getDetailsFromTokenMock(),
  };
});

jest.mock('~components/innBusiness/SuspendedNotification', () => ({
  __esModule: true,
  default: ({ isShown }: any) => (
    <>
      {isShown ? (
        <div data-testid="Notifications-AccountSuspended">SuspendedNotification</div>
      ) : (
        <></>
      )}
    </>
  ),
}));

jest.mock('./employee-requests-notification-wrapper.tsx', () => {
  return {
    EmployeeRequestsNotificationWrapper: () => (
      <div data-testid="Notifications-EmployeeRequestsWrapper">EmployeeRequests</div>
    ),
  };
});

describe('Notifications Component', () => {
  const account: CustomerAccountDetails = {
    accountName: 'Test Account',
    accountNumber: '123456',
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockGetTranslations.mockReset().mockImplementation(() => ({
      t: (key: string) => key,
    }));
    getNotificationsV2Mock.mockReset().mockReturnValue({
      profileUpdateRequired: false,
    });
    getAccountInfoMock.mockReset().mockReturnValue({
      status: 'Current',
    });
    getAccountRegistrationRoleDetailsMock.mockReset().mockReturnValue({
      isOnlyCardHolder: false,
      isOnlyFinanceUser: false,
      isCardHolderAndFinanceUser: false,
    });
    getDetailsFromTokenMock.mockReset().mockReturnValue({
      isTravelManager: true,
      isBusinessPayManager: false,
    });
  });

  it('renders account suspended alert', async () => {
    getAccountInfoMock.mockReturnValue({
      status: 'stop',
    });

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-AccountSuspended')).toBeInTheDocument();
  });

  it('renders account suspended alert for finance users', async () => {
    getAccountInfoMock.mockReturnValue({
      status: 'stop',
    });
    getAccountRegistrationRoleDetailsMock.mockReturnValue({
      isOnlyCardHolder: false,
      isOnlyFinanceUser: true,
      isCardHolderAndFinanceUser: false,
    });

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(getAccountInfoMock).toHaveBeenCalled();
    expect(screen.getByTestId('Notifications-AccountSuspended')).toBeInTheDocument();
  });

  it('renders account closed alert', async () => {
    getAccountInfoMock.mockReturnValue({
      status: 'closed',
    });

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-AccountClosed')).toBeInTheDocument();
  });

  it('renders profile update required alert', async () => {
    getNotificationsV2Mock.mockReturnValue({
      profileUpdateRequired: true,
    });

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-ProfileUpdateRequired')).toBeInTheDocument();
  });

  it('renders no alerts when all conditions are false', async () => {
    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.queryByTestId('Notifications-AccountClosed')).not.toBeInTheDocument();
    expect(screen.queryByTestId('Notifications-AdHocNotification')).not.toBeInTheDocument();
    expect(screen.queryByTestId('Notifications-ProfileUpdateRequired')).not.toBeInTheDocument();
  });

  it('renders ad hoc error alert when configured via translations', async () => {
    mockGetTranslations.mockImplementation(() => ({
      t: (key: string) => {
        if (key === 'notifications.notification.adhoc.isVisible') {
          return 'true';
        }
        if (key === 'notifications.notification.adhoc.type') {
          return 'error';
        }
        if (key === 'notifications.notification.adhoc.title') {
          return 'adhoc-title';
        }
        if (key === 'notifications.notification.adhoc.subtitle') {
          return 'adhoc-subtitle';
        }
        return key;
      },
    }));

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-AdHocNotification')).toBeInTheDocument();
    expect(screen.getByText('adhoc-title')).toBeInTheDocument();
  });

  it('renders ad hoc alert variant when type is alert', async () => {
    mockGetTranslations.mockImplementation(() => ({
      t: (key: string) => {
        if (key === 'notifications.notification.adhoc.isVisible') {
          return 'true';
        }
        if (key === 'notifications.notification.adhoc.type') {
          return 'alert';
        }
        if (key === 'notifications.notification.adhoc.title') {
          return 'alert-title';
        }
        if (key === 'notifications.notification.adhoc.subtitle') {
          return 'alert-subtitle';
        }
        return key;
      },
    }));

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-AdHocNotification')).toBeInTheDocument();
    expect(screen.getByText('alert-title')).toBeInTheDocument();
  });

  it('renders ad hoc info variant when type is info', async () => {
    mockGetTranslations.mockImplementation(() => ({
      t: (key: string) => {
        if (key === 'notifications.notification.adhoc.isVisible') {
          return 'true';
        }
        if (key === 'notifications.notification.adhoc.type') {
          return 'info';
        }
        if (key === 'notifications.notification.adhoc.title') {
          return 'info-title';
        }
        if (key === 'notifications.notification.adhoc.subtitle') {
          return 'info-subtitle';
        }
        return key;
      },
    }));

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-AdHocNotification')).toBeInTheDocument();
    expect(screen.getByText('info-title')).toBeInTheDocument();
  });

  it('skips account info lookup when no account is provided', async () => {
    render(
      await Notifications({
        account: null,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(getAccountInfoMock).not.toHaveBeenCalled();
  });

  it('skips account info lookup when user roles restrict access', async () => {
    getAccountRegistrationRoleDetailsMock.mockReturnValue({
      isOnlyCardHolder: true,
      isOnlyFinanceUser: false,
      isCardHolderAndFinanceUser: false,
    });

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(getAccountInfoMock).not.toHaveBeenCalled();
  });

  it('renders employee requests notification for travel managers', async () => {
    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-EmployeeRequestsWrapper')).toBeInTheDocument();
  });

  it('renders employee requests notification for business pay managers', async () => {
    getDetailsFromTokenMock.mockReturnValue({
      isTravelManager: false,
      isBusinessPayManager: true,
    });

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-EmployeeRequestsWrapper')).toBeInTheDocument();
  });

  it('does not render employee requests notification when not a travel or business pay manager', async () => {
    getDetailsFromTokenMock.mockReturnValue({
      isTravelManager: false,
      isBusinessPayManager: false,
    });

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.queryByTestId('Notifications-EmployeeRequestsWrapper')).not.toBeInTheDocument();
  });

  it('renders incomplete main contact alert for travel managers', async () => {
    const { unmount } = render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.getByTestId('Notifications-IncompleteMainEmployee')).toBeInTheDocument();
    unmount();

    getDetailsFromTokenMock.mockReturnValue({
      isTravelManager: false,
      isBusinessPayManager: false,
    });

    render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: 'test-token',
      })
    );

    expect(screen.queryByTestId('Notifications-IncompleteMainEmployee')).not.toBeInTheDocument();
  });

  it('returns null when token is empty to prevent API calls on session expiry', async () => {
    const result = render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: '',
      })
    );

    expect(result.container.firstChild).toBeNull();
    expect(getNotificationsV2Mock).not.toHaveBeenCalled();
    expect(getAccountInfoMock).not.toHaveBeenCalled();
    expect(getCompanyDetailsMock).not.toHaveBeenCalled();
  });

  it('returns null when token is whitespace to prevent API calls on session expiry', async () => {
    const result = render(
      await Notifications({
        account,
        locale: LOCALES.EN,
        token: '   ',
      })
    );

    expect(result.container.firstChild).toBeNull();
    expect(getNotificationsV2Mock).not.toHaveBeenCalled();
    expect(getAccountInfoMock).not.toHaveBeenCalled();
    expect(getCompanyDetailsMock).not.toHaveBeenCalled();
  });
});
