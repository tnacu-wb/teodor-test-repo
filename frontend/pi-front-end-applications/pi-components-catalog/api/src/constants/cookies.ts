// cookies
export const MARKETING_OPT_IN = 'marketingOptIn';
export const BILLING_ADDRESS_CAPTURE = 'billing_address_capture';
export const BILLING_ADDRESS_CAPTURE_VARIANT = 'variant';
export const SEARCH_ROOM_PICKER = 'search_room_picker';
export const RC_PRICE_MODIFIER = 'rcPriceModifier';
export const RC_DISTANCE_MODIFIER = 'rcDistanceModifier';
export const RC_HUB_MODIFIER = 'rcHubModifier';
// export const ANCILLARIES_TABS = 'ancillaries_tabs';

// cookie expire time
export const ONE_MINUTE = 1; // 1 min

// For A/B Testing - Analytics Team
type CookieConfigBase = {
  mode: string;
  expiryInMinutes: number;
};

// eslint-disable-next-line @typescript-eslint/no-empty-interface
interface CommonCookieConfigs extends CookieConfigBase {}

interface CookieConfig extends CookieConfigBase {
  cookieName: string;
  configName: string;
}

const COMMON_COOKIE_CONFIGS: CommonCookieConfigs = {
  mode: 'variant',
  expiryInMinutes: 30,
};

export const ANCILLARIES_TABS: CookieConfig = {
  ...COMMON_COOKIE_CONFIGS,
  cookieName: 'ancillaries_tabs',
  configName: 'ancillaries',
};

export const GDP_ACCOMPANYING_GUEST_DETAILS: CookieConfig = {
  ...COMMON_COOKIE_CONFIGS,
  cookieName: 'guestDetailsAccompanyingGuest',
  configName: 'guestDetailsAccompanyingGuest',
};

export const GDP_DIGI_REG_ADDITIONAL_INFO: CookieConfig = {
  ...COMMON_COOKIE_CONFIGS,
  cookieName: 'guestDetailsAdditionalInfo',
  configName: 'guestDetailsAdditionalInfo',
};
