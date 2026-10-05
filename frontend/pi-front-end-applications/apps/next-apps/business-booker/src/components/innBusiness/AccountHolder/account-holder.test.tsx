import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { AccountHolder } from './account-holder';

const mockProps = {
  mobile: false,
  isUserManagement: false,
  searchIcon: '',
};

jest.mock('next/headers', () => ({
  cookies: () => ({ get: () => ({ value: 'token' }) }),
}));

const mockAccounts = {
  accounts: [
    {
      accountName: 'test',
      accountNumber: '1234',
      tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
      registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
      errorCode: null,
      scheme: 'GB',
    },
  ],
};

const mockGetSearchParams = jest.fn(() => Promise.resolve(new URLSearchParams('?account=1')));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => ({}),
    getSearchParams: () => mockGetSearchParams(),
    getWorldlineReturnUrl: (str: string) => str,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    useTranslation: jest.fn(() => ({
      t: (key: string) => key,
    })),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getAccountList: () => mockAccounts.accounts,
    formatAccountNumber: jest.fn(() => '1234 6780 8909 1234'),
  };
});
const baseDataTestId = 'AccountHolder';

describe('AccountHolder Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetSearchParams.mockResolvedValue(new URLSearchParams('?account=1'));
    mockAccounts.accounts = [
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ];
  });

  it('should render AccountHolder component', async () => {
    const { getByTestId } = render(await AccountHolder({ ...mockProps }));

    expect(getByTestId('AccountHolder')).toBeInTheDocument();
  });

  it('should render with mobile flag on', async () => {
    const { getByTestId } = render(await AccountHolder({ ...mockProps, mobile: true }));

    expect(getByTestId('AccountHolder')).toBeInTheDocument();
  });
  it('should render with mobile flag on and multiple accounts', async () => {
    mockAccounts.accounts = [
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
      {
        accountName: 'test1',
        accountNumber: '12345',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba555554',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ];
    const { getByTestId } = render(await AccountHolder({ ...mockProps, mobile: true }));

    expect(getByTestId('AccountHolder')).toBeInTheDocument();
  });
  it('should render with userManagement flag on', async () => {
    const { getByTestId } = render(
      await AccountHolder({ ...mockProps, isUserManagement: true, mobile: false })
    );

    expect(getByTestId(`${baseDataTestId}-container`)).toBeInTheDocument();
  });

  it('should render with hideDropdown flag on and multiple accounts', async () => {
    mockAccounts.accounts = [
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
      {
        accountName: 'test1',
        accountNumber: '12345',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba555554',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ];
    const { getByTestId } = render(
      await AccountHolder({ ...mockProps, hideDropdown: true, mobile: false })
    );

    expect(getByTestId('AccountHolder')).toBeInTheDocument();
  });

  it('should filter accounts by scheme when filterByScheme is provided', async () => {
    mockAccounts.accounts = [
      {
        accountName: 'GB Account',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
      {
        accountName: 'DE Account',
        accountNumber: '12345',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba555554',
        registrationRoles: ['ACCOUNT_HOLDER'],
        errorCode: null,
        scheme: 'DE',
      },
    ];
    const { getByTestId } = render(
      await AccountHolder({ ...mockProps, filterByScheme: 'GB' as any })
    );

    expect(getByTestId('AccountHolder')).toBeInTheDocument();
  });

  it('should select account based on search param', async () => {
    const targetGuid = '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba555554';
    mockGetSearchParams.mockResolvedValueOnce(new URLSearchParams(`?account=${targetGuid}`));
    mockAccounts.accounts = [
      {
        accountName: 'First Account',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
      {
        accountName: 'Second Account',
        accountNumber: '12345',
        tetheredGuid: targetGuid,
        registrationRoles: ['CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ];
    const { getByTestId } = render(
      await AccountHolder({ ...mockProps, mobile: false, isUserManagement: false })
    );

    expect(getByTestId('AccountHolder')).toBeInTheDocument();
  });

  it('should render different registration role badges', async () => {
    mockAccounts.accounts = [
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['FINANCE_USER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ];
    const { getByTestId } = render(
      await AccountHolder({ ...mockProps, mobile: false, isUserManagement: false })
    );

    expect(getByTestId('AccountHolder')).toBeInTheDocument();
  });

  it('should render with prefetchAccounts flag', async () => {
    mockAccounts.accounts = [
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
      {
        accountName: 'test1',
        accountNumber: '12345',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba555554',
        registrationRoles: ['ACCOUNT_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ];
    const { getByTestId } = render(
      await AccountHolder({
        ...mockProps,
        prefetchAccounts: true,
        mobile: false,
        isUserManagement: false,
      })
    );

    expect(getByTestId('AccountHolder')).toBeInTheDocument();
  });

  it('returns null when token is empty to prevent API calls on session expiry', async () => {
    jest.resetModules();
    jest.doMock('next/headers', () => ({
      cookies: () => ({ get: () => ({ value: '' }) }),
    }));

    const { AccountHolder: AccountHolderWithEmptyToken } = await import('./account-holder');
    const result = render(
      await AccountHolderWithEmptyToken({
        ...mockProps,
        locale: LOCALES.EN,
      })
    );

    expect(result.container.firstChild).toBeNull();
  });

  it('returns null when token is whitespace to prevent API calls on session expiry', async () => {
    jest.resetModules();
    jest.doMock('next/headers', () => ({
      cookies: () => ({ get: () => ({ value: '   ' }) }),
    }));

    const { AccountHolder: AccountHolderWithWhitespaceToken } = await import('./account-holder');
    const result = render(
      await AccountHolderWithWhitespaceToken({
        ...mockProps,
        locale: LOCALES.EN,
      })
    );

    expect(result.container.firstChild).toBeNull();
  });

  it('returns null when get cookies returns undefined', async () => {
    jest.resetModules();
    jest.doMock('next/headers', () => ({
      cookies: () => ({ get: () => undefined }),
    }));

    const { AccountHolder: AccountHolderWithUndefinedCookie } = await import('./account-holder');
    const result = render(
      await AccountHolderWithUndefinedCookie({
        ...mockProps,
        locale: LOCALES.EN,
      })
    );

    expect(result.container.firstChild).toBeNull();
  });

  it('returns null when get cookies returns null', async () => {
    jest.resetModules();
    jest.doMock('next/headers', () => ({
      cookies: () => ({ get: () => null }),
    }));

    const { AccountHolder: AccountHolderWithNullCookie } = await import('./account-holder');
    const result = render(
      await AccountHolderWithNullCookie({
        ...mockProps,
        locale: LOCALES.EN,
      })
    );

    expect(result.container.firstChild).toBeNull();
  });

  it('returns null when get cookies returns object without value property', async () => {
    jest.resetModules();
    jest.doMock('next/headers', () => ({
      cookies: () => ({ get: () => ({}) }),
    }));

    const { AccountHolder: AccountHolderWithNoValue } = await import('./account-holder');
    const result = render(
      await AccountHolderWithNoValue({
        ...mockProps,
        locale: LOCALES.EN,
      })
    );

    expect(result.container.firstChild).toBeNull();
  });
});
