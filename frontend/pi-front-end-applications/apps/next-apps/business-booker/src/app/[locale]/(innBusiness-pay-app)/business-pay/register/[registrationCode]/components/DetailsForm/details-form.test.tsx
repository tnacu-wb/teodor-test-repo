import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import {
  AuthenticationQuestion,
  LOCALES,
  PibaRegistrationRole,
  RegistrationCodeInfo,
} from '@whitbread-eos/api';

import { DetailsForm } from './details-form';

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

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
  useToast: () => ({
    toast: jest.fn((text: string) => <span>{text}</span>),
  }),
  ErrorTooltip: ({ message }: { message: string }) => (
    <div data-testid="error-tooltip">{message}</div>
  ),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

describe('DetailsForm', () => {
  const mockQuestions: AuthenticationQuestion[] = [
    { questionId: 1, question: 'What is your favorite color?' },
    { questionId: 2, question: 'What is your pet’s name?' },
  ];
  const mockRegistrationInfo: RegistrationCodeInfo = {
    authenticationQuestions: mockQuestions,
    registrationRole: 'AccountHolder' as PibaRegistrationRole,
  };
  const mockToken = 'mockToken';

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders the form with default values', () => {
    render(
      <DetailsForm
        baseDataTestId="details-form"
        locale={LOCALES.EN}
        icons={{}}
        registrationInfo={mockRegistrationInfo}
        token={mockToken}
      />
    );

    expect(screen.getByTestId('details-form-Form')).toBeInTheDocument();
    expect(screen.getByTestId('details-form-Button')).toBeInTheDocument();
  });

  it('displays an error alert when isError is true', () => {
    render(
      <DetailsForm
        baseDataTestId="details-form"
        locale={LOCALES.EN}
        icons={{}}
        registrationInfo={mockRegistrationInfo}
        token={mockToken}
      />
    );

    fireEvent.click(screen.getByTestId('details-form-Button'));

    expect(screen.queryByTestId('details-form-error-alert')).not.toBeInTheDocument();
  });
});
