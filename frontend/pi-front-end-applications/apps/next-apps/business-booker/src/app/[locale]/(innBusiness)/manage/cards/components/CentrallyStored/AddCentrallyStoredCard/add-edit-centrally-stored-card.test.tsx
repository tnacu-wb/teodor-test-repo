import '@testing-library/jest-dom';
import { act, fireEvent, render, waitFor } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import { LOCALES, Language } from '@whitbread-eos/api';

import { AddEditCentrallyStoredCard } from './add-edit-centrally-stored-card';

const mockUseFeatureToggle = jest.fn().mockReturnValue({});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: () => mockUseFeatureToggle(),
}));

jest.mock('~components/innBusiness/ReviewChanges/index', () => ({
  ReviewChanges: () => <div data-testid="ReviewChanges" />,
}));

jest.mock('react-hook-form', () => {
  const mockFormState = { errors: {}, dirtyFields: {} } as any;
  let watchValues: Record<string, unknown> = {};
  const watch = jest.fn((field: string) => watchValues[field]);
  const setWatchValues = (values: Record<string, unknown>) => {
    watchValues = { ...values };
  };
  const resetFormState = () => {
    mockFormState.errors = {};
    mockFormState.dirtyFields = {};
    watchValues = {};
  };
  const setDirtyFields = (dirty: Record<string, unknown>) => {
    mockFormState.dirtyFields = dirty;
  };
  const createReturnValue = () => ({
    control: {},
    formState: mockFormState,
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    setValue: jest.fn(),
    getValues: jest.fn(),
    handleSubmit: jest.fn(),
    watch,
  });

  return {
    __esModule: true,
    useForm: jest.fn(() => createReturnValue()),
    useFormContext: jest.fn(() => createReturnValue()),
    Controller: jest.fn(({ render }: any) => render({ field: {} })),
    FormProvider: ({ children }: any) => <div>{children}</div>,
    __setWatchValues: setWatchValues,
    __setDirtyFields: setDirtyFields,
    __resetFormState: resetFormState,
  };
});

const mockedReactHookForm: any = jest.requireMock('react-hook-form');
const { __setWatchValues, __setDirtyFields, __resetFormState } = mockedReactHookForm;

const mockProps = {
  icons: {},
  language: 'en' as Language,
  initialAddress: {
    addressLine1: 'addressLine1',
    addressLine2: 'addressLine2',
    addressLine3: 'addressLine3',
    addressLine4: 'addressLine4',
    addressLine5: 'addressLine5',
    country: 'country',
    postCode: 'postcode',
  },
  companyName: 'test',
} as any;

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  const schemaResponseMock = () => {
    return {
      merge: jest.fn().mockReturnThis(),
      unknownKeys: jest.fn().mockReturnThis(),
      refine: jest.fn().mockReturnThis(),
    };
  };
  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCountriesList: () => {
      return;
    },
    getCountryName: () => {
      return 'United Kingdom (the)';
    },
    getLabelType: jest.fn(),
    addressSchema: () => {
      return {
        merge: jest.fn(),
      };
    },
    companyNameSchema: schemaResponseMock,
    cardTypeSchema: () => {
      return {
        merge: jest.fn(),
        refinement: ['1', '2'],
        validation: jest.fn(() => {
          return true;
        }),
      };
    },
    cardLabelSchema: () => {
      return {
        merge: jest.fn(),
      };
    },
    getSavedCardType: () => {
      return 'KEEP_PIBA';
    },
    findError: serverUtils.findError,
    getVariant: jest.fn(),
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));

describe('Add centrally stored card Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseFeatureToggle.mockReturnValue({});
    __resetFormState();
    __setWatchValues({
      postCode: mockProps.initialAddress.postCode,
      addressLine1: mockProps.initialAddress.addressLine1,
      addressLine2: mockProps.initialAddress.addressLine2,
      addressLine3: mockProps.initialAddress.addressLine3,
      addressLine4: mockProps.initialAddress.addressLine4,
      addressLine5: mockProps.initialAddress.addressLine5,
      country: mockProps.initialAddress.country,
    });
    __setDirtyFields({});
    mockProps.cardDetails = undefined;
  });

  it('should render AddCentrallyStoredCard component as Add', async () => {
    const { container, getByTestId } = render(<AddEditCentrallyStoredCard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('Centrally-Stored-Add-Card-page')).toBeInTheDocument();
      expect(getByTestId('Submit-Add-Card')).toBeInTheDocument();
      expect(container.querySelector('#NEW_PIBA')).toBeInTheDocument();
    });
    await act(async () => {
      fireEvent.click(getByTestId('Centrally-Card-Address-Edit'));

      const cardLabel = getByTestId('cardLabel-Form-Input');
      cardLabel.focus();
      fireEvent.change(cardLabel, { target: { value: '123' } });
      cardLabel.blur();
    });
  });

  it('should render AddCentrallyStoredCard component as Edit', async () => {
    mockProps.cardDetails = {
      cardType: 'VS',
      cardLabel: 'test',
      cardNumber: '****4444',
      expiryDate: '12/25',
      cardId: '123',
      cardNotPresentRequired: true,
    };
    const { getByTestId } = render(<AddEditCentrallyStoredCard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('Centrally-Stored-Add-Card-page')).toBeInTheDocument();
    });
  });

  it('should render AddCentrallyStoredCard component as Edit and delete card', async () => {
    mockProps.cardDetails = {
      cardType: 'VS',
      cardLabel: 'test',
      cardNumber: '****4444',
      expiryDate: '12/25',
      cardId: '123',
    };
    const { getByTestId } = render(<AddEditCentrallyStoredCard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('Centrally-Stored-Add-Card-page')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('delete-card-icon'));
      userEvent.click(getByTestId('Delete-Card-Modal-Open-Button'));
    });
  });

  it('should render AddCentrallyStoredCard component as Edit and submit changes', async () => {
    mockProps.cardDetails = {
      cardType: 'VS',
      cardLabel: 'test',
      cardNumber: '****4444',
      expiryDate: '12/25',
      cardId: '123',
    };
    const { getByTestId } = render(<AddEditCentrallyStoredCard {...mockProps} />);
    const submitButton = getByTestId('Submit-Add-Card');
    await waitFor(() => {
      expect(getByTestId('Centrally-Stored-Add-Card-page')).toBeInTheDocument();
      expect(submitButton).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(submitButton);
    });
  });

  it('should not show ReviewChanges component when watched values do not differ', async () => {
    __setWatchValues({
      postCode: mockProps.initialAddress.postCode,
      addressLine1: mockProps.initialAddress.addressLine1,
      addressLine2: mockProps.initialAddress.addressLine2,
      addressLine3: mockProps.initialAddress.addressLine3,
      addressLine4: mockProps.initialAddress.addressLine4,
      addressLine5: mockProps.initialAddress.addressLine5,
      country: mockProps.initialAddress.country,
    });

    const { queryByTestId } = render(<AddEditCentrallyStoredCard {...mockProps} />);

    await waitFor(() => {
      expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();
    });
  });

  it('should show ReviewChanges component when watched values differ', async () => {
    __setWatchValues({
      postCode: mockProps.initialAddress.postCode,
      addressLine1: mockProps.initialAddress.addressLine1,
      addressLine2: 'new address line 2',
      addressLine3: mockProps.initialAddress.addressLine3,
      addressLine4: mockProps.initialAddress.addressLine4,
      addressLine5: mockProps.initialAddress.addressLine5,
      country: mockProps.initialAddress.country,
    });

    const { getByTestId } = render(<AddEditCentrallyStoredCard {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('ReviewChanges')).toBeInTheDocument();
    });
  });
});
