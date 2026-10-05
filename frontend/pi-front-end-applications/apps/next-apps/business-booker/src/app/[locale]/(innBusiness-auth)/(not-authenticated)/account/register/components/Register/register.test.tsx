import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { CompanyType, LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';
import { InnBRegistrationStepOne, updateMarketingPreferences } from '@whitbread-eos/utils/server';
import React from 'react';
import { z } from 'zod';

import { RegisterSteps } from '../../page';
import RegisterValidation from '../RegisterValidation/register-validation';
import Register from './register';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useSearchParams: () => {
    return {
      get: () => '',
    };
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: any) => {
    return <img {...props} src={props.src || 'mock-image-url'} />;
  },
}));

const mockAnalyticsUpdate = jest.fn();
const mockSetPageAnalytics = jest.fn();

jest.mock('@whitbread-eos/utils', () => {
  const actualUtils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...actualUtils,
    analytics: {
      update: (...args: any[]) => mockAnalyticsUpdate(...args),
    },
    formatIBAssetsUrl: jest.fn((url) => url),
    getPathForLocale: jest.fn(() => '/'),
    setPageAnalytics: (...args: any[]) => mockSetPageAnalytics(...args),
    useTranslation: jest.fn(() => ({
      t: (key: string) => key,
    })),
  };
});

const mockIcons = {
  'icon.notification.error': 'mock-error-icon-url',
};

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: RegisterSteps.REGISTER,
  steps: [
    {
      id: RegisterSteps.REGISTER,
      component: (
        <Register
          baseDataTestId="register"
          icons={mockIcons}
          language="en"
          locale={LOCALES.EN}
          companyType=""
        />
      ),
    },
    {
      id: RegisterSteps.CONFIRMATION,
      component: <RegisterValidation locale={LOCALES.EN} />,
    },
  ],
};

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('../../utils/form-data', () => {
  const registerSchema = z.object({
    emailAddress: z.string().email(),
    companyName: z.string().min(1),
    postCode: z.string().min(1),
    uniqueTaxpayerReference: z.string().optional(),
  });

  return {
    registerSchema: jest.fn(() => ({
      merge: jest.fn((schema) => {
        const defaultSchema = z.object({});
        const resolvedSchema: z.AnyZodObject =
          schema instanceof z.ZodObject ? schema : defaultSchema;

        return registerSchema.merge(resolvedSchema);
      }),
      validate: jest.fn((data) => {
        try {
          registerSchema.parse(data);
          return { errors: [], isValid: true };
        } catch (err) {
          return { errors: err, isValid: false };
        }
      }),
    })),
  };
});

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');
  return {
    formatIBAssetsUrl: jest.fn((url) => url),
    cn: jest.fn(),
    getLocaleByPathname: jest.fn(() => 'en'),
    useTranslation: jest.fn(() => ({
      t: (key: string) => key,
    })),
    addressSchema: jest.fn(() => ({
      validate: jest.fn(() => ({
        errors: [],
        isValid: true,
      })),
    })),
    getCountryLanguageByLocale: jest.fn(() => 'en'),
    getPathForLocale: jest.fn(() => '/'),
    InnBRegistrationStepOne: jest.fn(() => ({
      innBRegistrationStepOne: {
        existingCompany: false,
        existingEmployee: false,
      },
    })),
    updateMarketingPreferences: jest.fn(() => ({
      data: {
        updateMarketingPreferences: {},
      },
    })),
    getCountriesList: () => {
      return [
        {
          countryCode: 'GB',
          countryCodeLegacy: 'GB',
          countryName: 'United Kingdom (the)',
          dialingCode: '+44',
          flagSrc: '/content/dam/global/flags/United-Kingdom.png',
          passportRequired: false,
          nationality: 'British, UK',
        },
      ];
    },
    getPostCodeAddresses: jest.fn(() => [
      {
        companyName: 'Test Comp',
        addressLine1: '12 Briton Ferry Road',
        addressLine2: null,
        addressLine3: null,
        addressLine4: 'NEATH',
        label: '12 Briton Ferry Road, NEATH, West Glamorgan, SA11 1AA',
        postalCode: 'SA111AA',
        country: 'GB',
      },
    ]),
    findError: serverUtils.findError,
    getVariant: () => {
      return 'variantName.fieldName';
    },
    getFormattedAddress: jest.fn(() => ({
      companyName: 'Test Company',
      addressLine1: '123 Test St',
      addressLine2: 'Test City',
      addressLine3: 'Test Region',
      addressLine4: 'Test State',
      addressLine5: 'Test Area',
      postalCode: 'SA111AA',
      country: 'GB',
    })),
  };
});

describe('Register Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the Register component', () => {
    render(<Wizard {...mockProps} />);

    expect(screen.getByText('auth.signup.accountCreation.title')).toBeInTheDocument();
    expect(screen.getByText('auth.signup.accountCreation.description')).toBeInTheDocument();
    expect(screen.getByText('auth.signup.accountCreation.continue.button')).toBeInTheDocument();
  });

  it('updates analytics with default Business Booker account type', async () => {
    render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
        businessAccountType: CompanyType.BUSINESS_BOOKER,
      });
    });
  });

  it('toggles the opt-in checkbox', () => {
    render(<Wizard {...mockProps} />);

    const checkbox = screen.getByRole('checkbox', {
      name: 'auth.signup.accountCreation.newsletter.checkbox.label',
    });
    expect(checkbox).toBeInTheDocument();
    expect(checkbox).not.toBeChecked();

    if (checkbox) {
      fireEvent.click(checkbox);
      expect(checkbox).toBeChecked();

      fireEvent.click(checkbox);
      expect(checkbox).not.toBeChecked();
    }
  });

  it('renders email and company name input fields', () => {
    render(<Wizard {...mockProps} />);

    const emailInput = screen.getByPlaceholderText('auth.signup.accountCreation.email.placeholder');
    const companyNameInput = screen.getByPlaceholderText(
      'auth.signup.accountCreation.companyName.placeholder'
    );

    expect(emailInput).toBeInTheDocument();
    expect(companyNameInput).toBeInTheDocument();
  });

  it('skips updateMarketingPreferences call when register is successful and it returns existingEmployee false', async () => {
    const updateMarketingPreferencesSpy = jest.spyOn(
      { updateMarketingPreferences },
      'updateMarketingPreferences'
    );
    render(<Wizard {...mockProps} />);
    const emailInput = screen.getByPlaceholderText('auth.signup.accountCreation.email.placeholder');
    const companyNameInput = screen.getByPlaceholderText(
      'auth.signup.accountCreation.companyName.placeholder'
    );
    const continueButton = screen.getByText('auth.signup.accountCreation.continue.button');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    });
    await waitFor(() => {
      fireEvent.change(companyNameInput, { target: { value: 'Test Company' } });
    });

    const postCodeInput = screen.getByPlaceholderText(
      'userMgmt.employee.add.companyAddress.postcode'
    );
    const findAddressButton = screen.getByTestId('RegisterForm-findAddressButton');

    await waitFor(() => {
      fireEvent.change(postCodeInput, { target: { value: 'SA111AA' } });
    });

    await waitFor(() => {
      fireEvent.click(findAddressButton);
    });

    const countrySelect = screen.getByTestId('Manual-Countries-IB-Form-Select-Button');

    await waitFor(() => {
      fireEvent.click(countrySelect);
    });
    const countryOption = screen.getByTestId('Manual-Countries-GB-Option');
    await waitFor(() => {
      countryOption.click();
    });

    expect(continueButton).toBeInTheDocument();

    await waitFor(() => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(updateMarketingPreferencesSpy).not.toHaveBeenCalled();
    });
  });

  it('skips updateMarketingPreferences call when register is successful and it returns existingEmployee true', async () => {
    (InnBRegistrationStepOne as jest.Mock).mockResolvedValue({
      innBRegistrationStepOne: {
        existingCompany: false,
        existingEmployee: true,
      },
    });
    const updateMarketingPreferencesSpy = jest.spyOn(
      { updateMarketingPreferences },
      'updateMarketingPreferences'
    );
    render(<Wizard {...mockProps} />);
    const emailInput = screen.getByPlaceholderText('auth.signup.accountCreation.email.placeholder');
    const companyNameInput = screen.getByPlaceholderText(
      'auth.signup.accountCreation.companyName.placeholder'
    );
    const continueButton = screen.getByText('auth.signup.accountCreation.continue.button');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    });
    await waitFor(() => {
      fireEvent.change(companyNameInput, { target: { value: 'Test Company' } });
    });

    const postCodeInput = screen.getByPlaceholderText(
      'userMgmt.employee.add.companyAddress.postcode'
    );
    const findAddressButton = screen.getByTestId('RegisterForm-findAddressButton');

    await waitFor(() => {
      fireEvent.change(postCodeInput, { target: { value: 'AB123CD' } });
    });

    await waitFor(() => {
      fireEvent.click(findAddressButton);
    });

    expect(continueButton).toBeInTheDocument();

    await waitFor(() => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(updateMarketingPreferencesSpy).not.toHaveBeenCalled();
    });
  });

  it('renders error notification when showReportError is set', async () => {
    (InnBRegistrationStepOne as jest.Mock).mockResolvedValue(null);

    render(<Wizard {...mockProps} />);
    const emailInput = screen.getByPlaceholderText('auth.signup.accountCreation.email.placeholder');
    const companyNameInput = screen.getByPlaceholderText(
      'auth.signup.accountCreation.companyName.placeholder'
    );
    const continueButton = screen.getByText('auth.signup.accountCreation.continue.button');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    });
    await waitFor(() => {
      fireEvent.change(companyNameInput, { target: { value: 'Test Company' } });
    });

    const postCodeInput = screen.getByPlaceholderText('profile.profile.form.postcode');
    const findAddressButton = screen.getByTestId('RegisterForm-findAddressButton');

    await waitFor(() => {
      fireEvent.change(postCodeInput, { target: { value: 'SA111AA' } });
    });

    await waitFor(() => {
      fireEvent.click(findAddressButton);
    });

    expect(continueButton).toBeInTheDocument();

    await waitFor(() => {
      fireEvent.click(continueButton);
    });
  });

  it('calls InnBRegistrationStepOne with updatePreferencesRequest when optIn is false', async () => {
    const InnBRegistrationStepOneSpy = jest.spyOn(
      { InnBRegistrationStepOne },
      'InnBRegistrationStepOne'
    );

    render(<Wizard {...mockProps} />);

    const emailInput = screen.getByPlaceholderText('auth.signup.accountCreation.email.placeholder');
    const companyNameInput = screen.getByPlaceholderText(
      'auth.signup.accountCreation.companyName.placeholder'
    );
    const continueButton = screen.getByText('auth.signup.accountCreation.continue.button');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    });
    await waitFor(() => {
      fireEvent.change(companyNameInput, { target: { value: 'Test Company' } });
    });

    const postCodeInput = screen.getByPlaceholderText(
      'userMgmt.employee.add.companyAddress.postcode'
    );
    const findAddressButton = screen.getByTestId('RegisterForm-findAddressButton');

    await waitFor(() => {
      fireEvent.change(postCodeInput, { target: { value: 'SA111AA' } });
    });

    await waitFor(() => {
      fireEvent.click(findAddressButton);
    });

    const countrySelect = screen.getByTestId('Manual-Countries-IB-Form-Select-Button');

    await waitFor(() => {
      fireEvent.click(countrySelect);
    });
    const countryOption = screen.getByTestId('Manual-Countries-GB-Option');
    await waitFor(() => {
      countryOption.click();
    });

    await waitFor(() => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(InnBRegistrationStepOneSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          updatePreferencesRequest: expect.objectContaining({
            brandCodes: ['PINN'],
            optIn: false,
            doubleOptIn: false,
            customer: expect.objectContaining({
              language: 'en',
            }),
            sourceDetails: expect.objectContaining({
              channel: 'BB',
              journey: 'SIGNUP',
              locale: 'UK',
            }),
          }),
        })
      );
    });
  });

  it('calls InnBRegistrationStepOne with updatePreferencesRequest when optIn is true', async () => {
    const InnBRegistrationStepOneSpy = jest.spyOn(
      { InnBRegistrationStepOne },
      'InnBRegistrationStepOne'
    );

    render(<Wizard {...mockProps} />);

    const emailInput = screen.getByPlaceholderText('auth.signup.accountCreation.email.placeholder');
    const companyNameInput = screen.getByPlaceholderText(
      'auth.signup.accountCreation.companyName.placeholder'
    );
    const checkbox = screen.getByRole('checkbox', {
      name: 'auth.signup.accountCreation.newsletter.checkbox.label',
    });
    const continueButton = screen.getByText('auth.signup.accountCreation.continue.button');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    });
    await waitFor(() => {
      fireEvent.change(companyNameInput, { target: { value: 'Test Company' } });
    });

    await waitFor(() => {
      fireEvent.click(checkbox);
    });

    const postCodeInput = screen.getByPlaceholderText(
      'userMgmt.employee.add.companyAddress.postcode'
    );
    const findAddressButton = screen.getByTestId('RegisterForm-findAddressButton');

    await waitFor(() => {
      fireEvent.change(postCodeInput, { target: { value: 'SA111AA' } });
    });

    await waitFor(() => {
      fireEvent.click(findAddressButton);
    });

    const countrySelect = screen.getByTestId('Manual-Countries-IB-Form-Select-Button');

    await waitFor(() => {
      fireEvent.click(countrySelect);
    });
    const countryOption = screen.getByTestId('Manual-Countries-GB-Option');
    await waitFor(() => {
      countryOption.click();
    });

    await waitFor(() => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(InnBRegistrationStepOneSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          updatePreferencesRequest: expect.objectContaining({
            brandCodes: ['PINN'],
            optIn: true,
            doubleOptIn: false,
            customer: expect.objectContaining({
              language: 'en',
            }),
            sourceDetails: expect.objectContaining({
              channel: 'BB',
              journey: 'SIGNUP',
              locale: 'UK',
            }),
          }),
        })
      );
    });
  });

  it('calls InnBRegistrationStepOne with doubleOptIn true when country is DE', async () => {
    const InnBRegistrationStepOneSpy = jest.spyOn(
      { InnBRegistrationStepOne },
      'InnBRegistrationStepOne'
    );

    const deProps = {
      ...mockProps,
      steps: [
        {
          id: RegisterSteps.REGISTER,
          component: (
            <Register
              baseDataTestId="register"
              icons={mockIcons}
              language="de"
              locale={LOCALES.DE}
              companyType=""
            />
          ),
        },
        {
          id: RegisterSteps.CONFIRMATION,
          component: <RegisterValidation locale={LOCALES.DE} />,
        },
      ],
    };

    render(<Wizard {...deProps} />);

    const emailInput = screen.getByPlaceholderText('auth.signup.accountCreation.email.placeholder');
    const companyNameInput = screen.getByPlaceholderText(
      'auth.signup.accountCreation.companyName.placeholder'
    );
    const continueButton = screen.getByText('auth.signup.accountCreation.continue.button');

    await waitFor(() => {
      fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    });
    await waitFor(() => {
      fireEvent.change(companyNameInput, { target: { value: 'Test Company' } });
    });

    const postCodeInput = screen.getByPlaceholderText(
      'userMgmt.employee.add.companyAddress.postcode'
    );
    const findAddressButton = screen.getByTestId('RegisterForm-findAddressButton');

    await waitFor(() => {
      fireEvent.change(postCodeInput, { target: { value: 'SA111AA' } });
    });

    await waitFor(() => {
      fireEvent.click(findAddressButton);
    });

    const countrySelect = screen.getByTestId('Manual-Countries-IB-Form-Select-Button');

    await waitFor(() => {
      fireEvent.click(countrySelect);
    });
    const countryOption = screen.getByTestId('Manual-Countries-GB-Option');
    await waitFor(() => {
      countryOption.click();
    });

    await waitFor(() => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(InnBRegistrationStepOneSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          updatePreferencesRequest: expect.objectContaining({
            brandCodes: ['PINN'],
            doubleOptIn: false,
            customer: expect.objectContaining({
              language: 'de',
            }),
            sourceDetails: expect.objectContaining({
              channel: 'BB',
              journey: 'SIGNUP',
              locale: 'DE',
            }),
          }),
        })
      );
    });
  });

  describe('Unique Taxpayer Reference Validation', () => {
    const taxpayerReferenceRegex = /^[a-zA-Z0-9]{4,20}$/;

    const submitRegisterFormWithTaxpayerReference = async (taxpayerReference: string) => {
      render(<Wizard {...mockProps} />);

      const emailInput = screen.getByPlaceholderText(
        'auth.signup.accountCreation.email.placeholder'
      );
      const companyNameInput = screen.getByPlaceholderText(
        'auth.signup.accountCreation.companyName.placeholder'
      );
      const uniqueTaxpayerReferenceInput = screen.getByPlaceholderText(
        'auth.signup.accountCreation.taxpayerReference.placeholder'
      );
      const postCodeInput = screen.getByPlaceholderText(
        'userMgmt.employee.add.companyAddress.postcode'
      );
      const findAddressButton = screen.getByTestId('RegisterForm-findAddressButton');
      const continueButton = screen.getByText('auth.signup.accountCreation.continue.button');

      fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
      fireEvent.change(companyNameInput, { target: { value: 'Test Company' } });
      fireEvent.change(uniqueTaxpayerReferenceInput, { target: { value: taxpayerReference } });
      fireEvent.change(postCodeInput, { target: { value: 'SA111AA' } });
      await waitFor(() => {
        fireEvent.click(findAddressButton);
      });

      const countrySelect = screen.getByTestId('Manual-Countries-IB-Form-Select-Button');
      await waitFor(() => {
        fireEvent.click(countrySelect);
      });

      const countryOption = await screen.findByTestId('Manual-Countries-GB-Option');
      await waitFor(() => {
        countryOption.click();
      });

      await waitFor(() => {
        fireEvent.click(continueButton);
      });
    };

    it('accepts valid taxpayer reference with 4 characters', () => {
      const value = 'AB12';
      expect(taxpayerReferenceRegex.test(value)).toBe(true);
    });

    it('accepts valid taxpayer reference with 20 characters', () => {
      const value = 'ABCDEFGH1234567890AB';
      expect(taxpayerReferenceRegex.test(value)).toBe(true);
    });

    it('accepts valid taxpayer reference with mixed case alphanumeric', () => {
      const validValues = ['ABC123', 'abc123', 'AbC123', '123ABC', 'A1B2C3D4', 'Test1234'];

      validValues.forEach((value) => {
        expect(taxpayerReferenceRegex.test(value)).toBe(true);
      });
    });

    it('rejects taxpayer reference with less than 4 characters', () => {
      const invalidValues = ['A', 'AB', 'A12', '123'];

      invalidValues.forEach((value) => {
        expect(taxpayerReferenceRegex.test(value)).toBe(false);
      });
    });

    it('rejects taxpayer reference with more than 20 characters', () => {
      const value = 'ABCDEFGH1234567890ABC';
      expect(taxpayerReferenceRegex.test(value)).toBe(false);
    });

    it('rejects taxpayer reference with special characters', () => {
      const invalidValues = [
        'ABC-123',
        'ABC_123',
        'ABC.123',
        'ABC@123',
        'ABC#123',
        'ABC!123',
        'ABC$123',
        'ABC%123',
      ];

      invalidValues.forEach((value) => {
        expect(taxpayerReferenceRegex.test(value)).toBe(false);
      });
    });

    it('rejects taxpayer reference with spaces', () => {
      const invalidValues = ['ABC 123', 'ABC  123', ' ABC123', 'ABC123 ', 'A BC 12 3'];

      invalidValues.forEach((value) => {
        expect(taxpayerReferenceRegex.test(value)).toBe(false);
      });
    });

    it('handles empty string correctly', () => {
      const value = '';
      expect(taxpayerReferenceRegex.test(value)).toBe(false);
    });

    it('validates boundary cases', () => {
      expect(taxpayerReferenceRegex.test('A1B2')).toBe(true);
      expect(taxpayerReferenceRegex.test('A1B2C3D4E5F6G7H8I9J0')).toBe(true);
      expect(taxpayerReferenceRegex.test('A1B')).toBe(false);
      expect(taxpayerReferenceRegex.test('A1B2C3D4E5F6G7H8I9J0K')).toBe(false);
    });

    it('blocks submit and shows an error for invalid unique taxpayer reference', async () => {
      const innBRegistrationStepOneSpy = jest.spyOn(
        { InnBRegistrationStepOne },
        'InnBRegistrationStepOne'
      );

      await submitRegisterFormWithTaxpayerReference('ABC-123');

      await waitFor(() => {
        expect(innBRegistrationStepOneSpy).not.toHaveBeenCalled();
      });
      expect(screen.getByText('Invalid input.')).toBeInTheDocument();
    });

    it('allows submit for valid unique taxpayer reference', async () => {
      const innBRegistrationStepOneSpy = jest.spyOn(
        { InnBRegistrationStepOne },
        'InnBRegistrationStepOne'
      );

      await submitRegisterFormWithTaxpayerReference('ABC1234');

      await waitFor(() => {
        expect(innBRegistrationStepOneSpy).toHaveBeenCalled();
      });
      expect(screen.queryByText('Invalid input.')).not.toBeInTheDocument();
    });
  });
});
