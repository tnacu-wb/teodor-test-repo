import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { RegistrationCodeForm } from './registration-code-form';

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => [],
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getPathForLocale: () => {
      return '/en-gb/business-pay/register';
    },
    getRegistrationInfo: jest.fn().mockResolvedValue({
      registrationCodeInfo: {
        registrationRole: 'mock-role',
      },
    }),
  };
});

const mockPush = jest.fn();
jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => ({ push: mockPush }),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  FormPage: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="form-page">{children}</div>
  ),
  FormInput: ({ ...props }) => <input {...props} />,
  Button: ({ children, ...props }: { children: React.ReactNode }) => (
    <button {...props}>{children}</button>
  ),
}));

describe('RegistrationCodeForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const defaultProps = {
    icons: { 'icon.notification.error': 'error-icon-url' },
    baseDataTestId: 'login-form',
    locale: LOCALES.EN,
    token: 'mock-token',
  };

  it('renders the RegistrationCodeForm component', () => {
    render(<RegistrationCodeForm {...defaultProps} />);

    expect(screen.getByTestId('form-page')).toBeInTheDocument();
    expect(screen.getByTestId(`${defaultProps.baseDataTestId}-description`)).toBeInTheDocument();
    expect(
      screen.getByTestId(`${defaultProps.baseDataTestId}-description-list`)
    ).toBeInTheDocument();
    expect(
      screen.getByTestId(`${defaultProps.baseDataTestId}-registration-code-form-title`)
    ).toBeInTheDocument();

    const input = screen.getByPlaceholderText('auth.payApp.application.registrationCode.example');
    expect(input).toBeInTheDocument();
    expect(screen.getByText('auth.payApp.application.registrationCode.format')).toBeInTheDocument();

    const submitButton = screen.getByTestId(`${defaultProps.baseDataTestId}-Submit-Button`);
    expect(submitButton).toBeInTheDocument();
    expect(submitButton).toHaveTextContent('auth.payApp.application.register');
    expect(
      screen.getByTestId(`${defaultProps.baseDataTestId}-registration-code`)
    ).toBeInTheDocument();
    expect(
      screen.getByTestId(`${defaultProps.baseDataTestId}-registration-code-subtitle`)
    ).toBeInTheDocument();
  });

  it('calls checkRegistrationCode on valid form submission', async () => {
    render(<RegistrationCodeForm {...defaultProps} />);

    const input = screen.getByPlaceholderText('auth.payApp.application.registrationCode.example');
    const submitButton = screen.getByTestId(`${defaultProps.baseDataTestId}-Submit-Button`);

    await act(async () => {
      fireEvent.change(input, { target: { value: 'ABCD-1234-EFGH-5678' } });
      fireEvent.blur(input);
    });

    await act(async () => {
      fireEvent.click(submitButton);
    });

    await waitFor(() => {
      expect(mockPush).toHaveBeenCalledWith('/en-gb/business-pay/register/ABCD-1234-EFGH-5678');
    });
  });

  it('should not submit when registration code is empty', async () => {
    const { getRegistrationInfo } = jest.requireMock('@whitbread-eos/utils/server');
    render(<RegistrationCodeForm {...defaultProps} />);

    const submitButton = screen.getByTestId(`${defaultProps.baseDataTestId}-Submit-Button`);

    await act(async () => {
      fireEvent.click(submitButton);
    });

    expect(getRegistrationInfo).not.toHaveBeenCalled();
  });

  it('should handle invalid registration code response', async () => {
    const { getRegistrationInfo } = jest.requireMock('@whitbread-eos/utils/server');

    getRegistrationInfo.mockResolvedValueOnce({
      registrationCodeInfo: {
        registrationRole: null,
      },
    });

    render(<RegistrationCodeForm {...defaultProps} />);

    const input = screen.getByPlaceholderText('auth.payApp.application.registrationCode.example');
    const submitButton = screen.getByTestId(`${defaultProps.baseDataTestId}-Submit-Button`);

    await act(async () => {
      fireEvent.change(input, { target: { value: 'ABCD-1234-EFGH-5678' } });
    });

    await act(async () => {
      fireEvent.click(submitButton);
    });

    await waitFor(() => {
      expect(getRegistrationInfo).toHaveBeenCalledWith('mock-token', 'ABCD-1234-EFGH-5678');
    });

    expect(mockPush).not.toHaveBeenCalled();
  });

  it('should handle missing registrationCodeInfo', async () => {
    const { getRegistrationInfo } = jest.requireMock('@whitbread-eos/utils/server');

    getRegistrationInfo.mockResolvedValueOnce({
      registrationCodeInfo: null,
    });

    render(<RegistrationCodeForm {...defaultProps} />);

    const input = screen.getByPlaceholderText('auth.payApp.application.registrationCode.example');
    const submitButton = screen.getByTestId(`${defaultProps.baseDataTestId}-Submit-Button`);

    await act(async () => {
      fireEvent.change(input, { target: { value: 'ABCD-1234-EFGH-5678' } });
    });

    await act(async () => {
      fireEvent.click(submitButton);
    });

    await waitFor(() => {
      expect(getRegistrationInfo).toHaveBeenCalled();
    });

    expect(mockPush).not.toHaveBeenCalled();
  });

  it('should handle API error in catch block', async () => {
    const { getRegistrationInfo } = jest.requireMock('@whitbread-eos/utils/server');

    getRegistrationInfo.mockRejectedValueOnce(new Error('API Error'));

    render(<RegistrationCodeForm {...defaultProps} />);

    const input = screen.getByPlaceholderText('auth.payApp.application.registrationCode.example');
    const submitButton = screen.getByTestId(`${defaultProps.baseDataTestId}-Submit-Button`);

    await act(async () => {
      fireEvent.change(input, { target: { value: 'ABCD-1234-EFGH-5678' } });
    });

    await act(async () => {
      fireEvent.click(submitButton);
    });

    await waitFor(() => {
      expect(getRegistrationInfo).toHaveBeenCalled();
    });

    expect(mockPush).not.toHaveBeenCalled();
  });

  it('should show loading state during form submission', async () => {
    const { getRegistrationInfo } = jest.requireMock('@whitbread-eos/utils/server');

    getRegistrationInfo.mockImplementationOnce(
      () =>
        new Promise((resolve) =>
          setTimeout(
            () =>
              resolve({
                registrationCodeInfo: { registrationRole: 'mock-role' },
              }),
            100
          )
        )
    );

    render(<RegistrationCodeForm {...defaultProps} />);

    const input = screen.getByPlaceholderText('auth.payApp.application.registrationCode.example');
    const submitButton = screen.getByTestId(`${defaultProps.baseDataTestId}-Submit-Button`);

    await act(async () => {
      fireEvent.change(input, { target: { value: 'ABCD-1234-EFGH-5678' } });
    });

    await act(async () => {
      fireEvent.click(submitButton);
    });

    expect(submitButton).toBeDisabled();

    await waitFor(() => {
      expect(mockPush).toHaveBeenCalled();
    });
  });

  it('should trigger validation on input blur', async () => {
    render(<RegistrationCodeForm {...defaultProps} />);

    const input = screen.getByPlaceholderText('auth.payApp.application.registrationCode.example');

    await act(async () => {
      fireEvent.change(input, { target: { value: 'invalid' } });
      fireEvent.blur(input);
    });

    expect(input).toBeInTheDocument();
  });
});
