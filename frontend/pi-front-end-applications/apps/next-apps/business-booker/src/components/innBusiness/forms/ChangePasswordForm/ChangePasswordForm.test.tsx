import '@testing-library/jest-dom';
import { render, waitFor, fireEvent, act } from '@testing-library/react';

import { ChangePasswordForm } from '~components/innBusiness/forms/ChangePasswordForm/ChangePasswordForm';

const mockProps = {
  icons: {},
  onSubmit: jest.fn(),
  formRef: { current: null },
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    findError: serverUtils.findError,
  };
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('ChangePasswordForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ChangePasswordForm component', async () => {
    const { getByTestId } = render(<ChangePasswordForm {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId(`ChangePassword-form`)).toBeInTheDocument();
    });
  });
  it('should show an error when the new password contains more than two consecutive identical characters', async () => {
    const { getByText, getByTestId } = render(<ChangePasswordForm {...(mockProps as any)} />);

    const newPasswordInput = getByTestId('newPassword-Form-Input');
    const confirmPasswordInput = getByTestId('confirmPassword-Form-Input');

    fireEvent.change(newPasswordInput, { target: { value: 'aaaBBB123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'aaaBBB123' } });

    fireEvent.blur(newPasswordInput);

    await waitFor(() => {
      expect(getByText('password.error.invalid')).toBeInTheDocument();
    });
  });
  it('should show an error when the new password is the same as the current password', async () => {
    const { getByTestId, getByText } = render(<ChangePasswordForm {...(mockProps as any)} />);

    const currentPasswordInput = getByTestId('currentPassword-Form-Input');
    const newPasswordInput = getByTestId('newPassword-Form-Input');
    const confirmPasswordInput = getByTestId('confirmPassword-Form-Input');

    fireEvent.change(currentPasswordInput, { target: { value: 'SamePassword123' } });
    fireEvent.change(newPasswordInput, { target: { value: 'SamePassword123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'DifferentPassword123' } });

    fireEvent.blur(newPasswordInput);

    await waitFor(() => {
      expect(getByText('password.error.same')).toBeInTheDocument();
    });
  });
  it('should show an error when the confirm password is the same as the current password', async () => {
    const { getByTestId, getByText } = render(<ChangePasswordForm {...(mockProps as any)} />);

    const currentPasswordInput = getByTestId('currentPassword-Form-Input');
    const newPasswordInput = getByTestId('newPassword-Form-Input');
    const confirmPasswordInput = getByTestId('confirmPassword-Form-Input');

    fireEvent.change(currentPasswordInput, { target: { value: 'SamePassword123' } });
    fireEvent.change(newPasswordInput, { target: { value: 'DifferentPassword123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'SamePassword123' } });

    fireEvent.blur(confirmPasswordInput);

    await waitFor(() => {
      expect(getByText('password.error.different')).toBeInTheDocument();
    });
  });
  it('should call trigger on currentPassword blur', async () => {
    const { getByTestId } = render(<ChangePasswordForm {...(mockProps as any)} />);
    const currentPasswordInput = getByTestId('currentPassword-Form-Input');

    await act(async () => {
      fireEvent.change(currentPasswordInput, { target: { value: 'SamePassword123' } });
      fireEvent.blur(currentPasswordInput);
    });
    await waitFor(() => {
      expect(currentPasswordInput).toHaveValue('SamePassword123');
    });
  });

  it('should call clearErrors on currentPassword focus', async () => {
    const { getByTestId } = render(<ChangePasswordForm {...(mockProps as any)} />);
    const currentPasswordInput = getByTestId('currentPassword-Form-Input');

    fireEvent.focus(currentPasswordInput);

    fireEvent.change(currentPasswordInput, { target: { value: 'SamePassword123' } });

    await waitFor(() => {
      expect(currentPasswordInput).toHaveValue('SamePassword123');
    });
  });
  it('should call clearErrors on newPassword and confirmPassword focus', async () => {
    const { getByTestId } = render(<ChangePasswordForm {...(mockProps as any)} />);

    const newPasswordInput = getByTestId('newPassword-Form-Input');
    const confirmPasswordInput = getByTestId('confirmPassword-Form-Input');

    fireEvent.focus(newPasswordInput);

    fireEvent.change(newPasswordInput, { target: { value: 'SamePassword123' } });

    fireEvent.focus(confirmPasswordInput);

    fireEvent.change(confirmPasswordInput, { target: { value: 'SamePassword123' } });

    await waitFor(() => {
      expect(newPasswordInput).toHaveValue('SamePassword123');
      expect(confirmPasswordInput).toHaveValue('SamePassword123');
    });
  });
});
