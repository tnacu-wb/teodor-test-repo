import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BusinessType, LOCALES, Scheme } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';
import React from 'react';

import { PayApplicationStep } from '../types';
import { CompanyDetailsBusinessInfo } from './company-details-business-info';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

const mockProps = {
  icons: {},
  header: null,
  titleValues: JSON.stringify(['Mr', 'Mrs', 'Miss']),
  initialState: {
    applicationGUID: '',
    applicationId: '',
    scheme: 'GB' as Scheme,
    companyDetails: {
      companyName: 'Whitbread PLC',
      companyType: BusinessType.Charity,
      charityNumber: '123',
      companyAddress: {
        addressLine1: '123 Test Street',
        addressLine2: 'Suite 456',
        addressLine3: '',
        addressLine4: '',
        addressLine5: '',
        country: 'United Kingdom',
        postCode: 'SW1A 1AA',
      },
      dateOfBirth: {
        day: '01',
        month: '01',
        year: '2000',
      },
      timeTradingID: '',
      nameOfEmployee: {
        titleEmployee: '',
        firstNameEmployee: '',
        lastNameEmployee: '',
      },
      companyRegNum: '',
      parentCompanyName: '',
    },
  },
  initialStepId: PayApplicationStep.COMPANY_DETAILS,
  steps: [
    {
      id: PayApplicationStep.COMPANY_DETAILS,
      component: (
        <CompanyDetailsBusinessInfo
          icons={{}}
          timeTrading=""
          titleValues={JSON.stringify(['Mr', 'Mrs', 'Miss'])}
        />
      ),
    },
  ],
};
const mockedPostCodeAddresses: { id: string; addressText: string }[] | null = null;

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getLocaleByPathname: () => LOCALES.EN,
    getCountryName: () => {
      return 'United Kingdom (the)';
    },
    getCountriesList: () => {
      return 'United Kingdom (the)';
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPostCodeAddresses: () => {
      return mockedPostCodeAddresses;
    },
    getPathForLocale: () => {
      return '/';
    },
    getBusinessTypeLabel: jest.fn(),
    addressSchema: () => {
      return {
        parseAsync: jest.fn(),
        nullable: jest.fn(),
        superRefine: jest.fn().mockReturnThis(),
      };
    },
    companyNameSchema: () => ({
      merge: jest.fn().mockReturnThis(),
      refine: jest.fn().mockReturnThis(),
      superRefine: jest.fn().mockReturnThis(),
    }),
    companyNameOfEmployeeSchema: () => ({
      merge: jest.fn().mockReturnThis(),
    }),
    companyRegistrationNumberSchema: () => ({
      merge: jest.fn().mockReturnThis(),
    }),
    registeredCharityNumberSchema: () => ({
      merge: jest.fn().mockReturnThis(),
    }),
    timeTradingSchema: () => ({
      merge: jest.fn().mockReturnThis(),
    }),
    dateOfBirthSchema: () => ({
      merge: jest.fn().mockReturnThis(),
    }),
    parentCompanySchema: () => ({
      merge: jest.fn().mockReturnThis(),
    }),
    getVariant: () => {
      return 'variantName.fieldName';
    },
    findError: serverUtils.findError,
  };
});

describe('CompanyDetailsBusinessInfo component', () => {
  it('should render CompanyDetailsBusinessInfo component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });
  it('should render CompanyDetailsBusinessInfo component when businessType is charity', async () => {
    const user = userEvent.setup();
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardContinueButton = getByTestId('footer-button');
    const registeredInput = getByTestId('RegisteredCharityNumberForm-Form-Input');

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardContinueButton).toBeInTheDocument();
      expect(registeredInput).toBeInTheDocument();
    });

    await user.type(registeredInput, 'Test');

    await waitFor(async () => {
      expect(registeredInput).toHaveValue(`123Test`);
    });
  });
  it('should render CompanyDetailsBusinessInfo component when businessType is government', async () => {
    const user = userEvent.setup();
    mockProps.initialState.companyDetails.companyType = BusinessType.Government;
    const { getByTestId, getAllByTestId } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardContinueButton = getByTestId('footer-button');
    const addCorrespondenceAddressButton = getByTestId(
      'CompanyDetailsBusinessInfo-button-add-correspondence'
    );
    const companyNameEdit = getAllByTestId('CompanyContainer-edit')[0];

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardContinueButton).toBeInTheDocument();
      expect(addCorrespondenceAddressButton).toBeInTheDocument();
      expect(companyNameEdit).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(companyNameEdit);
    });

    const companyNameInput = getByTestId('Company-name-Form-Input');

    await waitFor(() => {
      expect(companyNameInput).toBeInTheDocument();
    });
    await user.type(companyNameInput, 'Whitbread UK');

    await waitFor(() => {
      expect(companyNameInput).toHaveValue('Whitbread UK');
    });
    await waitFor(async () => {
      fireEvent.click(addCorrespondenceAddressButton);
    });
  });

  it('should render CompanyDetailsBusinessInfo component when businessType is sole trader', async () => {
    const user = userEvent.setup();
    mockProps.initialState.companyDetails.companyType = BusinessType.SoleTrader;
    mockProps.initialState.companyDetails.timeTradingID = '0-6 Months';
    mockProps.initialState.companyDetails.nameOfEmployee = {
      titleEmployee: 'Mr',
      firstNameEmployee: 'John',
      lastNameEmployee: 'Doe',
    };
    const { getByTestId, getAllByTestId } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardContinueButton = getByTestId('footer-button');
    const nameOfEmployeeEdit = getAllByTestId('CompanyContainer-edit')[1];

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardContinueButton).toBeInTheDocument();
      expect(nameOfEmployeeEdit).toBeInTheDocument();
    });

    await user.click(nameOfEmployeeEdit);

    const titleSelect = getByTestId('titleEmployee-IB-Form-Select-Button');
    const firstNameInput = getByTestId('firstNameEmployee-Form-Input');
    const lastNameInput = getByTestId('lastNameEmployee-Form-Input');

    await waitFor(() => {
      expect(titleSelect).toBeInTheDocument();
      expect(firstNameInput).toBeInTheDocument();
      expect(lastNameInput).toBeInTheDocument();
    });
  });
  it('should render CompanyDetailsBusinessInfo component when businessType is public limited', async () => {
    mockProps.initialState.companyDetails.companyType = BusinessType.PublicLimited;
    mockProps.initialState.companyDetails.companyRegNum = '123456';
    mockProps.initialState.companyDetails.parentCompanyName = '333333';
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardContinueButton = getByTestId('footer-button');

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardContinueButton).toBeInTheDocument();
    });
  });
  it('should render CompanyDetailsBusinessInfo component when businessType is limited company', async () => {
    mockProps.initialState.companyDetails.companyType = BusinessType.LimitedCompany;
    mockProps.initialState.companyDetails.companyRegNum = '123456';
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardContinueButton = getByTestId('footer-button');

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardContinueButton).toBeInTheDocument();
    });
  });

  it('should render CompanyDetailsBusinessInfo component when businessType is Other', async () => {
    mockProps.initialState.companyDetails.companyType = BusinessType.Other;
    mockProps.initialState.companyDetails.companyRegNum = 'OC123';
    mockProps.initialState.companyDetails.timeTradingID = '0-6 Months';
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardContinueButton = getByTestId('footer-button');

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardContinueButton).toBeInTheDocument();
    });
  });

  it('should render CompanyDetailsBusinessInfo component and click on Close', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardCloseLink = getByTestId('footer-link');

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardCloseLink).toBeInTheDocument();
    });

    await waitFor(() => {
      fireEvent.click(wizardCloseLink);
    });
  });
});
