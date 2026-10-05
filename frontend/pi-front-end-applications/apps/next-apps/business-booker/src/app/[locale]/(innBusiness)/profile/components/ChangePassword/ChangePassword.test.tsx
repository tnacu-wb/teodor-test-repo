import '@testing-library/jest-dom';
import { render, fireEvent, waitFor, act } from '@testing-library/react';

import { ChangePassword } from './ChangePassword';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getAuthCookie: jest.fn(() => 'mock-cookie'),
  formatIBAssetsUrl: jest.fn((url) => url),
  updateProfileDetails: jest.fn(),
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
  Button: ({ children, ...props }: any) => <button {...props}>{children}</button>,
  Notification: ({ title, message }: any) => (
    <div>
      <span>{title}</span>
      <span>{message}</span>
    </div>
  ),
  useToast: () => ({
    toast: jest.fn(),
  }),
}));

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    refresh: jest.fn(),
  }),
}));

jest.mock('~components/innBusiness/forms/ChangePasswordForm/ChangePasswordForm', () => ({
  ChangePasswordForm: ({ onSubmit, formRef }: any) => (
    <form
      ref={formRef}
      onSubmit={(e) => {
        e.preventDefault();
        onSubmit({ currentPassword: 'oldPass', newPassword: 'newPass' });
      }}
      data-testid="mock-change-password-form"
    >
      <button type="submit">Submit</button>
    </form>
  ),
}));

const mockProfileDetails = {
  contactDetail: {
    email: 'test@email.com',
    address: {
      line1: '123 Test St',
      street: '123 Test St',
      city: 'Test City',
      postalCode: '12345',
      country: 'Test Country',
      countryCode: 'TC',
    },
    firstName: 'Test',
    lastName: 'User',
    title: 'Mr',
  },
  paymentPreference: {},
  bookingPreference: {},
};

const icons = { 'icon.notification.error': 'error-icon-url' };

const baseProps = {
  baseDataTestId: 'test',
  icons,
  profileDetails: mockProfileDetails,
  onReviewChangesToggle: jest.fn(),
  onIsEditableToggle: jest.fn(),
  onIsUpdatingToggle: jest.fn(),
  onIsDirtyToggle: jest.fn(),
  isEditable: false,
  isUpdating: false,
  isDirty: false,
};

describe('ChangePassword', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders title and edit button', () => {
    const { getByTestId, getByText } = render(<ChangePassword {...baseProps} />);
    expect(getByTestId('test-Change-Password-Title')).toBeInTheDocument();
    expect(getByTestId('test-Change-Password-Button')).toBeInTheDocument();
    expect(getByText('profile.password.changepassword.title')).toBeInTheDocument();
  });

  it('shows ChangePasswordForm when edit button is clicked', () => {
    const { getByTestId } = render(<ChangePassword {...baseProps} />);
    fireEvent.click(getByTestId('test-Change-Password-Button'));
    expect(baseProps.onIsEditableToggle).toHaveBeenCalledWith('changePassword', true);
  });

  it('renders ChangePasswordForm and buttons when isEditable is true', () => {
    const props = { ...baseProps, isEditable: true };
    const { getByTestId } = render(<ChangePassword {...props} />);
    expect(getByTestId('mock-change-password-form')).toBeInTheDocument();
    expect(getByTestId('test-Change-Password-Update-Password')).toBeInTheDocument();
    expect(getByTestId('test-Change-Password-Cancel-update')).toBeInTheDocument();
  });

  it('calls onReviewChangesToggle when cancel is clicked', () => {
    const props = { ...baseProps, isEditable: true, isDirty: true };
    const { getByTestId } = render(<ChangePassword {...props} />);
    fireEvent.click(getByTestId('test-Change-Password-Cancel-update'));
    expect(props.onReviewChangesToggle).toHaveBeenCalledWith('changePassword', true);
  });

  it('doesnt call onReviewChangesToggle when cancel is clicked and is dirty false', () => {
    const props = { ...baseProps, isEditable: true, isDirty: false };
    const { getByTestId } = render(<ChangePassword {...props} />);
    fireEvent.click(getByTestId('test-Change-Password-Cancel-update'));
    expect(props.onReviewChangesToggle).not.toHaveBeenCalledWith('changePassword');
  });

  it('shows error notification if current password does not match', async () => {
    const onIsUpdatingToggle = jest.fn();
    const props = {
      ...baseProps,
      isEditable: true,
      onIsUpdatingToggle,
    };
    const { getByTestId } = render(<ChangePassword {...props} />);
    await act(async () => {
      fireEvent.click(getByTestId('test-Change-Password-Update-Password'));
    });
    await waitFor(() => {
      expect(onIsUpdatingToggle).toHaveBeenCalledWith('changePassword', false);
    });
  });

  it('shows generic error toast on other errors', async () => {
    const onIsUpdatingToggle = jest.fn();
    const props = {
      ...baseProps,
      isEditable: true,
      onIsUpdatingToggle,
    };
    const { getByTestId } = render(<ChangePassword {...props} />);
    await act(async () => {
      fireEvent.click(getByTestId('test-Change-Password-Update-Password'));
    });
    await waitFor(() => {
      expect(onIsUpdatingToggle).toHaveBeenCalledWith('changePassword', false);
    });
  });

  it('disables update button when isUpdating is true', () => {
    const props = { ...baseProps, isEditable: true, isUpdating: true };
    const { getByTestId } = render(<ChangePassword {...props} />);
    expect(getByTestId('test-Change-Password-Update-Password')).toBeDisabled();
  });
});
