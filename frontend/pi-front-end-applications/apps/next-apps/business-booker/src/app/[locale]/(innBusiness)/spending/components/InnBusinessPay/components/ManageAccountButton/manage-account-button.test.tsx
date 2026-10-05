import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import {
  BUSINESS_BOOKER_USER_ROLES,
  RegistrationRole,
  LOCALES,
  Scheme,
  CountryCode,
} from '@whitbread-eos/api';

import ManageAccountButton from './manage-account-button';

// Mock dependencies
jest.mock('@whitbread-eos/utils', () => ({
  getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
  useFeatureToggle: () => ({ FT_IB_PAY_PIBA_EURO: true }),
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  cn: (...args: string[]) => args.join(' '),
  getCountryLanguageByLocale: (locale: LOCALES) => ({
    language: locale === LOCALES.DE ? CountryCode.DE : CountryCode.GB,
  }),
}));
jest.mock('@whitbread-eos/utils/server', () => ({
  appPreCheck: jest.fn(),
}));
jest.mock('next/link', () => {
  const MockLink = ({ children, ...props }: any) => <a {...props}>{children}</a>;
  MockLink.displayName = 'MockNextLink';
  return MockLink;
});
jest.mock('~components/innBusiness/ExistingAccountModal', () => ({
  ExistingAccountModal: (props: any) =>
    props.isModalOpen ? <div data-testid="existing-account-modal" /> : null,
}));
jest.mock('~components/innBusiness/LinkAccountButton', () => ({
  LinkAccountButton: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="link-account-button">{children}</div>
  ),
}));
jest.mock('~components/innBusiness/ResponsiveDropdown', () => ({
  ResponsiveDropdown: ({ children, renderItems, isOpen, toggleOpen, dataTestId }: any) => (
    <div data-testid={dataTestId}>
      <div onClick={() => toggleOpen(!isOpen)}>{children}</div>
      {isOpen && <div data-testid="dropdown-items">{renderItems()}</div>}
    </div>
  ),
  ReponsiveDropdownLinkWrapper: ({ children, shouldCloseDropdown, closeDropdown }: any) => (
    <div onClick={shouldCloseDropdown ? closeDropdown : undefined}>{children}</div>
  ),
}));
jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({ children, ...props }: any) => <button {...props}>{children}</button>,
  WorldlineLink: ({ renderButton }: any) => renderButton(() => null),
}));

const defaultProps = {
  isTravelManager: true,
  locale: 'en' as LOCALES,
  bbUserRole: BUSINESS_BOOKER_USER_ROLES.SUPER,
  account: {
    registrationRoles: [RegistrationRole.AccountHolder],
    tetheredGuid: 'guid-123',
    scheme: 'GB' as Scheme,
  },
  wlReturnUrl: 'http://return.url',
  wlPostUrl: 'http://post.url',
  isAccountSuspended: false,
  token: 'token-123',
};

describe('ManageAccountButton', () => {
  let props: typeof defaultProps & { isBusinessPayManager?: boolean };

  beforeEach(() => {
    props = {
      ...defaultProps,
      account: {
        ...defaultProps.account,
        registrationRoles: [...defaultProps.account.registrationRoles],
      },
    };
  });

  it('renders the button', () => {
    render(<ManageAccountButton {...props} />);
    expect(screen.getByText('spending.summary.manage.account')).toBeInTheDocument();
  });

  it('opens dropdown on button click', () => {
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByTestId('dropdown-items')).toBeInTheDocument();
  });

  it('shows memorable word link for AccountHolder', () => {
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(
      screen.getByText('spending.manage.account.memorable.word.setOrReset')
    ).toBeInTheDocument();
  });

  it('shows edit account link for AccountHolder', () => {
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.accountDetails')).toBeInTheDocument();
  });

  it('shows manage employees link for SUPER user', () => {
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.manageEmployees')).toBeInTheDocument();
  });

  it('should render create account link when isTravelManager is true and user has eligible role', () => {
    props.isTravelManager = true;
    props.isBusinessPayManager = false;
    props.account.registrationRoles = [RegistrationRole.FinanceUser];
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.create')).toBeInTheDocument();
  });

  it('should render create account link when isBusinessPayManager is true and user has eligible role', () => {
    props.isTravelManager = false;
    props.isBusinessPayManager = true;
    props.account.registrationRoles = [RegistrationRole.CardHolder];
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.create')).toBeInTheDocument();
  });

  it('shows manage employees link for BPM manager user', () => {
    props.isTravelManager = false;
    props.isBusinessPayManager = true;
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.manageEmployees')).toBeInTheDocument();
  });

  it('shows manage cards link for SUPER user', () => {
    props.isTravelManager = true;
    props.isBusinessPayManager = false;
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.cardManagement')).toBeInTheDocument();
  });

  it('shows manage cards link for AccountHolder without manager permissions', () => {
    props.isTravelManager = false;
    props.isBusinessPayManager = false;
    props.account.registrationRoles = [RegistrationRole.AccountHolder];
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.cardManagement')).toBeInTheDocument();
    expect(screen.queryByText('spending.manage.account.manageEmployees')).not.toBeInTheDocument();
  });

  it('shows view offers link for AccountHolder', () => {
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.viewOffers')).toBeInTheDocument();
  });

  // it('it renders link account button', () => {
  //   render(<ManageAccountButton {...props} />);
  //   expect(screen.getByTestId('link-account-button')).toBeInTheDocument();
  // });

  it('shows create account link for SUPER user', () => {
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(screen.getByText('spending.manage.account.create')).toBeInTheDocument();
  });

  it('disables button when account is suspended', () => {
    render(<ManageAccountButton {...props} isAccountSuspended={true} />);
    expect(screen.getByRole('button')).toBeDisabled();
  });

  it('renders correctly for FinanceUser role', () => {
    props.account.registrationRoles = [RegistrationRole.FinanceUser];
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(
      screen.getByText('spending.manage.account.memorable.word.setOrReset')
    ).toBeInTheDocument();
    expect(screen.getByText('spending.manage.account.link')).toBeInTheDocument();
  });

  it('renders correctly for CardHolder role', () => {
    props.account.registrationRoles = [RegistrationRole.CardHolder];
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(
      screen.getByText('spending.manage.account.memorable.word.setOrReset')
    ).toBeInTheDocument();
    expect(screen.getByText('spending.manage.account.cardManagement')).toBeInTheDocument();
    expect(screen.getByText('spending.manage.account.link')).toBeInTheDocument();
  });

  it('renders correctly for CostCentreUser role', () => {
    props.account.registrationRoles = [RegistrationRole.CostCentreUser];
    render(<ManageAccountButton {...props} />);
    fireEvent.click(screen.getByRole('button'));
    expect(
      screen.getByText('spending.manage.account.memorable.word.setOrReset')
    ).toBeInTheDocument();
    expect(screen.getByText('spending.manage.account.cardManagement')).toBeInTheDocument();
    expect(screen.queryByText('spending.manage.account.create')).toBeInTheDocument();
    expect(screen.queryByText('spending.manage.account.link')).not.toBeInTheDocument();
  });

  it('does not render links if no account', () => {
    render(<ManageAccountButton {...props} account={null} />);
    fireEvent.click(screen.getByRole('button'));
    expect(
      screen.queryByText('spending.manage.account.memorable.word.setOrReset')
    ).not.toBeInTheDocument();
  });
});
