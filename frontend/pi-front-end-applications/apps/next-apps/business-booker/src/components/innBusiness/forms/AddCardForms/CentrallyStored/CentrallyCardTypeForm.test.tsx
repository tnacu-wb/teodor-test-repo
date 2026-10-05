import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { Language, LOCALES } from '@whitbread-eos/api';
import { FormProvider, useForm } from 'react-hook-form';

import { CentrallyCardTypeForm } from '~components/innBusiness/forms/AddCardForms/CentrallyStored/CentrallyCardTypeForm';

const mockUseFeatureToggle = jest.fn().mockReturnValue({});
const mockGetLocaleByPathname = jest.fn(() => LOCALES.EN);
const mockGetCountryLanguageByLocale = jest.fn((locale) => ({
  language: locale === LOCALES.DE ? 'de' : 'en',
  country: locale === LOCALES.DE ? 'de' : 'gb',
}));

jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: () => <div />,
}));

const Wrapper = ({
  children,
  defaultValues = {},
}: {
  children: React.ReactNode;
  defaultValues?: Record<string, unknown>;
}) => {
  const methods = useForm({ defaultValues });
  return <FormProvider {...methods}>{children}</FormProvider>;
};

const mockProps = {
  onSubmit: (data: any) => {
    return data;
  },
  icons: { 'icon.arrow.left': '/' },
  formRef: { current: document.createElement('form') },
  language: 'en' as Language,
  onCNPChange: () => {
    return;
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useTranslation: () => ({
    t: (str: string) => str,
  }),
  useFeatureToggle: () => mockUseFeatureToggle(),
  getLocaleByPathname: (...args) => mockGetLocaleByPathname(...args),
  getCountryLanguageByLocale: (...args) => mockGetCountryLanguageByLocale(...args),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    findError: serverUtils.findError,
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
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSavedCardType: jest.fn(),
  };
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    push: jest.fn(),
  })),
  usePathname: () => {
    return '/';
  },
}));

describe('CentrallyCardTypeForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseFeatureToggle.mockReturnValue({});
    mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);
  });

  it('should render CentrallyCardTypeForm component', async () => {
    const { getByTestId } = render(
      <Wrapper>
        <CentrallyCardTypeForm {...mockProps} />
      </Wrapper>
    );

    expect(getByTestId('Centrally-Stored-Card-Type')).toBeInTheDocument();
  });

  it('should show memorable word input when flag is OFF and PIBA + CNP selected', async () => {
    mockUseFeatureToggle.mockReturnValue({ release_piba_cnp_iframe_split: false });

    const { queryByTestId } = render(
      <Wrapper defaultValues={{ cardType: 'NEW_PIBA', CNP: true }}>
        <CentrallyCardTypeForm {...mockProps} />
      </Wrapper>
    );

    expect(queryByTestId('memorableWord-Form-Input')).toBeInTheDocument();
  });

  it('should hide memorable word input when flag is ON and PIBA + CNP selected', async () => {
    mockUseFeatureToggle.mockReturnValue({ release_piba_cnp_iframe_split: true });

    const { queryByTestId } = render(
      <Wrapper defaultValues={{ cardType: 'NEW_PIBA', CNP: true }}>
        <CentrallyCardTypeForm {...mockProps} />
      </Wrapper>
    );

    expect(queryByTestId('memorableWord-Form-Input')).not.toBeInTheDocument();
  });

  it('shows the NEW_PIBA option for GB when PIBA Euro flag is enabled', () => {
    mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);
    mockUseFeatureToggle.mockReturnValue({ release_ib_pay_piba_euro: true });

    render(
      <Wrapper>
        <CentrallyCardTypeForm {...mockProps} />
      </Wrapper>
    );

    expect(
      screen.getByText('cards.centrallyStoredCard.card.type.newInnBusiness.label')
    ).toBeInTheDocument();
  });

  it('shows the NEW_PIBA option for GB when PIBA Euro flag is disabled', () => {
    mockGetLocaleByPathname.mockReturnValue(LOCALES.EN);
    mockUseFeatureToggle.mockReturnValue({ release_ib_pay_piba_euro: false });

    render(
      <Wrapper>
        <CentrallyCardTypeForm {...mockProps} />
      </Wrapper>
    );

    expect(
      screen.getByText('cards.centrallyStoredCard.card.type.newInnBusiness.label')
    ).toBeInTheDocument();
  });

  it('hides the NEW_PIBA option for DE when PIBA Euro flag is disabled', () => {
    mockGetLocaleByPathname.mockReturnValue(LOCALES.DE);

    render(
      <Wrapper>
        <CentrallyCardTypeForm {...mockProps} />
      </Wrapper>
    );

    expect(
      screen.queryByText('cards.centrallyStoredCard.card.type.newInnBusiness.label')
    ).not.toBeInTheDocument();
  });

  it('shows the NEW_PIBA option for DE when PIBA Euro flag is enabled', () => {
    mockGetLocaleByPathname.mockReturnValue(LOCALES.DE);
    mockUseFeatureToggle.mockReturnValue({ release_ib_pay_piba_euro: true });

    render(
      <Wrapper>
        <CentrallyCardTypeForm {...mockProps} />
      </Wrapper>
    );

    expect(
      screen.getByText('cards.centrallyStoredCard.card.type.newInnBusiness.label')
    ).toBeInTheDocument();
  });
});
