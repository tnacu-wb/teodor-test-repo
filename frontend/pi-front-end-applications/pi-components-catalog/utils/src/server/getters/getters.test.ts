import {
  Area,
  BOOKING_CHANNEL,
  BOOKING_SUBCHANNEL,
  BUSINESS_BOOKER_USER_ROLES,
  Channel,
  Language,
  LOCALES,
  Room,
  ROOM_CODES,
  ShortCountry,
  CustomerAccountDetails,
  RegistrationRole,
  Scheme,
  CompanyDetailsResponse,
} from '@whitbread-eos/api';
import { cookies } from 'next/headers';

import {
  TOKEN_COMPANY_FIELD,
  TOKEN_EMPLOYEE_FIELD,
  TOKEN_EMAIL_FIELD,
  getDetailsFromToken,
} from '../edge';
import {
  getAccessLevel,
  getAccountList,
  getActivePath,
  getCommonIcons,
  getCompanyDetails,
  getCountryLanguageByLocale,
  getCountryName,
  getEmployeeDetails,
  getCookieConsentInfo,
  getEmployees,
  getEmployeesCSV,
  getHotelAvailabilitiesIB,
  getHotelInformationIB,
  getInnBusinessServerSideProps,
  getLocaleByPathname,
  getRegistrationQuestionsWithAnswers,
  getServerUnleashToggles,
  roomDefaultValueIfError,
  staticHotelInformationIB,
  getCompanyRegistrationQuestionsAndAnswers,
  getContactPreferences,
  getProfileDetails,
  getDaysInMonth,
  getSelectedAccountHolder,
  getManagementInformationReport,
  getEmergencyReport,
  getUserRolesForAccount,
  getAllPibaCards,
  getCompanySpending,
  getYourSpending,
  getUpcomingBookings,
  getUpcomingSpending,
  getPayApplications,
  getNotifications,
  getPaymentCards,
  companyDetailsLookup,
  getWorldlineUserPreferences,
  getAppLookupData,
  getSearchRules,
  getPayApplicationDetails,
  getActivationDetails,
  getRegistrationInfo,
  getMarketingPreferences,
  getCountriesList,
  hasAccountHolder,
  hasCardHolder,
  getLocationResults,
  getPostCodeAddresses,
  getAvailableTabs,
  getSpendOverTimeCSV,
  getAccountSpending,
  getOutOfPolicyReport,
  getIBActivationDetails,
  getEmployeesWithFilteringOptions,
  getBusinessBookerIpHeaders,
  appPreCheck,
  getAllPayRoles,
  getEmployeeDataforUserPilot,
  getStatementsPdf,
  getStatementsXls,
  getAccountRegistrationRoleDetails,
  getPIBACardDetails,
  getFormattedAddress,
  getWorldlineReturnUrl,
  getAccountTransactions,
  getCostCenterDetails,
  hasCostCenterHolder,
  showCardManagementForBusinessPayManager,
  setGuestFormData,
  clearGuestFormData,
} from './getters';
import { getFooterLabels } from './labels';

jest.mock('crypto', () => ({
  ...jest.requireActual('crypto'),
  randomBytes: () => 'testrandom',
}));

// Mock React's cache function which is not available in Jest environment
jest.mock('react', () => ({
  ...jest.requireActual('react'),
  cache: (fn: any) => fn,
}));

const mockFetchResponse = {
  data: {},
} as any;
const mockAvailabilityParams = {
  hotelId: 'LONEUS',
  arrival: '2024-03-30',
  departure: '2024-03-31',
  brand: Area.PI,
  country: 'gb',
  rooms: [],
  language: 'en',
  bookingChannel: {
    channel: Channel.Bb,
    language: 'EN',
    subchannel: BOOKING_SUBCHANNEL.WEB,
  },
  channel: BOOKING_CHANNEL.BB,
};

const mockStaticHotelInformationParams = {
  slug: '/hotels/england/greater-london/london/london-euston.html',
  language: 'en' as Language,
};

const mockOkStatus = { value: true };

const mockNavObject = {
  home: {
    label: 'Home',
    path: '/homepage',
  },
};

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockFetchResponse),
    ok: mockOkStatus.value,
    redirected: false,
    status: 200,
    statusText: 'OK',
    type: 'basic',
    url: '',
    body: null,
    bodyUsed: false,
  } as unknown as Response)
);

const mockUseRouter = jest.fn();
jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => mockUseRouter(),
}));

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

const mockRegistrationQuestionsAndAnswers = {
  purchaseOrderManagement: {
    questionId: '2',
    label: 'Question ?',
    mandatory: true,
    managementHeader: 'Customer reference',
    active: true,
    location: 'R',
    managementInformationAnswer: {
      answerType: 'F',
      answers: null,
    },
    type: 'customer reference',
    positionId: 0,
    answer: '7',
  },
  customerReferenceManagement: {
    questionId: '1',
    label: 'Question ?',
    mandatory: true,
    managementHeader: 'Customer reference',
    active: true,
    location: 'R',
    managementInformationAnswer: {
      answerType: 'F',
      answers: null,
    },
    type: 'customer reference',
    positionId: 0,
    answer: '7',
  },
  userDefinedQuestions: [
    {
      questionId: 'COQU_76f3817f-4940-43cc-89f8-7c1caf406f95',
      label: 'Dropdown Question?',
      mandatory: false,
      managementHeader: 'not the question itself',
      active: true,
      location: 'R',
      managementInformationAnswer: {
        answerType: 'U',
        answers: ['Option1', 'Option2'],
      },
      positionId: 1,
    },
  ],
};

const mockRegistrationQuestionsAndAnswersResult = [
  {
    answer: '7',
    id: 'purchaseOrderAnswer',
    label: 'Question ?',
    mandatory: true,
    options: null,
    type: 'text',
  },
  {
    answer: '7',
    id: 'customerReferenceAnswer',
    label: 'Question ?',
    mandatory: true,
    options: null,
    type: 'text',
  },
  {
    answer: '',
    id: 'COQU_76f3817f-4940-43cc-89f8-7c1caf406f95',
    label: 'Dropdown Question?',
    mandatory: false,
    options: ['Option1', 'Option2'],
    type: 'select',
  },
];

jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({ get: () => mockHeadersWbUrl }),
}));

jest.mock('nanoid', () => ({
  nanoid: () => 'id',
}));

jest.mock('../../utils/decodeIdToken', () => (token: any) => token);

jest.mock('../../utils/unleash', () => ({
  getUnleashTogglesServerOrClient: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));
describe('Server getters', () => {
  const originalWindow = global.window;

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {};
    // Remove window to simulate server-side environment
    // @ts-expect-error - intentionally deleting window for test
    delete global.window;
  });

  afterEach(() => {
    global.window = originalWindow;
  });
  it('should call getAccessLevel', async () => {
    const result = await getAccessLevel();
    expect(result).toBeTruthy();
  });

  it('should call getAccessLevel', async () => {
    const mockUserDetails = {
      email: 'test@test.com',
      companyId: 'test',
      business: {
        accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
        tethered: true,
      },
    };
    mockFetchResponse.data = {
      getProfileDetailsV2: mockUserDetails,
    };
    const result = await getAccessLevel();
    expect(result).toBeTruthy();
  });
  it('should call getAccessLevel', async () => {
    mockFetchResponse.data = {
      getAccountList: {
        accounts: [{ accountNumber: '123', registrationRoles: ['CARD_HOLDER', 'ACCOUNT_HOLDER'] }],
      },
    };
    const result = await getAccessLevel();
    expect(result).toBeTruthy();
  });

  it('should call getCompanyDetails', async () => {
    mockFetchResponse.data = null;
    const result = await getCompanyDetails('abc', mockToken as any);
    expect(result).toBeNull();
  });

  it('should call getAppLookupData for EN', async () => {
    mockFetchResponse.data = null;
    const result = await getAppLookupData('GB' as any, mockToken as any);

    expect(result).toBeNull();
  });

  it('should call getAppLookupData for DE', async () => {
    mockFetchResponse.data = null;
    const result = await getAppLookupData('DE' as any, mockToken as any);

    expect(result).toBeNull();
  });

  it('should call getSearchRules', async () => {
    mockFetchResponse.data = null;
    const result = await getSearchRules(mockToken as any);

    expect(result).toBeNull();
  });

  it('should call getPayApplicationDetails', async () => {
    mockFetchResponse.data = null;
    const result = await getPayApplicationDetails(mockToken as any, '', '', 'GB' as Scheme.Gb);

    expect(result).toBeNull();
  });

  it('should call getPayApplicationDetails with non-null result', async () => {
    mockFetchResponse.data = {
      getApplicationDetails: {},
    };
    const result = await getPayApplicationDetails(mockToken as any, '', '', 'GB' as Scheme.Gb);

    expect(result).toEqual({});
  });

  it('should call getPayApplicationDetails with error', async () => {
    mockFetchResponse.errors = [{ message: '{"errCode":262}' }];
    const result = await getPayApplicationDetails(mockToken as any, '', '', 'GB' as Scheme.Gb);

    expect(result.errCode).toEqual(262);
  });

  it('should call getRegistrationQuestionsWithAnswers', async () => {
    mockFetchResponse.data = {
      getEmployeeRegistrationQuestionsAndAnswers: mockRegistrationQuestionsAndAnswers,
    };
    const result = await getRegistrationQuestionsWithAnswers('123');
    expect(result).toEqual(mockRegistrationQuestionsAndAnswersResult);
  });

  it('should call getCostCenterDetails', async () => {
    mockFetchResponse.data = {
      getCostCentreDetails: [
        {
          accountUniqueCustomerId: '19000315',
          costCentreUniqueCustomerId: '19000316',
          costCentreCode: 'CC02',
          costCentreName: 'Digital',
        },
      ],
    };
    const result = await getCostCenterDetails('token', '123');
    expect(result).toEqual(mockFetchResponse.data.getCostCentreDetails);
  });

  it('should call getCostCenterDetails with errors', async () => {
    mockFetchResponse.data = {
      getCostCentreDetails: [
        {
          accountUniqueCustomerId: '19000315',
          costCentreUniqueCustomerId: '19000316',
          costCentreCode: 'CC02',
          costCentreName: 'Digital',
        },
      ],
    };
    mockFetchResponse.errors = ['error'];
    const result = await getCostCenterDetails('token', '123');
    expect(result).toEqual([]);

    mockFetchResponse.data = {
      getCostCentreDetails: null,
    };
    mockFetchResponse.errors = ['error'];
    const resultTwo = await getCostCenterDetails('token', '123');
    expect(resultTwo).toEqual([]);

    mockFetchResponse.data = {
      getCostCentreDetails: null,
    };
    mockFetchResponse.errors = undefined;
    const resultThree = await getCostCenterDetails('token', '123');
    expect(resultThree).toEqual([]);
  });

  it('should call getEmployeesWithFilteringOptions', async () => {
    mockOkStatus.value = true;
    const params = {
      companyId: 'ASDZXC',
      size: 15,
      token: 'asdzxc',
    };
    mockFetchResponse.data = {
      getEmployeesWithFilteringOptionsV2: {
        companyId: 'COMP_80eade56-67a5-4eb9-ac07-2e6fe5c24739',
        searchCriteria: '',
        awaitingApproval: true,
        bookingChannel: 'CBT',
        size: 30,
        shouldFilterEmployees: false,
      },
    };
    mockFetchResponse.errors = undefined;
    const result = await getEmployeesWithFilteringOptions(
      params.companyId,
      params.size,
      params.token
    );
    expect(result).toEqual({
      companyId: 'COMP_80eade56-67a5-4eb9-ac07-2e6fe5c24739',
      searchCriteria: '',
      awaitingApproval: true,
      bookingChannel: 'CBT',
      size: 30,
      shouldFilterEmployees: false,
    });
  });

  it('should call getEmployees', async () => {
    mockOkStatus.value = true;
    const params = {
      companyId: 'ASDZXC',
      size: 15,
      token: 'asdzxc',
    };
    mockFetchResponse.data = {
      getEmployeesV2: {
        id: 'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
        title: 'Mr',
        firstName: 'TestingNo',
        lastName: 'BookingsAutomation',
        emailAddress: 'testing.guest.no.bookings@mailinator.com',
        accessLevel: 'STAYER',
        employeeStatus: 'ACTIVE',
      },
    };
    const result = await getEmployees(params.companyId, params.size, params.token);
    expect(result).toEqual({
      id: 'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
      title: 'Mr',
      firstName: 'TestingNo',
      lastName: 'BookingsAutomation',
      emailAddress: 'testing.guest.no.bookings@mailinator.com',
      accessLevel: 'STAYER',
      employeeStatus: 'ACTIVE',
    });
  });

  it('should call getEmployeeDetails', async () => {
    mockOkStatus.value = true;
    const params = {
      companyId: 'ASDZXC',
      employeeId: 'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
      token: 'asdzxc',
    };
    mockFetchResponse.data = {
      getEmployeeDetailsV3: {
        id: 'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
        title: 'Mr',
        firstName: 'TestingNo',
        lastName: 'BookingsAutomation',
        emailAddress: 'testing.guest.no.bookings@mailinator.com',
        accessLevel: 'SUPER',
        employeeStatus: 'ACTIVE',
      },
    };
    const result = await getEmployeeDetails(params.companyId, params.employeeId, params.token);
    expect(result).toEqual({
      id: 'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
      title: 'Mr',
      firstName: 'TestingNo',
      lastName: 'BookingsAutomation',
      emailAddress: 'testing.guest.no.bookings@mailinator.com',
      accessLevel: 'SUPER',
      employeeStatus: 'ACTIVE',
    });
  });
  it('should call getProfileDetails', async () => {
    mockOkStatus.value = true;
    const params = {
      customerId: 'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
      token: 'asdzxc',
    };
    mockFetchResponse.data = {
      getProfileDetailsV3: {
        contactDetails: {
          title: 'Mr',
          firstName: 'TestingNo',
          lastName: 'BookingsAutomation',
          emailAddress: 'testing.guest.no.bookings@mailinator.com',
          phoneNumber: '+443332131',
          mobileNumber: '+443332131',
          address: {
            line1: 'test',
            postalCode: 'GU16 7HF',
            countryCode: 'GB',
          },
        },
      },
    };
    const result = await getProfileDetails(params.customerId, params.token);
    expect(result).toEqual({
      contactDetails: {
        title: 'Mr',
        firstName: 'TestingNo',
        lastName: 'BookingsAutomation',
        emailAddress: 'testing.guest.no.bookings@mailinator.com',
        phoneNumber: '+443332131',
        mobileNumber: '+443332131',
        address: {
          line1: 'test',
          postalCode: 'GU16 7HF',
          countryCode: 'GB',
        },
      },
    });
  });

  it('should call getCommonIcons', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { commonIconsEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getCommonIcons('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getAccountList', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getAccountList: { accounts: [] },
    };
    const result = await getAccountList('abc');
    expect(result).toEqual([]);
  });

  it('should not call getAccountList request when token is missing', async () => {
    const fetchMock = global.fetch as jest.Mock;
    fetchMock.mockClear();

    const result = await getAccountList('');

    expect(result).toEqual([]);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it('should call getServerUnleashToggles', async () => {
    const result = await getServerUnleashToggles(
      'Homepage',
      {},
      '/',
      new URLSearchParams(),
      { country: 'gb' as ShortCountry } // 5th param is context object
    );
    expect(result).toBeUndefined();
  });

  it('should call getActivePath and find Home', async () => {
    const result = getActivePath('/homepage', mockNavObject);
    expect(result).toEqual('Home');
  });

  it('should call getActivePath and not find any path', async () => {
    const result = getActivePath('/noPath', mockNavObject);
    expect(result).toEqual('');
  });

  it('should call getLocaleByPathname with en language', async () => {
    const result = getLocaleByPathname('ib/en-gb/homepage');
    expect(result).toEqual(LOCALES.EN);
  });
  it('should call getLocaleByPathname with de language', async () => {
    const result = getLocaleByPathname('ib/de/de/homepage');
    expect(result).toEqual(LOCALES.DE);
  });

  it('should call getCountryLanguageByLocale for en-gb', () => {
    const result = getCountryLanguageByLocale('en-gb');
    expect(result).toEqual({
      country: 'gb',
      language: 'en',
    });
  });

  it('should call getCountryLanguageByLocale for de-de', () => {
    const result = getCountryLanguageByLocale('de-de');
    expect(result).toEqual({
      country: 'de',
      language: 'de',
    });
  });

  it('should call getCountryLanguageByLocale for de', () => {
    const result = getCountryLanguageByLocale('de');
    expect(result).toEqual({
      country: 'de',
      language: 'de',
    });
  });

  it('should call getInnBusinessServerSideProps for EN without footer', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = { getProfileDetailsV2: {}, value: 'values' };
    const result = await getInnBusinessServerSideProps(mockToken as any, 'en');
    expect(result?.footer).toEqual(null);
  });

  it('should call getFooterLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      footer: { value: 'footerValues' },
    };
    const result = await getFooterLabels('en');
    expect(result.value).toEqual('footerValues');
  });

  it('should call getFooterLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      footer: { value: 'footerValues' },
    };
    const result = await getFooterLabels('de');
    expect(result.value).toEqual('footerValues');
  });

  it('should call getInnBusinessServerSideProps for EN with footer', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getProfileDetailsV2: {},
      footer: { value: 'footerValues' },
    };
    const result = await getInnBusinessServerSideProps(mockToken as any, 'en', true);
    expect(result?.footer).toEqual({ value: 'footerValues' });
  });

  it('should call getHotelInformationIB with hotelId and language', async () => {
    mockFetchResponse.data = {
      hotelInformation: { hotelId: 'LONEUS', name: 'London Euston' },
    };
    const result = await getHotelInformationIB('LONEUS', 'en');
    expect(result.name).toEqual('London Euston');
  });

  it('should call getHotelInformationIB with hotelId and language DE', async () => {
    mockFetchResponse.data = {
      hotelInformation: { hotelId: 'LONEUS', name: 'London Euston' },
    };
    const result = await getHotelInformationIB('LONEUS', 'de');
    expect(result.name).toEqual('London Euston');
  });

  it('should call getHotelAvailabilitiesIB with params and token', async () => {
    mockFetchResponse.data = {
      hotelAvailability: { available: true, rooms: [] },
    };
    const result = await getHotelAvailabilitiesIB(
      mockAvailabilityParams.hotelId,
      mockAvailabilityParams.arrival,
      mockAvailabilityParams.departure,
      mockAvailabilityParams.brand,
      mockAvailabilityParams.country,
      mockAvailabilityParams.rooms as any,
      mockAvailabilityParams.language,
      mockAvailabilityParams.bookingChannel,
      mockAvailabilityParams.channel,
      mockToken as any
    );
    expect(result.hotelAvailability.available).toEqual(true);
  });

  it('should return call getEmployeesCSV', async () => {
    const result = await getEmployeesCSV('test', '1');

    expect(result).toBeNull();
  });
  it('should return call getEmployeesCSV with no companyId', async () => {
    const result = await getEmployeesCSV('test', '');

    expect(result).toBeNull();
  });

  it('should call getCompanyRegistrationQuestionsAndAnswers', async () => {
    mockFetchResponse.data = {
      getCompanyRegistrationQuestionsAndAnswers: mockRegistrationQuestionsAndAnswers,
    };
    const result = await getCompanyRegistrationQuestionsAndAnswers('123', {});
    expect(result).toEqual(mockRegistrationQuestionsAndAnswersResult);
  });

  it('should call getAllPibaCards', async () => {
    mockFetchResponse.data = {
      getAllPIBACards: true,
    };
    const result = await getAllPibaCards(
      mockToken as any,
      LOCALES.EN,
      mockToken as any,
      false,
      false,
      1,
      10
    );
    expect(result).toEqual(true);
  });

  it('should call getPaymentCards', async () => {
    mockFetchResponse.data = {
      getPaymentCards: true,
    };
    const result = await getPaymentCards(mockToken as any, 'abc');
    expect(result).toEqual(true);
  });

  it('should call companyDetailsLookup', async () => {
    mockFetchResponse.data = {
      companyDetailsLookup: true,
    };
    const result = await companyDetailsLookup(mockToken as any, 'abc', 'GB' as Scheme);
    expect(result).toEqual(true);
  });

  it('should return call getOutOfPolicyReport', async () => {
    const result = await getOutOfPolicyReport('test', '1', '2025-05-03', '2025-05-10');

    expect(result).toBeNull();
  });

  it('should return call getOutOfPolicyReport with no companyId', async () => {
    const result = await getOutOfPolicyReport('test', '', '2025-05-03', '2025-05-10');

    expect(result).toBeNull();
  });

  it('should return call getBusinessBookerIpHeaders', () => {
    const result = getBusinessBookerIpHeaders({});

    expect(result).toEqual({
      'x-forwarded-for': '',
      'True-Client-IP': '',
    });
  });
});

describe('getActivationDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getActivationDetails', async () => {
    mockFetchResponse.data = {
      getActivationDetails: {
        emailAddress: 'test@test.com',
        employeeStatus: 'INACTIVE',
        accessLevel: 'SUPER',
      },
    };
    const result = await getActivationDetails('abc');
    expect(result).toEqual({
      accessLevel: 'SUPER',
      emailAddress: 'test@test.com',
      employeeStatus: 'INACTIVE',
    });
  });
});

describe('getIBActivationDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getIBActivationDetails', async () => {
    mockFetchResponse.data = {
      getInnBusinessActivationDetailsV2: {
        emailAddress: 'test@test.com',
        employeeStatus: 'INACTIVE',
        accessLevel: 'SUPER',
      },
    };
    const result = await getIBActivationDetails('abc');
    expect(result).toEqual({
      accessLevel: 'SUPER',
      emailAddress: 'test@test.com',
      employeeStatus: 'INACTIVE',
    });
  });
});

describe('getMarketingPreferences', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getMarketingPreferences', async () => {
    mockFetchResponse.data = {
      anonymousNewsletterPreferences: {
        optIn: false,
        secondOptIn: false,
        secondOptInReq: true,
        secondPartyOptIn: false,
        thirdPartyVendorsOptIn: false,
      },
    };
    const result = await getMarketingPreferences('abc', 'test@test.com');
    expect(result).toEqual(mockFetchResponse.data.anonymousNewsletterPreferences);
  });
});

describe('staticHotelInformationIB', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should call staticHotelInformationIB with params ', async () => {
    mockFetchResponse.data = { hotelInformationBySlug: { name: 'London Euston' } };
    const result = await staticHotelInformationIB(
      mockStaticHotelInformationParams.slug,
      mockStaticHotelInformationParams.language
    );
    expect(result.name).toEqual('London Euston');
  });
  it('should call staticHotelInformationIB with params and language DE ', async () => {
    mockFetchResponse.data = { hotelInformationBySlug: { name: 'London Euston' } };
    const result = await staticHotelInformationIB(mockStaticHotelInformationParams.slug, 'de');
    expect(result.name).toEqual('London Euston');
  });
});

describe('roomDefaultValueIfError', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const defaultRoom = [{ adultsNumber: 1, childrenNumber: 0, type: ROOM_CODES.double }];
  it('should return default values when room has invalid values', () => {
    const rooms = [
      { adultsNumber: 3, childrenNumber: 0, type: ROOM_CODES.double },
      { adultsNumber: 2, childrenNumber: 0, type: ROOM_CODES.single },
    ];

    const result = roomDefaultValueIfError(rooms);
    expect(result).toEqual(defaultRoom);
  });
  it('should return the original rooms when all values are valid', () => {
    const rooms = [
      { adultsNumber: 2, childrenNumber: 0, type: ROOM_CODES.double },
      { adultsNumber: 1, childrenNumber: 1, type: ROOM_CODES.family },
    ];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(rooms);
  });
  it('should return default values when room is empty', () => {
    const rooms: Room[] = [];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(defaultRoom);
  });
});

describe('getCountryName', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should return country Name', () => {
    const mockedProps = [
      {
        countryCode: ShortCountry.GB,
        countryName: 'United Kingdom (the)',
        dialingCode: '+00',
        flagSrc: '',
      },
    ];
    const result = getCountryName(ShortCountry.GB, mockedProps);

    expect(result).toBe('United Kingdom (the)');
  });
  it('should return country Name when country is D', () => {
    const mockedProps = [
      {
        countryCode: ShortCountry.DE,
        countryName: 'Germany',
        dialingCode: '+00',
        flagSrc: '',
      },
    ];
    const result = getCountryName('D', mockedProps);

    expect(result).toBe('Germany');
  });
});
describe('getDaysInMonth', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should return country Name', () => {
    const date = new Date();

    const month = date.getMonth() + 1;
    const year = date.getFullYear();
    const result = getDaysInMonth(month, year);

    const expectedDays = new Date(year, month, 0).getDate();

    expect(result).toBe(expectedDays);
  });
});

describe('getContactPreferences', () => {
  const mockEmail = 'test@example.com';
  const mockToken = 'mock-token';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch contact preferences successfully', async () => {
    mockFetchResponse.data = {
      getContactPreferences: {
        email: mockEmail,
        preferences: {
          marketing: true,
          service: false,
        },
      },
    };
    mockOkStatus.value = true;

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        headers: {
          Authorization: `Bearer ${mockToken}`,
          'Content-Type': 'application/json',
          'X-Dev-Nonce': 'testrandom',
          'apollographql-client-name': '',
          'apollographql-client-version': '',
        },
        cache: 'no-cache',
        body: expect.stringContaining('getContactPreferences'),
      })
    );

    expect(result).toEqual({
      email: mockEmail,
      preferences: {
        marketing: true,
        service: false,
      },
    });
  });

  it('should return default preferences when GraphQL returns null data', async () => {
    mockFetchResponse.data = { getContactPreferences: null };
    mockOkStatus.value = true;

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should return default preferences when there is an AppSync error', async () => {
    mockFetchResponse.data = null;
    mockFetchResponse.errors = [{ message: 'appsync.global.error' }];
    mockOkStatus.value = true;

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should return default preferences when there is a network error', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should show Authentication error if no token', async () => {
    await expect(getContactPreferences(mockEmail, '')).rejects.toThrowError(
      'Authentication token is required'
    );
  });

  it('should show Email error if no email', async () => {
    await expect(getContactPreferences('', mockToken)).rejects.toThrowError('Email is required');
  });

  it('should handle non-AppSync GraphQL errors by returning null', async () => {
    mockFetchResponse.data = null;
    mockFetchResponse.errors = [{ message: 'some other error' }];
    mockOkStatus.value = true;

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should verify the correct variables are sent in the GraphQL query', async () => {
    mockFetchResponse.data = {
      getContactPreferences: {
        email: mockEmail,
        preferences: {
          marketing: true,
          service: false,
        },
      },
    };
    mockOkStatus.value = true;

    await getContactPreferences(mockEmail, mockToken, { pageName: 'contact-preferences-test' });

    const requestBody = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body);
    expect(requestBody.variables).toEqual({
      request: {
        contactType: 'email',
        contactValue: mockEmail,
        brandCodes: 'PINN',
        business: true,
        contactChannelId: null,
      },
    });
  });

  it('should handle empty response data', async () => {
    mockFetchResponse.data = {};

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should handle undefined preferences', async () => {
    mockFetchResponse.data = { updateContactPreferences: '' };

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should handle null preferences', async () => {
    mockFetchResponse.data = { getContactPreferences: { preferences: null } };

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should handle malformed response data', async () => {
    mockFetchResponse.data = { getContactPreferences: { unexpected: 'data' } };

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should handle response with missing required fields', async () => {
    mockFetchResponse.data = { getContactPreferences: { email: mockEmail } };

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should handle non-200 response with data', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.data = {
      getContactPreferences: { email: mockEmail, preferences: { optIn: true } },
    };

    const result = await getContactPreferences(mockEmail, mockToken);

    expect(result).toEqual({
      contactChannelId: null,
      contactChannelValue: mockEmail,
      deleted: false,
      loyaltyAccounts: [],
      permissions: [
        {
          brand: 'Premier Inn',
          brandCode: 'PINN',
          optIn: false,
          secondOptIn: null,
          secondOptInReq: null,
          secondPartyOptIn: false,
          thirdPartyVendorsOptIn: false,
          suppressMarketingCheckbox: false,
        },
      ],
      valid: true,
    });
  });

  it('should call getSelectedAccountHolder', async () => {
    const accounts = [
      {
        accountName: 'test four',
        accountNumber: '6356290001000111',
        schemeCustomerId: 19000302,
        tetheredGuid: '4abb9835-8996-42bd-8199-5ba0919f1061',
        registrationRoles: ['COST_CENTRE_USER'],
        errorCode: undefined,
        scheme: 'DE',
      },
      {
        accountName: 'test four',
        accountNumber: '6356290001000112',
        schemeCustomerId: 19000302,
        tetheredGuid: '4abb9835-8996-42bd-8199-5ba0919f106e',
        registrationRoles: ['COST_CENTRE_USER'],
        errorCode: undefined,
        scheme: 'DE',
      },
    ] as CustomerAccountDetails[];
    const result = getSelectedAccountHolder(accounts, '4abb9835-8996-42bd-8199-5ba0919f106e');

    expect(result).toEqual(accounts[1]);
  });
  it('should return first account, if accountNumber param is not in the list', () => {
    const accounts = [
      {
        accountName: 'test four',
        accountNumber: '6356290001000111',
        schemeCustomerId: 19000302,
        tetheredGuid: '4abb9835-8996-42bd-8199-5ba0919f106e',
        registrationRoles: ['COST_CENTRE_USER'],
        errorCode: undefined,
        scheme: 'DE',
      },
      {
        accountName: 'test four',
        accountNumber: '6356290001000112',
        schemeCustomerId: 19000302,
        tetheredGuid: '4abb9835-8996-42bd-8199-5ba0919f106e',
        registrationRoles: ['COST_CENTRE_USER'],
        errorCode: undefined,
        scheme: 'DE',
      },
    ] as CustomerAccountDetails[];
    const result = getSelectedAccountHolder(accounts, 'nonexistent-account-number');
    expect(result).toEqual(accounts[0]);
  });
});

describe('getManagementInformationReport', () => {
  const mockToken = 'mock-token';
  const mockFromDate = '2023-01-01';
  const mockToDate = '2023-12-31';
  const mockShowQnAcolumns = true;
  const mockLanguage = 'en';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch management information report successfully', async () => {
    const response = {
      data: {
        managementInformationReport: {
          reportData: 'some data',
        },
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    jest.mock('..', () => ({
      ...jest.requireActual('..'),
      executeGraphQLQueries: jest.fn().mockResolvedValue({
        response,
      }),
    }));

    const result = await getManagementInformationReport(
      mockToken,
      mockFromDate,
      mockToDate,
      mockShowQnAcolumns,
      mockLanguage
    );

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query managementInformationReport(\\n    $fromDate: String!\\n    $toDate: String!\\n    $showQnAcolumns: Boolean!\\n    $language: String!\\n  ) {\\n    managementInformationReport(\\n      fromDate: $fromDate\\n      toDate: $toDate\\n      showQnAcolumns: $showQnAcolumns\\n      language: $language\\n    ) {\\n      downloadUrl\\n      fileName\\n      reportName\\n    }\\n  }\\n","variables":{"fromDate":"2023-01-01","toDate":"2023-12-31","showQnAcolumns":true,"language":"en"}}',
      headers: {
        Authorization: 'Bearer mock-token',
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
      next: {
        revalidate: 0,
      },
    });
    expect(result).toEqual(response);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getManagementInformationReport(
      mockToken,
      mockFromDate,
      mockToDate,
      mockShowQnAcolumns,
      mockLanguage
    );
    expect(result).toBeNull();
  });
  it('should throw error if no token', async () => {
    await expect(
      getManagementInformationReport('', mockFromDate, mockToDate, mockShowQnAcolumns, mockLanguage)
    ).rejects.toThrowError('Authentication token is required');
  });
});

describe('getCookieConsentInfo', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch cookie consent info successfully', async () => {
    const mockCountry = 'GB';
    const mockLanguage = 'en';
    const mockBrand = 'pi';
    const response = {
      data: {
        cookieConsent: {
          cookiePolicies: {
            config: {
              cookieOptInExpiryDays: 365,
              cookieOptOutExpiryDays: 30,
            },
            brand: mockBrand,
            introView: {
              title: 'Cookies and how we use them',
              description: 'Some description',
              manageButtonText: 'Manage cookies',
              acceptAllButtonText: 'Accept all cookies',
              necessaryOnlyButtonText: 'Necessary only',
            },
          },
        },
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getCookieConsentInfo(mockCountry, mockLanguage, mockBrand);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query getCookieConsentInfo($country: String!, $language: String!, $brand: String!) {\\n    cookieConsent(brand: $brand, country: $country, language: $language) {\\n      cookiePolicies {\\n        brand\\n        introView {\\n          acceptAllButtonText\\n          description\\n          manageButtonText\\n          title\\n          necessaryOnlyButtonText\\n        }\\n        manageView {\\n          alwaysActiveText\\n          description\\n          saveSettingsButtonText\\n          title\\n          cookieGroup {\\n            cookieName\\n            description\\n            isAlwaysActive\\n            title\\n            toggleLabel\\n          }\\n        }\\n        config {\\n          cookieOptOutExpiryDays\\n          cookieOptInExpiryDays\\n        }\\n      }\\n    }\\n  }\\n","variables":{"country":"GB","language":"en","brand":"pi"}}',
      headers: {
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
      next: {
        revalidate: 0,
      },
    });
    expect(result).toEqual(response.data);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getCookieConsentInfo('GB', 'en', 'pi');
    expect(result).toBeNull();
  });

  it('should return null if no data is returned', async () => {
    mockFetchResponse.data = null;
    const result = await getCookieConsentInfo('GB', 'en', 'pi');
    expect(result).toBeNull();
  });
});

describe('getEmergencyReport', () => {
  const mockToken = 'mock-token';
  const mockLanguage = 'en';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch emergency report successfully', async () => {
    const response = {
      data: {
        emergencyReport: {
          downloadUrl: 'https://example.com/file.xml',
          fileName: 'Emergency_Report.xls',
          reportName: 'Emergency Report',
        },
      },
    };
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    jest.mock('..', () => ({
      ...jest.requireActual('..'),
      executeGraphQLQueries: jest.fn().mockResolvedValue({
        data: response,
      }),
    }));

    const result = await getEmergencyReport(mockToken, mockLanguage);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query emergencyReport($language: String!) {\\n    emergencyReport(language: $language) {\\n      downloadUrl\\n      fileName\\n      reportName\\n    }\\n  }\\n","variables":{"language":"en"}}',
      headers: {
        Authorization: 'Bearer mock-token',
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
      next: {
        revalidate: 0,
      },
    });
    expect(result).toEqual(response);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getEmergencyReport(mockToken, mockLanguage);
    expect(result).toBeNull();
  });
  it('should throw error if no token', async () => {
    await expect(getEmergencyReport('', mockLanguage)).rejects.toThrowError(
      'Authentication token is required'
    );
  });
});

describe('getUserRolesForAccount', () => {
  const mockAccount: CustomerAccountDetails = {
    accountName: 'test account',
    accountNumber: '1234567890',
    schemeCustomerId: 12345,
    tetheredGuid: 'guid',
    registrationRoles: [RegistrationRole.AccountHolder],
    errorCode: undefined,
  };
  const originalWindow = global.window;

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
    // Remove window to simulate server-side environment
    // @ts-expect-error - intentionally deleting window for test
    delete global.window;
  });

  afterEach(() => {
    global.window = originalWindow;
  });

  it('should return user roles for account with SUPER access level', async () => {
    const mockUserDetails = {
      email: 'test@test.com',
      companyId: 'test',
      business: {
        accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
        tethered: false,
      },
    };
    const response = {
      data: {
        getProfileDetailsV3: mockUserDetails,
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getUserRolesForAccount(mockAccount);
    expect(result).toEqual({
      wl: [RegistrationRole.AccountHolder],
      bb: [BUSINESS_BOOKER_USER_ROLES.SUPER],
    });
  });

  it('should return user roles for account with no access level', async () => {
    const mockUserDetails = {
      email: 'test@test.com',
      companyId: 'test',
      business: {
        accessLevel: undefined,
        tethered: false,
      },
    };
    const response = {
      data: {
        getProfileDetailsV2: mockUserDetails,
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getUserRolesForAccount(mockAccount);
    expect(result).toEqual({
      wl: [RegistrationRole.AccountHolder],
      bb: [],
    });
  });

  it('should return user roles for account with undefined registration roles', async () => {
    const mockUserDetails = {
      email: 'test@test.com',
      companyId: 'test',
      business: {
        accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
        tethered: false,
      },
    };
    const mockAccountWithNoRoles = {
      ...mockAccount,
      registrationRoles: undefined,
    };
    const response = {
      data: {
        getProfileDetailsV3: mockUserDetails,
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getUserRolesForAccount(mockAccountWithNoRoles);
    expect(result).toEqual({
      wl: [],
      bb: [BUSINESS_BOOKER_USER_ROLES.SUPER],
    });
  });
});

describe('getCompanySpending', () => {
  const mockToken = 'mock-token';
  const mockFromMonthYear = '2023-01';
  const mockToMonthYear = '2023-12';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch company spending successfully', async () => {
    const response = {
      data: {
        getCompanySpending: {
          totalSpending: 10000,
          monthlySpending: [
            { month: '2023-01', amount: 1000 },
            { month: '2023-02', amount: 1200 },
          ],
        },
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    jest.mock('..', () => ({
      ...jest.requireActual('..'),
      executeGraphQLQueries: jest.fn().mockResolvedValue({
        data: response,
      }),
    }));

    const result = await getCompanySpending(mockToken, mockFromMonthYear, mockToMonthYear);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query getCompanySpending($fromMonthYear: String!, $toMonthYear: String!) {\\n    getCompanySpending(\\n      searchCompanySpending: { fromMonthYear: $fromMonthYear, toMonthYear: $toMonthYear }\\n    ) {\\n      companySpendingDtoList {\\n        bookingValue\\n        companyAccountId\\n        month\\n        noOfBookings\\n        year\\n        bookingCurrency\\n      }\\n    }\\n  }\\n","variables":{"fromMonthYear":"2023-01","toMonthYear":"2023-12"}}',
      cache: 'no-cache',
      headers: {
        Authorization: 'Bearer mock-token',
        'Content-Type': 'application/json',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
        'X-Dev-Nonce': 'testrandom',
      },
      method: 'POST',
    });
    expect(result).toEqual(response);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getCompanySpending(mockToken, mockFromMonthYear, mockToMonthYear);
    expect(result).toBeNull();
  });

  it('should throw error if no token', async () => {
    await expect(getCompanySpending('', mockFromMonthYear, mockToMonthYear)).rejects.toThrowError(
      'Authentication token is required'
    );
  });
});

describe('getYourSpending', () => {
  const mockToken = 'mock-token';
  const mockEmployeeSpendCriteria = {
    fromMonthYear: '05-2025',
    toMonthYear: '04-2026',
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch your spending successfully', async () => {
    const response = {
      data: {
        getEmployeeSpend: {
          employeeSpendDtoList: {
            totalSpending: 10000,
            monthlySpending: [
              { month: '2025-05', amount: 1000 },
              { month: '2026-04', amount: 1200 },
            ],
          },
        },
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getYourSpending(mockToken, mockEmployeeSpendCriteria);

    expect(result).toEqual(response);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getYourSpending(mockToken, mockEmployeeSpendCriteria);
    expect(result).toBeNull();
  });

  it('should throw error if no token', async () => {
    await expect(getYourSpending('', mockEmployeeSpendCriteria)).rejects.toThrowError(
      'Authentication token is required'
    );
  });
});

describe('getUpcomingBookings', () => {
  const mockToken = 'mock-token';
  const mockParams = {
    country: 'GB',
    language: 'en',
    channel: 'bb',
    subchannel: 'web',
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch upcoming bookings successfully', async () => {
    const response = {
      data: {
        getUpcomingBookings: {},
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    jest.mock('..', () => ({
      ...jest.requireActual('..'),
      executeGraphQLQueries: jest.fn().mockResolvedValue({
        data: response,
      }),
    }));

    const result = await getUpcomingBookings(mockToken, mockParams);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query getUpcomingBookings(\\n    $country: String!\\n    $language: String!\\n    $channel: String!\\n    $subchannel: String!\\n  ) {\\n    getUpcomingBookings(\\n      upcomingBookingsRequest: {\\n        country: $country\\n        language: $language\\n        channel: $channel\\n        subchannel: $subchannel\\n      }\\n    ) {\\n      arrivalDate\\n      arrivalTime\\n      bookingReference\\n      bookings\\n      brand\\n      departureDate\\n      departureTime\\n      galleryImages {\\n        alt\\n        imageSrc\\n      }\\n      hotelName\\n      stays\\n    }\\n  }\\n","variables":{"country":"GB","language":"en","channel":"bb","subchannel":"web"}}',
      cache: 'no-cache',
      headers: {
        Authorization: 'Bearer mock-token',
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
    });
    expect(result).toEqual(response);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getUpcomingBookings(mockToken, mockParams);
    expect(result).toBeNull();
  });

  it('should throw error if no token', async () => {
    await expect(getUpcomingBookings('', mockParams)).rejects.toThrowError(
      'Authentication token is required'
    );
  });
});

describe('getUpcomingSpending', () => {
  const mockToken = 'mock-token';
  const mockAccountId = '33333333';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch upcoming spending successfully', async () => {
    const response = {
      data: {
        getAccountUpcomingSpending: {},
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    jest.mock('..', () => ({
      ...jest.requireActual('..'),
      executeGraphQLQueries: jest.fn().mockResolvedValue({
        data: response,
      }),
    }));

    const result = await getUpcomingSpending(mockToken, mockAccountId);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query GetAccountUpcomingSpending($accountId: String!, $tetheredUserGuid: String) {\\n    getAccountUpcomingSpending(accountId: $accountId, tetheredUserGuid: $tetheredUserGuid) {\\n      expectedSpendTodayDate\\n      expectedSpendToday\\n      expectedNextBillingStartDate\\n      expectedNextBillingEndDate\\n      expectedNextBilling\\n      expectedNextPeriodStartDate\\n      expectedNextPeriodEndDate\\n      expectedNextPeriod\\n      currency\\n      accountStatus\\n    }\\n  }\\n","variables":{"accountId":"33333333"}}',
      cache: 'no-cache',
      headers: {
        Authorization: 'Bearer mock-token',
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
    });
    expect(result).toEqual(response);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getUpcomingSpending(mockToken, mockAccountId);
    expect(result).toBeNull();
  });

  it('should throw error if no token', async () => {
    await expect(getUpcomingSpending('', mockAccountId)).rejects.toThrowError(
      'Authentication token is required'
    );
  });
});

describe('getPayApplications', () => {
  const mockToken = 'mock-token';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch pay applications successfully', async () => {
    const response = {
      data: {
        getPayApplications: {},
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    jest.mock('..', () => ({
      ...jest.requireActual('..'),
      executeGraphQLQueries: jest.fn().mockResolvedValue({
        data: response,
      }),
    }));

    const result = await getPayApplications(mockToken);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query getPayApplications {\\n    getPayApplications {\\n      applications {\\n        status\\n        accountName\\n        applicationGuid\\n        applicationId\\n        resumeUrl\\n        scheme\\n      }\\n    }\\n  }\\n","variables":{}}',
      cache: 'no-cache',
      headers: {
        Authorization: 'Bearer mock-token',
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
    });
    expect(result).toEqual(response);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getPayApplications(mockToken);
    expect(result).toBeNull();
  });

  it('should throw error if no token', async () => {
    await expect(getPayApplications('')).rejects.toThrowError('Authentication token is required');
  });
});

describe('getNotifications', () => {
  const mockToken = 'mock-token';
  const mockScheme = 'GB';
  const tetherUserGuid = 'mock-tether-user-guid';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch notifications successfully', async () => {
    const response = {
      data: {
        getNotifications: {},
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    jest.mock('..', () => ({
      ...jest.requireActual('..'),
      executeGraphQLQueries: jest.fn().mockResolvedValue({
        data: response,
      }),
    }));

    const result = await getNotifications(mockToken, mockScheme as Scheme, tetherUserGuid);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query getNotifications($tetheredUserId: String, $scheme: Scheme!) {\\n    getNotifications(tetheredUserId: $tetheredUserId, scheme: $scheme) {\\n      status\\n      profileUpdateRequired\\n      usersAwaitingApproval\\n    }\\n  }\\n","variables":{"scheme":"GB","tetheredUserId":"mock-tether-user-guid"}}',
      cache: 'no-cache',
      headers: {
        Authorization: 'Bearer mock-token',
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
    });
    expect(result).toEqual(response);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getNotifications(mockToken, mockScheme as Scheme, tetherUserGuid);
    expect(result).toBeNull();
  });

  it('should throw error if no token', async () => {
    await expect(getNotifications('', mockScheme as Scheme, tetherUserGuid)).rejects.toThrowError(
      'Authentication token is required'
    );
  });
});

describe('getWorldlineUserPreferences', () => {
  const generateTestToken = () => `test-${Date.now()}-${Math.random().toString(36).substring(7)}`;

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should return null when no token is provided', async () => {
    const result = await getWorldlineUserPreferences('');
    expect(result).toBeNull();
  });

  it('should return null when no accounts are found', async () => {
    const testToken = generateTestToken();
    mockFetchResponse.data = { getAccountList: { accounts: [] } };
    const result = await getWorldlineUserPreferences(testToken);
    expect(result).toBeNull();
  });

  it('should handle network errors by returning null', async () => {
    const testToken = generateTestToken();
    global.fetch = jest.fn().mockRejectedValue(new Error('Network error'));
    const result = await getWorldlineUserPreferences(testToken);
    expect(result).toBeNull();
  });

  it('should handle missing account data by returning null', async () => {
    const testToken = generateTestToken();
    mockFetchResponse.data = { getAccountList: null };
    const result = await getWorldlineUserPreferences(testToken);
    expect(result).toBeNull();
  });

  it('should handle multiple accounts and merge preferences correctly', async () => {
    const testToken = generateTestToken();
    const mockMultipleAccounts = {
      data: {
        getAccountList: {
          accounts: [
            {
              tetheredGuid: 'test-guid-1',
              accountName: 'Account 1',
              accountNumber: '1111',
              registrationRoles: ['ACCOUNT_HOLDER'],
            },
            {
              tetheredGuid: 'test-guid-2',
              accountName: 'Account 2',
              accountNumber: '2222',
              registrationRoles: ['FINANCE_USER'],
            },
          ],
        },
      },
    };

    const mockPreferencesResponse = {
      data: {
        getWorldlineUserPreferences: [
          {
            tetheredUserGuid: 'test-guid-1',
            settings: [{ smsType: 'SMS001', isSmsSelected: true }],
            details: { showSmsStopsToCardholder: true, sendCardsToCardholder: false },
          },
          {
            tetheredUserGuid: 'test-guid-2',
            settings: [{ smsType: 'SMS002', isSmsSelected: false }],
            details: { showSmsStopsToCardholder: false, sendCardsToCardholder: true },
          },
        ],
      },
    };

    global.fetch = jest
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({
          json: () => Promise.resolve(mockMultipleAccounts),
          ok: true,
          status: 200,
        } as Response)
      )

      .mockImplementationOnce(() =>
        Promise.resolve({
          json: () => Promise.resolve(mockPreferencesResponse),
          ok: true,
          status: 200,
        } as Response)
      );

    const result = await getWorldlineUserPreferences(testToken);

    expect(result).toBeDefined();
    expect(result).toHaveLength(2);
    expect(result?.[0]).toEqual(
      expect.objectContaining({
        accountName: 'Account 1',
        accountNumber: '1111',
        tetheredUserGuid: 'test-guid-1',
        settings: [{ smsType: 'SMS001', isSmsSelected: true }],
      })
    );
    expect(result?.[1]).toEqual(
      expect.objectContaining({
        accountName: 'Account 2',
        accountNumber: '2222',
        tetheredUserGuid: 'test-guid-2',
        settings: [{ smsType: 'SMS002', isSmsSelected: false }],
      })
    );
  });
});

describe('getRegistrationInfo', () => {
  const mockToken = 'mock-token';
  const mockRegistrationCode = '123456';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch registration info successfully', async () => {
    const response = {
      data: {
        getRegistrationInfo: {},
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    jest.mock('..', () => ({
      ...jest.requireActual('..'),
      executeGraphQLQueries: jest.fn().mockResolvedValue({
        data: response,
      }),
    }));

    await getRegistrationInfo(mockToken, mockRegistrationCode);
    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query GetRegistrationInfo($registrationCode: String!) {\\n    getRegistrationInfo(registrationCode: $registrationCode) {\\n      registrationCodeInfo {\\n        authenticationQuestions {\\n          question\\n          questionId\\n        }\\n        primarySchemeCustomerId\\n        registrationCode\\n        registrationRole\\n        schemeCustomerId\\n      }\\n    }\\n  }\\n","variables":{"registrationCode":"123456"}}',
      cache: 'no-cache',
      headers: {
        Authorization: 'Bearer mock-token',
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
    });
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getRegistrationInfo(mockToken, mockRegistrationCode);
    expect(result).toBeNull();
  });
});

describe('getCountriesList', () => {
  const mockLanguage = 'en';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch countries list successfully', async () => {
    const response = {
      data: {
        countries: {
          countries: [
            { countryCode: 'GB', countryName: 'United Kingdom' },
            { countryCode: 'DE', countryName: 'Germany' },
          ],
        },
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getCountriesList(mockLanguage);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query getCountries($country: String!, $language: String!, $site: String!) {\\n    countries(country: $country, language: $language, site: $site) {\\n      countries {\\n        countryCode\\n        countryCodeLegacy\\n        countryName\\n        dialingCode\\n        flagSrc\\n        passportRequired\\n        nationality\\n      }\\n    }\\n  }\\n","variables":{"language":"en","country":"gb","site":"business-booker"}}',
      headers: {
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
      },
      method: 'POST',
      next: {
        revalidate: 0,
      },
    });
    expect(result).toEqual([
      { countryCode: 'GB', countryName: 'United Kingdom' },
      { countryCode: 'DE', countryName: 'Germany' },
    ]);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getCountriesList(mockLanguage);
    expect(result).toBeNull();
  });

  it('should return null if no data is returned', async () => {
    mockFetchResponse.data = null;
    const result = await getCountriesList(mockLanguage);
    expect(result).toBeNull();
  });

  it('should return null if countries list is empty', async () => {
    mockFetchResponse.data = {
      countries: {
        countries: [],
      },
    };
    const result = await getCountriesList(mockLanguage);
    expect(result).toEqual(null);
  });
});
describe('hasAccountHolder', () => {
  it('should return true if at least one account has the AccountHolder role', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: [RegistrationRole.AccountHolder],
        errorCode: undefined,
      },
      {
        accountName: 'Account 2',
        accountNumber: '654321',
        schemeCustomerId: 2,
        tetheredGuid: 'guid-2',
        registrationRoles: [RegistrationRole.CardHolder],
        errorCode: undefined,
      },
    ];

    const result = hasAccountHolder(accounts);
    expect(result).toBe(true);
  });

  it('should return false if no account has the AccountHolder role', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: [RegistrationRole.CardHolder],
        errorCode: undefined,
      },
      {
        accountName: 'Account 2',
        accountNumber: '654321',
        schemeCustomerId: 2,
        tetheredGuid: 'guid-2',
        registrationRoles: [RegistrationRole.CostCentreUser],
        errorCode: undefined,
      },
    ];

    const result = hasAccountHolder(accounts);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list is empty', () => {
    const accounts: CustomerAccountDetails[] = [];
    const result = hasAccountHolder(accounts);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list is undefined', () => {
    const result = hasAccountHolder(undefined);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list contains accounts with undefined registrationRoles', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: undefined,
        errorCode: undefined,
      },
    ];

    const result = hasAccountHolder(accounts);
    expect(result).toBe(false);
  });
});
describe('hasCostCenterHolder', () => {
  it('should return true if at least one account has the hasCostCenterHolder role', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: [RegistrationRole.CostCentreUser],
        errorCode: undefined,
      },
      {
        accountName: 'Account 2',
        accountNumber: '654321',
        schemeCustomerId: 2,
        tetheredGuid: 'guid-2',
        registrationRoles: [RegistrationRole.AccountHolder],
        errorCode: undefined,
      },
    ];

    const result = hasCostCenterHolder(accounts);
    expect(result).toBe(true);
  });

  it('should return false if the accounts list is undefined', () => {
    const result = hasAccountHolder(undefined);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list is empty', () => {
    const accounts: CustomerAccountDetails[] = [];
    const result = hasCostCenterHolder(accounts);
    expect(result).toBe(false);
  });

  it('should return false if no account has the hasCostCenterHolder role', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: [RegistrationRole.AccountHolder],
        errorCode: undefined,
      },
    ];

    const result = hasCostCenterHolder(accounts);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list is empty', () => {
    const accounts: CustomerAccountDetails[] = [];
    const result = hasCardHolder(accounts);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list is undefined', () => {
    const result = hasCardHolder(undefined);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list contains accounts with undefined registrationRoles', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: undefined,
        errorCode: undefined,
      },
    ];

    const result = hasCardHolder(accounts);
    expect(result).toBe(false);
  });
});

describe('hasCardHolder', () => {
  it('should return true if at least one account has the CardHolder role', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: [RegistrationRole.CardHolder],
        errorCode: undefined,
      },
      {
        accountName: 'Account 2',
        accountNumber: '654321',
        schemeCustomerId: 2,
        tetheredGuid: 'guid-2',
        registrationRoles: [RegistrationRole.AccountHolder],
        errorCode: undefined,
      },
    ];

    const result = hasCardHolder(accounts);
    expect(result).toBe(true);
  });

  it('should return false if no account has the CardHolder role', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: [RegistrationRole.AccountHolder],
        errorCode: undefined,
      },
      {
        accountName: 'Account 2',
        accountNumber: '654321',
        schemeCustomerId: 2,
        tetheredGuid: 'guid-2',
        registrationRoles: [RegistrationRole.CostCentreUser],
        errorCode: undefined,
      },
    ];

    const result = hasCardHolder(accounts);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list is empty', () => {
    const accounts: CustomerAccountDetails[] = [];
    const result = hasCardHolder(accounts);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list is undefined', () => {
    const result = hasCardHolder(undefined);
    expect(result).toBe(false);
  });

  it('should return false if the accounts list contains accounts with undefined registrationRoles', () => {
    const accounts: CustomerAccountDetails[] = [
      {
        accountName: 'Account 1',
        accountNumber: '123456',
        schemeCustomerId: 1,
        tetheredGuid: 'guid-1',
        registrationRoles: undefined,
        errorCode: undefined,
      },
    ];

    const result = hasCardHolder(accounts);
    expect(result).toBe(false);
  });
});
describe('getLocationResults', () => {
  const mockValue = 'London';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch location results successfully', async () => {
    const response = {
      predictions: [
        { description: 'London, UK', place_id: '123' },
        { description: 'London, Ontario, Canada', place_id: '456' },
      ],
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getLocationResults(mockValue);

    expect(global.fetch).toHaveBeenCalledWith(
      `${process.env.NEXT_PUBLIC_SNOWDROP_BASE_URL}v1/autocomplete?input=/${mockValue}&gplaces[components]=country:uk|country:de`
    );
    expect(result).toEqual(response);
  });

  it('should handle empty response data', async () => {
    const response = {};
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getLocationResults(mockValue);
    expect(result).toEqual({});
  });
});
describe('getPostCodeAddresses', () => {
  const mockPostCode = 'SW1A 1AA';
  const mockToken = 'mock-token';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch post code addresses successfully', async () => {
    const response = {
      data: {
        partialAddress: [
          { addressLine1: '10 Downing Street', postalCode: 'SW1A 1AA', country: 'GB' },
          { addressLine1: '11 Downing Street', postalCode: 'SW1A 1AA', country: 'GB' },
        ],
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getPostCodeAddresses(mockPostCode, mockToken);

    expect(global.fetch).toHaveBeenCalledWith(expect.any(String), {
      body: '{"query":"\\n  query addressSearch($searchTerm: String!, $countryCode: String) {\\n    partialAddress(partialAddressCriteria: { searchTerm: $searchTerm, countryCode: $countryCode }) {\\n      id\\n      addressText\\n    }\\n  }\\n","variables":{"searchTerm":"SW1A 1AA"}}',
      headers: {
        'Content-Type': 'application/json',
        'X-Dev-Nonce': 'testrandom',
        'apollographql-client-name': '',
        'apollographql-client-version': '',
        Authorization: 'Bearer mock-token',
      },
      method: 'POST',
      next: {
        revalidate: 0,
      },
    });
    expect(result).toEqual(response.data.partialAddress);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await getPostCodeAddresses(mockPostCode, mockToken);
    expect(result).toBeNull();
  });

  it('should return null if no data is returned', async () => {
    mockFetchResponse.data = null;
    const result = await getPostCodeAddresses(mockPostCode, mockToken);
    expect(result).toBeNull();
  });

  it('should handle empty addresses list', async () => {
    mockFetchResponse.data = {
      partialAddress: [],
    };
    const result = await getPostCodeAddresses(mockPostCode, mockToken);
    expect(result).toEqual(null);
  });
});

describe('getAvailableTabs', () => {
  const MANAGE_TABS = {
    INN_BUSINESS: 'innbusiness',
    INN_BUSINESS_PAY: 'innbusiness-pay',
  };
  it('should return an empty array if the user is neither a travel manager nor an account holder', () => {
    const result = getAvailableTabs(false, false, false);
    expect(result).toEqual([]);
  });

  it('should return only the INN_BUSINESS tab if the user is a travel manager and tethered but not an account holder', () => {
    const result = getAvailableTabs(true, true, false);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS]);
  });

  it('should return only the INN_BUSINESS tab for non-card management if the user is a business pay manager and tethered but not an account holder', () => {
    const result = getAvailableTabs(false, true, false, false, false, false, false, false, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS]);
  });

  it('should return both INN_BUSINESS and INN_BUSINESS_PAY tabs if the user is a travel manager and not tethered', () => {
    const result = getAvailableTabs(true, false, false);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS, MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return both INN_BUSINESS and INN_BUSINESS_PAY tabs for non-card management if the user is a BPM and not tethered', () => {
    const result = getAvailableTabs(false, false, false, false, false, false, false, false, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS, MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return both INN_BUSINESS and INN_BUSINESS_PAY tabs if the user is a travel manager and an account holder', () => {
    const result = getAvailableTabs(true, true, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS, MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return both INN_BUSINESS and INN_BUSINESS_PAY tabs for non-card management if the user is a BPM and an account holder', () => {
    const result = getAvailableTabs(false, true, true, false, false, false, false, false, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS, MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return only the INN_BUSINESS_PAY tab if the user is not a travel manager but is an account holder', () => {
    const result = getAvailableTabs(false, true, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return only the INN_BUSINESS_PAY tab if the user is not a travel manager but has a cost centre holder', () => {
    const result = getAvailableTabs(false, true, true, false, false, false, false, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return only the INN_BUSINESS_PAY tab if the user is not a travel manager but has a cost centre holder for card management', () => {
    const result = getAvailableTabs(false, true, true, false, true, false, false, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return only the INN_BUSINESS_PAY tab for non-tethered BPM users for card management', () => {
    const result = getAvailableTabs(false, false, false, false, true, false, false, false, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return only the INN_BUSINESS_PAY tab for tethered BPM users as account holder for card management', () => {
    const result = getAvailableTabs(false, true, true, false, true, false, false, false, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return no tabs for non-tethered BPM users for DE site with PIBA Euro disabled for card maangement', () => {
    const result = getAvailableTabs(false, false, false, false, true, true, false, false, true);
    expect(result).toEqual([]);
  });

  it('should return only the INN_BUSINESS_PAY tab for for non-tethered BPM users for card management for DE site with PIBA Euro disabled for card management', () => {
    const result = getAvailableTabs(false, false, false, false, true, true, true, false, true);
    expect(result).toEqual([MANAGE_TABS.INN_BUSINESS_PAY]);
  });

  it('should return no tabs for tethered BPM user as finance userfor card maangement', () => {
    const result = getAvailableTabs(false, true, false, false, true, false, false, false, true);
    expect(result).toEqual([]);
  });
});
describe('getSpendOverTimeCSV', () => {
  const mockToken = 'mock-token';
  const mockAccountNumber = '1234567890';
  const mockFromMonthYear = '2023-01';
  const mockToMonthYear = '2023-12';
  const mockScheme = 'GB';
  const mockLanguage = 'en';

  beforeEach(() => {
    jest.clearAllMocks();
    global.fetch = jest.fn();
    global.URL.createObjectURL = jest.fn();
    global.URL.revokeObjectURL = jest.fn();
  });

  it('should call fetch with the correct URL and headers', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        headers: {
          get: (header: string) => {
            if (header === 'content-disposition') {
              return 'attachment; filename="file.csv"';
            }
            return null;
          },
        },
        blob: () => Promise.resolve(new Blob(['mock data'], { type: 'text/csv' })),
      } as Response)
    );
    jest.mock('../formatters', () => ({
      ...jest.requireActual('../formatters'),
      resolveAndDownloadBlob: jest.fn(),
    }));

    await getSpendOverTimeCSV(
      mockToken,
      mockAccountNumber,
      mockFromMonthYear,
      mockToMonthYear,
      mockScheme as Scheme,
      mockLanguage
    );

    expect(global.fetch).toHaveBeenCalledWith(
      `${process.env.NEXT_PUBLIC_REST_API}/v1/spending/accountSpending?pibaAccountId=${mockAccountNumber}&fromMonthYear=${mockFromMonthYear}&toMonthYear=${mockToMonthYear}&scheme=${mockScheme}&language=${mockLanguage}`,
      {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${mockToken}`,
          'WB-Authorization': 'Bearer mock-token',
          accept: 'text/csv',
        },
        cache: 'no-cache',
      }
    );
  });

  it('should return null if any required parameter is missing', async () => {
    const result = await getSpendOverTimeCSV(
      '',
      mockAccountNumber,
      mockFromMonthYear,
      mockToMonthYear,
      mockScheme as Scheme,
      mockLanguage
    );

    expect(result).toBeNull();
  });
});

describe('getAccountSpending', () => {
  const mockFromMonthYear = '2023-01';
  const mockToMonthYear = '2023-12';
  const mockAccountNumber = '1234567890';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch account spending', async () => {
    const mockToken = 'mock-token';
    const response = {
      data: {
        getAccountSpending: {
          accountSpendingDtoList: [
            {
              year: 2025,
              month: '2',
              bookingValue: 1000,
              bookingCurrency: 'GBP',
            },
            {
              year: 2024,
              month: '1',
              bookingValue: 1000,
              bookingCurrency: 'EUR',
            },
          ],
        },
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getAccountSpending(
      mockToken,
      mockFromMonthYear,
      mockToMonthYear,
      mockAccountNumber
    );

    (expect(global.fetch).toHaveBeenCalled(),
      {
        body: '{"query":"\\n  query AccountSpendingDtoList($accountSpendingCriteria: AccountSpendingCriteria!) {\\n    getAccountSpending(accountSpendingCriteria: $accountSpendingCriteria) {\\n      accountSpendingDtoList {\\n        bookingValue\\n        month\\n        noOfBookings\\n        year\\n        pibaAccountId\\n      }\\n    }\\n  }\\n","variables":{"accountSpendingCriteria":{"fromMonthYear":"2023-01","toMonthYear":"2023-12","pibaAccountId":"1234567890"}}}',
        cache: 'no-cache',
        headers: {
          'Content-Type': 'application/json',
          'apollographql-client-name': '',
          'apollographql-client-version': '',
          Authorization: 'Bearer mock-token',
        },
        method: 'POST',
        next: {
          revalidate: 0,
        },
      });
    expect(result).toEqual(response);
  });

  it('should fetch account spending with no token', async () => {
    const mockToken = '';
    const response = null;
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getAccountSpending(
      mockToken,
      mockFromMonthYear,
      mockToMonthYear,
      mockAccountNumber
    );

    expect(global.fetch).toHaveBeenCalledTimes(0);
    expect(result).toBeNull();
  });
});
describe('appPreCheck', () => {
  const mockToken = 'mock-token';
  const mockScheme = 'GB';

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should fetch app pre-check data successfully', async () => {
    const response = {
      data: {
        appPreCheck: {
          isTetheredUser: false,
        },
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        cache: 'no-cache',
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as unknown as Response)
    );

    const result = await appPreCheck(mockToken, mockScheme as Scheme);

    expect(global.fetch).toHaveBeenCalled();
    expect(result).toEqual(response.data.appPreCheck);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error()));
    const result = await appPreCheck(mockToken, mockScheme as Scheme);
    expect(result).toBeNull();
  });

  it('should throw error if no token is provided', async () => {
    await expect(appPreCheck('', mockScheme as Scheme)).rejects.toThrowError(
      'Authentication token is required'
    );
  });

  it('should return null if no data is returned', async () => {
    mockFetchResponse.data = null;
    const result = await appPreCheck(mockToken, mockScheme as Scheme);
    expect(result).toBeNull();
  });

  it('should return null if appPreCheck data is missing', async () => {
    mockFetchResponse.data = {};
    const result = await appPreCheck(mockToken, mockScheme as Scheme);
    expect(result).toBeNull();
  });

  describe('getAllPayRoles', () => {
    it('should return all unique registration roles from the account list', () => {
      const accounts: CustomerAccountDetails[] = [
        {
          accountName: 'Account 1',
          accountNumber: '123456',
          schemeCustomerId: 1,
          tetheredGuid: 'guid-1',
          registrationRoles: [RegistrationRole.AccountHolder, RegistrationRole.CardHolder],
          errorCode: undefined,
        },
        {
          accountName: 'Account 2',
          accountNumber: '654321',
          schemeCustomerId: 2,
          tetheredGuid: 'guid-2',
          registrationRoles: [RegistrationRole.CardHolder, RegistrationRole.CostCentreUser],
          errorCode: undefined,
        },
      ];

      const result = getAllPayRoles(accounts);
      expect(result).toEqual([
        RegistrationRole.AccountHolder,
        RegistrationRole.CardHolder,
        RegistrationRole.CostCentreUser,
      ]);
    });

    it('should return an empty array if accountList is undefined', () => {
      const result = getAllPayRoles(undefined);
      expect(result).toEqual([]);
    });

    it('should return an empty array if accountList is empty', () => {
      const result = getAllPayRoles([]);
      expect(result).toEqual([]);
    });

    it('should handle accounts with undefined registrationRoles', () => {
      const accounts: CustomerAccountDetails[] = [
        {
          accountName: 'Account 1',
          accountNumber: '123456',
          schemeCustomerId: 1,
          tetheredGuid: 'guid-1',
          registrationRoles: undefined,
          errorCode: undefined,
        },
        {
          accountName: 'Account 2',
          accountNumber: '654321',
          schemeCustomerId: 2,
          tetheredGuid: 'guid-2',
          registrationRoles: [RegistrationRole.CardHolder],
          errorCode: undefined,
        },
      ];

      const result = getAllPayRoles(accounts);
      expect(result).toEqual([RegistrationRole.CardHolder]);
    });

    it('should return unique roles only if there are duplicates', () => {
      const accounts: CustomerAccountDetails[] = [
        {
          accountName: 'Account 1',
          accountNumber: '123456',
          schemeCustomerId: 1,
          tetheredGuid: 'guid-1',
          registrationRoles: [RegistrationRole.CardHolder, RegistrationRole.CardHolder],
          errorCode: undefined,
        },
        {
          accountName: 'Account 2',
          accountNumber: '654321',
          schemeCustomerId: 2,
          tetheredGuid: 'guid-2',
          registrationRoles: [RegistrationRole.CardHolder],
          errorCode: undefined,
        },
      ];

      const result = getAllPayRoles(accounts);
      expect(result).toEqual([RegistrationRole.CardHolder]);
    });
  });
});

describe('getEmployeeDataforUserPilot', () => {
  const mockTokenBase = {
    [TOKEN_COMPANY_FIELD]: 'COMPANY_ID',
    [TOKEN_EMPLOYEE_FIELD]: 'EMPLOYEE_ID',
    [TOKEN_EMAIL_FIELD]: 'EMAIL',
    profile: {
      accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
    },
  };
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const token = { ...mockTokenBase, profile: { accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER } };
  const { companyId, employeeId, accessLevel } = getDetailsFromToken(token as any);

  it('should return user pilot data with correct fields test', async () => {
    const decodedToken = {
      'https://premierinn.com/companyAccountId': 'COMPANY_ID',
      'https://premierinn.com/email': 'EMAIL',
      'https://premierinn.com/employeeAccountId': 'EMPLOYEE_ID',
      profile: {
        accessLevel: 'SUPER',
        companyId: '35086',
        employeeId: '1',
        isBusiness: true,
        sessionId: '8Rtr1RKehAOomkSb',
      },
    };
    const promiseAllSpy = jest.spyOn(Promise, 'all').mockImplementation(() => {
      return Promise.resolve([accountList, companyDetails]);
    });

    const accountList = [
      {
        accountNumber: '123',
        registrationRoles: ['CARD_HOLDER', 'ACCOUNT_HOLDER'],
      },
    ];

    const companyDetails: CompanyDetailsResponse = {
      requestedCompany: {
        companyDetails: {
          companySector: 'test-sector',
          numberOfEmployees: 100,
        },
      },
    };

    const result = await getEmployeeDataforUserPilot(decodedToken as any);

    expect(result).toEqual({
      role: accessLevel,
      innBusinessPayRoles: ['CARD_HOLDER', 'ACCOUNT_HOLDER'],
      employeeId: employeeId,
      companyId: companyId,
      companySector: 'test-sector',
      numberOfEmployees: 100,
    });
    promiseAllSpy.mockRestore();
  });

  it('should handle missing userDetails and accounts gracefully', async () => {
    const promiseAllSpy = jest.spyOn(Promise, 'all').mockImplementation(() => {
      return Promise.resolve([undefined, undefined]);
    });
    const result = await getEmployeeDataforUserPilot('mockToken');
    expect(result).toEqual({
      role: '',
      innBusinessPayRoles: [],
      employeeId: '',
      companyId: '',
      companySector: '',
      numberOfEmployees: 0,
    });
    promiseAllSpy.mockRestore();
  });

  it('should return null if an error is thrown', async () => {
    const promiseAllSpy = jest.spyOn(Promise, 'all').mockImplementation(() => {
      return Promise.reject();
    });
    const result = await getEmployeeDataforUserPilot('mockToken');
    expect(result).toBeNull();
    promiseAllSpy.mockRestore();
  });
  it('should return null if no token is provided', async () => {
    const promiseAllSpy = jest.spyOn(Promise, 'all').mockImplementation(() => {
      return Promise.reject();
    });
    const result = await getEmployeeDataforUserPilot('');
    expect(result).toBeNull();
    promiseAllSpy.mockRestore();
  });
});

describe('getAccountRegistrationRoleDetails', () => {
  it('should return correct details for CardHolder role', () => {
    const mockAccount: CustomerAccountDetails = {
      accountName: 'test account',
      accountNumber: '1234567890',
      schemeCustomerId: 12345,
      tetheredGuid: 'guid',
      registrationRoles: [RegistrationRole.CardHolder],
      errorCode: undefined,
    };
    expect(getAccountRegistrationRoleDetails(mockAccount)).toEqual({
      isOnlyCardHolder: true,
      isOnlyFinanceUser: false,
      isOnlyCostCenter: false,
      isCardHolderAndFinanceUser: false,
      isCardHolderAndCostCenterUser: false,
    });
  });
  it('should return correct details for FinanceUser role', () => {
    const mockAccount: CustomerAccountDetails = {
      accountName: 'test account',
      accountNumber: '1234567890',
      schemeCustomerId: 12345,
      tetheredGuid: 'guid',
      registrationRoles: [RegistrationRole.FinanceUser],
      errorCode: undefined,
    };
    expect(getAccountRegistrationRoleDetails(mockAccount)).toEqual({
      isOnlyCardHolder: false,
      isOnlyFinanceUser: true,
      isOnlyCostCenter: false,
      isCardHolderAndFinanceUser: false,
      isCardHolderAndCostCenterUser: false,
    });
  });
  it('should return correct details for CostCentreUser role', () => {
    const mockAccount: CustomerAccountDetails = {
      accountName: 'test account',
      accountNumber: '1234567890',
      schemeCustomerId: 12345,
      tetheredGuid: 'guid',
      registrationRoles: [RegistrationRole.CostCentreUser],
      errorCode: undefined,
    };
    expect(getAccountRegistrationRoleDetails(mockAccount)).toEqual({
      isOnlyCardHolder: false,
      isOnlyFinanceUser: false,
      isOnlyCostCenter: true,
      isCardHolderAndFinanceUser: false,
      isCardHolderAndCostCenterUser: false,
    });
  });
  it('should return correct details for CardHolder and FinanceUser roles', () => {
    const mockAccount: CustomerAccountDetails = {
      accountName: 'test account',
      accountNumber: '1234567890',
      schemeCustomerId: 12345,
      tetheredGuid: 'guid',
      registrationRoles: [RegistrationRole.CardHolder, RegistrationRole.FinanceUser],
      errorCode: undefined,
    };
    expect(getAccountRegistrationRoleDetails(mockAccount)).toEqual({
      isOnlyCardHolder: false,
      isOnlyFinanceUser: false,
      isOnlyCostCenter: false,
      isCardHolderAndFinanceUser: true,
      isCardHolderAndCostCenterUser: false,
    });
  });
  it('should return correct details for CardHolder and CostCenterUser roles', () => {
    const mockAccount: CustomerAccountDetails = {
      accountName: 'test account',
      accountNumber: '1234567890',
      schemeCustomerId: 12345,
      tetheredGuid: 'guid',
      registrationRoles: [RegistrationRole.CardHolder, RegistrationRole.CostCentreUser],
      errorCode: undefined,
    };
    expect(getAccountRegistrationRoleDetails(mockAccount)).toEqual({
      isOnlyCardHolder: false,
      isOnlyFinanceUser: false,
      isOnlyCostCenter: false,
      isCardHolderAndFinanceUser: false,
      isCardHolderAndCostCenterUser: true,
    });
  });
});

describe('getStatementsPdf', () => {
  const mockToken = 'mock-token';
  const mockTetheredUserGuid = '1234567890';
  const mockSchemeCustomerId = 123456;
  const mockfileAutoID = 12345;
  const mockStatementDate = '20250101';
  const mockInvoiceNo = '123';

  beforeEach(() => {
    jest.clearAllMocks();
    global.fetch = jest.fn();
    global.URL.createObjectURL = jest.fn();
    global.URL.revokeObjectURL = jest.fn();
  });

  it('should call getStatementsPdf with the correct params', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        headers: {
          get: (header: string) => {
            if (header === 'content-disposition') {
              return 'attachment; filename="file.pdf"';
            }
            return null;
          },
        },
        blob: () => Promise.resolve(new Blob(['mock data'], { type: 'application/pdf' })),
      } as Response)
    );
    jest.mock('../formatters', () => ({
      ...jest.requireActual('../formatters'),
      resolveAndDownloadBlob: jest.fn(),
    }));

    await getStatementsPdf(
      mockToken,
      mockTetheredUserGuid,
      mockSchemeCustomerId,
      mockfileAutoID,
      mockStatementDate,
      mockInvoiceNo,
      'GB'
    );

    expect(global.fetch).toHaveBeenCalledWith(
      `${process.env.NEXT_PUBLIC_REST_API}/v2/piba/account/invoices/download/${mockSchemeCustomerId}/${mockTetheredUserGuid}/${mockfileAutoID}?scheme=GB`,
      {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${mockToken}`,
          'WB-Authorization': 'Bearer mock-token',
          accept: 'application/pdf',
        },
        cache: 'no-cache',
      }
    );
  });

  it('should return null if any param is missing', async () => {
    const result = await getStatementsPdf(
      '',
      mockTetheredUserGuid,
      mockSchemeCustomerId,
      mockfileAutoID,
      mockStatementDate,
      mockInvoiceNo,
      'GB'
    );

    expect(result).toBeNull();
  });
});
describe('getStatementsXls', () => {
  const mockToken = 'mock-token';
  const mockTetheredUserGuid = '1234567890';
  const mockSchemeCustomerId = 123456;
  const mockfileAutoID = 12345;
  const mockStatementDate = '20250101';
  const mockInvoiceNo = '123';

  beforeEach(() => {
    jest.clearAllMocks();
    global.fetch = jest.fn();
    global.URL.createObjectURL = jest.fn();
    global.URL.revokeObjectURL = jest.fn();
  });

  it('should call getStatementsXls with the correct params', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        headers: {
          get: (header: string) => {
            if (header === 'content-disposition') {
              return 'attachment; filename="file.xls"';
            }
            return null;
          },
        },
        blob: () => Promise.resolve(new Blob(['mock data'], { type: 'application/vnd.ms-excel' })),
      } as Response)
    );
    jest.mock('../formatters', () => ({
      ...jest.requireActual('../formatters'),
      resolveAndDownloadBlob: jest.fn(),
    }));

    await getStatementsXls(
      mockToken,
      mockTetheredUserGuid,
      mockSchemeCustomerId,
      mockfileAutoID,
      mockStatementDate,
      mockInvoiceNo,
      'GB'
    );

    expect(global.fetch).toHaveBeenCalledWith(
      `${process.env.NEXT_PUBLIC_REST_API}/v2/piba/account/transactions/download/${mockSchemeCustomerId}/${mockTetheredUserGuid}/${mockInvoiceNo}?scheme=GB`,
      {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${mockToken}`,
          'WB-Authorization': `Bearer ${mockToken}`,
          accept: 'application/vnd.ms-excel',
        },
        cache: 'no-cache',
      }
    );
  });

  it('should return null if any param is missing', async () => {
    const result = await getStatementsXls(
      '',
      mockTetheredUserGuid,
      mockSchemeCustomerId,
      mockfileAutoID,
      mockStatementDate,
      mockInvoiceNo,
      'GB'
    );

    expect(result).toBeNull();
  });
});

describe('getPIBACardDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should call getPIBACardDetails', async () => {
    const response = {
      data: {
        getPIBACardDetails: { cardNumber: '123' },
      },
    };
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );
    const result = await getPIBACardDetails('', '123', '123', 'GB');

    expect(result).toEqual({ cardNumber: '123' });
  });
});

describe('getFormattedAddress', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return formatted address when valid addressId is provided', async () => {
    const mockAddressId = 'test-address-123';
    const mockFormattedAddress = {
      line1: '123 Test Street',
      line2: 'Test Area',
      city: 'Test City',
      postcode: 'TE5 T12',
    };

    const response = {
      data: {
        formattedAddress: mockFormattedAddress,
      },
    };

    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(response),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getFormattedAddress(mockAddressId);
    expect(result).toEqual(mockFormattedAddress);
  });
});

describe('getWorldlineReturnUrl', () => {
  const originalEnv = process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL;

  beforeEach(() => {
    process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL = 'https://test.premierinn.com';
  });

  afterEach(() => {
    process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL = originalEnv;
  });

  it('should return correct URL for EN locale', () => {
    const result = getWorldlineReturnUrl(LOCALES.EN, 'manage/cards');
    expect(result).toContain('https://test.premierinn.com');
    expect(result).toContain('en-gb');
    expect(result).toContain('manage/cards');
  });

  it('should return correct URL for DE locale', () => {
    const result = getWorldlineReturnUrl(LOCALES.DE, 'manage/cards');
    expect(result).toContain('https://test.premierinn.com');
    expect(result).toContain('de-de');
    expect(result).toContain('manage/cards');
  });
});

describe('getAccountTransactions', () => {
  const mockPayload = {
    pagingRequest: {
      page: 1,
      maximumDisplayRows: 10,
    },
    scheme: 'GB',
    schemeCustomerId: 123,
    searchCriteria: {
      dateSearch: {
        dateFrom: '2024-01-01',
        dateTo: '2024-12-31',
        transactionTypes: 'all',
      },
    },
    tetheredUserGuid: 'test-guid',
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should fetch account transactions successfully', async () => {
    const mockTransactions = {
      data: {
        transactions: [{ id: 1, amount: 100 }],
      },
    };

    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(mockTransactions),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getAccountTransactions('test-token', mockPayload);
    expect(result).toBeDefined();
  });

  it('should throw error when token is missing', async () => {
    await expect(getAccountTransactions('', mockPayload)).rejects.toThrow(
      'Authentication token is required'
    );
  });
});

describe('setGuestFormData', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call POST API and return true when response is ok', async () => {
    global.fetch = jest.fn().mockResolvedValue({ ok: true } as Response);

    const result = await setGuestFormData('AJK-abc12345-1a2b-3c4d-5e6f-123456789abc', {
      firstName: 'Jane',
      email: 'jane@test.com',
    });

    expect(global.fetch).toHaveBeenCalledWith('/api/guest-details/set-guest-form-data', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        basketReferenceId: 'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
        formData: {
          firstName: 'Jane',
          email: 'jane@test.com',
        },
      }),
    });
    expect(result).toBe(true);
  });

  it('should return false when response is not ok', async () => {
    global.fetch = jest.fn().mockResolvedValue({ ok: false } as Response);

    const result = await setGuestFormData('AJK-abc12345-1a2b-3c4d-5e6f-123456789abc', {
      firstName: 'Jane',
    });

    expect(result).toBe(false);
  });

  it('should return false when fetch throws', async () => {
    global.fetch = jest.fn().mockRejectedValue(new Error('Network error'));

    const result = await setGuestFormData('AJK-abc12345-1a2b-3c4d-5e6f-123456789abc', {
      firstName: 'Jane',
    });

    expect(result).toBe(false);
  });
});

describe('clearGuestFormData', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call PUT API and return true when response is ok', async () => {
    global.fetch = jest.fn().mockResolvedValue({ ok: true } as Response);

    const result = await clearGuestFormData('AJK-abc12345-1a2b-3c4d-5e6f-123456789abc');

    expect(global.fetch).toHaveBeenCalledWith('/api/guest-details/remove-guest-form-data', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        basketReferenceId: 'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
      }),
    });
    expect(result).toBe(true);
  });

  it('should return false when response is not ok', async () => {
    global.fetch = jest.fn().mockResolvedValue({ ok: false } as Response);

    const result = await clearGuestFormData('AJK-abc12345-1a2b-3c4d-5e6f-123456789abc');

    expect(result).toBe(false);
  });

  it('should return false when fetch throws', async () => {
    global.fetch = jest.fn().mockRejectedValue(new Error('Network error'));

    const result = await clearGuestFormData('AJK-abc12345-1a2b-3c4d-5e6f-123456789abc');

    expect(result).toBe(false);
  });
});

describe('showCardManagementForBusinessPayManager', () => {
  it('should return false when user is not a business pay manager', () => {
    const result = showCardManagementForBusinessPayManager(
      false, // isBusinessPayManager
      false, // isDeSite
      false, // isTethered
      false, // isPibaEuroActive
      true, // isAccountHolder
      true // isCardHolder
    );
    expect(result).toBe(false);
  });

  it('should return false for German site users who are not tethered and do not have PIBA Euro active', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      true, // isDeSite
      false, // isTethered
      false, // isPibaEuroActive
      true, // isAccountHolder
      true // isCardHolder
    );
    expect(result).toBe(false);
  });

  it('should return true for German site users who are tethered', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      true, // isDeSite
      true, // isTethered
      false, // isPibaEuroActive
      true, // isAccountHolder
      false // isCardHolder
    );
    expect(result).toBe(true);
  });

  it('should return true for German site users who have PIBA Euro active', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      true, // isDeSite
      false, // isTethered
      true, // isPibaEuroActive
      true, // isAccountHolder
      false // isCardHolder
    );
    expect(result).toBe(true);
  });

  it('should return true for German site users who are both tethered and have PIBA Euro active', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      true, // isDeSite
      true, // isTethered
      true, // isPibaEuroActive
      true, // isAccountHolder
      false // isCardHolder
    );
    expect(result).toBe(true);
  });

  it('should return false for tethered users who are neither account holder nor card holder', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      false, // isDeSite
      true, // isTethered
      false, // isPibaEuroActive
      false, // isAccountHolder
      false // isCardHolder
    );
    expect(result).toBe(false);
  });

  it('should return true for tethered users who are account holders', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      false, // isDeSite
      true, // isTethered
      false, // isPibaEuroActive
      true, // isAccountHolder
      false // isCardHolder
    );
    expect(result).toBe(true);
  });

  it('should return true for tethered users who are card holders', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      false, // isDeSite
      true, // isTethered
      false, // isPibaEuroActive
      false, // isAccountHolder
      true // isCardHolder
    );
    expect(result).toBe(true);
  });

  it('should return true for tethered users who are both account holder and card holder', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      false, // isDeSite
      true, // isTethered
      false, // isPibaEuroActive
      true, // isAccountHolder
      true // isCardHolder
    );
    expect(result).toBe(true);
  });

  it('should return true for non-German site business pay managers who are not tethered', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      false, // isDeSite
      false, // isTethered
      false, // isPibaEuroActive
      false, // isAccountHolder
      false // isCardHolder
    );
    expect(result).toBe(true);
  });

  it('should return true for business pay managers on non-German site regardless of other conditions', () => {
    const result = showCardManagementForBusinessPayManager(
      true, // isBusinessPayManager
      false, // isDeSite
      false, // isTethered
      true, // isPibaEuroActive
      true, // isAccountHolder
      true // isCardHolder
    );
    expect(result).toBe(true);
  });
});
