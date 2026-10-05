import {
  DEFAULT_TRACING_COOKIE_NAME,
  executeGraphQLQuery,
  executeContentGraphQLQuery,
  getCardManagementLabels,
  getFooterLabels,
  getInnBusinessHeaderLabels,
  ID_TOKEN_COOKIE,
  parseRegistrationQuestions,
  resolveAndDownloadBlob,
  getUserManagementLabels,
  getSpendingLabels,
  getInnBusinessLayoutLabels,
  getPathForLocale,
  getNotificationsLabels,
} from '..';
import {
  CountryInformation,
  GET_COUNTRIES,
  getEmployeesQuery,
  getEmployeeByIdQuery,
  getRegistrationQuestionsQuery,
  Language,
  GET_ACCOUNT_LIST,
  GET_ADDRESSES,
  LOCALES,
  ibNavDefaultType,
  CountryLanguage,
  CountryCode,
  InnBusinessServerSideProps,
  GET_HOTEL_INFORMATION,
  HOTEL_AVAILABILITY_QUERY,
  BookingChannelCriteria,
  SearchRoomType,
  STATIC_HOTEL_INFORMATION_QUERY,
  ShortCountry,
  CountryDetails,
  GET_FORMATTED_ADDRESS,
  FormattedAddress,
  CustomerAccountDetails,
  RegistrationRole,
  BUSINESS_BOOKER_USER_ROLES,
  getInnBusinessCommonIcons as getInnBusinessCommonIconsQuery,
  getCompanyRegistrationQuestionsQuery,
  GET_CONTACT_PREFERENCES,
  BRANDCODES,
  getProfileDetailsQuery,
  getCompanyDetailsQuery,
  CompanyDetailsResponse,
  managementInformationReportQuery,
  emergencyReportQuery,
  RolesList,
  getAllPibaCards as getAllPibaCardsQuery,
  getPibaCardDetailsQuery,
  getCompanySpendingQuery,
  getYourSpendingQuery,
  EmployeeSpendCriteria,
  getUpcomingBookingsQuery,
  UpcomingBookingsRequest,
  getUpcomingSpendingQuery,
  getPaymentCards as getPaymentCardsQuery,
  Scheme,
  getPayApplicationsQuery,
  getNotificationsQuery,
  companyDetailsLookupQuery,
  WORLDLINE_USER_PREFERENCES_QUERY,
  WorldlinePreference,
  WorldlineAccountInfo,
  WorldlineMergedPreference,
  WorldlineGraphQLResponse,
  getAppLookupDataQueryEN,
  getAppLookupDataQueryDE,
  GET_SEARCH_RULES_QUERY,
  getPayApplicationDetailsQuery,
  getActivationDetailsQuery,
  getRegistrationInfoQuery,
  GET_ANONYMOUS_NEWSLETTER_PREFERENCES,
  getAccountSpendingQuery,
  viewAccountTransactionsQuery,
  getIBActivationDetailsQuery,
  getEmployeesWithFilteringOptionsQuery,
  appPreCheckQuery,
  GET_COOKIE_CONSENT_INFO,
  InnBusinessUserPilotData,
  Query,
  getCostCenterDetailsQuery,
} from '@whitbread-eos/api';
import { IncomingHttpHeaders } from 'http';

import { roomDefaultValueIfError } from '../../helpers/roomHelpers';
import { logger } from '../../logger/logger';
import decodeIdToken from '../../utils/decodeIdToken';
import {
  DynamicObject,
  getUnleashTogglesServerOrClient,
  DynamicContext,
} from '../../utils/unleash';
import { getDetailsFromToken } from '../edge';
import { GraphQLRequestLogContext } from '../gql';
import { cachePromise } from '../helpers';

// Dynamic import helper for next/headers to avoid bundling in client components
const getNextCookies = async () => {
  if (typeof window !== 'undefined') return null;
  try {
    const { cookies } = await import('next/headers');
    return cookies;
  } catch {
    return null;
  }
};

export const getCostCenterDetails = async (
  token: string,
  tetheredUserGuid: string,
  logContext?: GraphQLRequestLogContext
) => {
  return await executeGraphQLQuery(
    getCostCenterDetailsQuery(),
    {
      tetheredUserGuid,
    },
    (result: any) => {
      if (!result?.errors && !!result?.data?.getCostCentreDetails) {
        return result?.data?.getCostCentreDetails;
      }
      return [];
    },
    token,
    false,
    true,
    undefined,
    logContext
  );
};

export const getCountriesList = async (language: Language) => {
  return await executeContentGraphQLQuery(
    GET_COUNTRIES,
    {
      language,
      country: language === 'en' ? 'gb' : 'de',
      site: 'business-booker',
    },
    (result: Record<string, Record<string, CountryInformation>>) =>
      result?.data?.countries?.countries
  );
};

export const getUserDetails = async (
  token: string,
  revalidateCache = false,
  headers: Record<string, string> = {}
): Promise<any> => {
  const { email } = decodeIdToken(token);
  if (!email) {
    return null;
  }

  return getProfileDetails(email, token, revalidateCache, headers);
};

export const getAccountRegistrationRoleDetails = (account: CustomerAccountDetails | null) => {
  const registrationRoles = account?.registrationRoles;
  const isValidArray = Array.isArray(registrationRoles);
  const rolesLength = isValidArray ? registrationRoles.length : 0;
  const oneRole = rolesLength === 1;
  const twoRoles = rolesLength === 2;
  const hasOneRole = isValidArray && oneRole;
  const hasTwoRoles = isValidArray && twoRoles;

  return {
    isOnlyCardHolder: hasOneRole && registrationRoles.includes(RegistrationRole.CardHolder),
    isOnlyFinanceUser: hasOneRole && registrationRoles.includes(RegistrationRole.FinanceUser),
    isOnlyCostCenter: hasOneRole && registrationRoles.includes(RegistrationRole.CostCentreUser),
    isCardHolderAndFinanceUser:
      hasTwoRoles &&
      registrationRoles.includes(RegistrationRole.CardHolder) &&
      registrationRoles.includes(RegistrationRole.FinanceUser),
    isCardHolderAndCostCenterUser:
      hasTwoRoles &&
      registrationRoles.includes(RegistrationRole.CardHolder) &&
      registrationRoles.includes(RegistrationRole.CostCentreUser),
  };
};

export const getAccessLevel = async (accountHolderNumber?: string) => {
  const cookiesFn = await getNextCookies();
  const cookieStore = cookiesFn ? await cookiesFn() : null;
  const token = cookieStore?.get(ID_TOKEN_COOKIE)?.value ?? '';
  const [userDetails = null, accounts = []] = await Promise.all([
    getUserDetails(token),
    getAccountList(token),
  ]);
  const accessLevel = userDetails?.business?.accessLevel;

  const selectedAccountHolder = accountHolderNumber
    ? getSelectedAccountHolder(accounts, accountHolderNumber)
    : accounts?.[0];

  return {
    accessLevel,
    isTravelManager: accessLevel === BUSINESS_BOOKER_USER_ROLES.SUPER,
    isBooker: accessLevel === BUSINESS_BOOKER_USER_ROLES.BOOKER,
    isSelfBooker: accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF,
    isGuest: accessLevel === BUSINESS_BOOKER_USER_ROLES.STAYER,
    isBusinessPayManager: accessLevel === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER,
    isBusinessPayUser: accessLevel === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_USER,
    isTethered: !!userDetails?.business?.tethered && accounts.length > 0,
    hasAccountHolder: hasAccountHolder(accounts),
    hasCardHolder: hasCardHolder(accounts),
    hasCostCenterHolder: hasCostCenterHolder(accounts),
    selectedAccount: {
      isCardHolder: !!selectedAccountHolder?.registrationRoles?.includes(
        RegistrationRole.CardHolder
      ),
      isAccountHolder: !!selectedAccountHolder?.registrationRoles?.includes(
        RegistrationRole.AccountHolder
      ),
      isCostCenterHolder: !!selectedAccountHolder?.registrationRoles?.includes(
        RegistrationRole.CostCentreUser
      ),
    },
  };
};

export const hasCostCenterHolder = (accountList?: CustomerAccountDetails[]) => {
  return !!(
    accountList &&
    accountList.some((x: CustomerAccountDetails) =>
      x.registrationRoles?.includes(RegistrationRole.CostCentreUser)
    )
  );
};

export const hasAccountHolder = (accountList?: CustomerAccountDetails[]) => {
  return !!(
    accountList &&
    accountList.some((x: CustomerAccountDetails) =>
      x.registrationRoles?.includes(RegistrationRole.AccountHolder)
    )
  );
};

export const getAllPayRoles = (accountList?: CustomerAccountDetails[]) => {
  if (!accountList) return [];
  return Array.from(new Set(accountList.flatMap((account) => account.registrationRoles ?? [])));
};

export const hasCardHolder = (accountList?: CustomerAccountDetails[]) => {
  return !!(
    accountList &&
    accountList.some((x: CustomerAccountDetails) =>
      x.registrationRoles?.includes(RegistrationRole.CardHolder)
    )
  );
};

export const getUserRolesForAccount = async (
  account: CustomerAccountDetails | null
): Promise<RolesList> => {
  try {
    const cookiesFn = await getNextCookies();
    const cookieStore = cookiesFn ? await cookiesFn() : null;
    const token = cookieStore?.get(ID_TOKEN_COOKIE)?.value ?? '';
    const userDetails = await getUserDetails(token);
    const accessLevel = userDetails?.business?.accessLevel;

    return {
      wl: account?.registrationRoles ?? [],
      bb: accessLevel ? [accessLevel] : [],
    };
  } catch {
    return {
      wl: [],
      bb: [],
    };
  }
};

export const getCompanyDetails = async (
  companyId: string,
  token: string,
  revalidateCache = true,
  logContext?: GraphQLRequestLogContext
): Promise<CompanyDetailsResponse> => {
  return await executeGraphQLQuery(
    getCompanyDetailsQuery(),
    {
      companyId,
    },
    (result: any) => result?.data?.companyDetailsV3,
    token,
    revalidateCache,
    false,
    undefined,
    logContext
  );
};

export const getLocationResults = async (value: string, includeHotels = false) => {
  let url = `${process.env.NEXT_PUBLIC_SNOWDROP_BASE_URL}v1/autocomplete?input=/${value}&gplaces[components]=country:uk|country:de`;

  if (includeHotels) {
    url += '&hotels.limit=100';
  }

  const response = await fetch(url);
  return response.json();
};

export const getRegistrationQuestionsWithAnswers = async (employeeId: string) => {
  const cookiesFn = await getNextCookies();
  const cookieStore = cookiesFn ? await cookiesFn() : null;
  const token = cookieStore?.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId } = getDetailsFromToken(token);

  return await executeGraphQLQuery(
    getRegistrationQuestionsQuery(),
    {
      companyId,
      employeeId,
    },
    (result: any) =>
      parseRegistrationQuestions(result?.data?.getEmployeeRegistrationQuestionsAndAnswers),
    token,
    true
  );
};

export const getCookieConsentInfo = async (country: string, language: string, brand: string) => {
  return await executeGraphQLQuery(
    GET_COOKIE_CONSENT_INFO,
    {
      country,
      language,
      brand,
    },
    (result: any) => result?.data
  );
};

export const getEmployees = async (
  companyId: string,
  size: number,
  token: string,
  page?: number,
  searchCriteria?: string,
  pageToken?: string,
  bookingChannel?: string,
  awaitingApproval?: boolean,
  employeeId?: string,
  logContext?: GraphQLRequestLogContext
) => {
  return await executeGraphQLQuery(
    getEmployeesQuery(),
    {
      companyId,
      size,
      page,
      searchCriteria,
      pageToken,
      bookingChannel,
      awaitingApproval,
      employeeId,
    },
    (result: any) => result?.data?.getEmployeesV2,
    token,
    true,
    false,
    undefined,
    logContext
  );
};

export const getEmployeeDetails = async (
  companyId: string,
  employeeId?: string,
  token?: string,
  logContext?: GraphQLRequestLogContext
) => {
  return await executeGraphQLQuery(
    getEmployeeByIdQuery(),
    {
      companyId,
      employeeId,
    },
    (result: any) => result?.data?.getEmployeeDetailsV3,
    token,
    true,
    false,
    undefined,
    logContext
  );
};

export const getProfileDetails = async (
  customerId: string,
  token?: string,
  revalidateCache = true,
  headers: Record<string, string> = {}
) => {
  return await executeGraphQLQuery(
    getProfileDetailsQuery(),
    {
      customerId,
      business: true,
      innBusiness: true,
    },
    (result: any) => result?.data?.getProfileDetailsV3,
    token,
    revalidateCache,
    false,
    headers
  );
};

export const getCommonIcons = async (language: Language, logContext?: GraphQLRequestLogContext) => {
  const data = await executeContentGraphQLQuery(
    getInnBusinessCommonIconsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.commonIconsEndpoint,
    logContext
  );
  return data ? JSON.parse(data) : null;
};

export const getPostCodeAddresses = async (postCode: string, token: string) => {
  return await executeGraphQLQuery(
    GET_ADDRESSES,
    {
      searchTerm: postCode,
    },
    (result: any) => result?.data?.partialAddress,
    token
  );
};

export const getAccountList = async (
  token: string,
  headers: Record<string, string> = {},
  logContext?: GraphQLRequestLogContext
) => {
  if (!token || (typeof token === 'string' && !token.trim())) {
    return [];
  }

  const accounts = await executeGraphQLQuery(
    GET_ACCOUNT_LIST,
    {
      viewAll: false,
    },
    (result: any) => result?.data?.getAccountList?.accounts,
    token,
    true,
    false,
    headers,
    logContext
  );

  return accounts?.filter((account: CustomerAccountDetails) => account.accountNumber);
};

export const getServerUnleashToggles = async (
  label: string,
  fallbackFlags: DynamicObject,
  url: string,
  query: URLSearchParams,
  context: DynamicContext = {}
) => {
  const cookiesFn = await getNextCookies();
  const cookieStore = cookiesFn ? await cookiesFn() : null;
  if (!cookieStore) {
    return fallbackFlags;
  }
  const tracingCookie = cookieStore.get(DEFAULT_TRACING_COOKIE_NAME);
  return getUnleashTogglesServerOrClient(
    cookieStore,
    label,
    fallbackFlags,
    url,
    query,
    {
      [DEFAULT_TRACING_COOKIE_NAME]: tracingCookie?.name ?? '',
    },
    context // 7th parameter, e.g. for country
  );
};

export function getActivePath(currentPath: string, navObj: ibNavDefaultType) {
  return Object.values(navObj)?.find((value) => currentPath?.includes(value?.path))?.label ?? '';
}

export const getLocaleByPathname = (pathname: string | null): LOCALES => {
  if (!pathname) return LOCALES.EN;
  const segments = pathname.split('/');

  const locale = segments[1].toLowerCase();

  const mappedLocale: { [key: string]: LOCALES } = {
    en: LOCALES.EN,
    gb: LOCALES.EN,
    de: LOCALES.DE,
  };

  return mappedLocale[locale] || (locale as LOCALES);
};

export const getCountryLanguageByLocale = (locale: string): CountryLanguage => {
  if (locale === CountryCode.DE) {
    return {
      country: CountryCode.DE,
      language: CountryCode.DE,
    };
  }

  return {
    country: locale === LOCALES.DE ? CountryCode.DE : CountryCode.GB,
    language: locale === LOCALES.DE ? CountryCode.DE : CountryCode.EN,
  };
};

export const getInnBusinessServerSideProps = async (
  token: string,
  language: Language,
  needsFooter = false,
  businessBookerRequestHeaders?: IncomingHttpHeaders
): Promise<InnBusinessServerSideProps | null> => {
  try {
    const headers = businessBookerRequestHeaders
      ? getBusinessBookerIpHeaders(businessBookerRequestHeaders)
      : undefined;

    const footerPromise = needsFooter ? getFooterLabels(language) : Promise.resolve(undefined);
    const { companyId } = getDetailsFromToken(token);

    const [
      userDetails,
      labels,
      icons,
      cards,
      accounts,
      users,
      spending,
      searchRules,
      layout,
      companyDetails,
      notifications,
      footer,
    ] = await Promise.all([
      getUserDetails(token, true, headers),
      getInnBusinessHeaderLabels(language),
      getCommonIcons(language),
      getCardManagementLabels(language),
      getAccountList(token, headers),
      getUserManagementLabels(language),
      getSpendingLabels(language),
      getSearchRules(token),
      getInnBusinessLayoutLabels(language),
      getCompanyDetails(companyId, token),
      getNotificationsLabels(language),
      footerPromise,
    ]);

    return {
      labels,
      icons,
      cards,
      users,
      accounts,
      spending,
      userDetails,
      companyDetails,
      footer: footer ?? null,
      language,
      isAccountHolder: hasAccountHolder(accounts),
      isCardHolder: hasCardHolder(accounts),
      searchRules,
      layout,
      notifications,
    };
  } catch {
    return null;
  }
};

export const getHotelInformationIB = async (hotelId: string, language: Language) => {
  return await executeGraphQLQuery(
    GET_HOTEL_INFORMATION,
    {
      hotelId,
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.hotelInformation
  );
};

export const getHotelAvailabilitiesIB = async (
  hotelId: string,
  arrival: string,
  departure: string,
  brand: string,
  country: string,
  rooms: Record<string, SearchRoomType>,
  language: string,
  bookingChannel: BookingChannelCriteria,
  channel: string,
  token: string
) => {
  return await executeGraphQLQuery(
    HOTEL_AVAILABILITY_QUERY,
    {
      hotelId,
      arrival,
      departure,
      brand,
      country,
      rooms,
      language,
      bookingChannel,
      channel,
    },
    (result: any) => result?.data,
    token
  );
};

export const staticHotelInformationIB = cachePromise(async (slug: string, language: Language) => {
  return await executeGraphQLQuery(
    STATIC_HOTEL_INFORMATION_QUERY,
    {
      slug,
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.hotelInformationBySlug
  );
});

// Re-export from roomHelpers to maintain backwards compatibility
export { roomDefaultValueIfError };

export const getDaysInMonth = (month: number, year: number) => {
  return new Date(year, month, 0).getDate();
};

export const getCountryName = (countryCode: string, countries: CountryDetails[]) => {
  if (countryCode === 'D') {
    countryCode = ShortCountry.DE;
  }
  const country = countries.find((country: CountryDetails) => country.countryCode === countryCode);
  return country?.countryName;
};

export const getFormattedAddress = async (addressId: string) => {
  return await executeGraphQLQuery(
    GET_FORMATTED_ADDRESS,
    {
      identifier: encodeURIComponent(addressId),
    },
    (result: Record<string, Record<string, FormattedAddress>>) => {
      return result?.data?.formattedAddress;
    }
  );
};

export const getEmployeesCSV = async (token: string, companyId: string): Promise<any> => {
  if (!companyId) {
    return null;
  }

  try {
    await fetch(`${process.env.NEXT_PUBLIC_REST_API}/companies/${companyId}/employees/bulk`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
      cache: 'no-cache',
    })
      .then((data) => data.blob())
      .then((response) => resolveAndDownloadBlob(response, 'Employees', '.csv'));
  } catch (error) {
    return null;
  }
};

export const getCompanyRegistrationQuestionsAndAnswers = async (
  companyId: string,
  headers: Record<string, string>,
  logContext?: GraphQLRequestLogContext
) => {
  const cookiesFn = await getNextCookies();
  const cookieStore = cookiesFn ? await cookiesFn() : null;
  const token = cookieStore?.get(ID_TOKEN_COOKIE)?.value ?? '';

  try {
    const res = await executeGraphQLQuery(
      getCompanyRegistrationQuestionsQuery(),
      {
        companyId,
      },
      (result: any) => {
        return parseRegistrationQuestions(result?.data?.getCompanyRegistrationQuestionsAndAnswers);
      },
      token,
      true,
      true,
      headers,
      logContext
    );

    return res;
  } catch (error) {
    throw new Error('Error fetching company registration questions and answers');
  }
};

type ContactPreferencePermission = {
  brandCode: string;
  brand: string;
  optIn: boolean;
  secondOptInReq: boolean | null;
  secondOptIn: boolean | null;
  secondPartyOptIn: boolean;
  thirdPartyVendorsOptIn: boolean;
  suppressMarketingCheckbox: boolean;
};

type ContactPreferencesResponse = {
  permissions: ContactPreferencePermission[];
  valid: boolean;
  deleted: boolean;
  contactChannelId: string | null;
  contactChannelValue: string;
  loyaltyAccounts: unknown[];
};

type GraphQLError = {
  message: string;
  extensions?: {
    code?: string;
    response?: {
      details?: unknown;
    };
    details?: unknown;
  };
  path?: string[];
  locations?: { line: number; column: number }[];
};

type GraphQLResponse = {
  data?: {
    getContactPreferences?: ContactPreferencesResponse;
  };
  errors?: GraphQLError[];
};

export type GqlResponse = {
  data?: Query;
};

const DEFAULT_CONTACT_PREFERENCE = (email: string): ContactPreferencesResponse => ({
  permissions: [
    {
      brandCode: BRANDCODES[0],
      brand: 'Premier Inn',
      optIn: false,
      secondOptInReq: null,
      secondOptIn: null,
      secondPartyOptIn: false,
      thirdPartyVendorsOptIn: false,
      suppressMarketingCheckbox: false,
    },
  ],
  loyaltyAccounts: [],
  valid: true,
  deleted: false,
  contactChannelId: null,
  contactChannelValue: email,
});

export const getContactPreferences = async (
  email: string,
  token: string,
  countryOfResidenceOrLogContext?: string | GraphQLRequestLogContext,
  language?: string,
  logContext?: GraphQLRequestLogContext
): Promise<ContactPreferencesResponse> => {
  if (!token) {
    throw new Error('Authentication token is required');
  }

  if (!email) {
    throw new Error('Email is required');
  }

  const countryOfResidence =
    typeof countryOfResidenceOrLogContext === 'string' ? countryOfResidenceOrLogContext : undefined;
  const resolvedLogContext =
    typeof countryOfResidenceOrLogContext === 'object'
      ? countryOfResidenceOrLogContext
      : logContext;

  const variables = {
    request: {
      contactType: 'email' as const,
      contactValue: email,
      brandCodes: BRANDCODES[0],
      business: true,
      contactChannelId: null,
      countryOfResidence,
      language,
    },
  };

  try {
    const result = await executeGraphQLQuery(
      GET_CONTACT_PREFERENCES,
      variables,
      (result: GraphQLResponse) => {
        if (!result?.errors) {
          return result?.data?.getContactPreferences;
        }

        const hasAppSyncError = result.errors.some(
          (error: GraphQLError) => error.message === 'appsync.global.error'
        );

        if (hasAppSyncError) {
          return DEFAULT_CONTACT_PREFERENCE(email);
        }

        return null;
      },
      token,
      true,
      true,
      undefined,
      resolvedLogContext
    );

    return result ?? DEFAULT_CONTACT_PREFERENCE(email);
  } catch (error) {
    return DEFAULT_CONTACT_PREFERENCE(email);
  }
};

export const getSelectedAccountHolder = (
  accounts: CustomerAccountDetails[],
  accountNumber: string | undefined
) => {
  let currentAccount = accounts[0];
  if (accountNumber) {
    const account = accounts.find((account) => String(account.tetheredGuid) === accountNumber);
    if (account) {
      currentAccount = account;
    }
  }
  return currentAccount;
};

export const getManagementInformationReport = async (
  token: string,
  fromDate: string,
  toDate: string,
  showQnAcolumns: boolean,
  language: string
) => {
  if (!token) {
    throw new Error('Authentication token is required');
  }
  const revalidateCache = false;
  const returnErrors = true;
  const response = await executeGraphQLQuery(
    managementInformationReportQuery(),
    {
      fromDate,
      toDate,
      showQnAcolumns,
      language,
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );
  return response;
};

export const getEmergencyReport = async (token: string, language: string) => {
  const revalidateCache = false;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }
  const response = await executeGraphQLQuery(
    emergencyReportQuery(),
    {
      language,
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );
  return response;
};

export const getPIBACardDetails = async (
  token: string,
  cardId: string,
  tetheredUserId: string,
  countryCode: string,
  logContext?: GraphQLRequestLogContext
) => {
  return await executeGraphQLQuery(
    getPibaCardDetailsQuery(),
    {
      tetheredUserId,
      countryCode,
      cardId,
    },
    (result: any) => result?.data?.getPIBACardDetails,
    token,
    true,
    false,
    undefined,
    logContext
  );
};

export const getAllPibaCards = async (
  token: string,
  scheme: string,
  tetheredUserId: string,
  includeCancelledCards: boolean,
  showMyCards: boolean,
  pageIndex: number,
  pageSize: number
) => {
  return await executeGraphQLQuery(
    getAllPibaCardsQuery(),
    {
      tetheredUserId,
      countryCode: scheme,
      pibaCardsCriteria: {
        userId: tetheredUserId,
        includeCancelledCards,
        showMyCards,
        pageNumber: pageIndex,
        maxRows: pageSize,
      },
    },
    (result: any) => result?.data?.getAllPIBACards,
    token,
    true
  );
};

export const getCompanySpending = async (
  token: string,
  fromMonthYear: string,
  toMonthYear: string
) => {
  const revalidateCache = true;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }
  const response = await executeGraphQLQuery(
    getCompanySpendingQuery(),
    {
      fromMonthYear,
      toMonthYear,
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );
  return response;
};

export const getYourSpending = async (
  token: string,
  employeeSpendCriteria: EmployeeSpendCriteria
) => {
  const revalidateCache = true;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }

  const response = await executeGraphQLQuery(
    getYourSpendingQuery(),
    {
      employeeSpendCriteria,
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );

  return response;
};

export const getUpcomingBookings = async (token: string, params: UpcomingBookingsRequest) => {
  const revalidateCache = true;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }
  const response = await executeGraphQLQuery(
    getUpcomingBookingsQuery(),
    params,
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );
  return response;
};

export const getUpcomingSpending = async (
  token: string,
  accountId: string,
  tetheredUserGuid?: string
) => {
  const revalidateCache = true;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }
  const response = await executeGraphQLQuery(
    getUpcomingSpendingQuery(),
    {
      accountId,
      tetheredUserGuid,
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );
  return response;
};

export const getPaymentCards = async (token: string, companyId: string) => {
  return await executeGraphQLQuery(
    getPaymentCardsQuery(),
    {
      companyId,
    },
    (result: any) => result?.data?.getPaymentCards,
    token,
    true
  );
};

export const companyDetailsLookup = async (
  token: string,
  companyRegistrationNumber: string,
  scheme: Scheme
) => {
  return await executeGraphQLQuery(
    companyDetailsLookupQuery(),
    {
      companyRegistrationNumber,
      scheme,
    },
    (result: any) => result?.data?.companyDetailsLookup,
    token,
    true
  );
};

export const getPayApplications = async (token: string) => {
  const revalidateCache = true;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }
  const response = await executeGraphQLQuery(
    getPayApplicationsQuery(),
    {},
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );
  return response;
};

export const getNotifications = async (token: string, scheme: Scheme, tetheredUserId: string) => {
  const revalidateCache = true;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }
  const response = await executeGraphQLQuery(
    getNotificationsQuery(),
    {
      scheme,
      tetheredUserId,
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );
  return response;
};

const formatAccountNumber = (accountNumber: string | null) => {
  if (!accountNumber) return '';
  return accountNumber.replace(/(\d{4})/g, '$1 ').trim();
};

export const getWorldlineUserPreferences = async (
  token: string,
  logContext?: GraphQLRequestLogContext
): Promise<WorldlineMergedPreference[] | null> => {
  if (!token) return null;

  try {
    const accounts = await getAccountList(token, {}, logContext);
    if (!accounts?.length) return null;

    const validAccounts = accounts.filter(
      (acc: Record<string, any>): acc is WorldlineAccountInfo =>
        !!acc?.tetheredGuid &&
        !!acc?.accountName &&
        !!acc?.accountNumber &&
        Array.isArray(acc?.registrationRoles)
    );

    if (!validAccounts.length) return null;

    const preferencesData = (await executeGraphQLQuery(
      WORLDLINE_USER_PREFERENCES_QUERY,
      { tetheredUserGuids: validAccounts.map((acc: WorldlineAccountInfo) => acc.tetheredGuid) },
      (result: unknown) => (result as WorldlineGraphQLResponse)?.data?.getWorldlineUserPreferences,
      token,
      true,
      false,
      undefined,
      logContext
    )) as WorldlinePreference[] | null;

    if (!preferencesData?.length) return null;

    const accountMap = new Map<string, WorldlineAccountInfo>(
      validAccounts.map((acc: WorldlineAccountInfo) => [acc.tetheredGuid, acc])
    );

    return preferencesData
      .map((pref: WorldlinePreference) => {
        const account = accountMap.get(pref.tetheredUserGuid);
        if (!account) return null;

        const result: WorldlineMergedPreference = {
          tetheredUserGuid: pref.tetheredUserGuid,
          settings: pref.settings,
          preferenceDetails: pref.details,
          accountName: account.accountName,
          registrationRoles: account.registrationRoles,
          accountNumber: formatAccountNumber(account.accountNumber),
        };

        return result;
      })
      .filter((item): item is WorldlineMergedPreference => item !== null);
  } catch {
    return null;
  }
};

export const getAppLookupData = async (scheme: Scheme, token: string) => {
  return await executeGraphQLQuery(
    scheme === 'GB' ? getAppLookupDataQueryEN : getAppLookupDataQueryDE,
    {
      scheme,
    },
    (result: any) => result?.data?.getAppLookupData,
    token
  );
};

export const getSearchRules = async (token: string) => {
  return await executeGraphQLQuery(
    GET_SEARCH_RULES_QUERY,
    {
      channel: 'BB',
    },
    (result: any) => result?.data,
    token
  );
};

export const getPayApplicationDetails = async (
  token: string,
  applicationGuid: string,
  applicationId: string,
  scheme: Scheme,
  logContext?: GraphQLRequestLogContext
) => {
  return await executeGraphQLQuery(
    getPayApplicationDetailsQuery(),
    {
      applicationGuid,
      applicationId,
      scheme,
    },
    (result: any) => {
      if (result?.errors?.length) {
        return { errCode: JSON.parse(result?.errors[0]?.message)?.errCode };
      }
      return result?.data.getApplicationDetails;
    },
    token,
    true,
    true,
    undefined,
    logContext
  );
};

export const getActivationDetails = async (
  activationKey: string,
  logContext?: GraphQLRequestLogContext
) => {
  return await executeGraphQLQuery(
    getActivationDetailsQuery(),
    {
      activationKey,
    },
    (result: any) => result?.data?.getActivationDetails,
    undefined,
    false,
    false,
    undefined,
    logContext
  );
};

export const getRegistrationInfo = async (
  token: string,
  registrationCode: string,
  logContext?: GraphQLRequestLogContext
) => {
  return await executeGraphQLQuery(
    getRegistrationInfoQuery(),
    {
      registrationCode,
    },
    (result: any) => result?.data?.getRegistrationInfo,
    token,
    true,
    false,
    undefined,
    logContext
  );
};

export const MANAGE_TABS = {
  INN_BUSINESS: 'innbusiness',
  INN_BUSINESS_PAY: 'innbusiness-pay',
};

export function getAvailableTabs(
  isTravelManager: boolean,
  isTethered: boolean,
  hasAccountHolder: boolean,
  hasCardHolder = false,
  forCardManagement = false,
  isDeLanguage = false,
  isPibaEuroEnabled = false,
  hasCostCenterHolder = false,
  isBusinessPayManager = false
) {
  if (
    !isTravelManager &&
    !hasAccountHolder &&
    !hasCardHolder &&
    !hasCostCenterHolder &&
    !isBusinessPayManager
  ) {
    return [];
  }
  const hasOnlyFinanceUser = isTethered && !hasAccountHolder && !hasCardHolder;
  const isBPMNonCardManagement = isBusinessPayManager && !forCardManagement;

  if (
    (isTravelManager || isBPMNonCardManagement) &&
    isDeLanguage &&
    !isTethered &&
    !isPibaEuroEnabled
  ) {
    return [MANAGE_TABS.INN_BUSINESS];
  }

  const tabs = new Set<string>();
  if (isTravelManager || isBPMNonCardManagement) {
    tabs.add(MANAGE_TABS.INN_BUSINESS);
    if (!hasOnlyFinanceUser) {
      tabs.add(MANAGE_TABS.INN_BUSINESS_PAY);
    }
  }

  if (forCardManagement) {
    if (
      showCardManagementForBusinessPayManager(
        isBusinessPayManager,
        isDeLanguage,
        isTethered,
        isPibaEuroEnabled,
        hasAccountHolder,
        hasCardHolder
      )
    ) {
      tabs.add(MANAGE_TABS.INN_BUSINESS_PAY);
    }
    if (isTethered && (hasCardHolder || hasAccountHolder || hasCostCenterHolder)) {
      tabs.add(MANAGE_TABS.INN_BUSINESS_PAY);
    }
  } else {
    if (isTethered && hasAccountHolder) {
      tabs.add(MANAGE_TABS.INN_BUSINESS_PAY);
    }
  }

  return Array.from(tabs).slice(0, 2);
}

export const getWorldlineReturnUrl = (locale: LOCALES, path: string) => {
  return `${process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL}${getPathForLocale(locale, path)}`;
};

export const getMarketingPreferences = async (
  brandCode: string,
  email: string,
  countryOfResidence?: string,
  language?: string
) => {
  return await executeGraphQLQuery(
    GET_ANONYMOUS_NEWSLETTER_PREFERENCES,
    {
      brandCode,
      email,
      countryOfResidence,
      language,
    },
    (result: any) => result?.data?.anonymousNewsletterPreferences
  );
};

export const getSpendOverTimeCSV = async (
  token: string,
  accountNumber: string,
  fromMonthYear: string,
  toMonthYear: string,
  scheme: Scheme,
  language: string
): Promise<any> => {
  if (!token || !accountNumber || !fromMonthYear || !toMonthYear || !scheme || !language) {
    return null;
  }

  const response = await fetch(
    `${process.env.NEXT_PUBLIC_REST_API}/v1/spending/accountSpending?pibaAccountId=${accountNumber}&fromMonthYear=${fromMonthYear}&toMonthYear=${toMonthYear}&scheme=${scheme}&language=${language}`,
    {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
        'WB-Authorization': `Bearer ${token}`,
        accept: 'text/csv',
      },
      cache: 'no-cache',
    }
  );

  if (!response.ok) {
    throw new Error(`Failed to fetch CSV: ${response.statusText}`);
  }

  const contentDisposition = response.headers.get('content-disposition');
  const filename = contentDisposition?.split('filename=')[1] || 'Inn Business Pay Spend Over Time';

  const blob = await response.blob();
  resolveAndDownloadBlob(blob, filename, '');
};

export const getAccountSpending = async (
  token: string,
  fromMonthYear: string,
  toMonthYear: string,
  pibaAccountId: string,
  tetheredUserGuid?: string
) => {
  const revalidateCache = true;
  const returnErrors = true;
  if (!token || !fromMonthYear || !toMonthYear || !pibaAccountId) {
    return null;
  }

  const response = await executeGraphQLQuery(
    getAccountSpendingQuery(),
    {
      accountSpendingCriteria: {
        fromMonthYear,
        toMonthYear,
        pibaAccountId,
        tetheredUserGuid,
      },
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );

  return response;
};

export const getAccountTransactions = async (
  token: string,
  payload: {
    pagingRequest: {
      page: number;
      maximumDisplayRows: number;
    };
    scheme: string;
    schemeCustomerId: number;
    searchCriteria: {
      dateSearch: {
        dateFrom: string;
        dateTo: string;
        transactionTypes: string;
      };
    };
    tetheredUserGuid: string;
  }
) => {
  const revalidateCache = true;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }

  const response = await executeGraphQLQuery(
    viewAccountTransactionsQuery(),
    {
      payload,
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );

  return response;
};

export const getOutOfPolicyReport = async (
  token: string,
  companyId: string,
  fromDate: string,
  toDate: string
): Promise<any> => {
  if (!token || !companyId || !fromDate || !toDate) {
    return null;
  }

  try {
    await fetch(
      `${process.env.NEXT_PUBLIC_REST_API}/companies/admin/${companyId}/reports/out-of-office?fromDate=${fromDate}&toDate=${toDate}`,
      {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        cache: 'no-cache',
      }
    )
      .then((data) => data.blob())
      .then((response) => resolveAndDownloadBlob(response, 'OutOfPolicyReport', '.csv'));
  } catch (error) {
    return null;
  }
};

export const getIBActivationDetails = async (
  activationKey: string,
  logContext?: GraphQLRequestLogContext
) => {
  try {
    return await executeGraphQLQuery(
      getIBActivationDetailsQuery(),
      {
        activationKey,
      },
      (result: any) => result?.data?.getInnBusinessActivationDetailsV2,
      '',
      true,
      true,
      undefined,
      logContext
    );
  } catch (error) {
    return null;
  }
};

export const getEmployeesWithFilteringOptions = async (
  companyId: string,
  size: number,
  token: string,
  page?: number,
  searchCriteria?: string,
  pageToken?: string,
  bookingChannel?: string,
  awaitingApproval?: boolean,
  shouldFilterEmployees?: boolean,
  logContext?: GraphQLRequestLogContext
) => {
  return await executeGraphQLQuery(
    getEmployeesWithFilteringOptionsQuery(),
    {
      companyId,
      size,
      page,
      searchCriteria,
      pageToken,
      bookingChannel,
      awaitingApproval,
      shouldFilterEmployees,
    },
    (result: any) => result?.data?.getEmployeesWithFilteringOptionsV2,
    token,
    true,
    false,
    undefined,
    logContext
  );
};

export const getBusinessBookerIpHeaders = (headers: IncomingHttpHeaders) => {
  return {
    'x-forwarded-for': String(headers['x-forwarded-for'] ?? ''),
    'True-Client-IP': String(headers['True-Client-IP'] ?? ''),
  };
};
export const appPreCheck = async (token: string, scheme: Scheme) => {
  const revalidateCache = false;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }
  const response = await executeGraphQLQuery(
    appPreCheckQuery(),
    {
      scheme,
    },
    (result: any) => result,
    token,
    revalidateCache,
    returnErrors
  );
  return response?.data?.appPreCheck ?? null;
};

export const getEmployeeDataforUserPilot = async (
  token: string
): Promise<InnBusinessUserPilotData | null> => {
  if (!token) {
    return null;
  }
  try {
    const { companyId, employeeId, accessLevel } = getDetailsFromToken(token);

    const [accounts, companyDetails] = await Promise.all([
      getAccountList(token),
      getCompanyDetails(companyId, token, false),
    ]);

    const userPilotData = {
      role: accessLevel ?? '',
      innBusinessPayRoles: getAllPayRoles(accounts),
      employeeId: employeeId ?? '',
      companyId: companyId ?? '',
      companySector: companyDetails?.requestedCompany?.companyDetails?.companySector ?? '',
      numberOfEmployees: companyDetails?.requestedCompany?.companyDetails?.numberOfEmployees ?? 0,
    };
    return userPilotData;
  } catch {
    return null;
  }
};

export const getStatementsPdf = async (
  token: string,
  tetheredUserGuid: string,
  schemeCustomerId: number,
  fileAutoID: number,
  statementDate: string,
  invoiceNo: string,
  scheme: string
): Promise<any> => {
  if (
    !token ||
    !tetheredUserGuid ||
    !schemeCustomerId ||
    !fileAutoID ||
    !statementDate ||
    !invoiceNo ||
    !scheme
  ) {
    return null;
  }

  const url = `${process.env.NEXT_PUBLIC_REST_API}/v2/piba/account/invoices/download/${schemeCustomerId}/${tetheredUserGuid}/${fileAutoID}?scheme=${scheme}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      'WB-Authorization': `Bearer ${token}`,
      accept: 'application/pdf',
    },
    cache: 'no-cache',
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch PDF`);
  }

  const filename = `Invoice_${invoiceNo}_${statementDate}`;

  const blob = await response.blob();
  resolveAndDownloadBlob(blob, filename, '.pdf');
};

export const getStatementsXls = async (
  token: string,
  tetheredUserGuid: string,
  schemeCustomerId: number,
  fileAutoID: number,
  statementDate: string,
  invoiceNo: string,
  scheme: string
): Promise<any> => {
  if (
    !token ||
    !tetheredUserGuid ||
    !schemeCustomerId ||
    !fileAutoID ||
    !statementDate ||
    !invoiceNo ||
    !scheme
  ) {
    return null;
  }

  const url = `${process.env.NEXT_PUBLIC_REST_API}/v2/piba/account/transactions/download/${schemeCustomerId}/${tetheredUserGuid}/${invoiceNo}?scheme=${scheme}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      'WB-Authorization': `Bearer ${token}`,
      accept: 'application/vnd.ms-excel',
    },
    cache: 'no-cache',
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch XLS`);
  }

  const filename = `Invoice_${invoiceNo}_${statementDate}`;

  const blob = await response.blob();
  resolveAndDownloadBlob(blob, filename, '.xls');
};

export const showCardManagementForBusinessPayManager = (
  isBusinessPayManager: boolean,
  isDeSite: boolean,
  isTethered: boolean,
  isPibaEuroActive: boolean,
  isAccountHolder: boolean,
  isCardHolder: boolean
) => {
  if (!isBusinessPayManager) {
    return false;
  }

  // German site users need PIBA Euro active or must be tethered
  if (isDeSite && !isTethered && !isPibaEuroActive) {
    return false;
  }

  // Tethered users must be either account holder or card holder (not finance users)
  if (isTethered && !isAccountHolder && !isCardHolder) {
    return false;
  }

  return true;
};

export const setGuestFormData = async (
  basketReferenceId: string,
  formData: Record<string, unknown>
): Promise<boolean> => {
  try {
    const response = await fetch('/api/guest-details/set-guest-form-data', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ basketReferenceId, formData }),
    });
    return response.ok;
  } catch (error) {
    logger.error({ error }, 'SAVE_GUEST_FORM_DATA_FAILED');
    return false;
  }
};

export const clearGuestFormData = async (basketReferenceId: string): Promise<boolean> => {
  try {
    const response = await fetch('/api/guest-details/remove-guest-form-data', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ basketReferenceId }),
    });
    return response.ok;
  } catch (error) {
    logger.error({ error }, 'CLEAR_GUEST_FORM_DATA_FAILED');
    return false;
  }
};

export * from './queries';
export { getSpendingSummaryV2 } from './queries/spending/getSpendingSummaryV2';
