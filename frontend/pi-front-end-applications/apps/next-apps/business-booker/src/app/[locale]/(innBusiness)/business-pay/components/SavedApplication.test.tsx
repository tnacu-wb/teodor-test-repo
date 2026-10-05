import '@testing-library/jest-dom';
import { render, waitFor, fireEvent, act } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { removeParticipant } from '@whitbread-eos/utils/server';

import { SavedApplication } from './SavedApplication';

jest.mock('next/navigation', () => ({
  usePathname: jest.fn(() => '/'),
  useRouter: jest.fn(() => ({
    push: jest.fn(),
    refresh: jest.fn(),
  })),
}));

jest.mock('../../../(innBusiness)/manage/cards/components/revalidate-link', () => ({
  revalidateCacheOnLink: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    formatIBAssetsUrl: jest.fn((url) => url),
    useTranslation: jest.fn(() => ({
      t: jest.fn((key) => key),
    })),
    getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
    getEmployees: jest.fn(),
    shareApplication: jest.fn(),
    removeParticipant: jest.fn(),
  };
});

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({ children, onClick, ...props }: { children: React.ReactNode; onClick: any }) => (
    <button onClick={onClick} {...props}>
      {children}
    </button>
  ),
  FormPeoplePicker: ({ handleEmployeeChange, ...props }: { handleEmployeeChange: any }) => (
    <input
      {...props}
      data-testid="PeoplePicker-Form-Input"
      onChange={() =>
        handleEmployeeChange &&
        handleEmployeeChange({
          value: 'employee1',
          label: 'Test Employee',
          employeeData: {
            emailAddress: 'employee@example.com',
            title: 'Mr',
            firstName: 'Test',
            lastName: 'Employee',
          },
        })
      }
    />
  ),
  Notification: ({ message }: { message: string }) => <div>{message}</div>,
  SanitizedContent: ({ children }: { children: React.ReactNode; replacements: any }) => (
    <div>{children}</div>
  ),
}));

jest.mock('date-fns', () => ({
  format: jest.fn(() => '01/01/2023'),
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

const mockProps = {
  icons: {
    'icon.checkmark-white-purple': 'checkmark-icon.svg',
    'icon.notification.alert': 'alert-icon.svg',
    'icon.notification.error': 'error-icon.svg',
    'icon.share.application': 'share-icon.svg',
  },
  participants: [
    {
      participantId: 1,
      initiator: true,
      email: 'initiator@example.com',
    },
    {
      participantId: 2,
      initiator: false,
      email: 'shared@example.com',
      shared: '2023-01-01T12:00:00Z',
    },
  ],
  companyName: 'Test Company',
  applicationReference: 123414,
  startedBy: 'test@example.com',
  companyId: 'COMP_123',
  locale: LOCALES.EN,
  applicationGuid: 'GUID_123',
  applicationId: 'APP_123',
  token: 'TOKEN_123',
  showShareAppWithColleague: true,
};

const mockPropsWithMaxParticipants = {
  ...mockProps,
  participants: [
    {
      participantId: 1,
      initiator: true,
      email: 'initiator@example.com',
    },
    {
      participantId: 2,
      initiator: false,
      email: 'shared1@example.com',
      shared: '2023-01-01T12:00:00Z',
    },
    {
      participantId: 3,
      initiator: false,
      email: 'shared2@example.com',
      shared: '2023-01-02T12:00:00Z',
    },
    {
      participantId: 4,
      initiator: false,
      email: 'shared3@example.com',
      shared: '2023-01-03T12:00:00Z',
    },
    {
      participantId: 5,
      initiator: false,
      email: 'shared4@example.com',
      shared: '2023-01-04T12:00:00Z',
    },
    {
      participantId: 6,
      initiator: false,
      email: 'shared5@example.com',
      shared: '2023-01-05T12:00:00Z',
    },
  ],
};

describe('SavedApplication component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render SavedApplication component', () => {
    const { getByTestId } = render(<SavedApplication {...mockProps} />);
    const container = getByTestId('SavedApplication-container');
    expect(container).toBeInTheDocument();
  });

  it('should render SavedApplication component and click to back to home', async () => {
    const { getByTestId } = render(<SavedApplication {...mockProps} />);
    const container = getByTestId('SavedApplication-container');
    const backToHomeButton = getByTestId('SavedApplication-back-to-home');
    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');

    expect(container).toBeInTheDocument();
    expect(backToHomeButton).toBeInTheDocument();

    await act(async () => {
      peoplePickerInput.focus();
      peoplePickerInput.blur();
    });

    await waitFor(async () => {
      fireEvent.click(backToHomeButton);
    });
  });

  it('should display shared participants section when non-initiator participants exist', () => {
    const { getByTestId } = render(<SavedApplication {...mockProps} />);

    const sharedTitleContainer = getByTestId('SavedApplication-shared-title-container');
    expect(sharedTitleContainer).toBeInTheDocument();

    const participantEmail = getByTestId('SavedApplication-participant-2-mail');
    expect(participantEmail).toHaveTextContent('shared@example.com');

    const removeButton = getByTestId('SavedApplication-delete-shared-link-2');
    expect(removeButton).toBeInTheDocument();
  });

  it('should show share form when showShareAppWithColleague is true', () => {
    const { queryByTestId } = render(<SavedApplication {...mockProps} />);

    const shareWithColleagueForm = queryByTestId('SavedApplication-form-share-with-colleague');
    expect(shareWithColleagueForm).toBeInTheDocument();
  });

  it('should hide share form when showShareAppWithColleague is false', () => {
    const updatedProps = {
      ...mockProps,
      showShareAppWithColleague: false,
    };
    const { queryByTestId } = render(<SavedApplication {...updatedProps} />);

    const shareWithColleagueForm = queryByTestId('SavedApplication-form-share-with-colleague');
    expect(shareWithColleagueForm).not.toBeInTheDocument();
  });

  it('should show max participants notification and hide share form when max shared participants is reached', () => {
    const { getByTestId, queryByTestId } = render(
      <SavedApplication {...mockPropsWithMaxParticipants} />
    );

    const maxLimitNotification = getByTestId('SavedApplication-notification-limit-5-users');
    expect(maxLimitNotification).toBeInTheDocument();

    const shareWithColleagueForm = queryByTestId('SavedApplication-form-share-with-colleague');
    expect(shareWithColleagueForm).not.toBeInTheDocument();

    for (let i = 2; i <= 6; i++) {
      const participant = getByTestId(`SavedApplication-participant-${i}`);
      expect(participant).toBeInTheDocument();
    }
  });

  it('should handle removing a participant successfully', async () => {
    const { getByTestId } = render(<SavedApplication {...mockProps} />);

    const removeButton = getByTestId('SavedApplication-delete-shared-link-2');
    expect(removeButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(removeButton);
    });

    expect(removeParticipant).toHaveBeenCalledWith('APP_123', 'GUID_123', '2', 'TOKEN_123');
  });
});
