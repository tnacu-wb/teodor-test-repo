import '@testing-library/jest-dom';
import { act, fireEvent, render, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { CompanyType } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { RegisterPersonalInformationStep } from '../types';
import { PersonalInfoLanding } from './personal-info-landing';

const mockAnalyticsUpdate = jest.fn();

jest.mock('@whitbread-eos/utils', () => {
  const actualUtils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...actualUtils,
    analytics: {
      update: (...args: any[]) => mockAnalyticsUpdate(...args),
    },
    formatAnalyticsFunnelStep: jest.fn((siteType, page, language) => {
      const country = language === 'de' ? 'DE' : 'UK';

      return `Web:${siteType === 'PIB' ? 'PB' : siteType}:${country}:${page}`;
    }),
    formatIBAssetsUrl: jest.fn(() => '/'),
    useTranslation: jest.fn(() => ({
      t: (str: string) => str,
    })),
  };
});

const mockPersonalInfoProps = {
  baseDataTestId: 'test123',
  language: 'en' as any,
};

const mockProps = {
  icons: {},
  header: null,
  initialState: {
    title: '',
    firstName: '',
    lastName: '',
    phone: {
      prefix: '',
      phoneNumber: '',
    },
    emailAddress: '',
    activationKey: 'abc',
    countryCode: 'GB',
  },
  initialStepId: RegisterPersonalInformationStep.PERSONAL_INFO_LANDING,
  steps: [
    {
      id: RegisterPersonalInformationStep.PERSONAL_INFO_LANDING,
      component: <PersonalInfoLanding {...mockPersonalInfoProps} />,
    },
  ],
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPathForLocale: (locale: string, path: string) => `/${locale}/${path}`,
    getCountriesList: () => {
      return;
    },
    findError: serverUtils.findError,
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    refresh: jest.fn(),
    prefetch: jest.fn(),
  }),
  usePathname: () => {
    return '/';
  },
}));

describe('PersonalInfoLanding', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should render PersonalInfoLanding component', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('updates analytics with Business Pay account type for Business Pay managers', async () => {
    render(
      <Wizard
        {...mockProps}
        steps={[
          {
            id: RegisterPersonalInformationStep.PERSONAL_INFO_LANDING,
            component: (
              <PersonalInfoLanding {...mockPersonalInfoProps} accessLevel="BUSINESS_PAY_MANAGER" />
            ),
          },
        ]}
      />
    );

    await waitFor(() => {
      expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
        siteType: 'PIB',
        funnel_step: 'Web:PB:UK:Confirm your Account - Details',
        userLevel: 'BUSINESS_PAY_MANAGER',
        businessAccountType: CompanyType.BUSINESS_PAY,
      });
    });
  });

  it('should click footer button after completing mandatory fields', async () => {
    const user = userEvent.setup();
    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('Title-IB-Form-Select')).toBeInTheDocument();
      expect(getByTestId('FirstName-Form-Input')).toBeInTheDocument();
      expect(getByTestId('LastName-Form-Input')).toBeInTheDocument();
      expect(getByTestId('PhoneNumber-IB-Form-Select')).toBeInTheDocument();
    });

    const titleInput = getByTestId('Title-IB-Form-Select-Button');
    await user.click(titleInput);
    await user.click(getByTestId('Title-auth.signup.form.nameTitles-Option'));
    await user.tab();

    await act(async () => {
      const firstNameInput = getByTestId('FirstName-Form-Input');
      firstNameInput.focus();
      fireEvent.change(firstNameInput, { target: { value: 'test' } });
      expect(firstNameInput).toHaveValue('test');
      await userEvent.tab();

      const lastNameInput = getByTestId('LastName-Form-Input');
      lastNameInput.focus();
      fireEvent.change(lastNameInput, { target: { value: 'testLast' } });
      expect(lastNameInput).toHaveValue('testLast');
      await userEvent.tab();

      const phoneNumberInput = getByTestId('PhoneNumber-Form-Input');
      fireEvent.click(phoneNumberInput);
      fireEvent.change(phoneNumberInput, { target: { value: '14811700000000' } });
      expect(phoneNumberInput).toHaveValue('14811700000000');
      await userEvent.tab();
    });

    await act(async () => {
      fireEvent.click(getByTestId('footer-button'));
    });

    await waitFor(() => {
      expect(getByTestId('wizard-page')).toBeInTheDocument();
    });
  });
});
