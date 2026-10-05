import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { CompanyInformation } from './CompanyInformation';

const mockProps = {
  icons: {},
  locale: LOCALES.EN,
  mainContact: {
    id: 'EMPL_adsaad-3132ads',
    title: 'Mr',
    firstName: 'John',
    lastName: 'Doe',
    emailAddress: 'john.doe@mailnator.com',
    phoneNumber: '+4477777777777',
    mobileNumber: '',
    position: 'mainEmployee',
  },
  companyAddress: {
    addressLine1: 'London',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    addressLine5: '',
    country: 'GB',
    postCode: 'E1A 2AA',
  },
  companyName: 'Company name',
  alternateCompanyName: 'Company name',
  companyId: 'COMP_adasdad13131',
  additionalDetails: {
    companySector: 'Arts',
    averageMonthlyBooking: '51-200 rooms',
    numberOfEmployee: '50-249 employees',
  },
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
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPathForLocale: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCountryName: () => {
      return 'United Kingdom (the)';
    },
    getCountriesList: () => {
      return 'United Kingdom (the)';
    },
    getEmployeeDetails: () => {
      return mockEmployeeDetails;
    },
    addressSchema: () => {
      return {
        parseAsync: jest.fn(),
      };
    },
    getVariant: () => {
      return 'variantName.fieldName';
    },
    findError: serverUtils.findError,
    companyNameSchema: () => ({
      merge: jest.fn().mockReturnThis(),
      refine: jest.fn().mockReturnThis(),
      superRefine: jest.fn().mockReturnThis(),
    }),
    updateCompanyDetails: () => {
      return Promise.resolve({
        success: true,
      });
    },
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));

jest.mock('react-hook-form', () => ({
  useForm: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    setValue: jest.fn(),
    getValues: jest.fn(),
    handleSubmit: jest.fn(),
    watch: jest.fn(),
    reset: jest.fn(),
  }),
  useFormContext: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    setValue: jest.fn(),
    getValues: jest.fn(),
    handleSubmit: jest.fn(),
    watch: jest.fn(),
    reset: jest.fn(),
  }),
  Controller: jest.fn(({ render }) => render({ field: {} })),
  FormProvider: jest.fn(({ children }) => <div>{children}</div>),
}));

describe('CompanyInformation Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyInformation component', async () => {
    const { getByTestId } = render(<CompanyInformation {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyInformation-container')).toBeInTheDocument();
    });
  });

  it('should render CompanyInformation component, click on first edit button (Company details container) and click on discard changes', async () => {
    const { getByTestId, getAllByTestId } = render(<CompanyInformation {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyInformation-container')).toBeInTheDocument();
    });

    const editButton = getAllByTestId('CompanyContainer-edit')[0];

    await waitFor(async () => {
      expect(editButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(editButton);
    });
    const discardChangesButton = getByTestId('CompanyInformation-Discard-changes-Company');

    await waitFor(async () => {
      expect(discardChangesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(discardChangesButton);
    });
  });
  it('should render CompanyInformation component, click on first edit button (Company details container) and submit form', async () => {
    const { getByTestId, getAllByTestId } = render(<CompanyInformation {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyInformation-container')).toBeInTheDocument();
    });

    const editButton = getAllByTestId('CompanyContainer-edit')[0];

    await waitFor(async () => {
      expect(editButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(editButton);
    });
    const saveUpdatesButton = getByTestId('CompanyInformation-Save-updates-Company');

    await waitFor(async () => {
      expect(saveUpdatesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(saveUpdatesButton);
    });
  });
  it('should render CompanyInformation component, click on second edit button (Main contact) and submit form', async () => {
    const { getByTestId, getAllByTestId } = render(<CompanyInformation {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyInformation-container')).toBeInTheDocument();
    });

    const editButton = getAllByTestId('CompanyContainer-edit')[1];

    await waitFor(async () => {
      expect(editButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(editButton);
    });
    const saveUpdatesButton = getByTestId('CompanyInformation-Save-updates-Company');

    await waitFor(async () => {
      expect(saveUpdatesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(saveUpdatesButton);
    });
  });

  it('should render CompanyInformation component, click on third edit button (Additional details) and submit form', async () => {
    const { getByTestId, getAllByTestId } = render(<CompanyInformation {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyInformation-container')).toBeInTheDocument();
    });

    const editButton = getAllByTestId('CompanyContainer-edit')[2];

    await waitFor(async () => {
      expect(editButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(editButton);
    });
    const saveUpdatesButton = getByTestId('CompanyInformation-Save-updates-Company');

    await waitFor(async () => {
      expect(saveUpdatesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(saveUpdatesButton);
    });
  });
});
