import type { APIRequestContext } from '@playwright/test';
import { expect } from '@playwright/test';
import { getEnvironmentConfig, type GetEnvironmentConfigOptions } from '../../../config/environments';
import { Constants } from '../../test-data/constants';
import { Locales } from '../../test-data/locales';
import { normalizeDlpContentPath } from '../dlpPath';
import { CookiesPolicies } from '../response/cookiesPolicies';
import { CountriesSuggestions } from '../response/countriesSuggestions';

type JsonRecord = Record<string, unknown>;

interface AemCookie {
  cookieName?: string;
  [key: string]: unknown;
}

interface CookiesPoliciesDictionary extends JsonRecord {
  cookiePolicies?: { cookieGroup?: AemCookie[] };
}

/** Methods for accessing English and German AEM dictionaries. */
export class ApiDictionary {
  [key: string]: unknown;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): ApiDictionary {
    return new ApiDictionary(data);
  }

  private static getRequestContext(request?: APIRequestContext): APIRequestContext {
    if (request) {
      return request;
    }

    if (!global.page) {
      throw new Error('global.page is not available. Initialize Playwright page before using ApiDictionary methods.');
    }

    return global.page.context().request;
  }

  private static getLocale() {
    const locale = global.browser?.options?.locale ?? 'gb-en';
    return Locales.getLocaleByString(locale);
  }

  private static getAemBaseUrlFromConfig(): string {
    const configured = global.browser?.options?.aemBaseUrl;
    if (configured) {
      return configured.replace(/\/$/, '');
    }

    return getEnvironmentConfig(global.browser?.options as GetEnvironmentConfigOptions | undefined).aemBaseUrl;
  }

  /**
   * Basic token
   * @param user username
   * @param password password
   * @returns token
   */
  static async getBasicToken(user: string, password: string): Promise<string> {
    const token = `${user}:${password}`;
    return `Basic ${Buffer.from(token).toString('base64')}`;
  }

  /**
   * Fetch AEM dictionary
   * @param relativeUrl relative path to a dictionary
  * @returns {JsonRecord} AEM dictionary
   */
  static async fetchDictionary(relativeUrl: string, request?: APIRequestContext): Promise<JsonRecord> {
    const requestContext = ApiDictionary.getRequestContext(request);
    const baseUrl = ApiDictionary.getAemBaseUrlFromConfig();
    const normalizedRelativeUrl = relativeUrl.startsWith('/') ? relativeUrl : `/${relativeUrl}`;
    const url = `${baseUrl}${normalizedRelativeUrl}`;

    const username = global.browser?.options?.aemAuthUsername;
    const password = global.browser?.options?.aemAuthPassword;
    const headers: Record<string, string> = { Accept: 'application/json' };

    if (username && password !== undefined) {
      headers.Authorization = await ApiDictionary.getBasicToken(username, password);
    }

    const response = await requestContext.get(url, { headers });
    if (!response.ok()) {
      throw new Error(`AEM dictionary request failed with status ${response.status()} for URL ${url}`);
    }

    const text = await response.text();
    return text ? JSON.parse(text) as JsonRecord : {};
  }

  /**
   * Fetch AEM label dictionary
   * @param language language
   * @returns Labels dictionary
   */
  static async fetchLabelsDictionary(language?: string): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const relativePath = `/etc/designs/global/dictionaries/labels/i18n.jsondict.${selectedLanguage}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch AEM hotel directory dictionary
  * @param {{ hotelId: string; language?: string; country?: string; type?: string }} data hotel directory query parameters
  * @returns {JsonRecord} hotel directory dictionary
   */
  static async fetchHotelDirectoryDictionary({ hotelId, language, country, type = 'complete' }: { hotelId: string; language?: string; country?: string; type?: string }): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const selectedCountry = country ?? locale.country;
    const relativePath = `/${selectedCountry}/${selectedLanguage}/hoteldirectory/${hotelId.charAt(0)}/${hotelId}.${type}.data`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch AEM room types dictionary
  * @param {{ language?: string; country?: string; brand?: string }} data room-type dictionary query parameters
  * @returns {JsonRecord} room-type dictionary
   */
  static async fetchRoomTypesDictionary({ language, country, brand = 'pi' }: { language?: string; country?: string; brand?: string } = {}): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const selectedCountry = country ?? locale.country;
    const relativePath = `/${selectedCountry}/${selectedLanguage}/content-service.room-types.detail/brand/${brand}.json`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch AEM rates dictionary
  * @param {{ language?: string; country?: string; brand?: string; hotelCode?: string | null }} data rates dictionary query parameters
  * @returns {JsonRecord} rates dictionary
   */
  static async fetchRatesDictionary({ language, country, brand = 'pi', hotelCode = null }: { language?: string; country?: string; brand?: string; hotelCode?: string | null } = {}): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const selectedCountry = country ?? locale.country;
    const project = global.browser?.options?.app;

    const relativePath = project === 'pib' && hotelCode
      ? `/${selectedCountry}/${selectedLanguage}/content-service.rates.detail/site/business-booker/brand/${brand}/hotelCode/${hotelCode}.json`
      : `/${selectedCountry}/${selectedLanguage}/content-service.rates.detail/site/leisure/brand/${brand}.json`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch AEM booking flow dictionary
  * @param {{ bookingFlow: string; language?: string; country?: string }} data booking-flow dictionary query parameters
  * @returns {JsonRecord} booking-flow dictionary
   */
  static async fetchBookingFlowDictionary({ bookingFlow, language, country }: { bookingFlow: string; language?: string; country?: string }): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const selectedCountry = country ?? locale.country;

    const relativePath = `/${selectedCountry}/${selectedLanguage}/content-service.booking-flow.detail/bookingId/${bookingFlow}.json`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * This dictionary contains generic information labels, not related to BART or Opera (ex: labels from home page)
   * @returns Generic information labels dictionary
   */
  static async fetchGenericDictionary(language?: string, country?: string): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const selectedCountry = country ?? locale.country;
    const project = global.browser?.options?.app;
    const relativePath = project === 'pib'
      ? `/${selectedCountry}/${selectedLanguage}/business-booker/index.header.data`
      : `/${selectedCountry}/${selectedLanguage}/index.header.data`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * This dictionary contains footer information labels, not related to BART or Opera (ex: labels from home page)
   * @returns Generic footer information labels dictionary
   */
  static async fetchGenericFooterDictionary(language?: string, country?: string, site: string = Constants.SITE_LEISURE): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const selectedCountry = country ?? locale.country;
    const relativePath = `/${selectedCountry}/${selectedLanguage}/index.footer.data/site/${site}.json`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * This dictionary contains generic labels used for Search Results page
   * @returns Search results labels dictionary
   */
  static async fetchSearchResultsDictionary(language?: string, country?: string): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const selectedCountry = country ?? locale.country;
    const relativePath = `/${selectedCountry}/${selectedLanguage}/search.searchresults.data`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch list of suggested countries from AEM
  * @returns {CountriesSuggestions} country suggestions response
   */
  static async fetchCountriesSuggestionsList({ countryProperty = '', languageProperty = '' }: { countryProperty?: string; languageProperty?: string } = {}): Promise<CountriesSuggestions> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = languageProperty || locale.language;
    const selectedCountry = countryProperty || locale.country;
    const relativePath = `/${selectedCountry}/${selectedLanguage}/content-service.countries.detail/site/leisure.json`;
    const response = await this.fetchDictionary(relativePath);

    return new CountriesSuggestions({ countriesSuggestionsApiResponse: response });
  }

  /**
   * Fetch list of labels for Dashboard page from AEM
   * @returns dashboard labels dictionary
   */
  static async fetchDashboardLabels(): Promise<JsonRecord> {
    const language = ApiDictionary.getLocale().language;
    const relativePath = `/etc/designs/global/dictionaries/pi-bookings/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch cookies policies from AEM
  * @param {{ brand?: string }} data cookie policies dictionary query parameters
  * @returns {CookiesPolicies} cookie policies response
   */
  static async fetchCookiesPolicies({ brand = 'pi' }: { brand?: string } = {}): Promise<CookiesPolicies> {
    const locale = ApiDictionary.getLocale();
    const relativePath = `/${locale.country}/${locale.language}/content-service.cookie-policies.detail/brand/${brand}.json`;
    const response = await this.fetchDictionary(relativePath);

    const cookiesPolicies = response as CookiesPoliciesDictionary;
    return new CookiesPolicies({ cookiesPolicies: cookiesPolicies.cookiePolicies });
  }

  /**
   * Helper method to find cookie by name from cookieGroup array
  * @param {AemCookie[]} cookieGroup array of AEM cookie objects
  * @param {string} cookieName cookie name to find
  * @returns {AemCookie} matching cookie object
   */
  static getCookieByName(cookieGroup: AemCookie[], cookieName: string): AemCookie {
    const matchedCookies = cookieGroup.filter((cookie) => cookie.cookieName === cookieName);
    if (matchedCookies.length === 0) {
      throw new Error(`Cookie not found in cookieGroup: ${cookieName}`);
    }
    return matchedCookies[0];
  }

  /**
   * This dictionary contains generic labels used for Booking
   * @returns Search results labels dictionary
   */
  static async fetchBookingDictionary(language?: string): Promise<JsonRecord> {
    const selectedLanguage = language ?? ApiDictionary.getLocale().language;
    const relativePath = `/etc/designs/global/dictionaries/booking/i18n.jsondict.${selectedLanguage}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * This dictionary contains generic labels used for allowances
   * @returns Search results labels dictionary
   */
  static async fetchAllowancesDictionary(language?: string): Promise<JsonRecord> {
    const selectedLanguage = language ?? ApiDictionary.getLocale().language;
    const relativePath = `/etc/designs/global/dictionaries/allowances/i18n.jsondict.${selectedLanguage}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business header dictionary from AEM.
   * @returns {JsonRecord} InnBusiness header dictionary
   */
  static async fetchInnBusinessHeaderDictionary(): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const relativePath = `/${locale.country}/${locale.language}/content-service.header.detail/site/business-booker.json`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business layout dictionary from AEM
   * @returns InnBusiness Layout labels dictionary
   */
  static async fetchInnBusinessLayoutDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/common-layout/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business card management dictionary from AEM
   * @returns InnBusiness Card Management labels dictionary
   */
  static async fetchInnBusinessCardManagementDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/card-management/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business company management dictionary from AEM
   * @returns InnBusiness Company Management labels dictionary
   */
  static async fetchInnBusinessCompanyManagementDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/company-management/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business user/employee management dictionary from AEM
   * @returns InnBusiness User Management labels dictionary
   */
  static async fetchInnBusinessUserManagementDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/user-management/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Ancillaries dictionary from AEM
   * @returns Ancillaries labels dictionary
   */
  static async fetchAncillariesDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/ancillaries/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Profile Management dictionary from AEM
   * @returns Profile Management labels dictionary
   */
  static async fetchInnBusinessProfileManagementDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/profile-management/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Home dictionary from AEM
   * @returns Home labels dictionary
   */
  static async fetchInnBusinessHomeDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/home/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business Spending and reporting dictionary from AEM
   * @returns InnBusiness Spending and reporting labels dictionary
   */
  static async fetchInnBusinessSpendingAndReportingDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/spending-reporting/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business Auth dictionary from AEM
   * @returns InnBusiness Auth labels dictionary
   */
  static async fetchInnBusinessAuthDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/auth/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Destination landing page (DLP) content service dictionary from AEM
  * @param {{ country?: string; language?: string; dlpPath: string }} data DLP dictionary query parameters
  * @returns {JsonRecord} DLP content service dictionary
   */
  static async fetchDlpContentServiceDictionary({ country, language, dlpPath }: { country?: string; language?: string; dlpPath: string }): Promise<JsonRecord> {
    const locale = ApiDictionary.getLocale();
    const selectedLanguage = language ?? locale.language;
    const selectedCountry = country ?? locale.country;
    const contentPath = normalizeDlpContentPath(dlpPath);

    const relativePath = `/${selectedCountry}/${selectedLanguage}/content-service.page-dlp.detail/path/${contentPath}.json`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business notifications dictionary from AEM
   * @returns InnBusiness notifications labels dictionary
   */
  static async fetchInnBusinessNotificationsDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/notifications/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business icon dictionary from AEM
   * @returns InnBusiness icon dictionary
   */
  static async fetchInnBusinessIconsDictionary(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/common-icons/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business Pay Application dictionary from AEM
   * @returns InnBusiness Pay application dictionary
   */
  static async fetchInnBusinessPayApplication(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/pay-application/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }

  /**
   * Fetch Inn business Contact us dictionary from AEM
   * @returns InnBusiness Contact us dictionary
   */
  static async fetchInnBusinessContactUs(language = ApiDictionary.getLocale().language): Promise<JsonRecord> {
    const relativePath = `/etc/designs/global/dictionaries/innbusiness/contact-us/i18n.jsondict.${language}`;
    return this.fetchDictionary(relativePath);
  }
}