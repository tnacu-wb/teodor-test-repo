import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import {
  RegistrationCodeInfo,
  AuthenticationQuestion,
  LOCALES,
  PibaRegistrationRole,
} from '@whitbread-eos/api';

import { QuestionsForm } from './questions-form';

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
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    authenticateRegistration: () => {
      return {
        registrationPrePopulatedItems: {
          surname: 'Doe',
        },
      };
    },
  };
});

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
  FormInput: ({ ...props }) => <input {...props} />,
}));

jest.mock('@whitbread-eos/layout', () => ({
  ...jest.requireActual('@whitbread-eos/layout'),
  WizardPage: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="wizard-page">{children}</div>
  ),
  useWizardContext: () => {
    return {
      wizardState: { emailAddress: 'test@example.com' },
      goToPreviousStep: jest.fn(),
    };
  },
}));

describe('QuestionsForm Component', () => {
  const mockQuestions: AuthenticationQuestion[] = [
    { questionId: 1, question: 'What is your favorite color?' },
    { questionId: 2, question: 'What is your pet’s name?' },
  ];
  const mockRegistrationInfo: RegistrationCodeInfo = {
    authenticationQuestions: mockQuestions,
    registrationRole: 'AccountHolder' as PibaRegistrationRole,
  };

  const defaultProps = {
    locale: LOCALES.EN,
    baseDataTestId: 'questions-form',
    icons: { 'icon.notification.error': 'error-icon-url' },
    registrationInfo: mockRegistrationInfo,
    token: 'mockToken',
  };

  it('renders the form with questions', () => {
    render(<QuestionsForm {...defaultProps} />);

    expect(screen.getByText('auth.payApp.security.question')).toBeInTheDocument();
    expect(screen.getByText('auth.payApp.security.question.description.valid')).toBeInTheDocument();

    mockQuestions.forEach((question) => {
      expect(screen.getByText(question?.question ?? '')).toBeInTheDocument();
    });

    expect(screen.getByTestId('questions-form-Button')).toBeInTheDocument();
  });
});
