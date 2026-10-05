import '@testing-library/jest-dom';
import { act, fireEvent, render, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { RegistrationQuestionWithAnswer, LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';
import React from 'react';

import { EmployeeActivationSteps } from '../../page';
import { EmployeeActivation } from './employee-activation';

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

let mockUpdateResponse = {
  status: 'success',
};

const mockPreferencesResponse = {
  optIn: false,
  secondOptIn: false,
  secondOptInReq: true,
  secondPartyOptIn: false,
  thirdPartyVendorsOptIn: false,
} as any;

const mockUpdateEmployeeDetails = jest.fn();
const mockUpdateMarketingPreferences = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: (locale: LOCALES, path: string): string => `/${locale}/${path}`,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCountriesList: () => {
      return;
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    updateEmployeeDetails: jest.fn((...args) => mockUpdateEmployeeDetails(...args)),
    updateMarketingPreferences: jest.fn((...args) => mockUpdateMarketingPreferences(...args)),
    findError: serverUtils.findError,
    addressSchema: () => {
      return {
        _def: {
          shape: jest.fn(),
        },
      };
    },
    defaultQuestionsAndSchema: (...params: any) => serverUtils.defaultQuestionsAndSchema(...params),
    parseAnswersObj: () => {
      return {
        userDefinedAnswers: [
          {
            miID: 'COQU_7bc823b1-40cc-4ec6-8102-f9fc818a72a2',
            miAnswer: '1',
          },
          {
            miID: 'COQU_8933007d-a8d3-4426-bb26-d4dba18a47a9',
            miAnswer: 'TEST',
          },
        ],
        customerReferenceAnswer: '123',
      };
    },
    getVariant: jest.fn(),
    getPostCodeAddresses: jest.fn(),
    getMarketingPreferences: () => mockPreferencesResponse,
  };
});

const mockRegistrationQuestions = [
  {
    id: 'customerReferenceAnswer',
    mandatory: false,
    type: 'text',
    options: null,
    answer: '1',
    label: 'Second question 2',
  },
  {
    id: 'COQU_7bc823b1-40cc-4ec6-8102-f9fc818a72a2',
    mandatory: true,
    type: 'select',
    options: ['ceva'],
    answer: '1',
    label: 'Some random question label 2?',
  },
  {
    id: 'COQU_8933007d-a8d3-4426-bb26-d4dba18a47a9',
    mandatory: true,
    type: 'text',
    options: null,
    answer: '2',
    label: 'Registration label number two 2',
  },
  {
    id: 'COQU_2aac133e-1e87-4063-a1f1-02bbf741521f',
    mandatory: true,
    type: 'text',
    options: null,
    answer: '3',
    label: 'S',
  },
  {
    id: 'COQU_04c85e49-f28c-49e4-a569-ca94d76d0806',
    mandatory: false,
    type: 'select',
    options: ['1', '2'],
    answer: '1',
    label: 'What BB sees in dahsboard',
  },
  {
    id: 'COQU_a46ad234-5fbe-4d5f-a64b-53a7254be861',
    mandatory: true,
    type: 'select',
    options: ['1', '2', '3'],
    answer: '2',
    label: 'Label nemodificabil',
  },
] as RegistrationQuestionWithAnswer[];

const componentMockProps = {
  baseDataTestId: 'EmployeeActivationPage',
  language: 'en',
  companyRegistrationQuestions: mockRegistrationQuestions,
  locale: LOCALES.EN,
  secureUrl: 'https://secure-url.com',
} as any;

const mockProps = {
  icons: {},
  header: null,
  initialState: {
    emailAddress: 'test123@test.com',
    companyId: 'COMP_ID_123456',
    companyName: 'Test Inc',
    employeeId: 'EMP_ID_123456',
    address: {
      postCode: 'HP23 4LD',
      country: 'GB',
      addressLine5: null,
      addressLine4: 'TRING',
      addressLine3: null,
      addressLine2: null,
      addressLine1: 'Tring Hill',
    },
    activationKey: 'activation-key-123456',
    accessLevel: 'SELF',
  },
  initialStepId: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
  steps: [
    {
      id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
      component: <EmployeeActivation {...componentMockProps} />,
    },
  ],
} as any;

const defaultInitialState = JSON.parse(JSON.stringify(mockProps.initialState));
const defaultComponentProps = JSON.parse(JSON.stringify(componentMockProps));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('Employee Activation Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUpdateResponse = {
      status: 'success',
    };
    mockUpdateEmployeeDetails.mockReturnValue(mockUpdateResponse);
    Object.assign(componentMockProps, JSON.parse(JSON.stringify(defaultComponentProps)));
    mockProps.initialState = JSON.parse(JSON.stringify(defaultInitialState));
    mockProps.steps = [
      {
        id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
        component: <EmployeeActivation {...componentMockProps} />,
      },
    ];
  });

  it('renders the Employee Activation component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('EmployeeActivationPage-EmployeeActivation-Title')).toBeInTheDocument();
    expect(getByTestId('EmployeeActivationPage-EmployeeActivation-Form')).toBeInTheDocument();
  });

  it('should click button after completing the fields', async () => {
    const user = userEvent.setup();
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const titleInput = getByTestId('Title-IB-Form-Select-Button');
    const firstNameInput = getByTestId('FirstName-Form-Input');
    const lastNameInput = getByTestId('LastName-Form-Input');
    const emailInput = getByTestId('CompanyEmailAddress-Form-Input');
    const phoneNumberInput = getByTestId('PhoneNumber-Form-Input');
    const alternatePhoneNumberInput = getByTestId('Alternate-Phone-Number-Form-Input');
    const passwordInput = getByTestId('CreatePassword-Form-Input');
    const postCodeInput = getByTestId('postCode-Form-Input');
    const findAddressButton = getByTestId('CompanyAddressForm-findAddressButton');

    await waitFor(() => {
      expect(titleInput).toBeInTheDocument();
      expect(firstNameInput).toBeInTheDocument();
      expect(lastNameInput).toBeInTheDocument();
      expect(phoneNumberInput).toBeInTheDocument();
      expect(alternatePhoneNumberInput).toBeInTheDocument();
      expect(passwordInput).toBeInTheDocument();
    });

    await user.click(titleInput);
    await user.click(getByTestId('Title-auth.signup.form.nameTitles-Option'));
    titleInput.blur();
    await user.tab();

    await act(async () => {
      firstNameInput.focus();
      fireEvent.change(firstNameInput, { target: { value: 'testFirst' } });
      expect(firstNameInput).toHaveValue('testFirst');
      firstNameInput.blur();
      await userEvent.tab();

      lastNameInput.focus();
      fireEvent.change(lastNameInput, { target: { value: 'testLast' } });
      expect(lastNameInput).toHaveValue('testLast');
      lastNameInput.blur();
      await userEvent.tab();

      emailInput.focus();
      fireEvent.change(emailInput, { target: { value: 'testEmail@test.com' } });
      expect(emailInput).toHaveValue('testEmail@test.com');
      emailInput.blur();
      await userEvent.tab();

      phoneNumberInput.focus();
      fireEvent.click(phoneNumberInput);
      fireEvent.change(phoneNumberInput, { target: { value: '14811700000000' } });
      expect(phoneNumberInput).toHaveValue('14811700000000');
      phoneNumberInput.blur();
      await userEvent.tab();

      alternatePhoneNumberInput.focus();
      fireEvent.click(alternatePhoneNumberInput);
      fireEvent.change(alternatePhoneNumberInput, { target: { value: '14811700000000' } });
      expect(alternatePhoneNumberInput).toHaveValue('14811700000000');
      alternatePhoneNumberInput.blur();
      await userEvent.tab();

      await waitFor(() => {
        fireEvent.change(postCodeInput, { target: { value: 'TW8 0BE' } });
      });
      await waitFor(() => {
        fireEvent.click(findAddressButton);
      });

      passwordInput.focus();
      fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
      expect(passwordInput).toHaveValue('Test12345');
      await userEvent.tab();
    });

    await act(async () => {
      fireEvent.click(getByTestId('footer-button'));
    });

    await waitFor(() => {
      expect(getByTestId('wizard-page')).toBeInTheDocument();
    });

    expect(mockUpdateMarketingPreferences).not.toHaveBeenCalled();
    expect(mockUpdateEmployeeDetails).toHaveBeenCalledWith(
      'COMP_ID_123456',
      'EMP_ID_123456',
      'EN',
      expect.objectContaining({
        firstName: 'testFirst',
        lastName: 'testLast',
        title: 'auth.signup.form.nameTitles',
        emailAddress: 'testEmail@test.com',
        phoneNumber: '+4414811700000000',
        mobileNumber: '+4414811700000000',
        password: 'Test12345',
      }),
      '',
      'activation-key-123456',
      expect.objectContaining({
        brandCodes: ['PINN'],
        optIn: false,
        doubleOptIn: false,
        customer: expect.objectContaining({
          title: 'auth.signup.form.nameTitles',
          firstName: 'testFirst',
          lastName: 'testLast',
          language: 'en',
        }),
        sourceDetails: expect.objectContaining({
          channel: 'BB',
          journey: 'ACTIVATE',
          locale: 'UK',
        }),
      })
    );
  });

  it('should click button after completing the fields and get failed request', async () => {
    const user = userEvent.setup();
    mockUpdateResponse = { status: 'fail' };
    componentMockProps.locale = LOCALES.DE;
    componentMockProps.language = 'de';
    mockProps.initialState.address.addressLine1 = '';
    mockProps.initialState.address.addressLine4 = '';
    mockProps.initialState.address.postCode = '';
    mockProps.initialState.address.country = 'D';
    mockProps.initialState.accessLevel = 'SUPER';
    mockProps.steps = [
      {
        id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
        component: <EmployeeActivation {...componentMockProps} />,
      },
    ];
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const titleInput = getByTestId('Title-IB-Form-Select-Button');
    const firstNameInput = getByTestId('FirstName-Form-Input');
    const lastNameInput = getByTestId('LastName-Form-Input');
    const emailInput = getByTestId('CompanyEmailAddress-Form-Input');
    const phoneNumberInput = getByTestId('PhoneNumber-Form-Input');
    const alternatePhoneNumberInput = getByTestId('Alternate-Phone-Number-Form-Input');
    const passwordInput = getByTestId('CreatePassword-Form-Input');
    const postCodeInput = getByTestId('postCode-Form-Input');
    const findAddressButton = getByTestId('CompanyAddressForm-findAddressButton');

    await waitFor(() => {
      expect(titleInput).toBeInTheDocument();
      expect(firstNameInput).toBeInTheDocument();
      expect(lastNameInput).toBeInTheDocument();
      expect(phoneNumberInput).toBeInTheDocument();
      expect(alternatePhoneNumberInput).toBeInTheDocument();
      expect(passwordInput).toBeInTheDocument();
    });

    await user.click(titleInput);
    await user.click(getByTestId('Title-auth.signup.form.nameTitles-Option'));
    titleInput.blur();
    await user.tab();

    await act(async () => {
      firstNameInput.focus();
      fireEvent.change(firstNameInput, { target: { value: 'testFirst' } });
      expect(firstNameInput).toHaveValue('testFirst');
      await user.tab();

      lastNameInput.focus();
      fireEvent.change(lastNameInput, { target: { value: 'testLast' } });
      expect(lastNameInput).toHaveValue('testLast');
      await user.tab();

      emailInput.focus();
      fireEvent.change(emailInput, { target: { value: 'testEmail@test.com' } });
      expect(emailInput).toHaveValue('testEmail@test.com');
      await user.tab();

      phoneNumberInput.focus();
      fireEvent.click(phoneNumberInput);
      fireEvent.change(phoneNumberInput, { target: { value: '14811700000000' } });
      expect(phoneNumberInput).toHaveValue('14811700000000');
      await user.tab();

      alternatePhoneNumberInput.focus();
      fireEvent.click(alternatePhoneNumberInput);
      fireEvent.change(alternatePhoneNumberInput, { target: { value: '14811700000000' } });
      expect(alternatePhoneNumberInput).toHaveValue('14811700000000');
      await user.tab();

      await waitFor(() => {
        fireEvent.change(postCodeInput, { target: { value: 'TW8 0BE' } });
      });
      await waitFor(() => {
        fireEvent.click(findAddressButton);
      });

      passwordInput.focus();
      fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
      expect(passwordInput).toHaveValue('Test12345');
      await userEvent.tab();
    });

    await act(async () => {
      fireEvent.click(getByTestId('footer-button'));
    });

    await waitFor(() => {
      expect(getByTestId('wizard-page')).toBeInTheDocument();
    });

    expect(mockUpdateMarketingPreferences).not.toHaveBeenCalled();
    expect(mockUpdateEmployeeDetails).toHaveBeenCalledWith(
      'COMP_ID_123456',
      'EMP_ID_123456',
      'DE',
      expect.objectContaining({
        firstName: 'testFirst',
        lastName: 'testLast',
        title: 'auth.signup.form.nameTitles',
        emailAddress: 'testEmail@test.com',
        phoneNumber: '+4914811700000000',
        mobileNumber: '+4914811700000000',
        password: 'Test12345',
        accessLevel: 'SUPER',
      }),
      '',
      'activation-key-123456',
      expect.objectContaining({
        brandCodes: ['PINN'],
        optIn: false,
        doubleOptIn: false, // false because country is not set to 'DE' in form
        customer: expect.objectContaining({
          title: 'auth.signup.form.nameTitles',
          firstName: 'testFirst',
          lastName: 'testLast',
          language: 'de',
        }),
        sourceDetails: expect.objectContaining({
          channel: 'BB',
          journey: 'ACTIVATE',
          locale: 'DE', // Should be DE for German
        }),
      })
    );
  });

  it('should include DE locale in sourceDetails when URL locale is DE', async () => {
    const user = userEvent.setup();
    mockUpdateResponse = { status: 'success' };
    componentMockProps.locale = LOCALES.DE;
    componentMockProps.language = 'de';
    mockProps.initialState.address.addressLine1 = '';
    mockProps.initialState.address.addressLine4 = '';
    mockProps.initialState.address.postCode = '';
    mockProps.initialState.address.country = 'D';
    mockProps.steps = [
      {
        id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
        component: <EmployeeActivation {...componentMockProps} />,
      },
    ];

    const { getByTestId } = render(<Wizard {...mockProps} />);

    const titleInput = getByTestId('Title-IB-Form-Select-Button');
    const firstNameInput = getByTestId('FirstName-Form-Input');
    const lastNameInput = getByTestId('LastName-Form-Input');
    const emailInput = getByTestId('CompanyEmailAddress-Form-Input');
    const phoneNumberInput = getByTestId('PhoneNumber-Form-Input');
    const alternatePhoneNumberInput = getByTestId('Alternate-Phone-Number-Form-Input');
    const passwordInput = getByTestId('CreatePassword-Form-Input');
    const postCodeInput = getByTestId('postCode-Form-Input');
    const findAddressButton = getByTestId('CompanyAddressForm-findAddressButton');

    await waitFor(() => {
      expect(titleInput).toBeInTheDocument();
      expect(firstNameInput).toBeInTheDocument();
      expect(lastNameInput).toBeInTheDocument();
      expect(phoneNumberInput).toBeInTheDocument();
      expect(alternatePhoneNumberInput).toBeInTheDocument();
      expect(passwordInput).toBeInTheDocument();
    });

    await user.click(titleInput);
    await user.click(getByTestId('Title-auth.signup.form.nameTitles-Option'));
    titleInput.blur();
    await user.tab();

    await act(async () => {
      firstNameInput.focus();
      fireEvent.change(firstNameInput, { target: { value: 'testFirst' } });
      await user.tab();

      lastNameInput.focus();
      fireEvent.change(lastNameInput, { target: { value: 'testLast' } });
      await user.tab();

      emailInput.focus();
      fireEvent.change(emailInput, { target: { value: 'testEmail@test.com' } });
      await user.tab();

      phoneNumberInput.focus();
      fireEvent.click(phoneNumberInput);
      fireEvent.change(phoneNumberInput, { target: { value: '14811700000000' } });
      await user.tab();

      alternatePhoneNumberInput.focus();
      fireEvent.click(alternatePhoneNumberInput);
      fireEvent.change(alternatePhoneNumberInput, { target: { value: '14811700000000' } });
      await user.tab();

      await waitFor(() => {
        fireEvent.change(postCodeInput, { target: { value: 'TW8 0BE' } });
      });
      await waitFor(() => {
        fireEvent.click(findAddressButton);
      });

      passwordInput.focus();
      fireEvent.change(passwordInput, { target: { value: 'Test12345' } });
      await userEvent.tab();
    });

    await act(async () => {
      fireEvent.click(getByTestId('footer-button'));
    });

    await waitFor(() => {
      expect(mockUpdateEmployeeDetails).toHaveBeenCalledWith(
        expect.any(String),
        expect.any(String),
        'DE',
        expect.any(Object),
        '',
        expect.any(String),
        expect.objectContaining({
          sourceDetails: expect.objectContaining({
            channel: 'BB',
            journey: 'ACTIVATE',
            locale: 'DE',
          }),
        })
      );
    });
  });

  it('renders the Employee Activation component with no address predefined', () => {
    mockProps.initialState.address = null;
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('EmployeeActivationPage-EmployeeActivation-Title')).toBeInTheDocument();
    expect(getByTestId('EmployeeActivationPage-EmployeeActivation-Form')).toBeInTheDocument();
  });

  it('does not render employee questions section when companyRegistrationQuestions is an empty array', () => {
    const propsWithEmptyQuestions = {
      ...componentMockProps,
      companyRegistrationQuestions: [],
    };
    const localMockProps = {
      ...mockProps,
      steps: [
        {
          id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
          component: <EmployeeActivation {...propsWithEmptyQuestions} />,
        },
      ],
    };
    const { queryByTestId } = render(<Wizard {...localMockProps} />);

    expect(
      queryByTestId('EmployeeActivationPage-Registration-Questions-Wrapper')
    ).not.toBeInTheDocument();
  });

  it('does not render employee questions section when companyRegistrationQuestions is undefined', () => {
    const propsWithNoQuestions = {
      ...componentMockProps,
      companyRegistrationQuestions: undefined,
    };
    const localMockProps = {
      ...mockProps,
      steps: [
        {
          id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
          component: <EmployeeActivation {...propsWithNoQuestions} />,
        },
      ],
    };
    const { queryByTestId } = render(<Wizard {...localMockProps} />);

    expect(
      queryByTestId('EmployeeActivationPage-Registration-Questions-Wrapper')
    ).not.toBeInTheDocument();
  });

  it('renders employee questions section when companyRegistrationQuestions has items', () => {
    const propsWithQuestions = {
      ...componentMockProps,
      companyRegistrationQuestions: mockRegistrationQuestions,
    };
    const localMockProps = {
      ...mockProps,
      steps: [
        {
          id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
          component: <EmployeeActivation {...propsWithQuestions} />,
        },
      ],
    };
    const { getByTestId } = render(<Wizard {...localMockProps} />);

    expect(
      getByTestId('EmployeeActivationPage-Registration-Questions-Wrapper')
    ).toBeInTheDocument();
  });

  it('prefills registration questions with existing answers', async () => {
    const wizardProps = {
      icons: {},
      header: null,
      initialState: JSON.parse(JSON.stringify(defaultInitialState)),
      initialStepId: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
      steps: [
        {
          id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
          component: <EmployeeActivation {...JSON.parse(JSON.stringify(defaultComponentProps))} />,
        },
      ],
    };
    const { getByTestId } = render(<Wizard {...wizardProps} />);

    await waitFor(() => {
      expect(getByTestId('customerReferenceAnswer-Form-Input')).toHaveValue('1');
    });
    await waitFor(() => {
      expect(
        getByTestId('COQU_7bc823b1-40cc-4ec6-8102-f9fc818a72a2-IB-Form-Select-Button')
      ).toHaveTextContent('ceva');
    });
    expect(getByTestId('COQU_8933007d-a8d3-4426-bb26-d4dba18a47a9-Form-Input')).toHaveValue('2');
  });

  it('constructs mobileNumber correctly from alternatePhoneNumber', () => {
    // alternatePhoneNumber is present
    const data = {
      alternatePhoneNumber: {
        prefix: '+44',
        phoneNumber: '1234567890',
      },
    };
    const mobileNumber = data.alternatePhoneNumber.phoneNumber
      ? `${data.alternatePhoneNumber.prefix}${data.alternatePhoneNumber.phoneNumber}`
      : '';
    expect(mobileNumber).toBe('+441234567890');

    // alternatePhoneNumber is empty, so don't add +44 country prefix
    const dataEmpty = {
      alternatePhoneNumber: {
        prefix: '+44',
        phoneNumber: '',
      },
    };
    const mobileNumberEmpty = dataEmpty.alternatePhoneNumber.phoneNumber
      ? `${dataEmpty.alternatePhoneNumber.prefix}${dataEmpty.alternatePhoneNumber.phoneNumber}`
      : '';
    expect(mobileNumberEmpty).toBe('');
  });
});
