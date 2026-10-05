/* These are the complete application specific global constants. This is added as an experimental first step towards refactoring our existing codebase and have uniform SoT for `final` values. This needs to be taken care of by everyone from the next time/when you refactor or add features
 */

export const GLOBALS = {
  locale: {
    GB: 'gb',
    DE: 'de',
  },
  localeUpper: {
    GB: 'GB',
    DE: 'DE',
  },
  language: {
    EN: 'en',
    DE: 'de',
  },
  addressType: {
    HOME: 'HOME',
    BUSINESS: 'BUSINESS',
  },
  billingAddressType: {
    DIFFERENT: 'DifferentAddress',
    CURRENT: 'CurrentAddress',
  },
  country: {
    DE: 'DE',
    D: 'D', //legacy country code for DE
    GB: 'GB',
  },
};

export const SCROLL_AMOUNTS = {
  monthTab: 146,
};

/**
 * Safety ceiling for room counts derived from the `ROOMS` URL query param (SRE-350).
 * Use the real `maxRooms` instead wherever it is already in scope.
 */
export const MAX_ROOMS_SEARCH_LIMIT = 20;

export const PROD_ENVS = ['hulk', 'wanda'];
export const WB_SESSION_ID = 'WB-SESSION-ID';
export const ID_TOKEN_COOKIE = 'id_token_cookie';
export const DEFAULT_TRACING_COOKIE_NAME = 'WB-SESSION-ID';
export const MANUAL_ADDRESS = 'manualAddress';
export const REGISTER_BASKET_REF_COOKIE = 'register_basket_ref';
export const CONSENT_COOKIE = 'consent_cookie';
export const BASKET_IDS_COOKIE = 'basketIds';
export const BASKET_COOKIE_EXPIRY_HOURS = 1; // 1 hour

export const PAGE_UNAVAILABLE_URL_EN = 'page-unavailable.html';
export const PAGE_UNAVAILABLE_URL_DE = 'seite-nicht-verfuegbar.html';

export const TWO_MONTH_SEARCH = 'display_two_month_search';
export const BUNDLE_CHOICE = 'targetVariant';
export const BUNDLE_CHOICE_OPTIONS = {
  class: 'roomClass',
  rate: 'rate',
  noRoomTypeSearch: 'noRoomTypeSearch',
  roomOnly: 'roomOnly',
};
export const EXTRAS_ROOM_TAB_CHANGED = 'extras_room_tab_changed';
export const MONTHS = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December',
];

export const BOOKING_REFERENCE_ID = 'booking_reference_id';

export const PIB_MANUAL_LOGOUT_FLAG = 'pib_manual_logout';

export const ONE_YEAR_IN_DAYS = 365; // 1 year
