import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES, FT_IB_WL_CARD_EDIT } from '@whitbread-eos/api';
import { getAccessLevel } from '@whitbread-eos/utils/server';

import { InnBusinessPay } from './inn-business-pay';

const mockProps = {
  locale: LOCALES.EN,
  token: '123',
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    getLocaleByPathname: () => LOCALES.EN,
    getPathForLocale: () => '/',
    getCountryLanguageByLocale: () => ({ language: 'en' }),
    getWorldlineReturnUrl: (str: string) => str,
    getTranslations: () => ({
      t: (str: string) => str,
    }),
    getSearchParams: () => new URLSearchParams(),
    getCardManagementLabels: () => null,
    getCommonIcons: () => ({}),
    formatIBAssetsUrl: () => '/',
    getServerUnleashToggles: jest.fn().mockResolvedValue({
      [FT_IB_WL_CARD_EDIT]: true,
    }),
    getAccountList: () => [
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ],
    getSelectedAccountHolder: () => ({
      accountName: 'test four',
      accountNumber: '6356290001000112',
      schemeCustomerId: 19000302,
      tetheredGuid: '4abb9835-8996-42bd-8199-5ba0919f106e',
      registrationRoles: ['COST_CENTRE_USER'],
      errorCode: null,
      scheme: 'DE',
    }),
    getAccountInfo: jest.fn().mockResolvedValue({
      status: 'current',
    }),
    getAccountRegistrationRoleDetails: jest.fn(() => ({
      isOnlyCardHolder: false,
    })),
    useTranslation: () => ({
      t: (str: string) => str,
    }),
    getAccessLevel: jest.fn().mockResolvedValue({
      accessLevel: 'SUPER',
      isTravelManager: true,
      isAccountHolder: false,
      selectedAccount: {
        isCardHolder: true,
        isAccountHolder: true,
      },
    }),
    cn: (...inputs: any[]) => inputs.join(' '),
  };
});

jest.mock('next/cache', () => ({
  revalidatePath: (path: string) => {
    return path;
  },
}));

jest.mock('~components/innBusiness/CardStatus', () => ({
  CardStatus: () => null,
}));

jest.mock('~components/innBusiness/DataTable', () => ({
  DataTable: ({ columns, getFilters }: any) => {
    getFilters();
    columns.forEach((column: any) => {
      if (column.render) {
        column.render('', {});
      }
    });
    return null;
  },
}));

jest.mock('~components/innBusiness/AccountHolder/account-holder', () => ({
  AccountHolder: () => null,
}));

jest.mock('~components/innBusiness/SuspendedNotification', () => ({
  __esModule: true,
  default: () => <div data-testid="Notifications-AccountSuspended" />,
}));

jest.mock('~components/innBusiness/DownloadButton/download-button', () => ({
  DownloadButton: () => null,
}));

jest.mock('./inn-business-pay-filters', () => ({
  InnBusinessPayFilters: () => null,
}));

jest.mock('next/headers', () => ({
  headers: () => ({
    get: () => 'test',
  }),
}));

describe('InnBusinessPay Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render InnBusinessPay component', async () => {
    const { getByTestId } = render(await InnBusinessPay(mockProps));
    expect(getByTestId('InnBusinessPayTab-container')).toBeInTheDocument();
    expect(getByTestId('InnBusinessPayTab-add-card-button')).toBeInTheDocument();
  });

  it('should render InnBusinessPay component with all features for Travel Manager + Account Holder', async () => {
    (getAccessLevel as jest.Mock).mockResolvedValueOnce({
      accessLevel: 'SUPER',
      isTravelManager: true,
      isAccountHolder: true,
      selectedAccount: {
        isCardHolder: false,
        isAccountHolder: true,
      },
    });

    const { getByTestId } = render(await InnBusinessPay(mockProps));
    expect(getByTestId('InnBusinessPayTab-container')).toBeInTheDocument();
    expect(getByTestId('InnBusinessPayTab-add-card-button')).toBeInTheDocument();
  });

  it('should hide Add Card button for Card Holder without Travel Manager role', async () => {
    (getAccessLevel as jest.Mock).mockResolvedValueOnce({
      accessLevel: 'SUPER',
      isTravelManager: false,
      isAccountHolder: false,
      selectedAccount: {
        isCardHolder: false,
        isAccountHolder: false,
      },
    });

    const { queryByTestId } = render(await InnBusinessPay(mockProps));
    expect(queryByTestId('InnBusinessPayTab-add-card-button')).not.toBeInTheDocument();
  });

  it('should hide Add Card button for Card Holder with Travel Manager role', async () => {
    (getAccessLevel as jest.Mock).mockResolvedValueOnce({
      accessLevel: 'SUPER',
      isTravelManager: true,
      isAccountHolder: false,
      selectedAccount: {
        isCardHolder: true,
        isAccountHolder: false,
      },
    });

    const { queryByTestId } = render(await InnBusinessPay(mockProps));
    expect(queryByTestId('InnBusinessPayTab-add-card-button')).not.toBeInTheDocument();
  });

  it('should disable Add Card button when account is suspended', async () => {
    (getAccessLevel as jest.Mock).mockResolvedValueOnce({
      accessLevel: 'SUPER',
      isTravelManager: true,
      isAccountHolder: true,
      selectedAccount: {
        isCardHolder: false,
        isAccountHolder: true,
      },
    });
    const { getAccountInfo } = jest.requireMock('@whitbread-eos/utils/server');
    getAccountInfo.mockResolvedValueOnce({ status: 'stop' });

    const { getByTestId } = render(await InnBusinessPay(mockProps));
    expect(getByTestId('InnBusinessPayTab-add-card-button')).toBeDisabled();
  });
});
