import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { ForgotConfirmationState, ForgotStep } from '../types';
import { ConfirmationView } from './confirmation-view';

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: ForgotStep.FORGOT_CONFIRMATION,
  steps: [
    {
      id: ForgotStep.FORGOT_CONFIRMATION,
      component: <ConfirmationView locale={LOCALES.EN} />,
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
    getCountryLanguageByLocale: actual.getCountryLanguageByLocale,
    cn: jest.fn(),
    useTranslation: () => ({
      t: (key: string) => key,
    }),
    getPathForLocale: (locale: string, path: string) => path,
    forgotPassword: () => forgotPasswordMock(),
  };
});

const wizardState = {
  confirmationState: ForgotConfirmationState.DEFAULT,
};

const setWizardState = jest.fn();

jest.mock('@whitbread-eos/layout', () => {
  return {
    ...jest.requireActual('@whitbread-eos/layout'),
    useWizardContext: () => ({
      wizardState,
      setWizardState: (...args: any[]) => setWizardState(...args),
    }),
  };
});

describe('ConfirmationView', () => {
  it('should render default state', () => {
    wizardState.confirmationState = ForgotConfirmationState.DEFAULT;

    const { getByTestId, queryByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('EmailSentConfirmation-wrapper')).toBeInTheDocument();

    expect(getByTestId('EmailSentConfirmation-title')).toBeInTheDocument();
    expect(getByTestId('EmailSentConfirmation-defaultMessage')).toBeInTheDocument();

    expect(queryByTestId('EmailSentConfirmation-successMessage')).not.toBeInTheDocument();
    expect(queryByTestId('EmailSentConfirmation-errorMessage')).not.toBeInTheDocument();

    expect(getByTestId('EmailSentConfirmation-resendEmail')).toBeInTheDocument();
    expect(getByTestId('EmailSentConfirmation-backToLogin')).toBeInTheDocument();
  });

  it('should render success state', () => {
    wizardState.confirmationState = ForgotConfirmationState.SUCCESS;

    const { getByTestId, queryByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('EmailSentConfirmation-wrapper')).toBeInTheDocument();

    expect(getByTestId('EmailSentConfirmation-title')).toBeInTheDocument();
    expect(queryByTestId('EmailSentConfirmation-defaultMessage')).not.toBeInTheDocument();

    expect(getByTestId('EmailSentConfirmation-successMessage')).toBeInTheDocument();
    expect(queryByTestId('EmailSentConfirmation-errorMessage')).not.toBeInTheDocument();

    expect(getByTestId('EmailSentConfirmation-resendEmail')).toBeInTheDocument();
    expect(getByTestId('EmailSentConfirmation-backToLogin')).toBeInTheDocument();
  });

  it('should render error state', () => {
    wizardState.confirmationState = ForgotConfirmationState.ERROR;

    const { getByTestId, queryByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('EmailSentConfirmation-wrapper')).toBeInTheDocument();

    expect(getByTestId('EmailSentConfirmation-title')).toBeInTheDocument();
    expect(queryByTestId('EmailSentConfirmation-defaultMessage')).not.toBeInTheDocument();

    expect(queryByTestId('EmailSentConfirmation-successMessage')).not.toBeInTheDocument();
    expect(getByTestId('EmailSentConfirmation-errorMessage')).toBeInTheDocument();

    expect(getByTestId('EmailSentConfirmation-resendEmail')).toBeInTheDocument();
    expect(getByTestId('EmailSentConfirmation-backToLogin')).toBeInTheDocument();
  });

  it('should succeed sending email', async () => {
    forgotPasswordMock.mockResolvedValue({ success: true });

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const resendButton = getByTestId('EmailSentConfirmation-resendEmail');

    await waitFor(() => {
      resendButton.click();
    });

    expect(setWizardState).toBeCalledWith({
      confirmationState: ForgotConfirmationState.SUCCESS,
    });
  });

  it('should reject sending email', async () => {
    forgotPasswordMock.mockResolvedValue({ success: false });

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const resendButton = getByTestId('EmailSentConfirmation-resendEmail');

    await waitFor(() => {
      resendButton.click();
    });

    expect(setWizardState).toBeCalledWith({
      confirmationState: ForgotConfirmationState.ERROR,
    });
  });

  it('should fail sending email', async () => {
    forgotPasswordMock.mockRejectedValue(new Error('Error'));

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const resendButton = getByTestId('EmailSentConfirmation-resendEmail');

    await waitFor(() => {
      resendButton.click();
    });

    expect(setWizardState).toBeCalledWith({
      confirmationState: ForgotConfirmationState.ERROR,
    });
  });
});
