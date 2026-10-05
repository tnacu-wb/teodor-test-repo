import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import type { RegisterPersonalDetails } from '@whitbread-eos/api';
import * as yup from 'yup';

import { render, userEvent, fireEvent } from '../../utils/test-utils';
import RegisterPagePi from './page.pi';

const mockReservationId = 'test-reservationId';
const mockEncodedReservationId = 'encoded-reservationId';
const mockReservationId2 = 'test-reservationId2';
const mockEncodedReservationId2 = 'encoded-reservationId2';
const mockGetCookie = jest.fn();
const mockSetCookie = jest.fn();
const mockCustomLocale = jest.fn();
const mockDecodeFromBase64 = jest.fn();
const mockValidateBasketIdInCookie = jest.fn();
const mockUseFeatureToggle = jest.fn(() => ({
  release_basket_ids_cookie_validation: true,
}));
const mockMutate = jest.fn();
const queryClient = new ReactQuery.QueryClient();

const getCountriesData = {
  data: {
    countries: {
      countries: [
        {
          countryCode: 'AT',
          countryCodeLegacy: 'A',
          countryName: 'Austria',
          dialingCode: '+43',
          flagSrc: '',
          passportRequired: true,
        },
        {
          countryCode: 'GB',
          countryCodeLegacy: 'GB',
          countryName: 'United Kingdom (the)',
          dialingCode: '+44',
          flagSrc: '',
          passportRequired: true,
        },
        {
          countryCode: 'DE',
          countryCodeLegacy: 'D',
          countryName: 'Germany',
          dialingCode: '+49',
          flagSrc: '',
          passportRequired: true,
        },
        {
          countryCode: 'RO',
          countryCodeLegacy: 'RO',
          countryName: 'Romania',
          dialingCode: '+40',
          flagSrc: '',
          passportRequired: false,
        },
      ],
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error country selection',
  },
};

const getPostCodeAddresesData = {
  data: {
    partialAddress: [
      {
        addressText:
          'Prime Minister & First Lord of the Treasury, 10 Downing Street, LONDON SW1A 2AA',
        id: 'GBB|926690f9-91c9-4e57-a1fd-a04b84d923b0|7.730lOGBBEAbnBwAAAAABAwEAAAABsApUEgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAAc3cxYSAyYWEAAAAAAA--$8',
      },
      {
        addressText: 'Star Commerce Partners Ltd, 48 Downing Street, LONDON SW1A 2AA',
        id: 'GBB|926690f9-91c9-4e57-a1fd-a04b84d923b0|7.7309OGBBEAbnBwAAAAABAwEAAAACdp1sEgAhEAYRAKEAAgAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAAc3cxYSAyYWEAAAAAAA--$8',
      },
    ],
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'Error Address',
  },
};

const getPostCodeAddresesInfoData = {
  data: {
    formattedAddress: {
      addressLine1: '10 Downing Street',
      addressLine2: null,
      addressLine3: null,
      addressLine4: 'London',
      addressLine5: null,
      companyName: 'Prime Minister & First Lord of the Treasury',
      country: 'GB',
      label: 'Prime Minister & First Lord of the Treasury, 10 Downing Street, LONDON, SW1A 2AA',
      postalCode: 'SW1A 2AA',
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'Error formattedAddress',
  },
};

function mockUseQueryRequest(queryKey: string[]) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    switch (key) {
      case 'GetCountries':
        return getCountriesData;
      case 'addresses':
        return getPostCodeAddresesData;
      case 'addresses-info':
        return getPostCodeAddresesInfoData;
      default:
        return {};
    }
  }
}

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: mockGetCookie,
    set: mockSetCookie,
  }));
});

jest.mock('@whitbread-eos/organisms', () => ({
  SearchContainer: () => <div data-testid="Search" />,
}));

jest.mock('./piFormConfig/formValidation', () => ({
  __esModule: true,
  default: () => {
    return {
      formValidationObject: {},
      formValidationSchema: yup.object().shape({}),
    };
  },
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: () => <div></div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  useQuery: () => mockUseQueryRequest,
  useMutationRequest: () => mockMutationResponse,
  useQueryRequest: () => ({}),
  useFeatureSwitch: () => true,
  useFeatureToggle: () => mockUseFeatureToggle(),
  getCookie: (name: string) => mockGetCookie(name),
  decodeFromBase64: (val: string) => mockDecodeFromBase64(val),
  validateBasketIdInCookie: (basketRef: string) => mockValidateBasketIdInCookie(basketRef),
}));

const mockMutationResponse = {
  mutation: {
    mutate: mockMutate,
  },
  isSuccess: false,
  isError: false,
  error: { message: '' },
  isLoading: false,
  data: {},
};

const mockRouter = {
  push: jest.fn(),
  query: {},
  asPath: '/en/account/register',
};

const mockRouterWithReservationId = {
  push: jest.fn(),
  query: {
    reservationId: mockReservationId,
  },
  asPath: '/en/account/register',
};

const mockProps = {
  queryClient,
  router: mockRouter as any,
};

const mockPropsWithReservationId = {
  queryClient,
  router: mockRouterWithReservationId as any,
};

describe('Register PI ', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockMutationResponse.isSuccess = false;
    mockMutationResponse.isError = false;
    mockMutationResponse.isLoading = false;
    mockMutationResponse.data = {};
    // Set default feature toggle return value
    mockUseFeatureToggle.mockReturnValue({
      release_basket_ids_cookie_validation: true,
    });
  });

  it('should render register page skeleton', async () => {
    const { findByTestId } = render(<RegisterPagePi {...mockProps} />);

    expect(await findByTestId('RegisterPIPage-Wrapper')).toBeInTheDocument();
  });

  it('should render register page skeleton in DE', () => {
    mockCustomLocale.mockReturnValue({
      language: 'de',
      country: 'de',
    });

    const { getByTestId } = render(<RegisterPagePi {...mockProps} />);

    expect(getByTestId('RegisterPIPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('RegisterPIPage-CreateAccount')).toBeInTheDocument();
  });

  it('should show error message when register mutation returns an error', async () => {
    mockMutationResponse.isError = true;

    const { getByText } = render(<RegisterPagePi {...mockProps} />);

    expect(getByText('bart.accountRegister.ErrorCode.7007.globalMessage')).toBeInTheDocument();
  });

  it('fill in address field and trigger country dropdown', async () => {
    const user = userEvent.setup();

    const { getByTestId, getByText, container } = render(<RegisterPagePi {...mockProps} />);

    const manualAddress = getByText('account.register.enterManualAddress');

    await user.click(manualAddress);

    const radioOptionHomeAddress = getByTestId('RegisterPIPage-AddressSelection-CompanyAddress');

    await user.click(radioOptionHomeAddress);

    const inputPostAlCode = getByTestId('input-postalCode');

    const postCode = 'HA3 5RP' as string;
    fireEvent.change(inputPostAlCode, { target: { value: postCode } });

    expect(inputPostAlCode).toHaveValue(postCode);

    const dropdownToggle = getByTestId('DropdownComp-Country-menuButton');

    const selector = container.querySelector('[aria-haspopup="menu"]');

    await user.click(dropdownToggle);

    expect(selector).toBeInTheDocument();

    expect(getByTestId('DropdownComp-Country-entireList')).toBeInTheDocument();
  });

  it('should NOT show Manual address for DE', async () => {
    mockCustomLocale.mockReturnValue({
      language: 'de',
      country: 'de',
    });

    const { queryByTestId } = render(<RegisterPagePi {...mockProps} />);

    const manualAddressToggle = queryByTestId('RegisterPIPage-ManualAddressToggle');

    expect(manualAddressToggle).not.toBeInTheDocument();
  });

  it('should show loading when mutation is in loading or success state', async () => {
    mockMutationResponse.isSuccess = true;
    mockMutationResponse.isLoading = true;

    const { getByText } = render(<RegisterPagePi {...mockProps} />);

    expect(getByText('booking.loading')).toBeInTheDocument();
  });

  it('should fill the first name and last name', async () => {
    const { getByTestId } = render(<RegisterPagePi {...mockProps} />);

    const inputFirstName = getByTestId('input-firstName');
    fireEvent.change(inputFirstName, { target: { value: 'Stefan' } });

    const inputLastName = getByTestId('input-lastName');
    fireEvent.change(inputLastName, { target: { value: 'Ciora' } });

    expect(inputFirstName).toHaveValue('Stefan');
    expect(inputLastName).toHaveValue('Ciora');
  });

  it('should enter email and phone number', async () => {
    const { getByTestId } = render(<RegisterPagePi {...mockProps} />);

    const inputEmail = getByTestId('input-email');
    fireEvent.change(inputEmail, { target: { value: 'stefan.ciora@whitbread.com' } });

    const inputPhone = getByTestId('RegisterPIPage-Mobile-phoneNumber');
    fireEvent.change(inputPhone, { target: { value: '+40728954441' } });

    expect(inputEmail).toHaveValue('stefan.ciora@whitbread.com');
    expect(inputPhone).toHaveValue('+40728954441');
  });

  it('should interact with manual address entry', async () => {
    const user = userEvent.setup();

    const { getByText, getByTestId } = render(<RegisterPagePi {...mockProps} />);

    const manualAddress = getByText('account.register.enterManualAddress');
    await user.click(manualAddress);

    const radioOptionHomeAddress = getByTestId('RegisterPIPage-AddressSelection-PersonalAddress');
    await user.click(radioOptionHomeAddress);

    const inputAddressLine1 = getByTestId('input-addressLine1');
    fireEvent.change(inputAddressLine1, { target: { value: 'Street 22 December' } });

    const inputPostAlCode = getByTestId('input-postalCode');
    const postCode = '20097' as string;
    fireEvent.change(inputPostAlCode, { target: { value: postCode } });

    expect(inputPostAlCode).toHaveValue(postCode);
  });

  it('should check terms checkbox and submit the form', async () => {
    const user = userEvent.setup();

    const { getByText, getByTestId } = render(<RegisterPagePi {...mockProps} />);

    const termsCheckbox = getByText('account.register.checkboxPrivacy');
    await user.click(termsCheckbox);

    const submitBtn = getByTestId('RegisterPIPage-CreateAccount');
    await user.click(submitBtn);

    mockRouter.push('/gb/en/creating-your-account.html');
    expect(mockRouter.push).toHaveBeenCalledWith('/gb/en/creating-your-account.html');
  });

  it('should trigger success mutation when submit is successful', async () => {
    mockMutationResponse.isError = false;
    mockMutationResponse.isSuccess = true;
    mockMutationResponse.isLoading = false;
    mockMutationResponse.data = {
      success: true,
      sessionId: 'O3wSIa0zSxx3INyh',
      customerId: 'testdev2123@mailinator.com',
      existingCompany: false,
      existingEmployee: false,
    };

    render(<RegisterPagePi {...mockProps} />);

    mockRouter.push('/gb/en/creating-your-account.html');
    expect(mockRouter.push).toHaveBeenCalledWith('/gb/en/creating-your-account.html');
  });

  it('should include basket reference in create account mutation parameters', async () => {
    const user = userEvent.setup();
    mockGetCookie.mockReturnValue(mockEncodedReservationId);
    mockDecodeFromBase64.mockReturnValue(mockReservationId);
    const { getByTestId } = render(<RegisterPagePi {...mockPropsWithReservationId} />);

    const submitBtn = getByTestId('RegisterPIPage-CreateAccount');
    await user.click(submitBtn);

    expect(mockMutate).toHaveBeenCalledTimes(1);
    expect(mockMutate).toHaveBeenCalledWith(
      expect.objectContaining({ basketReference: mockReservationId })
    );
  });

  it('should not include basket reference in create account mutation parameters when no reservation Id in query', async () => {
    const user = userEvent.setup();
    const { getByTestId } = render(<RegisterPagePi {...mockProps} />);

    const submitBtn = getByTestId('RegisterPIPage-CreateAccount');
    await user.click(submitBtn);

    expect(mockMutate).toHaveBeenCalledTimes(1);
    expect(mockMutate).not.toHaveBeenCalledWith(
      expect.objectContaining({ basketReference: expect.anything() })
    );
  });

  it('should not include basket reference in create account mutation parameters when no reservation Id in cookies', async () => {
    const user = userEvent.setup();
    mockGetCookie.mockReturnValue(null);
    mockDecodeFromBase64.mockReturnValue(null);
    const { getByTestId } = render(<RegisterPagePi {...mockPropsWithReservationId} />);

    const submitBtn = getByTestId('RegisterPIPage-CreateAccount');
    await user.click(submitBtn);

    expect(mockMutate).toHaveBeenCalledTimes(1);
    expect(mockMutate).not.toHaveBeenCalledWith(
      expect.objectContaining({ basketReference: expect.anything() })
    );
  });

  it('should not include basket reference in create account mutation parameters when reservation Id in cookies and query do not match', async () => {
    const user = userEvent.setup();
    mockGetCookie.mockReturnValue(mockEncodedReservationId2);
    mockDecodeFromBase64.mockReturnValue(mockReservationId2);
    const { getByTestId } = render(<RegisterPagePi {...mockPropsWithReservationId} />);

    const submitBtn = getByTestId('RegisterPIPage-CreateAccount');
    await user.click(submitBtn);

    expect(mockMutate).toHaveBeenCalledTimes(1);
    expect(mockMutate).not.toHaveBeenCalledWith(
      expect.objectContaining({ basketReference: expect.anything() })
    );
  });

  describe('Basket Access Validation', () => {
    beforeEach(() => {
      jest.clearAllMocks();
      mockCustomLocale.mockReturnValue({
        language: 'en',
        country: 'gb',
      });
      mockMutationResponse.isSuccess = false;
      mockMutationResponse.isError = false;
      mockMutationResponse.isLoading = false;
      mockMutationResponse.data = {};
      mockValidateBasketIdInCookie.mockReturnValue(true);
      mockDecodeFromBase64.mockReturnValue(mockReservationId);
      mockGetCookie.mockReturnValue(mockEncodedReservationId);
      // Default feature toggle to enabled
      mockUseFeatureToggle.mockReturnValue({
        release_basket_ids_cookie_validation: true,
      });
    });

    it('should validate basket access when basketReference exists', () => {
      mockGetCookie.mockReturnValue(mockEncodedReservationId);
      mockDecodeFromBase64.mockReturnValue(mockReservationId);
      mockValidateBasketIdInCookie.mockReturnValue(true);

      render(<RegisterPagePi {...mockPropsWithReservationId} />);

      expect(mockValidateBasketIdInCookie).toHaveBeenCalledWith(mockReservationId);
    });

    it('should not validate basket access when basketReference is undefined', () => {
      mockGetCookie.mockReturnValue(null);
      mockDecodeFromBase64.mockReturnValue(null);

      render(<RegisterPagePi {...mockProps} />);

      expect(mockValidateBasketIdInCookie).not.toHaveBeenCalled();
    });

    it('should set hasBasketAccess to true when basket validation passes', () => {
      mockGetCookie.mockReturnValue(mockEncodedReservationId);
      mockDecodeFromBase64.mockReturnValue(mockReservationId);
      mockValidateBasketIdInCookie.mockReturnValue(true);

      const { container } = render(<RegisterPagePi {...mockPropsWithReservationId} />);

      expect(mockValidateBasketIdInCookie).toHaveBeenCalledWith(mockReservationId);
      expect(container).toBeInTheDocument();
    });

    it('should set hasBasketAccess to false when basket validation fails', () => {
      mockGetCookie.mockReturnValue(mockEncodedReservationId);
      mockDecodeFromBase64.mockReturnValue(mockReservationId);
      mockValidateBasketIdInCookie.mockReturnValue(false);

      const { container } = render(<RegisterPagePi {...mockPropsWithReservationId} />);

      expect(mockValidateBasketIdInCookie).toHaveBeenCalledWith(mockReservationId);
      expect(container).toBeInTheDocument();
    });

    it('should not fetch booking data when hasBasketAccess is false', () => {
      mockGetCookie.mockReturnValue(mockEncodedReservationId);
      mockDecodeFromBase64.mockReturnValue(mockReservationId);
      mockValidateBasketIdInCookie.mockReturnValue(false);

      render(<RegisterPagePi {...mockPropsWithReservationId} />);

      // The useQueryRequest should be called but with enabled: false
      expect(mockValidateBasketIdInCookie).toHaveBeenCalledWith(mockReservationId);
    });

    it('should fetch booking data only when both hasBasketAccess and basketReference are truthy', () => {
      mockGetCookie.mockReturnValue(mockEncodedReservationId);
      mockDecodeFromBase64.mockReturnValue(mockReservationId);
      mockValidateBasketIdInCookie.mockReturnValue(true);

      const { container } = render(<RegisterPagePi {...mockPropsWithReservationId} />);

      expect(mockValidateBasketIdInCookie).toHaveBeenCalledWith(mockReservationId);
      expect(container).toBeInTheDocument();
    });

    describe('Feature Toggle Tests', () => {
      beforeEach(() => {
        mockGetCookie.mockReturnValue(mockEncodedReservationId);
        mockDecodeFromBase64.mockReturnValue(mockReservationId);
      });

      it('should NOT validate basket when feature toggle is disabled', () => {
        mockUseFeatureToggle.mockReturnValue({
          release_basket_ids_cookie_validation: false,
        });

        render(<RegisterPagePi {...mockPropsWithReservationId} />);

        // validateBasketIdInCookie should not be called when toggle is off
        expect(mockValidateBasketIdInCookie).not.toHaveBeenCalled();
      });

      it('should validate basket when feature toggle is enabled', () => {
        mockUseFeatureToggle.mockReturnValue({
          release_basket_ids_cookie_validation: true,
        });
        mockValidateBasketIdInCookie.mockReturnValue(true);

        render(<RegisterPagePi {...mockPropsWithReservationId} />);

        // validateBasketIdInCookie should be called when toggle is on
        expect(mockValidateBasketIdInCookie).toHaveBeenCalledWith(mockReservationId);
      });

      it('should set hasBasketAccess to false when feature toggle is disabled', () => {
        mockUseFeatureToggle.mockReturnValue({
          release_basket_ids_cookie_validation: false,
        });
        mockValidateBasketIdInCookie.mockReturnValue(true);

        const { container } = render(<RegisterPagePi {...mockPropsWithReservationId} />);

        // hasBasketAccess should be false when toggle is off (even if validation would pass)
        expect(container).toBeInTheDocument();
        // The validation should not even be called
        expect(mockValidateBasketIdInCookie).not.toHaveBeenCalled();
      });
    });
  });
});
describe('registerAccount', () => {
  it('should call registerMutation.mutate with correct data and captchaToken', () => {
    const mockRegisterMutation = { mutate: jest.fn() };
    const mockCurrentCountry = 'gb';
    const mockCurrentLang = 'en';
    const mockBasketReference = 'basketRef';
    const mockCaptchaToken = 'captcha-token';

    // Mock dependencies
    const data: RegisterPersonalDetails = {
      companyName: 'Test Company',
      addressLine1: 'Line 1',
      addressLine2: 'Line 2',
      addressLine3: 'Line 3',
      addressLine4: 'Line 4',
      addressSelection: 'companyAddress',
      cityName: 'London',
      countryCode: 'GB',
      postalCode: 'SW1A 2AA',
      title: 'Mr',
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      phone: '+441234567890',
      password: 'password123',
      acceptFutureMailing: true,
    };

    // Simulate the registerAccount callback
    const registerAccount = (data: RegisterPersonalDetails, captchaToken?: string) => {
      mockRegisterMutation.mutate({
        country: mockCurrentCountry,
        language: mockCurrentLang,
        companyName: data.companyName,
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2,
        addressLine3: data.addressLine3,
        addressLine4: data.addressLine4,
        addressType: data.addressSelection,
        cityName: data.cityName,
        countryCode: data.countryCode,
        postalCode: data.postalCode,
        title: data.title,
        firstName: data.firstName,
        lastName: data.lastName,
        emailAddress: data.email,
        mobile: data.phone,
        password: data.password,
        captcha: captchaToken,
        acceptFutureMailing: data.acceptFutureMailing,
        basketReference: mockBasketReference,
      });
    };

    registerAccount(data, mockCaptchaToken);

    expect(mockRegisterMutation.mutate).toHaveBeenCalledWith(
      expect.objectContaining({
        country: mockCurrentCountry,
        language: mockCurrentLang,
        companyName: 'Test Company',
        addressLine1: 'Line 1',
        addressLine2: 'Line 2',
        addressLine3: 'Line 3',
        addressLine4: 'Line 4',
        addressType: 'companyAddress',
        cityName: 'London',
        countryCode: 'GB',
        postalCode: 'SW1A 2AA',
        title: 'Mr',
        firstName: 'John',
        lastName: 'Doe',
        emailAddress: 'john.doe@test.com',
        mobile: '+441234567890',
        password: 'password123',
        captcha: mockCaptchaToken,
        acceptFutureMailing: true,
        basketReference: mockBasketReference,
      })
    );
  });

  it('should not convert countryCode "DE" to legacy code "D"', () => {
    const mockRegisterMutation = { mutate: jest.fn() };
    const mockCurrentCountry = 'de';
    const mockCurrentLang = 'de';
    const mockBasketReference = 'basketRef';

    const data: RegisterPersonalDetails = {
      companyName: 'Firma',
      addressLine1: 'Straße 1',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressSelection: 'homeAddress',
      cityName: 'Berlin',
      countryCode: 'DE',
      postalCode: '10115',
      title: 'Herr',
      firstName: 'Max',
      lastName: 'Mustermann',
      email: 'max@mustermann.de',
      phone: '+491234567890',
      password: 'geheim',
      acceptFutureMailing: false,
    };

    const registerAccount = (data: RegisterPersonalDetails) => {
      mockRegisterMutation.mutate({
        country: mockCurrentCountry,
        language: mockCurrentLang,
        companyName: data.companyName,
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2,
        addressLine3: data.addressLine3,
        addressLine4: data.addressLine4,
        addressType: 'homeAddress',
        cityName: data.cityName,
        countryCode: data.countryCode, // should remain 'DE' as no longer need to convert to 'D' (legacy BART country code)
        postalCode: data.postalCode,
        title: data.title,
        firstName: data.firstName,
        lastName: data.lastName,
        emailAddress: data.email,
        mobile: data.phone,
        password: data.password,
        captcha: undefined,
        acceptFutureMailing: data.acceptFutureMailing,
        basketReference: mockBasketReference,
      });
    };

    registerAccount(data);

    expect(mockRegisterMutation.mutate).toHaveBeenCalledWith(
      expect.objectContaining({
        countryCode: 'DE',
      })
    );
    expect(mockRegisterMutation.mutate).not.toHaveBeenCalledWith(
      expect.objectContaining({
        countryCode: 'D',
      })
    );
  });
});

describe('remove separate marketing preferences', () => {
  const mockMarketingMutation = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should not call separate marketing preferences mutation after registration', () => {
    mockMutationResponse.isSuccess = true;
    mockMutationResponse.data = { success: true };

    render(<RegisterPagePi {...mockProps} />);

    // Verify only one useMutationRequest is being used (REGISTER_USER_ACCOUNT)
    // No separate REGISTER_USER_MARKETING call should be made
    expect(mockMarketingMutation).not.toHaveBeenCalled();
  });
});
