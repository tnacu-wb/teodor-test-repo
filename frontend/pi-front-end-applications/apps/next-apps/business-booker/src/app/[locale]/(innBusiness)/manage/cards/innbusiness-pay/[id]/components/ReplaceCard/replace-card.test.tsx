import '@testing-library/jest-dom';
import { render, act, fireEvent } from '@testing-library/react';
import { Language, Scheme, LOCALES } from '@whitbread-eos/api';

import { ReplaceCard, getErrorMessage } from './replace-card';

const mockProps = {
  open: true,
  onOpenChange: jest.fn(),
  icons: {
    'icon.notification.alert': '/path/to/error-icon.svg',
  },
  cardDetails: {
    myCard: false,
  },
  language: 'en' as Language,
  accountDetails: { tetheredGuid: 'test-guid', accountNumber: '123456', scheme: 'GB' as Scheme },
  correspondenceAddress: {
    addressLine1: 'Line 1',
    addressLine2: 'Line 2',
    addressLine3: 'Line 3',
    addressLine4: 'Line 4',
    addressLine5: 'Line 5',
    postCode: 'AB1 2CD',
    country: 'GB',
  },
  sendCardsToCardholder: true,
  onReplaceFail: jest.fn(),
  onReplaceSuccess: jest.fn(),
};

const mockWatchValues: Record<string, any> = {
  reason: 'This card has been lost or stolen',
  delivery: undefined,
  title: undefined,
  firstName: undefined,
  lastName: undefined,
  addressLine1: undefined,
  postCode: undefined,
};

jest.mock('react-hook-form', () => {
  const actual = jest.requireActual('react-hook-form');
  return {
    ...actual,
    useForm: jest.fn((options) => {
      const methods = actual.useForm(options);
      return {
        ...methods,
        watch: jest.fn((field?: string) => {
          if (!field) {
            return mockWatchValues;
          }
          return mockWatchValues[field];
        }),
        getValues: jest.fn(() => mockWatchValues),
        setValue: jest.fn(),
        trigger: jest.fn().mockResolvedValue(true),
        clearErrors: jest.fn(),
        formState: { errors: {}, isDirty: false },
      };
    }),
  };
});

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');
  const z = jest.requireActual('zod');
  return {
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
    findError: serverUtils.findError,
    getVariant: () => {
      return 'variantName.fieldName';
    },
    IBPayCardDeliverySchema: () =>
      z.object({
        reason: z.string(),
        delivery: z.string().optional(),
        title: z.any().optional(),
        firstName: z.string().optional(),
        lastName: z.string().optional(),
        addressLine1: z.string().optional(),
        addressLine2: z.string().optional(),
        addressLine3: z.string().optional(),
        addressLine4: z.string().optional(),
        addressLine5: z.string().optional(),
        postCode: z.string().optional(),
        country: z.string().optional(),
      }),
    addressSchema: () =>
      z.object({
        addressLine1: z.string(),
        addressLine2: z.string().optional(),
        addressLine3: z.string().optional(),
        addressLine4: z.string().optional(),
        addressLine5: z.string().optional(),
        postCode: z.string(),
        country: z.string(),
      }),
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

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
}));

window.scrollTo = jest.fn();

describe('ReplaceCard Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any)._satellite = { track: jest.fn() };
  });

  it('should render ReplaceCard component and close it', async () => {
    const { getByTestId } = render(<ReplaceCard {...mockProps} />);

    expect(getByTestId('Replace-Card-Dialog')).toBeInTheDocument();

    const closeButton = getByTestId('Replace-Card-Cancel-Button');
    expect(closeButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(closeButton);
    });

    expect(mockProps.onOpenChange).toHaveBeenCalledWith(false);
  });

  it('should render ReplaceCard component and continue to delivery step', async () => {
    const { getByTestId, queryByTestId, getByRole } = render(
      <ReplaceCard
        {...mockProps}
        employeeDetails={{
          title: 'test@test.com',
          firstName: 'test',
          lastName: 'test',
          emailAddress: 'test@test.com',
        }}
      />
    );

    expect(getByTestId('Replace-Card-Dialog')).toBeInTheDocument();

    expect(getByTestId('Reason-Radio-Group')).toBeInTheDocument();

    const continueButton = getByTestId('Replace-Card-Continue-Button');
    expect(continueButton).toHaveTextContent(
      'cards.cardMgmt.cardDetails.editCard.replaceCard.continue'
    );

    await act(async () => {
      fireEvent.click(continueButton);
    });

    expect(continueButton).toHaveTextContent(
      'cards.cardMgmt.cardDetails.editCard.replaceCard.button'
    );

    expect(queryByTestId('Reason-Radio-Group')).not.toBeInTheDocument();
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

  it('should render ReplaceCard component when the user is not allowed to send card to an alternative address', async () => {
    const { getByTestId, queryByTestId, getByRole } = render(
      <ReplaceCard
        {...mockProps}
        sendCardsToCardholder={false}
        employeeDetails={{
          title: 'test@test.com',
          firstName: 'test',
          lastName: 'test',
          emailAddress: 'test@test.com',
        }}
      />
    );

    expect(getByTestId('Replace-Card-Dialog')).toBeInTheDocument();

    expect(getByTestId('Reason-Radio-Group')).toBeInTheDocument();

    const continueButton = getByTestId('Replace-Card-Continue-Button');
    expect(continueButton).toHaveTextContent(
      'cards.cardMgmt.cardDetails.editCard.replaceCard.continue'
    );

    await act(async () => {
      fireEvent.click(continueButton);
    });

    expect(continueButton).toHaveTextContent(
      'cards.cardMgmt.cardDetails.editCard.replaceCard.button'
    );

    expect(queryByTestId('Reason-Radio-Group')).not.toBeInTheDocument();
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

describe('getErrorMessage function', () => {
  it('should extract error message', () => {
    const error1 = { displayValue: { message: 'Please select title', type: 'too_small' } };
    const error2 = { message: 'Incorrect name format', type: 'too_small', ref: {} };
    expect(getErrorMessage(error1)).toEqual('Please select title');
    expect(getErrorMessage(error2)).toEqual('Incorrect name format');
  });

  it('should return null when there is no error message', () => {
    const error = {};
    expect(getErrorMessage(error)).toEqual(null);
  });
});
