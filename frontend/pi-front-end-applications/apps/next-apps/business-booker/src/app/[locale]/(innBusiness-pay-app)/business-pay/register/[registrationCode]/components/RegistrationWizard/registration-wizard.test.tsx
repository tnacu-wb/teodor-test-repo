import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { RegistrationCodeInfo, LOCALES, PibaRegistrationRole } from '@whitbread-eos/api';

import { RegistrationWizard } from './registration-wizard';

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

jest.mock('next/headers', () => ({
  cookies: jest.fn(() => ({
    get: jest.fn(() => ({ value: 'mockToken' })),
  })),
}));

jest.mock('@whitbread-eos/layout', () => ({
  Wizard: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="wizard">{children}</div>
  ),
}));

describe('RegistrationWizard', () => {
  const mockBaseDataTestId = 'test-id';
  const mockLocale = LOCALES.EN;
  const mockRegistrationCodeInfo: RegistrationCodeInfo = {
    registrationCode: 'mockCode',
    registrationRole: 'AccountHolder' as PibaRegistrationRole,
    authenticationQuestions: [],
  };
  const mockProps = {
    baseDataTestId: mockBaseDataTestId,
    locale: mockLocale,
    registrationCodeInfo: mockRegistrationCodeInfo,
  };
  test('renders the Wizard component', async () => {
    render(await RegistrationWizard(mockProps));

    expect(screen.getByTestId('wizard')).toBeInTheDocument();
  });
});
