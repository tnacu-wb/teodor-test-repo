import '@testing-library/jest-dom';
import { act, render, fireEvent, waitFor } from '@testing-library/react';
import { Language, LOCALES, UserAccessLevels } from '@whitbread-eos/api';

import { AddCard } from './add-card';

const mockProps = {
  accountHolderContent: 'test',
  icons: { 'icon.arrow.left': '/' },
  cardHolderName: 'test',
  companyId: '123',
  employeeId: '123',
  calendarLabels: {},
  language: 'en' as Language,
  companyAddress: {
    addressLine1: '1',
    postCode: '1',
    country: 'DE',
  },
  accessLevel: 'SUPER' as UserAccessLevels,
  loggedEmployeeDetails: {},
  isAccountHolder: true,
  sendCardsToCardholder: true,
};

const mockEmployeeDetails = {
  id: '123',
  ghNumber: null,
  emailAddress: 'john@mailnator.com',
  position: null,
  phoneNumber: '+11111111111',
  mobileNumber: '',
  textConfirmation: false,
  title: 'Mr',
  firstName: 'John',
  lastName: 'Doe',
  centralCardId: '1',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
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
    getEmployeeDetails: () => {
      return mockEmployeeDetails;
    },
    addressSchema: () => ({
      shape: {
        addressLine1: true,
        postCode: true,
        country: true,
      },
      merge: jest.fn().mockReturnThis(),
    }),
    IBPayCardUserSchema: () => ({
      validation: jest.fn(),
      schema: {
        merge: jest.fn().mockReturnThis(),
      },
    }),
    IBPayCardLimitsSchema: () => ({
      validation: jest.fn(),
      schema: {
        merge: jest.fn().mockReturnThis(),
      },
    }),
    IBPayCardDeliverySchema: () => ({
      merge: jest.fn().mockReturnThis(),
    }),
    addCardPIBAMutation: jest.fn(),
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

const scrollIntoViewMock = jest.fn();
Object.defineProperty(window.HTMLElement.prototype, 'scrollIntoView', {
  configurable: true,
  value: scrollIntoViewMock,
});

const mockPush = jest.fn();

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    push: mockPush,
  })),
  usePathname: () => {
    return '/';
  },
  useSearchParams: jest.fn(() => ({
    get: jest.fn(() => null),
  })),
}));

window.scrollTo = jest.fn();

const { addCardPIBAMutation } = jest.requireMock('@whitbread-eos/utils/server');

describe('AddCard Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any)._satellite = { track: jest.fn() };
  });

  it('should render Add Card component', async () => {
    const { getByTestId } = render(<AddCard {...mockProps} />);

    expect(getByTestId('Inn-Business-Pay-Add-Card-page')).toBeInTheDocument();
    const continueButton = getByTestId('Continue-Button-Add-Card');
    expect(continueButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(continueButton);
    });

    await waitFor(async () => {
      expect(getByTestId('Delivery-Title')).toBeInTheDocument();
    });
  });

  it('should track satellite event on mount', () => {
    render(<AddCard {...mockProps} />);
    expect(window._satellite.track).toHaveBeenCalledWith('addNewCard');
  });

  it('should navigate to step 2 and show delivery form', async () => {
    const { getByTestId } = render(<AddCard {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');

    await act(async () => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(getByTestId('Delivery-Title')).toBeInTheDocument();
    });

    expect(getByTestId('Add-Card-Submit')).toBeInTheDocument();
  });

  it('should show back button functionality on step 2', async () => {
    const { getByTestId } = render(<AddCard {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    await act(async () => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(getByTestId('Delivery-Title')).toBeInTheDocument();
    });

    const backIcon = getByTestId('Inn-Business-Pay-Add-Card-back-icon');
    expect(backIcon).toBeInTheDocument();
  });

  it('should handle successful card submission', async () => {
    addCardPIBAMutation.mockResolvedValueOnce({
      status: 'SUCCESS',
    });

    const { getByTestId } = render(<AddCard {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    await act(async () => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(getByTestId('Add-Card-Submit')).toBeInTheDocument();
    });

    const submitButton = getByTestId('Add-Card-Submit');
    expect(submitButton).toBeInTheDocument();
    expect(addCardPIBAMutation).toBeDefined();
  });

  it('should handle failed card submission', async () => {
    addCardPIBAMutation.mockResolvedValueOnce({
      status: 'FAILED',
    });

    const { getByTestId } = render(<AddCard {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    await act(async () => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(getByTestId('Add-Card-Submit')).toBeInTheDocument();
    });

    const submitButton = getByTestId('Add-Card-Submit');
    expect(submitButton).toBeInTheDocument();
    expect(addCardPIBAMutation).toBeDefined();
  });

  it('should handle custom address delivery option', async () => {
    const { getByTestId } = render(<AddCard {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    await act(async () => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(getByTestId('Delivery-Title')).toBeInTheDocument();
    });

    const submitButton = getByTestId('Add-Card-Submit');
    expect(submitButton).toBeInTheDocument();
  });

  it('should disable submit button when submitting', async () => {
    const { getByTestId } = render(<AddCard {...mockProps} />);

    const continueButton = getByTestId('Continue-Button-Add-Card');
    await act(async () => {
      fireEvent.click(continueButton);
    });

    await waitFor(() => {
      expect(getByTestId('Add-Card-Submit')).toBeInTheDocument();
    });

    const submitButton = getByTestId('Add-Card-Submit');
    expect(submitButton).toBeInTheDocument();
    expect(submitButton).not.toBeDisabled();
  });

  it('should render with new employee ID from search params', () => {
    const mockUseSearchParams = jest.requireMock('next/navigation').useSearchParams;
    mockUseSearchParams.mockReturnValueOnce({
      get: jest.fn((param) => (param === 'employeeId' ? '456' : null)),
    });

    const { getByTestId } = render(<AddCard {...mockProps} />);
    expect(getByTestId('Inn-Business-Pay-Add-Card-page')).toBeInTheDocument();
  });
});

describe('CardUserForm costCenterSelect', () => {
  const baseProps = {
    ...mockProps,
    isCostCentreManagementEnabled: true,
    costCenters: [
      {
        costCentreUniqueCustomerId: '1',
        costCentreName: 'Cost Centre 1',
        costCentreCode: 'CC1',
      },
      {
        costCentreUniqueCustomerId: '2',
        costCentreName: 'Cost Centre 2',
        costCentreCode: 'CC2',
      },
    ],
  };

  it('renders cost center select when enabled', () => {
    const { getByText, getByTestId } = render(<AddCard {...baseProps} />);
    expect(getByText('cards.cardMgmt.costCentre.assign.label')).toBeInTheDocument();
    expect(getByTestId('Who-Container-Title')).toBeInTheDocument();
  });

  it('does not render cost center select if isCostCentreManagementEnabled is false', () => {
    const props = {
      ...baseProps,
      isCostCentreManagementEnabled: false,
    };
    const { queryByText } = render(<AddCard {...props} />);
    expect(queryByText('cards.cardMgmt.costCentre.assign.label')).not.toBeInTheDocument();
  });
});

describe('CardDeliveryForm', () => {
  it('renders when is my card', () => {
    const { getByText, getByRole } = render(<AddCard {...mockProps} />);
    expect(getByText('cards.cardMgmt.delivery.where.title')).toBeInTheDocument();
    const companyCorrespondenceAddressRadio = getByRole('radio', {
      name: 'cards.cardMgmt.delivery.where.options.companyCorrespondence',
    }) as HTMLInputElement;
    expect(companyCorrespondenceAddressRadio.value).toBe('COMPANY_CORRESPONDENCE_ADDRESS');
    expect(companyCorrespondenceAddressRadio.disabled).toBe(false);
    const cardholderAddressRadio = getByRole('radio', {
      name: /cards.cardMgmt.delivery.where.options.cardHolderAddress/i,
    }) as HTMLInputElement;
    expect(cardholderAddressRadio).toBeInTheDocument();
    expect(cardholderAddressRadio.value).toBe('CARDHOLDER_ALTERNATIVE_ADDRESS');
    expect(cardholderAddressRadio.disabled).toBe(true);
  });

  it('renders when is not my card and the user is allowed to send card to an alternative address', () => {
    const mockUseSearchParams = jest.requireMock('next/navigation').useSearchParams;
    mockUseSearchParams.mockReturnValueOnce({
      get: jest.fn((param) => (param === 'employeeId' ? '456' : null)),
    });
    const { getByText, getByRole } = render(<AddCard {...mockProps} />);
    expect(getByText('cards.cardMgmt.delivery.where.title')).toBeInTheDocument();
    const companyCorrespondenceAddressRadio = getByRole('radio', {
      name: 'cards.cardMgmt.delivery.where.options.companyCorrespondence',
    }) as HTMLInputElement;
    expect(companyCorrespondenceAddressRadio.disabled).toBe(false);
    const cardholderAddressRadio = getByRole('radio', {
      name: /cards.cardMgmt.delivery.where.options.cardHolderAddress/i,
    }) as HTMLInputElement;
    expect(cardholderAddressRadio).toBeInTheDocument();
    expect(cardholderAddressRadio.disabled).toBe(false);
  });

  it('renders when is not my card and the user is not allowed to send card to an alternative address', () => {
    const mockUseSearchParams = jest.requireMock('next/navigation').useSearchParams;
    mockUseSearchParams.mockReturnValueOnce({
      get: jest.fn((param) => (param === 'employeeId' ? '456' : null)),
    });
    const updatedProps = {
      ...mockProps,
      sendCardsToCardholder: false,
    };
    const { getByText, getByRole } = render(<AddCard {...updatedProps} />);
    expect(getByText('cards.cardMgmt.delivery.where.title')).toBeInTheDocument();
    const companyCorrespondenceAddressRadio = getByRole('radio', {
      name: 'cards.cardMgmt.delivery.where.options.companyCorrespondence',
    }) as HTMLInputElement;
    expect(companyCorrespondenceAddressRadio.disabled).toBe(false);
    const cardholderAddressRadio = getByRole('radio', {
      name: /cards.cardMgmt.delivery.where.options.cardHolderAddress/i,
    }) as HTMLInputElement;
    expect(cardholderAddressRadio).toBeInTheDocument();
    expect(cardholderAddressRadio.disabled).toBe(true);
  });
});
