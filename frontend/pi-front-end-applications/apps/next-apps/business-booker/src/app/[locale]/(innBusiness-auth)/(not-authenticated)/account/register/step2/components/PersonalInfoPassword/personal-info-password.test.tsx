import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { userEvent } from '~utils/test-utils';

import { RegisterPersonalInformationStep } from '../types';
import { PersonalInfoPassword } from './personal-info-password';

const mockInfoProps = {
  baseDataTestId: 'PersonalInfoPassword',
  locale: LOCALES.EN,
  secureUrl: '',
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
  initialStepId: RegisterPersonalInformationStep.PERSONAL_INFO_PASSWORD,
  steps: [
    {
      id: RegisterPersonalInformationStep.PERSONAL_INFO_PASSWORD,
      component: <PersonalInfoPassword {...mockInfoProps} />,
    },
  ],
};

let mockResponse = null as any;
let mockPreferencesResponse = {
  optIn: false,
  secondOptIn: false,
  secondOptInReq: true,
  secondPartyOptIn: false,
  thirdPartyVendorsOptIn: false,
} as any;
let mockUpdateResponse = {
  updateMarketingPreferences: '',
} as any;

const mockRegistrationStepTwo = jest.fn();
const mockHashString = jest.fn();
const mockAnalyticsUpdate = jest.fn();

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    replace: jest.fn(),
    push: jest.fn(),
  }),
}));

interface AnalyticsUpdatePayload {
  signupID?: string;
  [key: string]: unknown;
}

type HashStringArgs = [value: string, algorithm: string];

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  hashString: (...args: HashStringArgs) => mockHashString(...args),
  analytics: {
    update: (payload: AnalyticsUpdatePayload) => mockAnalyticsUpdate(payload),
  },
}));

jest.mock('@whitbread-eos/utils/server', () => {
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
    registrationStepTwo: (...args: any[]) => mockRegistrationStepTwo(...args),
    getPathForLocale: (locale: string, path: string) => `/${locale}/${path}`,
    getMarketingPreferences: () => mockPreferencesResponse,
    updateMarketingPreferences: () => mockUpdateResponse,
  };
});

describe('PersonalInfoPassword', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockRegistrationStepTwo.mockResolvedValue(mockResponse);
    mockHashString.mockResolvedValue('hashed-email-123');
  });

  it('should render PersonalInfoPassword component', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('updates analytics with the set password funnel step', async () => {
    render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
        funnel_step: 'Web:PB:UK:Confirm your Account - Set Password',
      });
    });
  });

  it('should click footer button after completing the password field', async () => {
    mockResponse = {
      email: 'test@test.com',
    };
    mockRegistrationStepTwo.mockResolvedValue(mockResponse);
    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('SetPassword-Form-Input')).toBeInTheDocument();
    });

    const passwordInput = getByTestId('SetPassword-Form-Input');
    passwordInput.focus();
    fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
    expect(passwordInput).toHaveValue('Test12345');
    await userEvent.tab();

    fireEvent.click(getByTestId('footer-button'));

    await waitFor(() => {
      expect(getByTestId('wizard-page')).toBeInTheDocument();
    });
  });

  it('should update analytics signupID when companyId exists on successful registration', async () => {
    mockResponse = {
      companyId: 'COMP_21eade56-67a5-4eb9-ac07-2e6fe5c24122',
      email: 'test@test.com',
    };
    mockRegistrationStepTwo.mockResolvedValue(mockResponse);
    mockHashString.mockResolvedValue('hashed-email-123');
    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('SetPassword-Form-Input')).toBeInTheDocument();
    });

    const passwordInput = getByTestId('SetPassword-Form-Input');
    passwordInput.focus();
    fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
    expect(passwordInput).toHaveValue('Test12345');
    await userEvent.tab();

    fireEvent.click(getByTestId('footer-button'));

    await waitFor(() => {
      expect(mockHashString).not.toHaveBeenCalled();
      expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
        signupID: 'COMP_21eade56-67a5-4eb9-ac07-2e6fe5c24122',
      });
    });
  });

  it('should hash email and update analytics with signupID on successful registration', async () => {
    mockResponse = {
      email: 'test@test.com',
    };
    mockRegistrationStepTwo.mockResolvedValue(mockResponse);
    mockHashString.mockResolvedValue('hashed-email-123');
    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('SetPassword-Form-Input')).toBeInTheDocument();
    });

    const passwordInput = getByTestId('SetPassword-Form-Input');
    passwordInput.focus();
    fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
    expect(passwordInput).toHaveValue('Test12345');
    await userEvent.tab();

    fireEvent.click(getByTestId('footer-button'));

    await waitFor(() => {
      expect(mockHashString).toHaveBeenCalledWith('test@test.com', 'SHA-256');
      expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
        signupID: 'hashed-email-123',
      });
    });
  });

  it('should click footer button with null response and DE language', async () => {
    ((mockProps.steps = [
      {
        id: RegisterPersonalInformationStep.PERSONAL_INFO_PASSWORD,
        component: <PersonalInfoPassword {...{ ...mockInfoProps, language: 'de' }} />,
      },
    ]),
      (mockResponse = null));
    mockPreferencesResponse = null;
    mockUpdateResponse = null;
    mockRegistrationStepTwo.mockResolvedValue(null);
    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('SetPassword-Form-Input')).toBeInTheDocument();
    });

    const passwordInput = getByTestId('SetPassword-Form-Input');
    passwordInput.focus();
    fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
    expect(passwordInput).toHaveValue('Test12345');
    await userEvent.tab();

    fireEvent.click(getByTestId('footer-button'));

    await waitFor(() => {
      expect(getByTestId('wizard-page')).toBeInTheDocument();
    });
  });

  it('should include updatePreferencesRequest field in registrationStepTwo call', async () => {
    mockResponse = {
      email: 'test@test.com',
    };
    mockPreferencesResponse = {
      optIn: true,
      secondOptIn: true,
      secondOptInReq: true,
      secondPartyOptIn: false,
      thirdPartyVendorsOptIn: false,
    };
    mockRegistrationStepTwo.mockResolvedValue(mockResponse);

    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('SetPassword-Form-Input')).toBeInTheDocument();
    });

    const passwordInput = getByTestId('SetPassword-Form-Input');
    passwordInput.focus();
    fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
    expect(passwordInput).toHaveValue('Test12345');
    await userEvent.tab();

    fireEvent.click(getByTestId('footer-button'));

    await waitFor(() => {
      expect(mockRegistrationStepTwo).toHaveBeenCalledWith(
        expect.objectContaining({
          updatePreferencesRequest: expect.objectContaining({
            brandCodes: ['PINN'],
            optIn: true,
            doubleOptIn: true,
            customer: expect.any(Object),
            sourceDetails: expect.objectContaining({
              channel: 'BB',
              journey: 'ACTIVATE',
              locale: 'UK',
            }),
          }),
        })
      );
    });
  });

  it('should include updatePreferencesRequest with optIn false when marketing preferences are not opted in', async () => {
    mockResponse = {
      email: 'test@test.com',
    };
    mockPreferencesResponse = {
      optIn: false,
      secondOptIn: false,
      secondOptInReq: false,
      secondPartyOptIn: false,
      thirdPartyVendorsOptIn: false,
    };
    mockRegistrationStepTwo.mockResolvedValue(mockResponse);

    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('SetPassword-Form-Input')).toBeInTheDocument();
    });

    const passwordInput = getByTestId('SetPassword-Form-Input');
    passwordInput.focus();
    fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
    expect(passwordInput).toHaveValue('Test12345');
    await userEvent.tab();

    fireEvent.click(getByTestId('footer-button'));

    await waitFor(() => {
      expect(mockRegistrationStepTwo).toHaveBeenCalledWith(
        expect.objectContaining({
          updatePreferencesRequest: expect.objectContaining({
            brandCodes: ['PINN'],
            optIn: false,
            doubleOptIn: false,
            customer: expect.any(Object),
            sourceDetails: expect.objectContaining({
              channel: 'BB',
              journey: 'ACTIVATE',
              locale: 'UK',
            }),
          }),
        })
      );
    });
  });

  it('should include DE locale in sourceDetails when URL locale is DE', async () => {
    mockResponse = {
      email: 'test@test.com',
    };
    mockPreferencesResponse = {
      optIn: true,
      secondOptIn: true,
      secondOptInReq: true,
      secondPartyOptIn: false,
      thirdPartyVendorsOptIn: false,
    };
    mockRegistrationStepTwo.mockResolvedValue(mockResponse);

    const deProps = {
      ...mockProps,
      initialState: {
        ...mockProps.initialState,
        countryCode: 'DE',
      },
      steps: [
        {
          id: RegisterPersonalInformationStep.PERSONAL_INFO_PASSWORD,
          component: (
            <PersonalInfoPassword {...mockInfoProps} locale={LOCALES.DE} language={'de' as any} />
          ),
        },
      ],
    };

    const { getByTestId } = render(<Wizard {...deProps} />);

    await waitFor(() => {
      expect(getByTestId('SetPassword-Form-Input')).toBeInTheDocument();
    });

    const passwordInput = getByTestId('SetPassword-Form-Input');
    passwordInput.focus();
    fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
    expect(passwordInput).toHaveValue('Test12345');
    await userEvent.tab();

    fireEvent.click(getByTestId('footer-button'));

    await waitFor(() => {
      expect(mockRegistrationStepTwo).toHaveBeenCalledWith(
        expect.objectContaining({
          updatePreferencesRequest: expect.objectContaining({
            sourceDetails: expect.objectContaining({
              channel: 'BB',
              journey: 'ACTIVATE',
              locale: 'DE',
            }),
          }),
        })
      );
    });
  });
});
