import '@testing-library/jest-dom';
import { fireEvent, render } from '@testing-library/react';
import { act } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { ResetState, ResetStep } from '../types';
import { ResetPasswordForm } from './reset-password-form';

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: ResetStep.RESET_FORM,
  steps: [
    {
      id: ResetStep.RESET_FORM,
      component: <ResetPasswordForm locale={LOCALES.EN} icons={{}} />,
    },
  ],
};

const pushMock = jest.fn();

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: () => pushMock(),
  }),
}));

const resetPasswordMock = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => {
  const actual = jest.requireActual('@whitbread-eos/utils/server');

  return {
    findError: actual.findError,
    getCountryLanguageByLocale: actual.getCountryLanguageByLocale,
    formatIBAssetsUrl: actual.formatIBAssetsUrl,
    cn: jest.fn(),
    useTranslation: () => ({
      t: (key: string) => key,
    }),
    getPathForLocale: (locale: string, path: string) => path,
    resetPassword: (...args: unknown[]) => resetPasswordMock(...args),
  };
});

const mockContext = {
  wizardState: {
    email: 'test@test.com',
    passwordToken: 'token',
    isInvalidKey: false,
  } as ResetState,
};

jest.mock('@whitbread-eos/layout', () => {
  return {
    ...jest.requireActual('@whitbread-eos/layout'),
    useWizardContext: () => mockContext,
  };
});

const toastMock = jest.fn();

jest.mock('@whitbread-eos/atoms/ui', () => {
  const actual = jest.requireActual('@whitbread-eos/atoms/ui');

  return {
    ...actual,
    useToast: () => ({
      toast: () => toastMock(),
    }),
  };
});

describe('ResetPasswordForm', () => {
  beforeEach(() => {
    mockContext.wizardState = {
      email: 'test@test.com',
      passwordToken: 'token',
      isInvalidKey: false,
    };
  });

  it('should render without crashing', () => {
    const { getByTestId, queryByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('ResetPasswordForm-wrapper')).toBeInTheDocument();
    expect(getByTestId('ResetPasswordForm-title')).toBeInTheDocument();
    expect(getByTestId('ResetPasswordForm-description')).toBeInTheDocument();
    expect(getByTestId('ResetPasswordForm-password-Form-Input')).toBeInTheDocument();
    expect(getByTestId('ResetPasswordForm-rules')).toBeInTheDocument();
    expect(getByTestId('ResetPasswordForm-confirmPassword-Form-Input')).toBeInTheDocument();
    expect(getByTestId('ResetPasswordForm-savePassword')).toBeInTheDocument();

    expect(queryByTestId('ResetPasswordForm-errorMessage')).not.toBeInTheDocument();
  });

  it('should call savePassword', async () => {
    resetPasswordMock.mockResolvedValue(true);

    const { getByTestId, queryByTestId } = render(<Wizard {...mockProps} />);

    const savePasswordButton = getByTestId('ResetPasswordForm-savePassword');
    const passwordInput = getByTestId('ResetPasswordForm-password-Form-Input');
    const confirmPasswordInput = getByTestId('ResetPasswordForm-confirmPassword-Form-Input');

    await act(() => {
      fireEvent.change(passwordInput, { target: { value: 'ABCabc123' } });
      fireEvent.change(confirmPasswordInput, { target: { value: 'ABCabc123' } });

      savePasswordButton.click();
    });

    expect(resetPasswordMock).toBeCalledWith('token', {
      customerId: 'test@test.com',
      newPassword: 'ABCabc123',
    });
    expect(pushMock).toBeCalled();
    expect(toastMock).toBeCalled();

    expect(queryByTestId('ResetPasswordForm-errorMessage')).not.toBeInTheDocument();
  });

  it('should show alert when backend responds with false', async () => {
    resetPasswordMock.mockResolvedValue(false);

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const savePasswordButton = getByTestId('ResetPasswordForm-savePassword');
    const passwordInput = getByTestId('ResetPasswordForm-password-Form-Input');
    const confirmPasswordInput = getByTestId('ResetPasswordForm-confirmPassword-Form-Input');

    await act(() => {
      fireEvent.change(passwordInput, { target: { value: 'ABCabc123' } });
      fireEvent.change(confirmPasswordInput, { target: { value: 'ABCabc123' } });

      savePasswordButton.click();
    });

    expect(getByTestId('ResetPasswordForm-errorMessage')).toBeInTheDocument();
  });

  it('should show alert when backend responds with error', async () => {
    resetPasswordMock.mockRejectedValue(new Error());

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const savePasswordButton = getByTestId('ResetPasswordForm-savePassword');
    const passwordInput = getByTestId('ResetPasswordForm-password-Form-Input');
    const confirmPasswordInput = getByTestId('ResetPasswordForm-confirmPassword-Form-Input');

    await act(() => {
      fireEvent.change(passwordInput, { target: { value: 'ABCabc123' } });
      fireEvent.change(confirmPasswordInput, { target: { value: 'ABCabc123' } });

      savePasswordButton.click();
    });

    expect(getByTestId('ResetPasswordForm-errorMessage')).toBeInTheDocument();
  });

  it('should restrict access if isInvalidKey flag is true', async () => {
    mockContext.wizardState.isInvalidKey = true;

    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('ResetPasswordForm-invalidKeyError')).toBeInTheDocument();

    expect(getByTestId('ResetPasswordForm-password-Form-Input')).toBeDisabled();
    expect(getByTestId('ResetPasswordForm-confirmPassword-Form-Input')).toBeDisabled();
    expect(getByTestId('ResetPasswordForm-savePassword')).toBeDisabled();
  });
});
