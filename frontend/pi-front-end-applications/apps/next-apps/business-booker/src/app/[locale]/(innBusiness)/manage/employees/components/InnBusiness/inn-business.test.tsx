import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES, BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';
import { cookies } from 'next/headers';

import { getEmployeesTotal, InnBusiness } from './inn-business';

const mockProps = {
  locale: LOCALES.EN,
};

const mockCompanyDetails = {
  requestedCompany: {
    companyDetails: {
      companyName: 'Inn Business Company Testing_M',
      alternateCompanyName: 'Inn Business Company Testing_M',
      numberOfEmployees: 4,
      companyAddress: {
        addressLine1: '25 Truthan View',
        addressLine2: 'Trispen',
        addressLine3: '',
        addressLine4: 'TRURO',
        addressLine5: '',
        postCode: 'TR4 9AS',
        country: 'GB',
      },
      mainEmployee: {
        id: 'EMPL_2fe46cbe-e7f9-4b4b-9114-14a1b8ee609e',
        ghNumber: 'G81023449',
        emailAddress: 'innbusiness_travelmanager@mailinator.com',
        position: '',
        phoneNumber: '234234444',
        mobileNumber: '',
        textConfirmation: false,
        title: 'Mrs',
        firstName: 'Cristina',
        lastName: 'Travel Manager',
        centralCardId: '1',
        address: {
          addressLine1: '25 Truthan View',
          addressLine2: 'Trispen',
          addressLine3: '',
          addressLine4: 'TRURO',
          addressLine5: '',
          postCode: 'TR4 9AS',
          country: 'GB',
        },
        accessLevel: 'SUPER',
        employeeStatus: 'ACTIVE',
        employeeAnswers: {},
      },
    },
  },
};

const mockDetailsFromToken = { companyId: 'ASDZXC' };
const mockedCommonIBIcons = {};
const mockEmployees = {};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCommonIcons: () => {
      return mockedCommonIBIcons;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCompanyDetails: () => {
      return mockCompanyDetails;
    },
    getDetailsFromToken: () => {
      return mockDetailsFromToken;
    },
    getSearchParams: () => Promise.resolve(new URLSearchParams()),
    getEmployees: () => {
      return mockEmployees;
    },
    sanitize: (html: string) => html,
  };
});

jest.mock('./company-employees', () => {
  return { CompanyEmployees: () => null };
});
jest.mock('./search-employee', () => {
  return { SearchEmployeeInput: () => null };
});
jest.mock('~components/innBusiness/DownloadButton/download-button', () => {
  return { DownloadButton: () => null };
});

jest.mock('~components/innBusiness/DataTable', () => {
  return {
    DataTable: ({ columns }: any) => {
      columns.forEach((column: any) => {
        if (column.render) {
          column.render({}, {});
        }
      });
      return null;
    },
  };
});

jest.mock('./manage-pending-employees', () => {
  return { ManagePendingEmployees: () => null };
});

const mockSuperRole = BUSINESS_BOOKER_USER_ROLES.SUPER;

const mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    accessLevel: mockSuperRole,
    tethered: false,
  },
};

const mockCookieData = {
  value: mockToken,
};
const mockCookieStore = {
  get: () => mockCookieData,
} as unknown as ReturnType<typeof cookies>;
const mockHeadersWbUrl: string | null = 'http://test.com?a=1';

jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({ get: () => mockHeadersWbUrl }),
}));

describe('InnBusiness Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const baseDataTestId = 'InnBusinessTab';

  it('should render InnBusiness component', async () => {
    const { getByTestId } = render(await InnBusiness(mockProps));

    expect(getByTestId(`${baseDataTestId}-container`)).toBeInTheDocument();
    expect(getByTestId(`${baseDataTestId}-add-employee-button`)).toBeInTheDocument();

    const result = await getEmployeesTotal();

    expect(result).toBe(15);
  });
});
