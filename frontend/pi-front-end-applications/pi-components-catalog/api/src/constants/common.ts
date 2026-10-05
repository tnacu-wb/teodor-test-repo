import { ExtrasId } from '../enums';

export const CITYTAX_PACKAGE = 'CITYTAX';
export const CCUI_LOCALE_COOKIE = 'CCUI_LOCALE';

export const RESERVATION_STATUS = {
  CANCELLED: 'CANCELLED',
  UPCOMING: 'UPCOMING',
};

export const RESERVATION_SOURCE_PMS = {
  OPERA: 'OPERA',
  BART: 'BART',
};

export const PurposeOfStayAnalytics = {
  LEISURE: 'leisure',
  BUSINESS: 'business',
};
export const PI_FAVICON = '/content/dam/pi/websites/desktop/icons/favicons/favicon.ico';

export const MAX_NIGHTS_CCUI = 364;

export const AMEND_COOKIE_NAME = 'bb-amend';
export const AMEND_COOKIE_EXPIRY_MINUTES = 30;

export const BB_LIVE_ASSIST = '/etc/clientlibs/pi/resources/js/live-assist.js';
export const BB_CO_BROWSE = '/etc/clientlibs/pi/resources/js/co-browse.js';

export enum LOCALES {
  DE = 'de-de',
  EN = 'en-gb',
}
export const FREE_FOOD_OPTIONS = {
  DINNER: 'dinner',
  BREAKFAST: 'breakfast',
};
export const DLP_AKAMAI_HEADER_NAME = 'Akamai-Cache';

export const DEFAULT_REDIS_TTL = 30 * 60; // 30 minutes

export const CACHED_QUERIES_LIST = ['GetStaticContent', 'seoInformation'];

export const VALID_EXTRAS_IDS = [
  ExtrasId.EARLY_CHECK_IN,
  ExtrasId.EARLY_CHECK_IN_FREE,
  ExtrasId.LATE_CHECK_OUT,
  ExtrasId.LATE_CHECK_OUT_FREE,
  ExtrasId.ULTIMATE_WIFI,
  ExtrasId.ULTIMATE_WIFI_FREE,
  ExtrasId.BOTTLE_OF_PROSECCO,
] as const;

export const EARLY_CHECKIN_IDS = [ExtrasId.EARLY_CHECK_IN, ExtrasId.EARLY_CHECK_IN_FREE] as const;

export const LATE_CHECKOUT_IDS = [ExtrasId.LATE_CHECK_OUT, ExtrasId.LATE_CHECK_OUT_FREE] as const;

export const WIFI_IDS = [ExtrasId.ULTIMATE_WIFI, ExtrasId.ULTIMATE_WIFI_FREE] as const;

export const PROSECCO_IDS = [ExtrasId.BOTTLE_OF_PROSECCO] as const;
