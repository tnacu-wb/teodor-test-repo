import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES, UserAccessLevels } from '@whitbread-eos/api';
import { Language } from '@whitbread-eos/api/src/index';
import { Wizard } from '@whitbread-eos/layout';

import { PayApplicationStep } from '../types';
import { AddPayAppCard } from './add-pay-app-card';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    TranslationProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSearchParams: () => new URLSearchParams(),
    getCardManagementLabels: () => null,
    getCommonIcons: () => ({}),
    formatIBAssetsUrl: () => {
      return '/';
    },
    getAccountList: () => [
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'GB',
      },
    ],
    getSelectedAccountHolder: () => {
      return {
        accountName: 'test four',
        accountNumber: '6356290001000112',
        schemeCustomerId: 19000302,
        tetheredGuid: '4abb9835-8996-42bd-8199-5ba0919f106e',
        registrationRoles: ['COST_CENTRE_USER'],
        errorCode: null,
        scheme: 'DE',
      };
    },
    getCountriesList: () => {
      return 'United Kingdom (the)';
    },
    findError: serverUtils.findError,
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    addressSchema: () => ({
      merge: jest.fn(),
    }),
    IBPayCardUserSchema: () => ({
      validation: jest.fn(),
      schema: {
        merge: jest.fn().mockReturnThis(),
      },
    }),
    IBPayCardLimitsSchema: () => ({
      validation: jest.fn(),
      merge: jest.fn(),
    }),
    IBPayCardDeliverySchema: jest.fn(),
    getVariant: () => {
      return 'variantName.fieldName';
    },
  };
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('@whitbread-eos/layout', () => {
  return {
    ...jest.requireActual('@whitbread-eos/layout'),
    useWizardContext: () => ({
      wizardState: {},
      setWizardState: (...args: any[]) => jest.fn(...args),
      goToNextStep: (...args: any[]) => jest.fn(...args),
    }),
  };
});

const mockAddCardDetailsProps = {
  locale: LOCALES.EN,
  cardHolderName: 'test',
  companyId: '123',
  language: 'en' as Language,
  calendarLabels: {},
  accessLevel: 'SUPER' as UserAccessLevels,
  loggedEmployeeDetails: {},
  isCurrentUserInitiator: true,
};

const mockProps = {
  icons: {},
  estimatedMonthlySpend: '',
  locale: LOCALES.EN,
  header: null,
  initialState: {
    companyDetails: {
      companyName: '',
      companyBusinessType: '',
    },
    cardDetails: [],
  },
  initialStepId: PayApplicationStep.CARD_DETAILS,
  steps: [
    {
      id: PayApplicationStep.CARD_DETAILS,
      component: <AddPayAppCard {...mockAddCardDetailsProps} />,
    },
  ],
};

describe('Add pay app card component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CardDetails component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('should display Add a new employee button ', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const addEmployeeButton = getByTestId('Add-Card-New-Employee-Button');
    expect(addEmployeeButton).toBeInTheDocument();
  });
});
