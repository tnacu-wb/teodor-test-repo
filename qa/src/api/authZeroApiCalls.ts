import type { APIRequestContext } from '@playwright/test';
import { asObject, getBrowserOptions, isApiRequestContext } from './apiValueUtils';
import { ApiContentCalls } from './graphql/apiContentCalls';
import { SearchBookings } from './response/searchBookings';
import { Constants } from '../test-data/constants';
import { EncryptionUtils } from '../utils/encryptionUtils';

interface Auth0TokenResponse {
  access_token: string;
  token_type?: string;
  expires_in?: number;
  scope?: string;
  [key: string]: unknown;
}

interface SearchBookingsFilters {
  arrivalDate?: string;
  bookerEmail?: string;
  bookerLastName?: string;
  bookerPhone?: string;
  hotelId?: string;
  bookerPostcode?: string;
  bookingReference?: string;
  cancellationDate?: string;
  companyName?: string;
  guestLastName?: string;
  limit?: number;
  offset?: number;
  thirdPartyBookingReferenceNumber?: string;
}

interface AccountServiceCustomer {
  [key: string]: unknown;
}

interface PayApplication {
  applicationId?: string;
  applicationGuid?: string;
  scheme?: string;
  status?: string;
  [key: string]: unknown;
}

interface PayApplicationStatusResponse {
  status: number;
  message: string;
  applicationId?: string;
  [key: string]: unknown;
}

/**
 * Methods for calling Auth0 Api Calls.
 * More info can be found at this confluence page
 * (https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/3644424223/Auth0+Environment+Strategy+for+PI+and+CCUI)
 */
export class AuthZeroApiCalls {
  [key: string]: unknown;

  private static oAuthToken: Auth0TokenResponse | null = null;
  private static ccuiOAuthToken: string | null = null;
  private static accountServicesBearerToken: string | null = null;

  private static readonly asObject = asObject;

  private static readonly isApiRequestContext = isApiRequestContext;

  private static withContext(args: unknown[]): { request: APIRequestContext; data: Record<string, unknown> } {
    const { request, values } = AuthZeroApiCalls.splitArgs(args);
    const data = AuthZeroApiCalls.asObject(values[0]);
    return { request, data };
  }

  private static splitArgs(args: unknown[]): { request: APIRequestContext; values: unknown[] } {
    const request = AuthZeroApiCalls.isApiRequestContext(args[0]) ? args[0] : undefined;
    const values = AuthZeroApiCalls.isApiRequestContext(args[0]) ? args.slice(1) : args;
    if (!request && !global.page) {
      throw new Error('global.page is not available. Initialize Playwright page before using AuthZeroApiCalls methods.');
    }
    return { request: request ?? global.page.context().request, values };
  }

  private static readonly getBrowserOptions = getBrowserOptions;

  private static toQueryParams(data: Record<string, unknown>): { [key: string]: string | number | boolean } {
    const params: { [key: string]: string | number | boolean } = {};
    for (const [key, value] of Object.entries(data)) {
      if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') {
        params[key] = value;
      }
    }
    return params;
  }

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): AuthZeroApiCalls {
    return new AuthZeroApiCalls(data);
  }

  /**
   * Generate the Auth0 token
   * More info here:
   * https://auth0.com/docs/get-started/authentication-and-authorization-flow/call-your-api-using-resource-owner-password-flow
  * @returns {Auth0TokenResponse} Auth0 access-token response
    */
  static async getAuthZeroToken(...args: unknown[]): Promise<Auth0TokenResponse> {
    if (AuthZeroApiCalls.oAuthToken) {
      return AuthZeroApiCalls.oAuthToken;
    }
    const { request } = AuthZeroApiCalls.withContext(args);
    const options = AuthZeroApiCalls.getBrowserOptions();
    const form = {
      username: String(options.usernameAuth0 ?? ''),
      password: String(options.passwordAuth0 ?? ''),
      grant_type: String(options.grant_type ?? 'password'),
      client_id: String(options.clientIdAuth0 ?? ''),
      client_secret: String(options.clientSecretAuth0 ?? ''),
      realm: String(options.auth0Realm ?? 'bart-users-beta-u'),
      audience: String(options.audience ?? ''),
      scope: 'openid',
    };
    const response = await request.post(String(options.auth0TokenUrl ?? ''), { form });
    if (!response.ok()) {
      throw new Error(`getAuthZeroToken failed with status ${response.status()}`);
    }
    const body = AuthZeroApiCalls.asObject(await response.json()) as Auth0TokenResponse;
    if (!body.access_token) {
      throw new Error('getAuthZeroToken returned no access_token.');
    }
    AuthZeroApiCalls.oAuthToken = body;
    return body;
  }

  /**
   * Generate ccui Auth0 bearer token
   * @returns {string} CCUI bearer token
   */
  static async getCcuiAuthZeroToken(...args: unknown[]): Promise<string> {
    if (AuthZeroApiCalls.ccuiOAuthToken) {
      return AuthZeroApiCalls.ccuiOAuthToken;
    }
    const { request } = AuthZeroApiCalls.withContext(args);
    const options = AuthZeroApiCalls.getBrowserOptions();
    const form = {
      grant_type: 'password',
      username: String(options.ccuiUsername ?? Constants.AUTOMATION_MANAGER_USERNAME_CCUI),
      password: EncryptionUtils.decode(String(options.ccuiPassword ?? Constants.AUTOMATION_MANAGER_PASSWORD_CCUI)),
      client_id: EncryptionUtils.decode(String(options.ccuiClientId ?? 'M1RSdDlQbHNib0RhSkNXU0t1N3hvdmFmT21tRGw3eDY=')),
      audience: String(options.ccuiAudience ?? 'https://api.dev.whitbread.digital'),
      scope: 'access_token',
    };
    const response = await request.post(
      String(options.ccuiAuth0TokenUrl ?? 'https://dev-whitbread-digital.eu.auth0.com/oauth/token'),
      { form },
    );
    if (!response.ok()) {
      throw new Error(`getCcuiAuthZeroToken failed with status ${response.status()}`);
    }
    const body = AuthZeroApiCalls.asObject(await response.json());
    const accessToken = String(body.access_token ?? '');
    if (!accessToken) {
      throw new Error('getCcuiAuthZeroToken returned no access_token.');
    }
    AuthZeroApiCalls.ccuiOAuthToken = accessToken;
    return accessToken;
  }

  /**
   * Generate oAuth2 bearerToken
   * @param {Object} options options
   * @param {boolean} options.isCdhUat2 if true it will generate the token for cdh-uat2, otherwise for cdh-uat
   * @returns {string} account-services bearer token
   */
  static async getAuthBearerTokenForAccountServices(...args: unknown[]): Promise<string> {
    if (AuthZeroApiCalls.accountServicesBearerToken) {
      return AuthZeroApiCalls.accountServicesBearerToken;
    }
    const { request, data } = AuthZeroApiCalls.withContext(args);
    const isCdhUat2 = Boolean(data.isCdhUat2 ?? false);
    const options = AuthZeroApiCalls.getBrowserOptions();
    const form = {
      grant_type: 'client_credentials',
      client_id: String(isCdhUat2 ? (options.clientIdOAuth2TokenCDH2 ?? '8d199ea7-fb0f-437d-9f5d-928d6ac66394') : (options.clientIdOAuth2TokenCDH1 ?? '2d2fccd2-f338-4fc2-82c6-ca31f18c8aca')),
      client_secret: String(isCdhUat2 ? (options.clientSecretOAuth2TokenCDH2 ?? '') : (options.clientSecretOAuth2TokenCDH1 ?? '')),
      scope: String(isCdhUat2 ? 'api://8d199ea7-fb0f-437d-9f5d-928d6ac66394/.default' : 'https://wbch-uat-api-v2.azurewebsites.net/.default'),
    };
    const response = await request.post('https://login.microsoftonline.com/fdfed904-9e03-4e17-89c4-61053e0777be/oauth2/v2.0/token', { form });
    if (!response.ok()) {
      throw new Error(`getAuthBearerTokenForAccountServices failed with status ${response.status()}`);
    }
    const body = AuthZeroApiCalls.asObject(await response.json());
    const accessToken = String(body.access_token ?? '');
    if (!accessToken) {
      throw new Error('getAuthBearerTokenForAccountServices returned no access_token.');
    }
    AuthZeroApiCalls.accountServicesBearerToken = accessToken;
    return accessToken;
  }

  /**
   * Get search bookings results
   * @param {Object} searchResultsFilters used to set the query variables
   * @param {String} searchResultsFilters.arrivalDate the arrival date in the format "23 Feb 2023"
   * @param {String} searchResultsFilters.bookerEmail booker email
   * @param {String} searchResultsFilters.bookerLastName booker last name
   * @param {String} searchResultsFilters.bookerPhone booker phone
   * @param {String} searchResultsFilters.hotelId hotel ID
   * @param {String} searchResultsFilters.bookerPostcode booker postcode
   * @param {String} searchResultsFilters.bookingReference booking reference
   * @param {String} searchResultsFilters.cancellationDate cancellation date
   * @param {String} searchResultsFilters.companyName company name
   * @param {String} searchResultsFilters.guestLastName guest last name
   * @param {Number} searchResultsFilters.limit number of bookings per page (10 by default)
   * @param {Number} searchResultsFilters.offset number of displayed bookings
   * @param {String} searchResultsFilters.thirdPartyBookingReferenceNumber third party booking reference number
   * @returns {SearchBookings} returns the response
   */
  static async getSearchBookingsResults(...args: unknown[]): Promise<SearchBookings> {
    const { request, data } = AuthZeroApiCalls.withContext(args);
    const options = AuthZeroApiCalls.getBrowserOptions();
    const token = AuthZeroApiCalls.asObject(await AuthZeroApiCalls.getAuthZeroToken(request));
    if (data.limit === undefined) {
      data.limit = 10;
    }
    if (data.offset === undefined) {
      data.offset = 0;
    }
    const url = `${String(options.entityApiBaseUrl ?? '')}/v1/reservations/search`;
    const response = await request.get(url, {
      headers: {
        Accept: '*/*',
        'WB-Authorization': `Bearer ${String(token.access_token ?? '')}`,
      },
      params: AuthZeroApiCalls.toQueryParams(data),
    });
    if (!response.ok()) {
      throw new Error(`getSearchBookingsResults failed with status ${response.status()} for URL ${url}`);
    }
    const body = AuthZeroApiCalls.asObject(await response.json());
    return SearchBookings.fromResponse({ searchBookingsResults: body });
  }

  /**
   * Get customer account and preferences from CDH
  * @param {String} emailAddress user/company email address
  * @returns {AccountServiceCustomer} customer account and preferences
   */
  static async getCustomersByEmailFromAccountServices(...args: unknown[]): Promise<AccountServiceCustomer> {
    const { request, data } = AuthZeroApiCalls.withContext(args);
    const { values } = AuthZeroApiCalls.splitArgs(args);
    const emailAddress = String(data.emailAddress ?? values[0] ?? '');
    const options = AuthZeroApiCalls.getBrowserOptions();
    const token = String(await AuthZeroApiCalls.getAuthBearerTokenForAccountServices(request));
    const url = `${String(options.cdhUatEndpoint ?? '')}/AccountServices/V1/customers?Email=${encodeURIComponent(emailAddress)}`;
    const response = await request.get(url, {
      headers: {
        Accept: '*/*',
        'Ocp-Apim-Subscription-Key': '3d77069db84b4181bae20c7297d989fd',
        Authorization: `Bearer ${token}`,
        AccessContext: 'test',
        AccessedBy: 'test@adbcl.com',
        'X-Azure-FDID': '1f941d44-3bc3-42f5-a72f-7beb4225fe74',
      },
    });
    if (!response.ok()) {
      throw new Error(`getCustomersByEmailFromAccountServices failed with status ${response.status()} for URL ${url}`);
    }
    const body = AuthZeroApiCalls.asObject(await response.json());
    const results = Array.isArray(body.Results) ? body.Results : [];
    if (results.length === 0) {
      throw new Error('No results returned from API');
    }
    return AuthZeroApiCalls.asObject(results[0]);
  }

  /**
   * Generate the Auth0 token for IBb user
   * @param {String} username email address of the IB user
   * @param {String} password user password
  * @returns {string} IB bearer token
   */
  static async getIBAuthZeroToken(...args: unknown[]): Promise<string> {
    const { request, data } = AuthZeroApiCalls.withContext(args);
    const { values } = AuthZeroApiCalls.splitArgs(args);
    const options = AuthZeroApiCalls.getBrowserOptions();
    const form = {
      username: String(data.username ?? values[0] ?? ''),
      password: String(data.password ?? values[1] ?? ''),
      grant_type: String(options.grant_type ?? 'password'),
      client_id: String(options.clientIdAuth0 ?? ''),
      client_secret: String(options.clientSecretAuth0 ?? ''),
      realm: String(options.ibRealm ?? 'bb-uat'),
      audience: String(options.ibAudience ?? 'https://api.dev.whitbread.digital'),
      scope: 'openid',
    };
    const response = await request.post(String(options.auth0TokenUrl ?? ''), { form });
    if (!response.ok()) {
      throw new Error(`getIBAuthZeroToken failed with status ${response.status()}`);
    }
    const body = AuthZeroApiCalls.asObject(await response.json());
    const accessToken = String(body.access_token ?? '');
    if (!accessToken) {
      throw new Error('getIBAuthZeroToken returned no access_token.');
    }
    return accessToken;
  }

  /**
   * Update Pay Application Status
  * @param {PayApplication} app - pay application object
   * @param {String} stage - Stage to set ("Completed" or "Cancelled")
  * @returns {PayApplicationStatusResponse} API response body
   */
  static async updatePayApplicationStatus(...args: unknown[]): Promise<PayApplicationStatusResponse> {
    const { request, data } = AuthZeroApiCalls.withContext(args);
    const { values } = AuthZeroApiCalls.splitArgs(args);
    const options = AuthZeroApiCalls.getBrowserOptions();
    const app = (data.app ? AuthZeroApiCalls.asObject(data.app) : AuthZeroApiCalls.asObject(values[0])) as PayApplication;
    const stage = String(data.stage ?? values[1] ?? '');
    const bearerToken = String(await AuthZeroApiCalls.getAuthBearerTokenForAccountServices(request));
    const payload = {
      applicationId: app.applicationId,
      stage,
    };
    const url = `${String(options.cdhUatEndpoint ?? '')}/IBPay/V1/Application/Status`;
    const response = await request.post(url, {
      headers: {
        Accept: '*/*',
        'Ocp-Apim-Subscription-Key': '17f68a47cee64729bf1c6157bc9f4ea2',
        Authorization: `Bearer ${bearerToken}`,
        AccessContext: 'InBusiness',
        AccessedBy: 'InBusiness@Whitbread.com',
        'Content-Type': 'application/json',
      },
      data: payload,
    });
    if (!response.ok()) {
      throw new Error(`updatePayApplicationStatus failed with status ${response.status()} for URL ${url}`);
    }
    const result = AuthZeroApiCalls.asObject(await response.json()) as PayApplicationStatusResponse;
    const resultStatus = Number(result.status ?? NaN);
    const resultMessage = String(result.message ?? '');
    if (response.status() !== 200 || resultStatus !== 200 || resultMessage !== 'Application status updated Successfully.') {
      throw new Error(`Unexpected response for applicationId ${String(app.applicationId ?? '')}: ${JSON.stringify(result)}`);
    }
    return result;
  }

  /**
   * Delete Pay Application
    * @returns {PayApplicationStatusResponse | null} last deletion status, if an eligible application was found
   */
    static async deletePayApplication(...args: unknown[]): Promise<PayApplicationStatusResponse | null> {
    const { request } = AuthZeroApiCalls.withContext(args);
    const applications = await ApiContentCalls.graphqlGetPayApplications();
    if (!Array.isArray(applications) || applications.length === 0) {
      return null;
    }
    const filteredApps = applications.filter((app) => {
      const appObj = AuthZeroApiCalls.asObject(app) as PayApplication;
      const status = String(appObj.status ?? '');
      return status === 'Outstanding' || status === 'Incomplete Application';
    });
    if (filteredApps.length === 0) {
      return null;
    }

    const appDetailsList = await Promise.all(filteredApps.map(async (app) => {
      const appObj = AuthZeroApiCalls.asObject(app);
      return ApiContentCalls.graphqlGetApplicationDetails({
        scheme: String(appObj.scheme ?? ''),
        applicationId: String(appObj.applicationId ?? ''),
        applicationGuid: String(appObj.applicationGuid ?? ''),
      });
    }));

    const cutoffDate = new Date(Date.now() - 60 * 60 * 1000);
    const toDelete: PayApplication[] = [];
    for (let i = 0; i < filteredApps.length; i++) {
      const details = AuthZeroApiCalls.asObject(appDetailsList[i]);
      const created = details.created;
      if (!created) {
        continue;
      }
      const createdDate = new Date(String(created));
      if (createdDate < cutoffDate) {
        toDelete.push(AuthZeroApiCalls.asObject(filteredApps[i]) as PayApplication);
      }
    }

    if (toDelete.length === 0) {
      return null;
    }

    let lastResult: PayApplicationStatusResponse | null = null;
    for (const appObj of toDelete) {
      const status = String(appObj.status ?? '');
      const stage = status === 'Outstanding' ? 'Completed' : 'Cancelled';
      lastResult = await AuthZeroApiCalls.updatePayApplicationStatus(request, { app: appObj, stage });
    }
    return lastResult;
  }

}
