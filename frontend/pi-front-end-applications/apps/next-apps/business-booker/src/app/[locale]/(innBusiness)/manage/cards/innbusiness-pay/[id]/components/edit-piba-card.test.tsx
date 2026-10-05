import '@testing-library/jest-dom';
import { render, fireEvent, act, waitFor } from '@testing-library/react';
import { LOCALES, Scheme, RegistrationRole, Language } from '@whitbread-eos/api';

import { EditPIBACard } from './edit-piba-card';

const mockProps = {
  cardDetails: {
    cardHolderName: 'Ms Testuser Demo',
    cardLimit: 8000,
    cardId: 39166,
    cardNumber: '308950*********6492',
    myCard: false,
    activated: false,
    status: 'CURRENT',
    primaryUserId: 783995,
    userId: 783995,
    expiryDate: '2028-07-01T00:00:00.000+00:00',
    title: 'Ms',
    firstName: 'Testuser',
    lastName: 'Demo',
    email: 'ib_piuk_demo11@yopmaill.com',
    cardAction:
      'CardAlternativeCardAddress,CardCancel,CardChangeOwnerShipOf,CardEditUpdate,CardEditUpdateCardLimit,CardEditUpdateDisplayName,CardEditUpdateRestrictCardUsage,CardInvite,CardReplaceNewNumber',
    registeredUsers: [],
    cardRestriction: { startDate: undefined, endDate: undefined, restrictCardUsage: false },
    amountSpend: { amount: 0, currencyCode: '826' },
  },
  sendCardsToCardholder: true,
  icons: { 'icon.arrow.left': '/' },
  calendarLabels: {},
  accountDetails: {
    tetheredGuid: 'test-guid',
    accountNumber: '123456',
    scheme: 'GB' as Scheme,
    registrationRoles: [RegistrationRole.CardHolder, RegistrationRole.AccountHolder],
  },
  companyDetails: {
    requestedCompany: {
      companyDetails: {
        companySector: 'test-sector',
        numberOfEmployees: 100,
        companyAddress: {
          addressLine1: 'Line 1',
          addressLine2: 'Line 2',
          addressLine3: 'Line 3',
          addressLine4: 'Line 4',
          addressLine5: 'Line 5',
          postCode: 'AB1 2CD',
          country: 'GB',
        },
      },
    },
  },
  language: 'en' as Language,
};

const mockIsDirty = { value: false };

jest.mock('react-hook-form', () => {
  const serverUtils = jest.requireActual('react-hook-form');
  return {
    ...serverUtils,
    useForm: () => ({
      control: {},
      formState: { errors: {}, isDirty: mockIsDirty.value },
      trigger: jest.fn(),
      clearErrors: jest.fn(),
      setValue: jest.fn(),
      getValues: () => {
        return {
          cardLimit: false,
          creditLimitNumber: 0,
          usageRestriction: false,
          startDate: null,
          endDate: null,
        };
      },
      handleSubmit: jest.fn(),
      watch: jest.fn(),
    }),
    Controller: jest.fn(({ render }) => render({ field: {} })),
    FormProvider: jest.fn(({ children }) => <div>{children}</div>),
  };
});

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    IBPayCardLimitsSchema: () => ({
      validation: jest.fn().mockReturnValue(true),
      schema: {
        merge: jest.fn().mockReturnThis(),
      },
    }),
    findError: serverUtils.findError,
    ParseDateToYMD: serverUtils.parseDateToYMD,
    updatePIBACardMutation: jest.fn(),
  };
});

jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: () => <div data-testid="ReviewChanges" />,
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
  useToast: () => ({
    toast: jest.fn((text: string) => <span>{text}</span>),
  }),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => jest.fn(),
}));

window.scrollTo = jest.fn();

describe('EditPIBACard Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockIsDirty.value = false;
    (window as any)._satellite = { track: jest.fn() };
  });

  it('should not render ReviewChanges when form is not dirty', async () => {
    const { getByTestId, queryByTestId } = render(<EditPIBACard {...mockProps} />);

    expect(getByTestId('Inn-Business-Pay-Edit-Card-page')).toBeInTheDocument();
    expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('should render ReviewChanges when form is dirty', async () => {
    mockIsDirty.value = true;
    const { getByTestId } = render(<EditPIBACard {...mockProps} />);

    expect(getByTestId('ReviewChanges')).toBeInTheDocument();
  });

  it('should hide ReviewChanges when cancel card dialog is opened', async () => {
    mockIsDirty.value = true;
    const { getByTestId, queryByTestId } = render(<EditPIBACard {...mockProps} />);

    expect(getByTestId('ReviewChanges')).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(getByTestId('Cancel-Card-Button'));
    });

    expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('should render EditPIBACard component', async () => {
    mockIsDirty.value = true;
    const { getByTestId } = render(<EditPIBACard {...mockProps} />);

    const submitButton = getByTestId('Edit-Card-Submit');

    await waitFor(async () => {
      expect(submitButton).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(submitButton);
    });

    expect(getByTestId('Inn-Business-Pay-Edit-Card-page')).toBeInTheDocument();
    expect(getByTestId('Cancel-Card-Button')).toBeInTheDocument();
  });

  it('should not render Cancel Card option when the user is not account holder', async () => {
    const newMockProps = {
      ...mockProps,
      accountDetails: {
        tetheredGuid: 'test-guid',
        accountNumber: '123456',
        scheme: 'GB' as Scheme,
        registrationRoles: [RegistrationRole.CardHolder],
      },
    };
    const { queryByTestId } = render(<EditPIBACard {...newMockProps} />);

    expect(queryByTestId('Cancel-Card-Button')).not.toBeInTheDocument();
  });

  it('should not render Cancel Card option when the card status is cancelled', async () => {
    const newMockProps = {
      ...mockProps,
      cardDetails: {
        ...mockProps.cardDetails,
        status: 'CANCELLED',
      },
    };
    const { queryByTestId } = render(<EditPIBACard {...newMockProps} />);

    expect(queryByTestId('Cancel-Card-Button')).not.toBeInTheDocument();
  });

  it('should render EditPIBACard component with employee details', async () => {
    mockIsDirty.value = true;
    const { getByTestId } = render(
      <EditPIBACard
        {...mockProps}
        employeeDetails={{
          title: 'test@test.com',
          firstName: 'test',
          lastName: 'test',
          emailAddress: 'test@test.com',
        }}
      />
    );

    const submitButton = getByTestId('Edit-Card-Submit');

    await waitFor(async () => {
      expect(submitButton).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(submitButton);
    });

    expect(getByTestId('Inn-Business-Pay-Edit-Card-page')).toBeInTheDocument();
  });
});
