import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { useRestQueryRequest } from '@whitbread-eos/utils';
import i18n from 'i18next';
import { I18nextProvider, initReactI18next } from 'react-i18next';

import { fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import SearchAccountPageCcui from './page.ccui';

i18n.use(initReactI18next).init({
  lng: 'en',
  fallbackLng: 'en',
  ns: ['common', 'examples'],
  defaultNS: 'common',
  interpolation: {
    escapeValue: false,
  },
  resources: {
    en: {
      common: {},
      examples: {},
    },
    de: {
      common: {},
      examples: {},
    },
  },
});

const useQueryResponse = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  isIdle: false,
  error: {
    request: {
      status: 200,
    },
    response: {
      status: {
        details: '',
      },
    },
  },
  data: [
    {
      customerAccountId: 'CUST_1d80b042-64c3-4609-bc5c-9be8cf1547db',
      contactDetail: {
        title: 'Mr',
        firstName: 'Sasa1',
        lastName: 'Rai',
        email: 'sasa.radivoi@whitbread.com',
        telephone: '+44271160522',
        mobile: '43252345',
        nationality: '',
        passport: {
          number: '',
          countryOfIssue: '',
        },
        address: {
          line1: '120 Holborn',
          line2: 'MITCHELL',
          line3: 'NEWQUAY',
          line4: 'CORNWALL',
          line5: '',
          postCode: 'EC1N 2TD',
          countryCode: 'GB',
          type: 'BUSINESS',
          companyName: 'Whitbread Plc',
        },
      },
      paymentPreference: {
        electronicInvoiceRequired: false,
      },
      additionalGuests: [],
      bookingPreference: {
        roomRequirements: {
          type: 'DB',
          adults: 1,
          children: 0,
          cotRequired: false,
        },
        reason: 'LEISURE',
        foodPreference: 0,
        preselectWifi: false,
      },
      guestHistoryNumber: 'G80887917',
    },
    {
      customerAccountId: 'CUST_b33fed6f-94b5-4b86-95c0-01cca8f84781',
      contactDetail: {
        title: 'Mr',
        firstName: 'Sasa2',
        lastName: 'Radivoi',
        email: 'sasa.radivoi01@whitbread.com',
        telephone: '+44271160522',
        mobile: '+441071687470',
        nationality: 'RO',
        passport: {
          number: 'number',
          countryOfIssue: 'countryOfIssue',
        },
        carRegistration: 'carRegistration',
        address: {
          line1: '120 Holborn',
          postCode: 'EC1N 2TD',
          countryCode: 'GB',
          type: 'BUSINESS',
          companyName: 'Whitbread',
        },
      },
      paymentPreference: {
        electronicInvoiceRequired: false,
        paymentCard: {
          cardType: 'VI',
          cardNumber: '************1111',
          expiryDate: '02/30',
          cardHolderName: 'Mr John Doe',
          cardToken: '4764776852337921234',
          billingAddress: {
            line1: '120 Holborn',
            postCode: 'EC1N 2TD',
            countryCode: 'GB',
            type: 'BUSINESS',
            companyName: 'Whitbread',
          },
        },
      },
      additionalGuests: [
        {
          title: 'Mr',
          firstName: 'Test',
          lastName: 'Guest',
          email: 'testguestperf01@mailinator.com',
          telephone: '+44271160522',
          mobile: '+440757233444',
          nationality: 'RO',
          passport: {
            number: '345622222',
            countryOfIssue: 'Romania',
          },
          carRegistration: 'carRegistration',
        },
      ],
      bookingPreference: {
        roomRequirements: {
          type: 'DB',
          lettingType: 'lettingType',
          adults: 2,
          children: 0,
          cotRequired: false,
          hotelBrand: 'PI',
        },
        reason: 'LEISURE',
        foodPreference: 11,
        preselectWifi: true,
      },
      guestHistoryNumber: 'G99709052',
      guestHistoryCreation: '2021-02-04',
    },
    {
      customerAccountId: 'CUST_0cdb36ac-7bcc-4c16-a0ee-6e8aa661cb10',
      contactDetail: {
        title: 'Mr',
        firstName: '',
        lastName: '',
        email: 'sasa.radivoi@whitbread.com',
        telephone: '+44271160522',
        address: {
          line1: '120 Holborn',
          postCode: 'EC1N 2TD',
          countryCode: 'GB',
          type: 'BUSINESS',
          companyName: 'Whitbread',
        },
      },
      paymentPreference: {
        electronicInvoiceRequired: false,
        paymentCard: {
          cardType: 'VI',
          cardNumber: '************1111',
          expiryDate: '02/30',
          cardHolderName: 'Sasa Radivoi',
          billingAddress: {
            line1: '120 Holborn',
            postCode: 'EC1N 2TD',
            countryCode: 'GB',
            type: 'BUSINESS',
            companyName: 'Whitbread',
          },
        },
      },
      additionalGuests: [
        {
          title: 'Mr',
          firstName: 'Test',
          lastName: 'Guest',
          email: 'testguest1@mailinator.com',
          telephone: '+44271160522',
          mobile: '+440757233444',
          nationality: 'RO',
          passport: {
            number: '345622222',
            countryOfIssue: 'Romania',
          },
          carRegistration: 'carRegistration',
        },
      ],
      bookingPreference: {
        roomRequirements: {
          type: 'DB',
          lettingType: 'lettingType',
          adults: 2,
          children: 0,
          cotRequired: false,
          hotelBrand: 'PI',
        },
        reason: 'LEISURE',
        foodPreference: 11,
        preselectWifi: true,
      },
      guestHistoryNumber: 'G99709052',
      guestHistoryCreation: '2021-02-04',
    },
    {
      customerAccountId: 'CUST_0cdb36ac-7bcc-4c16-a0ee-6e8aa661cb10',
      contactDetail: {
        title: '',
        firstName: 'Length is more than 30 chars to test this info',
        lastName: 'Length is more than 30 chars to test this info',
        email: '',
        telephone: '+44271160522',
        address: {
          line1: '120 Holborn',
          postCode: '',
          countryCode: 'GB',
          type: 'BUSINESS',
          companyName: '',
        },
      },
      paymentPreference: {
        electronicInvoiceRequired: false,
        paymentCard: {
          cardType: 'VI',
          cardNumber: '************1111',
          expiryDate: '02/30',
          cardHolderName: 'Sasa Radivoi',
          billingAddress: {
            line1: '120 Holborn',
            postCode: 'EC1N 2TD',
            countryCode: 'GB',
            type: 'BUSINESS',
            companyName: 'Whitbread',
          },
        },
      },
      additionalGuests: [
        {
          title: 'Mr',
          firstName: 'Test',
          lastName: 'Guest',
          email: 'testguest1@mailinator.com',
          telephone: '+44271160522',
          mobile: '+440757233444',
          nationality: 'RO',
          passport: {
            number: '345622222',
            countryOfIssue: 'Romania',
          },
          carRegistration: 'carRegistration',
        },
      ],
      bookingPreference: {
        roomRequirements: {
          type: 'DB',
          lettingType: 'lettingType',
          adults: 2,
          children: 0,
          cotRequired: false,
          hotelBrand: 'PI',
        },
        reason: 'LEISURE',
        foodPreference: 11,
        preselectWifi: true,
      },
      guestHistoryNumber: 'G99709052',
      guestHistoryCreation: '2021-02-04',
    },
  ],
};

jest.mock('@whitbread-eos/organisms', () => ({
  CCUISearchContainer: ({ children }: any) => (
    <div data-testid="Search-Account-Container">{children}</div>
  ),
  SearchAccountResultsContainer: ({ children }: any) => (
    <div data-testid="SearchAccountResultsContainer">{children}</div>
  ),
}));
const mockCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: jest.fn(),
  useRestQueryRequest: jest.fn(() => useQueryResponse),
  setAnalyticsUser: jest.fn(),
  analytics: {
    update: jest.fn(),
  },
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({ back: mockUseRouter }),
}));

const queryClient = new QueryClient();

describe('Search page CCUI', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'en',
    });
  });
  it('should render the search for account container', async () => {
    const { getByTestId } = render(
      <I18nextProvider i18n={i18n}>
        <SearchAccountPageCcui queryClient={queryClient} accessToken="test123" />
      </I18nextProvider>
    );
    expect(getByTestId('Search-Account-Container')).toBeInTheDocument();
  });
  it('should show a error notification for adding min 2 params', async () => {
    const { getByTestId, getByText } = render(
      <I18nextProvider i18n={i18n}>
        <SearchAccountPageCcui queryClient={queryClient} accessToken="test123" />
      </I18nextProvider>
    );
    const inputFirstName = getByTestId('input-firstName');
    userEvent.type(inputFirstName, 'Alex');

    const button = getByTestId('searchButton');

    fireEvent.click(button);
    await waitFor(() => {
      expect(getByText('ccui.guestAccounts.error.minCriteria')).toBeInTheDocument();
    });
  });
  it('should show the results table', async () => {
    const { getByTestId } = render(
      <I18nextProvider i18n={i18n}>
        <SearchAccountPageCcui queryClient={queryClient} accessToken="test123" />
      </I18nextProvider>
    );

    const inputFirstName = getByTestId('input-firstName');
    userEvent.type(inputFirstName, 'Alex');
    const inputPostcode = getByTestId('input-postalCode');
    userEvent.type(inputPostcode, 'E1 6AN');

    const button = getByTestId('searchButton');
    fireEvent.click(button);

    await waitFor(() => {
      expect(getByTestId('SearchAccountPage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render the search for account container with out first and last name', async () => {
    const useQueryResponseTmp = { ...useQueryResponse };
    useQueryResponseTmp.data = [];
    (useRestQueryRequest as jest.Mock).mockImplementation(() => useQueryResponseTmp);
    const { getByTestId } = render(
      <I18nextProvider i18n={i18n}>
        <SearchAccountPageCcui queryClient={queryClient} accessToken="test123" />
      </I18nextProvider>
    );
    expect(getByTestId('Search-Account-Container')).toBeInTheDocument();
  });
});
