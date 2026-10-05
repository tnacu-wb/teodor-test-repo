import '@testing-library/jest-dom';
import { render, fireEvent, act, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import {
  removeParticipant,
  getEmployeesWithFilteringOptions,
  shareApplication,
} from '@whitbread-eos/utils/server';

import { BaseApplicationComponent } from './BaseApplicationComponent';

const mockPush = jest.fn();
const mockRefresh = jest.fn();
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: mockPush,
    refresh: mockRefresh,
  })),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  formatIBAssetsUrl: jest.fn((url) => url),
  useTranslation: jest.fn(() => ({
    t: jest.fn((key) => key),
  })),
  getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
  getEmployeesWithFilteringOptions: jest.fn(),
  shareApplication: jest.fn(),
  removeParticipant: jest.fn(),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({
    children,
    onClick,
    disabled,
    ...props
  }: {
    children: React.ReactNode;
    onClick?: any;
    disabled?: boolean;
  }) => (
    <button onClick={onClick} disabled={disabled} {...props}>
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
  Notification: ({ message, type }: { message: any; type: string }) => (
    <div data-testid="notification" data-type={type}>
      {message}
    </div>
  ),
  SanitizedContent: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="sanitized-content">{children}</div>
  ),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ alt, ...props }: any) => <img alt={alt} {...props} />,
}));

jest.mock('next/link', () => ({
  __esModule: true,
  default: ({ children, href, ...props }: any) => (
    <a href={href} {...props}>
      {children}
    </a>
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

const mockRemoveParticipant = removeParticipant as jest.MockedFunction<typeof removeParticipant>;
const mockGetEmployees = getEmployeesWithFilteringOptions as jest.MockedFunction<
  typeof getEmployeesWithFilteringOptions
>;
const mockShareApplication = shareApplication as jest.MockedFunction<typeof shareApplication>;

const mockProps = {
  companyName: 'Test Company Ltd',
  applicationReference: 123456,
  startedBy: 'john.doe@test.com',
  companyId: 'COMP_123',
  icons: {
    'icon.notification.alert': 'alert-icon.svg',
    'icon.notification.error': 'error-icon.svg',
    'icon.share.application': 'share-icon.svg',
  },
  participants: [
    {
      participantId: 1,
      initiator: true,
      email: 'john.doe@test.com',
    },
    {
      participantId: 2,
      initiator: false,
      email: 'shared@example.com',
      shared: '2023-01-01T12:00:00Z',
    },
  ],
  locale: LOCALES.EN,
  applicationGuid: 'GUID_123',
  applicationId: 'APP_123',
  token: 'TOKEN_123',
  titleSection: <div data-testid="custom-title">Custom Title</div>,
  bottomButton: <button data-testid="custom-bottom-button">Custom Button</button>,
  showResumeLink: false,
  baseDataTestId: 'TestComponent',
  showShareAppWithColleague: true,
};

const mockPropsWithMaxParticipants = {
  ...mockProps,
  participants: [
    {
      participantId: 1,
      initiator: true,
      email: 'john.doe@test.com',
    },
    ...Array.from({ length: 5 }, (_, i) => ({
      participantId: i + 2,
      initiator: false,
      email: `shared${i + 1}@example.com`,
      shared: '2023-01-01T12:00:00Z',
    })),
  ],
};

describe('BaseApplicationComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockPush.mockClear();
    mockRefresh.mockClear();
    mockRemoveParticipant.mockClear();
    mockGetEmployees.mockClear();
    mockShareApplication.mockClear();
  });

  it('should render BaseApplicationComponent with basic props', () => {
    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const container = getByTestId('TestComponent-container');
    expect(container).toBeInTheDocument();

    const customTitle = getByTestId('custom-title');
    expect(customTitle).toBeInTheDocument();

    const customBottomButton = getByTestId('custom-bottom-button');
    expect(customBottomButton).toBeInTheDocument();
  });

  it('should display application details correctly', () => {
    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const companyName = getByTestId('TestComponent-application-details-company-name');
    expect(companyName).toHaveTextContent('Test Company Ltd');

    const applicationReference = getByTestId('TestComponent-application-details-reference');
    expect(applicationReference).toBeInTheDocument();

    const startedBy = getByTestId('TestComponent-application-details-started-by');
    expect(startedBy).toBeInTheDocument();
  });

  it('should display shared participants section when non-initiator participants exist', () => {
    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const sharedTitleContainer = getByTestId('TestComponent-shared-title-container');
    expect(sharedTitleContainer).toBeInTheDocument();

    const participantEmail = getByTestId('TestComponent-participant-2-mail');
    expect(participantEmail).toHaveTextContent('shared@example.com');

    const removeButton = getByTestId('TestComponent-delete-shared-link-2');
    expect(removeButton).toBeInTheDocument();
  });

  it('should show max participants notification when limit is reached', () => {
    const { getByTestId, queryByTestId } = render(
      <BaseApplicationComponent {...mockPropsWithMaxParticipants} />
    );

    const maxLimitNotification = getByTestId('TestComponent-notification-limit-5-users');
    expect(maxLimitNotification).toBeInTheDocument();

    const shareWithColleagueForm = queryByTestId('TestComponent-form-share-with-colleague');
    expect(shareWithColleagueForm).not.toBeInTheDocument();
  });

  it('should display share with colleague form when under max participants', () => {
    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const shareWithColleagueForm = getByTestId('TestComponent-form-share-with-colleague');
    expect(shareWithColleagueForm).toBeInTheDocument();

    const shareColleagueTitle = getByTestId('TestComponent-share-colleague-title');
    expect(shareColleagueTitle).toBeInTheDocument();

    const peoplePickerContainer = getByTestId('TestComponent-people-picker-container');
    expect(peoplePickerContainer).toBeInTheDocument();
  });

  it('should remove participant successfully', async () => {
    mockRemoveParticipant.mockResolvedValue({});

    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const removeButton = getByTestId('TestComponent-delete-shared-link-2');

    await act(async () => {
      fireEvent.click(removeButton);
    });

    await waitFor(() => {
      expect(mockRemoveParticipant).toHaveBeenCalledWith('APP_123', 'GUID_123', '2', 'TOKEN_123');
      expect(mockRefresh).toHaveBeenCalled();
    });
  });

  it('should show resume link when showResumeLink is true', () => {
    const propsWithResumeLink = {
      ...mockProps,
      showResumeLink: true,
    };

    const { getByTestId } = render(<BaseApplicationComponent {...propsWithResumeLink} />);

    const resumeLinkContainer = getByTestId('TestComponent-resume-application-link-container');
    expect(resumeLinkContainer).toBeInTheDocument();

    const resumeLink = getByTestId('TestComponent-resume-application-link');
    expect(resumeLink).toBeInTheDocument();
  });

  it('should not show resume link when showResumeLink is false', () => {
    const { queryByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const resumeLinkContainer = queryByTestId('TestComponent-resume-application-link-container');
    expect(resumeLinkContainer).not.toBeInTheDocument();
  });

  it('should disable share button when no employee is selected', () => {
    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const shareButton = getByTestId('TestComponent-share-app');
    expect(shareButton).toBeDisabled();
  });

  it('should not show share app form when showShareAppWithColleague is false', () => {
    const updatedProps = {
      ...mockProps,
      showShareAppWithColleague: false,
    };
    const { queryByTestId } = render(<BaseApplicationComponent {...updatedProps} />);
    const shareWithColleagueForm = queryByTestId(
      'TestComponent-resume-application-form-share-with-colleague'
    );
    expect(shareWithColleagueForm).not.toBeInTheDocument();
  });

  it('should render notification with correct props', () => {
    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const notification = getByTestId('notification');
    expect(notification).toBeInTheDocument();
    expect(notification).toHaveAttribute('data-type', 'warning');
  });

  it('should handle all participants when none are shared', () => {
    const propsWithNoSharedParticipants = {
      ...mockProps,
      participants: [
        {
          participantId: 1,
          initiator: true,
          email: 'john.doe@test.com',
        },
      ],
    };

    const { queryByTestId } = render(
      <BaseApplicationComponent {...propsWithNoSharedParticipants} />
    );

    const sharedTitleContainer = queryByTestId('TestComponent-shared-title-container');
    expect(sharedTitleContainer).not.toBeInTheDocument();
  });

  it('should handle employee selection', async () => {
    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');

    await act(async () => {
      fireEvent.change(peoplePickerInput, { target: { value: 'Test Employee' } });
    });

    expect(getByTestId('TestComponent-container')).toBeInTheDocument();
  });

  it('should handle sharing when employee email already exists', async () => {
    const propsWithExistingEmail = {
      ...mockProps,
      participants: [
        {
          participantId: 1,
          initiator: true,
          email: 'john.doe@test.com',
        },
        {
          participantId: 2,
          initiator: false,
          email: 'employee@example.com',
          shared: '2023-01-01T12:00:00Z',
        },
      ],
    };

    const { getByTestId } = render(<BaseApplicationComponent {...propsWithExistingEmail} />);

    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');
    const shareButton = getByTestId('TestComponent-share-app');

    await act(async () => {
      fireEvent.change(peoplePickerInput, { target: { value: 'Test Employee' } });
    });

    await act(async () => {
      fireEvent.click(shareButton);
    });

    await waitFor(() => {
      expect(mockGetEmployees).not.toHaveBeenCalled();
      expect(mockShareApplication).not.toHaveBeenCalled();
    });
  });

  it('should share the application successfully for a valid employee', async () => {
    mockGetEmployees.mockResolvedValue({
      employees: [
        {
          emailAddress: 'employee@example.com',
          employeeId: '42',
        },
      ],
    });
    mockShareApplication.mockResolvedValue({
      status: 'SUCCESS',
    });

    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);
    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');
    const shareButton = getByTestId('TestComponent-share-app');

    await act(async () => {
      fireEvent.change(peoplePickerInput, { target: { value: 'Test Employee' } });
    });

    await waitFor(() => {
      expect(shareButton).not.toBeDisabled();
    });

    await act(async () => {
      fireEvent.click(shareButton);
    });

    await waitFor(() => {
      expect(mockGetEmployees).toHaveBeenCalledWith(
        'COMP_123',
        20,
        'TOKEN_123',
        1,
        'employee@example.com',
        '',
        undefined,
        false,
        false
      );
      expect(mockShareApplication).toHaveBeenCalledWith(
        'APP_123',
        'GUID_123',
        42,
        'TOKEN_123',
        'employee@example.com',
        'Mr Test Employee'
      );
    });

    await waitFor(() => {
      expect(mockRefresh).toHaveBeenCalled();
      expect(shareButton).toBeDisabled();
    });
  });

  it('should log an error when no employees are returned from the lookup', async () => {
    mockGetEmployees.mockResolvedValue({
      employees: [],
    });
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => undefined);

    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);
    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');
    const shareButton = getByTestId('TestComponent-share-app');

    await act(async () => {
      fireEvent.change(peoplePickerInput, { target: { value: 'Test Employee' } });
    });
    await waitFor(() => {
      expect(shareButton).not.toBeDisabled();
    });

    await act(async () => {
      fireEvent.click(shareButton);
    });

    await waitFor(() => {
      expect(mockGetEmployees).toHaveBeenCalled();
      expect(mockShareApplication).not.toHaveBeenCalled();
      expect(consoleSpy).toHaveBeenCalledWith('Error sharing application:', expect.any(Error));
      expect(mockRefresh).toHaveBeenCalled();
    });

    consoleSpy.mockRestore();
  });

  it('should stop sharing when the employee id is null', async () => {
    mockGetEmployees.mockResolvedValue({
      employees: [
        {
          emailAddress: 'employee@example.com',
          employeeId: null,
        },
      ],
    });

    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);
    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');
    const shareButton = getByTestId('TestComponent-share-app');

    await act(async () => {
      fireEvent.change(peoplePickerInput, { target: { value: 'Test Employee' } });
    });
    await waitFor(() => {
      expect(shareButton).not.toBeDisabled();
    });

    await act(async () => {
      fireEvent.click(shareButton);
    });

    await waitFor(() => {
      expect(mockShareApplication).not.toHaveBeenCalled();
      expect(mockRefresh).toHaveBeenCalled();
    });
  });

  it('should not share when the employee id cannot be converted to a number', async () => {
    mockGetEmployees.mockResolvedValue({
      employees: [
        {
          emailAddress: 'employee@example.com',
          employeeId: 'not-a-number',
        },
      ],
    });

    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);
    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');
    const shareButton = getByTestId('TestComponent-share-app');

    await act(async () => {
      fireEvent.change(peoplePickerInput, { target: { value: 'Test Employee' } });
    });
    await waitFor(() => {
      expect(shareButton).not.toBeDisabled();
    });

    await act(async () => {
      fireEvent.click(shareButton);
    });

    await waitFor(() => {
      expect(mockShareApplication).not.toHaveBeenCalled();
      expect(mockRefresh).toHaveBeenCalled();
    });
  });

  it('should handle sharing error path coverage', async () => {
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);
    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');

    await act(async () => {
      fireEvent.change(peoplePickerInput, { target: { value: 'Test Employee' } });
    });

    expect(consoleSpy).not.toHaveBeenCalled();
    consoleSpy.mockRestore();
  });

  it('should handle remove participant error', async () => {
    mockRemoveParticipant.mockRejectedValue(new Error('Remove Error'));
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const removeButton = getByTestId('TestComponent-delete-shared-link-2');

    await act(async () => {
      fireEvent.click(removeButton);
    });

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith('Error removing participant:', expect.any(Error));
      expect(mockRefresh).toHaveBeenCalled();
    });

    consoleSpy.mockRestore();
  });

  it('should handle participant without shared date', () => {
    const propsWithoutSharedDate = {
      ...mockProps,
      participants: [
        {
          participantId: 1,
          initiator: true,
          email: 'john.doe@test.com',
        },
        {
          participantId: 2,
          initiator: false,
          email: 'shared@example.com',
          shared: undefined,
        },
      ],
    };

    const { getByTestId } = render(<BaseApplicationComponent {...propsWithoutSharedDate} />);

    const participantEmail = getByTestId('TestComponent-participant-2-mail');
    expect(participantEmail).toHaveTextContent('shared@example.com');
  });

  it('should not try to share when no selected employee', async () => {
    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const shareButton = getByTestId('TestComponent-share-app');

    await act(async () => {
      fireEvent.click(shareButton);
    });

    expect(mockGetEmployees).not.toHaveBeenCalled();
    expect(mockShareApplication).not.toHaveBeenCalled();
  });

  it('should not try to share when selected employee has no employeeData', async () => {
    const MockFormPeoplePicker = ({
      handleEmployeeChange,
      ...props
    }: {
      handleEmployeeChange: any;
    }) => (
      <input
        {...props}
        data-testid="PeoplePicker-Form-Input"
        onChange={() => {
          if (handleEmployeeChange) {
            handleEmployeeChange({
              value: 'employee1',
              label: 'Test Employee',
              employeeData: null,
            });
          }
        }}
      />
    );

    const originalMock = jest.requireMock('@whitbread-eos/atoms/ui').FormPeoplePicker;
    jest.requireMock('@whitbread-eos/atoms/ui').FormPeoplePicker = MockFormPeoplePicker;

    const { getByTestId } = render(<BaseApplicationComponent {...mockProps} />);

    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');
    const shareButton = getByTestId('TestComponent-share-app');

    await act(async () => {
      fireEvent.change(peoplePickerInput, { target: { value: 'Test Employee' } });
    });

    await act(async () => {
      fireEvent.click(shareButton);
    });

    expect(mockGetEmployees).not.toHaveBeenCalled();
    expect(mockShareApplication).not.toHaveBeenCalled();

    jest.requireMock('@whitbread-eos/atoms/ui').FormPeoplePicker = originalMock;
  });

  it('should handle remove participant with undefined participantId', async () => {
    const propsWithUndefinedId = {
      ...mockProps,
      participants: [
        {
          participantId: 1,
          initiator: true,
          email: 'john.doe@test.com',
        },
        {
          participantId: undefined,
          initiator: false,
          email: 'shared@example.com',
          shared: '2023-01-01T12:00:00Z',
        },
      ],
    };

    const { getByTestId } = render(<BaseApplicationComponent {...propsWithUndefinedId} />);

    const removeButton = getByTestId('TestComponent-delete-shared-link-undefined');

    await act(async () => {
      fireEvent.click(removeButton);
    });

    expect(mockRemoveParticipant).not.toHaveBeenCalled();
  });
});
