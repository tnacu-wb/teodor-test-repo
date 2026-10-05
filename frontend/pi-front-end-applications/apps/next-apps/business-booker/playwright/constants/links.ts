import { config } from '@WB-playwright/config';

//region Fragments
const IB_LANGUAGE = config.LANGUAGE === 'en' ? 'en-gb' : 'de-de';
const LANGUAGE = config.LANGUAGE === 'en' ? 'gb/en' : 'de/de';
//endregion

//region Base Links
export const IB_BASE_URL = `https://innbusiness.${config.ENVIRONMENT}.premierinn.digital`;
export const LOWER_ENV_BASE_URL = `https://www.${config.ENVIRONMENT}.premierinn.digital/${LANGUAGE}`;
export const SECURE2_BASE_URL = `https://secure2.${config.ENVIRONMENT}.premierinn.digital/${LANGUAGE}`;
//endregion

//region Secure2 Links
export const SECURE2_404_URL = `${SECURE2_BASE_URL}/404`;
//endregion

//region Business Booker Links
export const BB_HOME_URL = `${LOWER_ENV_BASE_URL}/business-booker/home.html`;
export const BB_BOOKINGS_URL = `${LOWER_ENV_BASE_URL}/business-booker/account/dashboard.html`;
//endregion

//region Inn Business Links
export const IB_HOME = `${IB_BASE_URL}/${IB_LANGUAGE}/homepage`;
export const IB_CARD_MANAGEMENT = `${IB_BASE_URL}/${IB_LANGUAGE}/manage/cards`;
export const IB_PAY_CARD_MANAGEMENT = `${IB_BASE_URL}/${IB_LANGUAGE}/manage/cards?tab=innbusiness-pay`;
export const IB_CENTRALLY_CARD_MANAGEMENT = `${IB_BASE_URL}/${IB_LANGUAGE}/manage/cards?tab=centrally-stored`;
export const IB_USER_MANAGEMENT = `${IB_BASE_URL}/${IB_LANGUAGE}/manage/employees`;
export const IB_FLOW_URL = `${IB_BASE_URL}/${LANGUAGE}/business-booker`;
//endregion

//region AEM Links
export const AEM_BASE_URL = `https://www.${config.ENVIRONMENT}.premierinn.digital`;
export const IB_BOOKINGS_URL = `https://innbusiness.${config.ENVIRONMENT}.premierinn.digital/${LANGUAGE}/business-booker/account/dashboard.html`;

//endregion

//region Worldline Links
export const WORLDLINE_URL = 'https://test-premierinn.worldline.global/Secure/home.aspx';
//endregion

//region graph Links
export const BASE_EXPERIENCE_API = `https://api.${config.ENVIRONMENT}.premierinn.digital/graphql`;
//endregion
