import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { ForgotConfirmationState, ForgotStep } from '../types';
import { ForgotForm } from './forgot-form';

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: ForgotStep.FORGOT_FORM,
  steps: [
    {
      id: ForgotStep.FORGOT_FORM,
      component: <ForgotForm locale={LOCALES.EN} icons={{}} />,
    },
  ],
};

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
  redirect: jest.fn(),
}));

const forgotPasswordMock = jest.fn();

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
    forgotPassword: () => forgotPasswordMock(),
  };
});

const setWizardState = jest.fn();
const goToNextStep = jest.fn();

jest.mock('@whitbread-eos/layout', () => {
  return {
    ...jest.requireActual('@whitbread-eos/layout'),
    useWizardContext: () => ({
      wizardState: {},
      setWizardState: (...args: any[]) => setWizardState(...args),
      goToNextStep: (...args: any[]) => goToNextStep(...args),
    }),
  };
});

describe('ForgotForm', () => {
  it('should render without crashing', () => {
    const { getByTestId, queryByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('ForgotForm-wrapper')).toBeInTheDocument();

    expect(getByTestId('ForgotForm-title')).toBeInTheDocument();
    expect(getByTestId('ForgotForm-description')).toBeInTheDocument();

    expect(queryByTestId('ForgotForm-accountNotExist')).not.toBeInTheDocument();

    expect(getByTestId('ForgotForm-email-Form-Input')).toBeInTheDocument();

    expect(getByTestId('ForgotForm-sendEmail')).toBeInTheDocument();
    expect(getByTestId('ForgotForm-backToLogin')).toBeInTheDocument();
  });

  it('should show account not exist alert when email is not registered', async () => {
    forgotPasswordMock.mockResolvedValue({ success: false });

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const sendEmailButton = getByTestId('ForgotForm-sendEmail');
    const emailInput = getByTestId('ForgotForm-email-Form-Input');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@test.com' } });
      sendEmailButton.click();
      expect(getByTestId('ForgotForm-accountNotExist')).toBeInTheDocument();
    });
  });

  it('should go to next step with error when forgotPassword fails', async () => {
    forgotPasswordMock.mockRejectedValue(new Error('Network error'));

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const sendEmailButton = getByTestId('ForgotForm-sendEmail');
    const emailInput = getByTestId('ForgotForm-email-Form-Input');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@test.com' } });
      sendEmailButton.click();

      expect(setWizardState).toHaveBeenCalledWith(
        expect.objectContaining({
          confirmationState: ForgotConfirmationState.ERROR,
        })
      );

      expect(goToNextStep).toHaveBeenCalled();
    });
  });

  it('should submit form successfully', async () => {
    forgotPasswordMock.mockResolvedValue({ success: true });

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const sendEmailButton = getByTestId('ForgotForm-sendEmail');
    const emailInput = getByTestId('ForgotForm-email-Form-Input');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@test.com' } });
      sendEmailButton.click();
      expect(setWizardState).toHaveBeenCalledWith(
        expect.objectContaining({
          email: 'test@test.com',
          confirmationState: ForgotConfirmationState.DEFAULT,
        })
      );
      expect(goToNextStep).toHaveBeenCalled();
    });
  });
});
