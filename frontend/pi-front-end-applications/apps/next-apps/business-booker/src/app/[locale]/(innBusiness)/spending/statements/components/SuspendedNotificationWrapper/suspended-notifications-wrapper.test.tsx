import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { PayAccountStatus } from '@whitbread-eos/api';

import SuspendedNotificationsWrapper, {
  SuspendedNotificationsWrapperSkeleton,
  getAccountSuspensionStatus,
} from './suspended-notifications-wrapper';

jest.mock('~components/innBusiness/SuspendedNotification', () => ({
  __esModule: true,
  default: jest.fn(({ isShown }) =>
    isShown ? (
      <div data-testid="mocked-suspended-notification">Mocked SuspendedNotification</div>
    ) : null
  ),
}));

const getAccountInfoMock = jest.fn().mockResolvedValue({
  status: PayAccountStatus.Suspended,
});
const getAccountRegistrationRoleDetailsMock = jest.fn(() => ({
  isOnlyCardHolder: false,
  isOnlyFinanceUser: false,
  isCardHolderAndFinanceUser: false,
}));
jest.mock('@whitbread-eos/utils/server', () => ({
  getAccountInfo: () => getAccountInfoMock(),
  getAccountRegistrationRoleDetails: () => getAccountRegistrationRoleDetailsMock(),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Skeleton: jest.fn(({ className }) => <div className={className} />),
}));

describe('SuspendedNotificationsWrapper', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    window.analyticsData = {};
    getAccountInfoMock.mockResolvedValue({
      status: PayAccountStatus.Suspended,
    });
    getAccountRegistrationRoleDetailsMock.mockReturnValue({
      isOnlyCardHolder: false,
      isOnlyFinanceUser: false,
      isCardHolderAndFinanceUser: false,
    });
  });
  it('renders correctly when account is suspended', async () => {
    const { getByTestId } = render(
      await SuspendedNotificationsWrapper({
        account: {
          scheme: 'test-scheme',
          tetheredGuid: 'test-guid',
        },
        locale: 'en',
      } as any)
    );

    expect(getAccountInfoMock).toHaveBeenCalled();
    expect(window.analyticsData.validation).not.toBeUndefined();
    expect(getByTestId('mocked-suspended-notification')).toHaveTextContent(
      'Mocked SuspendedNotification'
    );
  });

  it('not shown when account is not suspended', async () => {
    getAccountInfoMock.mockResolvedValueOnce({
      status: PayAccountStatus.Active,
    });

    const { queryByTestId } = render(
      await SuspendedNotificationsWrapper({
        account: {
          scheme: 'test-scheme',
          tetheredGuid: 'test-guid',
        },
        locale: 'en',
      } as any)
    );

    expect(getAccountInfoMock).toHaveBeenCalled();
    expect(window.analyticsData.validation).toBeUndefined();
    expect(queryByTestId('mocked-suspended-notification')).not.toBeInTheDocument();
  });

  it('uses provided suspension flag without fetching status', async () => {
    const { getByTestId } = render(
      await SuspendedNotificationsWrapper({
        account: {
          scheme: 'test-scheme',
          tetheredGuid: 'test-guid',
        },
        locale: 'en',
        isAccountSuspended: true,
      } as any)
    );

    expect(getAccountInfoMock).not.toHaveBeenCalled();
    expect(getByTestId('mocked-suspended-notification')).toBeInTheDocument();
  });
});

describe('SuspendedNotificationsWrapperSkeleton', () => {
  it('renders skeleton correctly', () => {
    const { container } = render(<SuspendedNotificationsWrapperSkeleton />);
    expect(container.getElementsByClassName('h-[6.125rem] w-full')).toHaveLength(1);
  });
});

describe('getAccountSuspensionStatus', () => {
  it('returns false for card holders', async () => {
    getAccountRegistrationRoleDetailsMock.mockReturnValue({
      isOnlyCardHolder: true,
      isOnlyFinanceUser: false,
      isCardHolderAndFinanceUser: false,
    });
    const status = await getAccountSuspensionStatus({
      scheme: 'test-scheme',
      tetheredGuid: 'test-guid',
      registrationRoles: ['CARD_HOLDER'],
    } as any);

    expect(status).toBe(false);
    expect(getAccountInfoMock).not.toHaveBeenCalled();
  });
});
