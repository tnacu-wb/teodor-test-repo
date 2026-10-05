import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import {
  AddressCorrespondenceEnum,
  Language,
  UserAccessLevels,
  cardDisplayNameOptions,
  cardUserType,
  requestErrors,
  requestStatus,
} from '@whitbread-eos/api';

import { AddCard } from './add-card';

const mockPush = jest.fn().mockReturnValue(undefined);
const mockToast = jest.fn();
const mockUseSearchParams = jest.fn(() => ({
  get: jest.fn(() => null),
}));
const mockIsValidPhoneNumber = jest.fn();
const mockFormValues: Record<string, any> = {};
const formMethodsMock = {
  getValues: jest.fn((field?: string) => (field ? mockFormValues[field] : { ...mockFormValues })),
  trigger: jest.fn(() => Promise.resolve(true)),
  watch: jest.fn((field: string) => mockFormValues[field]),
  setValue: jest.fn(),
  formState: { errors: {}, isDirty: false },
  register: jest.fn(),
  handleSubmit: jest.fn(),
  reset: jest.fn(),
  control: {},
};
const addCardPIBAMutationMock = jest.fn();
const scrollIntoViewMock = jest.fn();
const parseDisplayNameMock = jest.fn(
  (option: string, title: string, firstName: string, lastName: string) => {
    switch (option) {
      case cardDisplayNameOptions.fullName:
        return `${firstName} ${lastName}`;
      case cardDisplayNameOptions.titleFullName:
        return `${title} ${firstName} ${lastName}`;
      case cardDisplayNameOptions.initialLastName:
        return `${firstName[0]} ${lastName}`;
      case cardDisplayNameOptions.titleInitialLastName:
        return `${title} ${firstName[0]} ${lastName}`;
      default:
        return `${firstName} ${lastName}`;
    }
  }
);

jest.mock('react-hook-form', () => ({
  useForm: () => formMethodsMock,
  FormProvider: ({ children }: any) => <div>{children}</div>,
}));

jest.mock('@hookform/resolvers/zod', () => ({
  zodResolver: jest.fn(() => () => ({ values: {}, errors: {} })),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  FormPage: ({ baseDataTestId, children, onBackClick }: any) => (
    <div data-testid={`${baseDataTestId}-page`}>
      {onBackClick && (
        <button data-testid={`${baseDataTestId}-back-button`} onClick={onBackClick}>
          Back
        </button>
      )}
      {children}
    </div>
  ),
  Button: ({ children, ...props }: any) => (
    <button type="button" {...props}>
      {children}
    </button>
  ),
  useToast: () => ({ toast: mockToast }),
  SanitizedContent: ({ children }: any) => <span>{children}</span>,
}));

jest.mock('next/navigation', () => ({
  useRouter: () => ({ push: mockPush }),
  usePathname: () => '/en',
  useSearchParams: () => mockUseSearchParams(),
}));

jest.mock('react-phone-number-input', () => ({
  isValidPhoneNumber: (...args: unknown[]) => mockIsValidPhoneNumber(...args),
}));

jest.mock('i18n-iso-countries', () => ({
  __esModule: true,
  default: {
    registerLocale: jest.fn(),
    alpha2ToAlpha3: jest.fn((code: string) => (code === 'GB' ? 'GBR' : code)),
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  getAuthCookie: () => 'auth-token',
  ParseDateToYMD: jest.fn((date) => date),
  useTranslation: () => ({ t: (key: string) => key }),
  getLocaleByPathname: jest.fn(() => 'en'),
  getPathForLocale: jest.fn((_locale: string, path: string) => `/${_locale}/${path}`),
  formatIBAssetsUrl: jest.fn(() => '/asset'),
  cn: jest.fn((...classes: string[]) => classes.filter(Boolean).join(' ')),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  addressSchema: () => ({
    shape: {
      addressLine1: true,
      addressLine2: true,
      postCode: true,
      country: true,
    },
    merge: jest.fn().mockReturnThis(),
  }),
  IBPayCardUserSchema: () => ({
    schema: {
      merge: jest.fn().mockReturnThis(),
    },
  }),
  IBPayCardLimitsSchema: () => ({
    schema: {
      merge: jest.fn().mockReturnThis(),
    },
  }),
  IBPayCardDeliverySchema: () => ({
    merge: jest.fn().mockReturnThis(),
  }),
  ParseDateToYMD: () => '2024-01-01',
  addCardPIBAMutation: (...args: unknown[]) => addCardPIBAMutationMock(...args),
}));

jest.mock('~components/innBusiness/forms/AddCardForms/CardUserForm', () => ({
  CardUserForm: ({ onContinue }: any) => (
    <button data-testid="CardUserForm-Continue" onClick={onContinue}>
      Continue
    </button>
  ),
  parseDisplayName: (option: string, title: string, firstName: string, lastName: string) => {
    switch (option) {
      case cardDisplayNameOptions.fullName:
        return `${firstName} ${lastName}`;
      case cardDisplayNameOptions.titleFullName:
        return `${title} ${firstName} ${lastName}`;
      case cardDisplayNameOptions.initialLastName:
        return `${firstName[0]} ${lastName}`;
      case cardDisplayNameOptions.titleInitialLastName:
        return `${title} ${firstName[0]} ${lastName}`;
      default:
        return `${firstName} ${lastName}`;
    }
  },
}));

jest.mock('~components/innBusiness/forms/AddCardForms/CardDeliveryForm', () => ({
  CardDeliveryForm: ({ className }: any) => (
    <div data-testid="Delivery-Form" data-classname={className} />
  ),
}));

jest.mock('~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields', () => ({
  CompanyAddressFields: () => <div data-testid="Company-Address-Fields" />,
}));

jest.mock('~components/innBusiness/ReviewChanges/index', () => ({
  ReviewChanges: () => <div data-testid="Review-Changes" />,
}));

Object.defineProperty(window.HTMLElement.prototype, 'scrollIntoView', {
  configurable: true,
  value: scrollIntoViewMock,
});

const baseProps = {
  accountHolderContent: 'content',
  icons: { 'icon.arrow.left.purple': '/arrow' },
  cardHolderName: 'Holder',
  companyId: 'company-1',
  employeeId: 'employee-1',
  calendarLabels: {},
  language: 'en' as Language,
  accessLevel: UserAccessLevels.SUPER,
  loggedEmployeeDetails: {
    phoneNumber: '+441234567890',
  },
  paymentCards: [],
  costCenters: [],
};

const defaultFormValues = {
  tetheredGuid: 't-guid',
  schemeCountry: 'GB',
  schemeCustomerId: '999',
  apiUserGuid: 'api-guid',
  user: cardUserType.me,
  employeeId: 'employee-1',
  cardDisplayNameOption: { value: cardDisplayNameOptions.custom },
  cardDisplayName: 'Custom Name',
  creditLimit: true,
  creditLimitNumber: '5000',
  usageRestriction: true,
  startDate: new Date('2024-01-01'),
  endDate: new Date('2024-02-01'),
  delivery: AddressCorrespondenceEnum.CardholderAlternativeAddress,
  firstName: 'John',
  lastName: 'Doe',
  title: { value: 'Mr' },
  postCode: 'AB12',
  addressLine1: 'Address 1',
  addressLine2: 'Address 2',
  addressLine3: '',
  addressLine4: '',
  addressLine5: '',
  country: 'GB',
  costCenterOption: { value: '111' },
  tetheredGuidTarget: 'target',
  schemeCountryTarget: 'GB',
};

describe('AddCard submit behaviour', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockPush.mockClear();
    mockToast.mockClear();
    addCardPIBAMutationMock.mockClear();
    Object.assign(mockFormValues, defaultFormValues);
    (window as any)._satellite = { track: jest.fn() };
    (window as any).scrollTo = jest.fn();
    formMethodsMock.trigger.mockResolvedValue(true);
    mockIsValidPhoneNumber.mockReturnValue(true);
    parseDisplayNameMock.mockClear();
  });

  it('submits successfully and navigates to confirmation when mutation succeeds', async () => {
    addCardPIBAMutationMock.mockResolvedValueOnce({ status: requestStatus.success });

    const { getByTestId } = render(<AddCard {...baseProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));
    fireEvent.click(getByTestId('Add-Card-Submit'));

    await waitFor(() => {
      expect(addCardPIBAMutationMock).toHaveBeenCalled();
    });

    const [payload, token] = addCardPIBAMutationMock.mock.calls[0];

    expect(token).toBe('auth-token');
    expect(payload.addInnBPIBACardCriteria.cardCorrespondenceAddress).toMatchObject({
      line1: 'Address 1',
      forename: 'John',
      surname: 'Doe',
    });
    expect(payload.addInnBPIBACardCriteria.cardLimit).toBe(5000);
    expect(mockPush).toHaveBeenCalledWith('/en/manage/cards/innbusiness-pay/add/confirmation');
  });

  it('navigates to fail page when mutation fails and omits custom address payload', async () => {
    addCardPIBAMutationMock.mockResolvedValueOnce({ status: requestStatus.fail });
    mockFormValues.delivery = AddressCorrespondenceEnum.CompanyCorrespondenceAddress;
    mockFormValues.cardDisplayNameOption = { value: cardDisplayNameOptions.fullName };

    const { getByTestId } = render(<AddCard {...baseProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));
    fireEvent.click(getByTestId('Add-Card-Submit'));

    await waitFor(() => {
      expect(addCardPIBAMutationMock).toHaveBeenCalled();
      expect(mockPush).toHaveBeenCalled();
    });

    const payload = addCardPIBAMutationMock.mock.calls[0][0];
    expect(payload.addInnBPIBACardCriteria.cardCorrespondenceAddress).toBeNull();
    expect(mockPush).toHaveBeenCalledWith('/en/manage/cards/innbusiness-pay/add/fail');
  });

  it('prevents submission when validation fails', async () => {
    formMethodsMock.trigger.mockResolvedValueOnce(false);

    const { getByTestId } = render(<AddCard {...baseProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));
    fireEvent.click(getByTestId('Add-Card-Submit'));

    await waitFor(() => {
      expect(formMethodsMock.trigger).toHaveBeenCalled();
    });

    expect(addCardPIBAMutationMock).not.toHaveBeenCalled();
    expect(mockPush).not.toHaveBeenCalled();
    expect(getByTestId('Add-Card-Submit')).not.toBeDisabled();
  });

  it('shows phone invalid toast and prevents submission when current user phone is invalid', async () => {
    mockIsValidPhoneNumber.mockReturnValue(false);

    const { getByTestId } = render(<AddCard {...baseProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));
    fireEvent.click(getByTestId('Add-Card-Submit'));

    await waitFor(() => {
      expect(mockToast).toHaveBeenCalledWith(
        expect.objectContaining({
          variant: 'error',
        })
      );
    });

    expect(mockIsValidPhoneNumber).toHaveBeenCalledWith('+441234567890');
    expect(addCardPIBAMutationMock).not.toHaveBeenCalled();
    expect(mockPush).not.toHaveBeenCalled();
  });

  it('validates current user local phone with default GB country', async () => {
    const localPhoneProps = {
      ...baseProps,
      loggedEmployeeDetails: {
        phoneNumber: '02071234567',
      },
    };

    const { getByTestId } = render(<AddCard {...localPhoneProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));
    fireEvent.click(getByTestId('Add-Card-Submit'));

    await waitFor(() => {
      expect(addCardPIBAMutationMock).toHaveBeenCalled();
    });

    expect(mockIsValidPhoneNumber).toHaveBeenCalledWith('02071234567', 'GB');
  });

  it('scrolls delivery step into view and returns to details step via back button', async () => {
    const { getByTestId } = render(<AddCard {...baseProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));

    await waitFor(() => {
      expect(scrollIntoViewMock).toHaveBeenCalledWith({ behavior: 'smooth', block: 'start' });
    });

    expect((window as any).scrollTo).not.toHaveBeenCalled();

    expect(getByTestId('Delivery-Form')).toHaveAttribute('data-classname', '');

    fireEvent.click(getByTestId('Inn-Business-Pay-Add-Card-back-button'));

    await waitFor(() => {
      expect(getByTestId('Delivery-Form')).toHaveAttribute('data-classname', 'hidden');
    });
  });

  it('should keep delivery step visible when Continue is clicked multiple times', async () => {
    const { getByTestId } = render(<AddCard {...baseProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));
    fireEvent.click(getByTestId('CardUserForm-Continue'));

    await waitFor(() => {
      expect(getByTestId('Delivery-Form')).toHaveAttribute('data-classname', '');
    });

    expect(getByTestId('Inn-Business-Pay-Add-Card-back-button')).toBeInTheDocument();
    expect(getByTestId('Add-Card-Submit')).toBeInTheDocument();
  });

  it('submits with existing user details and cost centre fallback', async () => {
    mockFormValues.user = cardUserType.existing;
    mockFormValues.employeeId = 'employee-42';
    mockFormValues.cardDisplayNameOption = { value: cardDisplayNameOptions.fullName };
    mockFormValues.cardDisplayName = '';
    mockFormValues.creditLimit = false;
    mockFormValues.usageRestriction = false;
    mockFormValues.costCenterOption = { value: 'none' };

    addCardPIBAMutationMock.mockResolvedValueOnce({ status: requestStatus.success });

    const { getByTestId } = render(<AddCard {...baseProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));
    fireEvent.click(getByTestId('Add-Card-Submit'));

    await waitFor(() => {
      expect(addCardPIBAMutationMock).toHaveBeenCalled();
    });

    const payload = addCardPIBAMutationMock.mock.calls[0][0];

    expect(payload.addInnBPIBACardCriteria.companyAccountId).toBe(baseProps.companyId);
    expect(payload.addInnBPIBACardCriteria.employeeAccountId).toBe('employee-42');
    expect(payload.addInnBPIBACardCriteria.cardLimit).toBeNull();
    expect(payload.addInnBPIBACardCriteria.restrictCardUsage).toBe(false);
  });

  it('shows phone invalid toast from mutation error code', async () => {
    addCardPIBAMutationMock.mockResolvedValueOnce({
      status: requestStatus.fail,
      errorCode: requestErrors.phoneNumberInvalid,
    });

    const { getByTestId } = render(<AddCard {...baseProps} />);

    fireEvent.click(getByTestId('CardUserForm-Continue'));
    fireEvent.click(getByTestId('Add-Card-Submit'));

    await waitFor(() => {
      expect(mockToast).toHaveBeenCalledWith(
        expect.objectContaining({
          variant: 'error',
        })
      );
    });

    expect(addCardPIBAMutationMock).toHaveBeenCalled();
    expect(mockPush).not.toHaveBeenCalled();
  });
});
