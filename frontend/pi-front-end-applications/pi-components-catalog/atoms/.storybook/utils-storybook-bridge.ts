const isAbsoluteOrDataUrl = (path: string): boolean => /^(?:data:|https?:\/\/|\/\/)/.test(path);

const isStorybookLocalAsset = (path: string): boolean => path.startsWith('/');

export const formatIBAssetsUrl = (path = '/'): string => {
  if (!path) {
    return path;
  }

  if (isAbsoluteOrDataUrl(path) || isStorybookLocalAsset(path)) {
    return path;
  }

  const checkedPath = path.includes('/') ? path : '/';
  const assetsHost =
    typeof process !== 'undefined' && process.env ? process.env.NEXT_PUBLIC_ASSETS_URL : undefined;

  return assetsHost ? `https://${assetsHost}${checkedPath}` : checkedPath;
};

export const cn = (...classes: Array<string | undefined | null | false>): string =>
  classes.filter(Boolean).join(' ');

export const GLOBALS = { PARTNER_CODE: 'DEFAULT' };
export const ID_TOKEN_COOKIE = 'id_token';
export const REGISTER_BASKET_REF_COOKIE = 'basket_ref';
export const TWO_MONTH_SEARCH = 60;

export type PromoActionsType = Record<string, unknown>;
export type PromotionsInformation = Record<string, unknown>;
export const PromoActionsType: PromoActionsType = {};
export const PromotionsInformation: PromotionsInformation = {};

export const analytics = { trackEvent: () => {}, trackPageView: () => {} };
export const restaurantFormAnalytics = new Proxy(
  {},
  {
    get: () => () => {},
  }
) as Record<string, (...args: unknown[]) => void>;
export const clientLogger = { info: () => {}, warn: () => {}, error: () => {} };
export const timeTrackerSeconds = (_name: string) => () => {};

export const setPageAnalytics = (..._args: unknown[]): void => {};
export const sanitize = (value: string): string => value;
export const encodeToBase64 = (str: string): string => {
  if (typeof window !== 'undefined' && typeof window.btoa === 'function') {
    return window.btoa(str);
  }
  return str;
};
export const hashString = (str: string): string => {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i);
    hash |= 0;
  }
  return String(hash);
};

export const logicalAndOperator = (...values: unknown[]): boolean => values.every(Boolean);
export const ternaryCondition = (
  condition: boolean,
  trueValue: string,
  falseValue: string
): string => (condition ? trueValue : falseValue);

export const formatAssetsUrl = (path = '/'): string => path;
export const formatDataTestId = (id: string): string => id;
export const formatCurrency = (amount: number, currency = 'GBP'): string =>
  `${currency} ${(amount / 100).toFixed(2)}`;
export const formatDate = (date: Date | string): string =>
  typeof date === 'string' ? date : date.toLocaleDateString();
export const formatPrice = (price: number): string => `£${(price / 100).toFixed(2)}`;
export const formatRatePrice = (price: number): string => formatPrice(price);
export const formatNextLocaleLink = (link: string): string => link;
export const renderSanitizedHtml = (html: string): { __html: string } => ({ __html: html });
export const akamaiImageLoader = ({ src, width }: { src: string; width?: number }): string =>
  width ? `${src}?w=${width}` : src;

export const getCookie = (name: string): string | undefined => {
  if (typeof document === 'undefined') return undefined;
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(';').shift();
  return undefined;
};
export const setCookie = (
  name: string,
  value: string,
  options?: { maxAge?: number; path?: string; domain?: string }
): void => {
  if (typeof document === 'undefined') return;
  let cookieStr = `${name}=${value}`;
  if (options?.maxAge) cookieStr += `; max-age=${options.maxAge}`;
  if (options?.path) cookieStr += `; path=${options.path}`;
  if (options?.domain) cookieStr += `; domain=${options.domain}`;
  document.cookie = cookieStr;
};
export const getAuthCookie = (): string | undefined => getCookie(ID_TOKEN_COOKIE);
export const getLocalStorageMock = (): Storage =>
  typeof window !== 'undefined' ? window.localStorage : ({} as Storage);
export const isIVMEnabled = (): boolean => false;
export const isSameDate = (date1: Date | string, date2: Date | string): boolean => {
  const d1 = typeof date1 === 'string' ? new Date(date1) : date1;
  const d2 = typeof date2 === 'string' ? new Date(date2) : date2;
  return d1.toDateString() === d2.toDateString();
};
export const getSavedCardType = (..._args: unknown[]): string => '';
export const getBookingConfirmationMessage = (
  status: string,
  confirmationNumber?: string
): string => `Booking ${status}${confirmationNumber ? ` - Ref: ${confirmationNumber}` : ''}`;
export const getCurrentReservationStorageData = (): Record<string, unknown> => ({});
export const getClientDefaultSessionTracing = () => ({ sessionId: '', traceId: '' });
export const getSortedCountriesByCurrentLang = (): Array<{ code: string; name: string }> => [];
export const extrasNamingCheck = (_t: unknown, id: string): string => id || 'Extra';
export const getExtrasPackagePrice = (
  _id: string,
  _language?: string,
  _extras?: unknown[]
): string => '0.00';
export const fetchFormattedDateValues = (..._args: unknown[]): Record<string, string> => ({});
export const findError = (..._args: unknown[]): undefined => undefined;

export const useAppData = () => ({ userData: null, isLoading: false, isError: false });
export const useAuth0Navigation = () => ({ loginUrl: '/', logoutUrl: '/' });
export const useCustomLocale = () => ({ currentLocale: 'en', LOCALE_LIST: ['en'] });
export const useElementDimensions = (_selector?: string) => ({ width: 0, height: 0 });
export const useFeatureSwitch = (_name: string) => ({ enabled: false });
export const useFeatureToggle = (_toggleName: string): boolean => false;
export const useGetCountryLanguage = () => ({ country: 'GB', language: 'en' });
export const useLocalStorage = (_key: string, initialValue?: unknown) => [initialValue, () => {}];
export const useQueryRequestRestaurants = () => ({
  data: null,
  isLoading: false,
  isError: false,
  refetch: async () => null,
});
export const useScreenSize = () => ({ isMobile: false, isTablet: false, isDesktop: true });
export const useTranslation = () => ({
  t: (key: string) => key,
  i18n: { language: 'en' },
  ready: true,
});

export default {
  GLOBALS,
  ID_TOKEN_COOKIE,
  REGISTER_BASKET_REF_COOKIE,
  TWO_MONTH_SEARCH,
  PromoActionsType,
  PromotionsInformation,
  analytics,
  restaurantFormAnalytics,
  clientLogger,
  timeTrackerSeconds,
  setPageAnalytics,
  sanitize,
  encodeToBase64,
  hashString,
  logicalAndOperator,
  ternaryCondition,
  formatAssetsUrl,
  formatDataTestId,
  formatCurrency,
  formatDate,
  formatPrice,
  formatRatePrice,
  formatNextLocaleLink,
  renderSanitizedHtml,
  akamaiImageLoader,
  getCookie,
  setCookie,
  getAuthCookie,
  getLocalStorageMock,
  isIVMEnabled,
  isSameDate,
  getSavedCardType,
  getBookingConfirmationMessage,
  getCurrentReservationStorageData,
  getClientDefaultSessionTracing,
  getSortedCountriesByCurrentLang,
  extrasNamingCheck,
  getExtrasPackagePrice,
  fetchFormattedDateValues,
  findError,
  useAppData,
  useAuth0Navigation,
  useCustomLocale,
  useElementDimensions,
  useFeatureSwitch,
  useFeatureToggle,
  useGetCountryLanguage,
  useLocalStorage,
  useQueryRequestRestaurants,
  useScreenSize,
  useTranslation,
  cn,
  formatIBAssetsUrl,
};
