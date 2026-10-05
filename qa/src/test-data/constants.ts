import { Strings } from './strings';
import { IbStrings } from './pib/ibStrings';
import { viewports } from '../../config/browsers';

/**
 * Shared constants preserved for migrated Premier Inn test data.
 * Only static data values are kept here; runtime-generated values remain in dedicated helpers.
 */
export class Constants {
  private constructor() {}

  private static get options(): Record<string, unknown> {
    return (global.browser?.options ?? {}) as Record<string, unknown>;
  }

  private static get environment(): string {
    return String(Constants.options.environment ?? Constants.options.env ?? '');
  }

  private static get locale(): string {
    return String(Constants.options.locale ?? 'gb-en');
  }

  private static get baseUrl(): string {
    return String(Constants.options.baseUrl ?? '');
  }

  private static get browserResolution(): string {
    return String(Constants.options.browserResolution ?? Constants.options.viewport ?? 'desktop');
  }

  private static toResolution(viewport: { width: number; height: number }): string {
    return `${viewport.width},${viewport.height}`;
  }

  private static randomString(length: number, pool = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'): string {
    const seed = crypto.randomUUID().replace(/-/g, '');
    let value = '';
    for (let index = 0; index < length; index++) {
      value += pool[parseInt(seed[index % seed.length], 16) % pool.length];
    }
    return value;
  }

  static readonly CODE_FACILITY_FOR_PREMIER_PLUS_ROOMS_BADGE = 'PRR';
  static readonly DATE_FORMAT_SLASH = 'DD/MM/YYYY';
  static readonly DAY_NAME_DAY_DATE_FORMAT = 'ddd DD MMM YYYY';
  static readonly HOUR_FORMAT = 'HH:mm:ss';
  static readonly HOUR_AM_PM_FORMAT = 'ha';
  static readonly DAY_NAME_DAY_MONTH_NAME_DATE_FORMAT = 'ddd DD MMM';
  static readonly HOUR_DAYNAME_DAY_DATE_FORMAT_GB = 'ha - ddd D MMM YYYY';
  static readonly HOUR_DAYNAME_DAY_DATE_FORMAT_DE = 'HH:mm - ddd D MMM YYYY';
  static readonly DAY_DATE_FORMAT = 'DD MMM YYYY';
  static readonly DAY_DATE_FULL_MONTH_FORMAT = 'DD MMMM YYYY';
  static readonly DAY_SHORT_DATE_FORMAT = 'DD MMM YY';
  static readonly DAY_SHORT_DATE_POINT_FORMAT = 'DD. MMM YY';
  static readonly DAY_MONTH_DATE_FORMAT = 'DD MMM';
  static readonly SHORT_MONTH_YEAR_FORMAT = 'LLL yy';
  static readonly SHORT_DAY_MONTH_LONG_YEAR_FORMAT = 'd LLL yyyy';
  static readonly SHORT_MONTH_LONG_YEAR_FORMAT = 'dd LLL yyyy';
  static readonly LONG_MONTH_YEAR_FORMAT = 'MMMM yyyy';
  static readonly DIACRITICS_STRING = 'crème brûléeŰêø';
  static readonly SPECIAL_CHARACTERS_STRING = '-\',./ßÜÖÄüöä';
  static readonly UNACCEPTED_CHARACTERS = ':;~#';
  static readonly ALPHA_NUMERICAL_STRING = 'abcdef123456';
  static readonly FORESEE_OPT_OUT_POSTFIX = '#fscommand=fsoptout';
  static readonly ISO_DAY_DATE_FORMAT = 'YYYY-MM-DD';
  static readonly YEAR_MONTH_FORMAT = 'yyyy-MM';
  static readonly MAX_FIRST_NAME_CHARACTERS = 20;
  static readonly MAX_LAST_NAME_CHARACTERS = 30;
  static readonly MAX_LOCATION_SUGGESTIONS_NUMBER = 5;
  static readonly MAX_NIGHTS_FOR_RESERVATION = 9;
  static readonly MAX_NUMBER_PER_NIGHTS = 14;
  static readonly MAX_ROOMS_NUMBER_CCUI = 9;
  static readonly MAX_ROOMS_NUMBER_PI = 4;
  static readonly MONTH_YEAR = 'MMMM yyyy';
  static readonly NUMBER_1000 = 1000;
  static get NUMBER_WITH_1_DIGIT(): string { return Constants.randomString(1, '0123456789'); }
  static get NUMBERS(): string { return Constants.randomString(10, '0123456789'); }
  static get NUMBERS_WITH_5_DIGITS(): string { return Constants.randomString(5, '123456789'); }
  static get NUMBERS_WITH_6_DIGITS(): string { return Constants.randomString(6, '123456789'); }
  static get NUMBERS_WITH_7_DIGITS(): string { return Constants.randomString(7, '123456789'); }
  static get NUMBERS_WITH_MORE_THAN_12_DIGITS(): string { return Constants.randomString(13, '123456789'); }
  static get NUMBERS_WITH_MORE_THAN_25_DIGITS(): string { return Constants.randomString(26, '123456789'); }
  static readonly LAT_LONG_LOCATION_FORMAT = 'latlong';
  static readonly VALID_PHONE_NUMBER = '712123123';
  static readonly VALID_LAND_LINE_PAY_APP_GB = '02071231234';
  static readonly VALID_LAND_LINE_PAY_APP_DE = '03012341234';
  static readonly VALID_MOBILE_NUMBER_PAY_APP_GB = '07755671234';
  static readonly VALID_MOBILE_NUMBER_PAY_APP_DE = '15123451234';
  static get INVALID_ONLY_CHARACTERS(): string { return Constants.randomString(6, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static readonly INVALID_SPECIAL_CHARACTERS = '/^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ \']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ \']*$/g';
  static readonly SPECIAL_CHARACTERS = '!@#$%^&*():";<>,.~`?/\|"';
  static readonly SPECIAL_CHARACTERS_FIRST_NAME = 'ÀÖØöøɏ-test\' ';
  static readonly EMPTY_STR = '';
  static readonly PRIVACY_OFFICER_EMAIL = 'privacyofficer@whitbread.com';
  static readonly SEARCH_RESULTS_FIRST_PAGE_SIZE = 40;
  static readonly SEARCH_RESULTS_FOLLOWING_PAGES_SIZE = 10;
  static readonly SEARCH_RESULTS_RADIUS_UNIT_DE = 'KILOMETERS';
  static readonly SEARCH_RESULTS_RADIUS_UNIT_EN = 'MILES';
  static readonly SEARCH_RESULTS_URL_LIST_VIEW_TYPE = 2;
  static readonly SEARCH_RESULTS_URL_MAP_VIEW_TYPE = 1;
  static readonly ACCEPTED_SPECIAL_CHARACTERS = 'aAÀÖØʒͰͳͶͷͻͽv ΑϿἀῼЀӿễấ-';
  static readonly SPECIAL_CHARACTERS_COMPANY_NAME_DE = '.äöüß&\'()- ';
  static readonly NEW_REQUIREMENT_SPECIAL_CHARACTERS = 'Withbread ABC & 123 £$€#-.';
  static readonly CARD_LABEL = 'New Card Label';
  static readonly CNP_PASSWORD = 'HelloWorl!';
  static get STRING_WITH_1_CHARACTERS(): string { return Constants.randomString(1, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_5_CHARACTERS(): string { return Constants.randomString(5); }
  static get STRING_WITH_8_CHARACTERS(): string { return Constants.randomString(8, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_9_CHARACTERS(): string { return Constants.randomString(9); }
  static get STRING_WITH_10_CHARACTERS_NUMBERS(): string { return Constants.randomString(10, '0123456789'); }
  static get STRING_WITH_10_CHARACTERS_ALPHA(): string { return Constants.randomString(10, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_11_CHARACTERS(): string { return Constants.randomString(11); }
  static get STRING_WITH_20_CHARACTERS(): string { return Constants.randomString(20, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_30_CHARACTERS(): string { return Constants.randomString(30, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_35_CHARACTERS(): string { return Constants.randomString(35, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_40_CHARACTERS(): string { return Constants.randomString(40, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_41_CHARACTERS(): string { return Constants.randomString(41, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_43_CHARACTERS(): string { return Constants.randomString(43, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_44_CHARACTERS(): string { return Constants.randomString(44, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_50_CHARACTERS(): string { return Constants.randomString(50, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_100_CHARACTERS(): string { return Constants.randomString(100, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_LESS_THAN_20_CHARACTERS(): string { return Constants.randomString(19, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_LESS_THAN_30_CHARACTERS(): string { return Constants.randomString(29, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_MORE_THAN_20_CHARACTERS(): string { return Constants.randomString(21, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_MORE_THAN_30_CHARACTERS(): string { return Constants.randomString(31, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_MORE_THAN_35_CHARACTERS(): string { return Constants.randomString(36, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_MORE_THAN_40_CHARACTERS(): string { return Constants.randomString(41, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static get STRING_WITH_MORE_THAN_50_CHARACTERS(): string { return Constants.randomString(51, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'); }
  static readonly TEXT_HEIGHT_COLLAPSED_CONTAINER = 95;
  static get MAX_HOTEL_FACILITIES_NUMBER(): number { return Constants.BROWSER_RESOLUTIONS.isMobilePhone() ? 4 : 6; }
  static get MAX_ROOM_FACILITIES_NUMBER(): number { return Constants.BROWSER_RESOLUTIONS.isMobilePhone() ? 5 : 6; }
  static get SEARCH_RESULTS_RADIUS(): number { return Constants.locale === 'de-de' ? 40 : 50; }
  static readonly FOOTER_ONE_TAB = 1;
  static readonly NO_OF_DONATION_PACKAGES_PER_RESERVATION = 1;
  static readonly REJECT_PAYMENT_EMAIL_ADDRESS = 'reject@email.com';
  static readonly Y_AXIS_LENGTH_SPEND_OVER_TIME_CHART = 6;
  static readonly MAX_HOTEL_ALERT_SUGGESTIONS = 100;
  static readonly EMPLOYEE_STATUS_PURGED = 'PURGED';
  static get NEGATIVE_NUMBER(): string { return `-${Constants.randomString(3, '0123456789')}`; }
  static get RANDOM_GENERATED_FIRST_NAME(): string { return `Test${Constants.randomString(6, 'abcdefghijklmnopqrstuvwxyz')}`; }
  static get RANDOM_GENERATED_LAST_NAME(): string { return `Auto${Constants.randomString(6, 'abcdefghijklmnopqrstuvwxyz')}`; }
  static readonly IB_TABLE_ROWS_NUMBER = 15;
  // Environments where the new home page header is available.
  static readonly NEW_HEADER_ENVIRONMENTS = ['dit', 'uat', 'demo'];
  static get IS_NEW_HEADER_ENVIRONMENT(): boolean { return Constants.NEW_HEADER_ENVIRONMENTS.includes(Constants.environment); }
  static readonly PRIVACY_LINK = 'https://secure2.premierinn.com/de/de/bedingungen-booking-flow/datenschutz.html';
  static get ASSETS_URL(): string { return `https://www.${Constants.environment}.premierinn.digital`; }
  static get PRIVACY_NOTICE_UK(): string { return `${Constants.ASSETS_URL}/gb/en/terms/privacy-policy.html`; }
  static get PRIVACY_NOTICE_GERMANY(): string { return `${Constants.ASSETS_URL}/de/de/bedingungen/datenschutz.html`; }
  static get LOGO_IMAGE_UK_URL(): string { return `/content/dam/pi/websites/desktop/icons/brand/${Constants.environment === 'uat' ? 'premier-inn-master-logo-purple-2.png' : 'premier-inn-master-logo-purple-2026.png'}`; }
  static readonly LOGO_IMAGE_DE_URL = '/etc/clientlibs/pi-header/resources/images/pi-refresh-logo.svg';
  static readonly SIDEBAR_TOGGLE_LEFT_URL = '/content/dam/global/icons/common/chevron-left-purple.svg';
  static readonly SIDEBAR_TOGGLE_RIGHT_URL = '/content/dam/global/icons/common/chevron-right-purple.svg';
  static get LOCALIZED_BASE_URL(): string {
    const baseUrl = Constants.baseUrl.replace(/\/$/, '');
    const localeSegment = Constants.locale === 'gb-en' ? 'en-gb' : 'de-de';
    return /\/[a-z]{2}-[a-z]{2}$/i.test(baseUrl) ? baseUrl : `${baseUrl}/${localeSegment}`;
  }
  static get TERMS_AND_CONDITIONS_URL(): string {
    return `${Constants.LOCALIZED_BASE_URL}/info/${Constants.locale === 'gb-en' ? 'terms/booking-terms-and-conditions' : 'bedingungen/allgemeine-geschaeftsbedingungen-business'}.html`;
  }
  static readonly WORLDLINE_ABOUT_REPORTS_PAGE = 'https://test-premierinn.worldline.global/Secure/ReportsList.aspx';
  static readonly WORLDLINE_ACCOUNT = 'https://test-premierinn.worldline.global/Secure/Account.aspx';
  static readonly WORLDLINE_ACCOUNT_DE = 'https://test-premierinn.worldline.global/PremierInnEuroZoneB2B/Secure/Account.aspx';
  static readonly WORLDLINE_BB_LINK_CODE = 'https://test-premierinn.worldline.global/Secure/BBLinkCode.aspx';
  static readonly WORLDLINE_CREATE_REPORTS_PAGE = 'https://test-premierinn.worldline.global/Secure/ReportDefine.aspx';
  static readonly WORLDLINE_CREDIT_LIMIT_INCREASE_PAGE = 'https://test-premierinn.worldline.global/Secure/IncreaseCreditLimit.aspx';
  static readonly WORLDLINE_CREDIT_LIMIT_INCREASE_PAGE_DE = 'https://test-premierinn.worldline.global/PremierInnEuroZoneB2B/Secure/IncreaseCreditLimit.aspx';
  static readonly WORLDLINE_HOME_DE = 'https://test-premierinn.worldline.global/PremierInnEuroZoneB2B/Secure/Home.aspx';
  static readonly WORLDLINE_LINKED_ACCOUNTS_PAGE = 'https://test-premierinn.worldline.global/Secure/LinkedAccounts.aspx';
  static readonly WORLDLINE_MAKE_A_PAYMENT_PAGE = 'https://test-premierinn.worldline.global/Secure/CardPayment.aspx';
  static readonly WORLDLINE_MAKE_A_PAYMENT_PAGE_DE = 'https://test-premierinn.worldline.global/PremierInnEuroZoneB2B/Secure/InterimPayment.aspx';
  static readonly WORLDLINE_PAGE_LINK_CODE = 'https://test-premierinn.worldline.global/Secure/Login.aspx?ReturnUrl=%2fSecure%2fBBLinkCode.aspx';
  static readonly WORLDLINE_SCHEDULED_REPORTS_PAGE = 'https://test-premierinn.worldline.global/Secure/DownloadScheduledReports.aspx';
  static readonly WORLDLINE_USER_LIST_PAGE = 'https://test-premierinn.worldline.global/Secure/UserList.aspx';
  static readonly WORLDLINE_VIEW_OFFERS = 'https://test-premierinn.worldline.global/Secure/ViewMyOffers.aspx';
  static readonly WORDLINE_REGISTER_PAGE = 'https://test-premierinn.worldline.global/Secure/Register.aspx';
  static readonly WORLDLINE_ACCOUNT_HOLDER = 'Account holder';
  static readonly WORLDLINE_FINANCE_USER = 'Reports & Invoices';
  static readonly BREAKFAST_GERMAN_PACKAGE_CODE = 'BBIB';
  static readonly PI_BREAKFAST_PACKAGE_CODE = 'BFADBF';
  static readonly CONTINENTAL_BREAKFAST_PACKAGE_CODE = 'BFADCT';
  static readonly MEAL_DEAL_PACKAGE_CODE = 'MDP';
  static readonly KIDS_MEAL_PACKAGE_CODE = 'BFCHDF';
  static readonly EARLY_CHECK_IN_CODE = 'HSCKIN';
  static readonly LATE_CHECK_OUT_CODE = 'HSCOU2';
  static readonly WIFI_CODE = 'FI24HR';
  static readonly CITY_TAX_PACKAGE_CODE = 'CITYTAX';
  static readonly ANONYMOUS_USER = 'anonymous';
  static readonly LEISURE_USER = 'leisure';
  static readonly PI_USERS = [Constants.ANONYMOUS_USER, Constants.LEISURE_USER];
  static readonly LEISURE_REASON = 'LEISURE';
  static readonly BUSINESS_REASON_OR_ADDRESS = 'BUSINESS';
  static readonly HOME_OPTION_ADDRESS = 'HOME';
  static readonly REASON_FOR_STAY = {
    leisure: 'LEI',
    business: 'BUS',
  } as const;
  static readonly UK_CURRENCY_CODE = 'GBP';
  static readonly EURO_CURRENCY_CODE = 'EUR';
  static readonly UK_CURRENCY_CODE_NUMBER = '826';
  static readonly EURO_CURRENCY_CODE_NUMBER = '978';
  static readonly UK_SCHEME_CODE = 'GB';
  static readonly DE_SCHEME_CODE = 'DE';
  static readonly GB_MOBILE_PREFIX = '+44';
  static readonly DE_MOBILE_PREFIX = '+49';
  static readonly UK_COUNTRY_CODE = 'gb';
  static readonly IRELAND_COUNTRY_CODE = 'ie';
  static readonly GERMANY_COUNTRY_CODE = 'de';
  static readonly ISLE_OF_MAN_COUNTRY_CODE = 'iom';
  static readonly POLICY_CODE_D1 = 'D1';
  static readonly POLICY_CODE_D1A = 'D1A';
  static readonly POLICY_CODE_DAX = 'DAX';
  static readonly POLICY_CODE_OA = 'OA';
  static readonly PAYMENT_METHOD_CODE_DVA = 'DVA';
  static readonly PAYMENT_METHOD_CODE_VA = 'VA';
  static readonly PAYMENT_OPTION = {
    payNow: 'PAY_NOW',
    payOnArrival: 'PAY_ON_ARRIVAL',
    reserveWithoutCreditCard: 'RESERVE_WITHOUT_CARD',
    nonGuaranteedBooking: 'RESERVE_WITHOUT_CARD',
  } as const;
  static readonly CARD_TYPE_AC = 'AC';
  static readonly CARD_TYPE_AM = 'AM';
  static readonly CARD_TYPE_AT = 'AT';
  static readonly CARD_TYPE_DI = 'DI';
  static readonly CARD_TYPE_DL = 'DL';
  static readonly CARD_TYPE_EL = 'EL';
  static readonly CARD_TYPE_MA = 'MA';
  static readonly CARD_TYPE_VI = 'VI';
  static readonly CARD_TYPE_MC = 'MC';
  static readonly CARD_TYPE_AX = 'AX';
  static readonly CARD_TYPE_PI = 'PI';
  static readonly CARD_TYPE_DN = 'DN';
  static readonly CARD_TYPE_VS = 'VS';
  static readonly CARD_TYPE_BD = 'BD';
  static readonly CARD_TYPE_PE = 'PE';
  static readonly UNITED_KINGDOM = 'United-Kingdom';
  static readonly GWF_BOOK10ROOMS_FIRSTNAME = 'John';
  static readonly GWF_BOOK10ROOMS_LASTNAME = 'Wick';
  static readonly GWF_PHONENUMBER = 123123123;
  static readonly GWF_LESSTHAN1OROOMS_FIRSTNAME = 'Tony';
  static readonly GWF_LESSTHAN1OROOMS_LASTNAME = 'Stark';
  static readonly GWF_ACCESSIBLEANDCHILDREN_FIRSTNAME = 'Steve';
  static readonly GWF_ACCESSIBLEANDCHILDREN_LASTNAME = 'Rogers';
  static readonly GWF_YOUTHORSCHOOLGROUP_FIRSTNAME = 'Thor';
  static readonly GWF_YOUTHORSCHOOLGROUP_LASTNAME = 'Odinson';
  static readonly PI_LEISURE_EMAIL = 'uk_0@yopmail.com';
  static readonly PI_LEISURE_PASSWORD = 'UGFzc3dvcmQwMQ==';
  // Simple accounts used for testing password strength validation.
  static readonly PI_PASSWORD_TESTING_EMAIL = 'pi_password_tester@yopmail.com';
  static readonly PI_PASSWORD_TESTING_PASSWORD = 'UGFzc3dvcmQwMQ==';
  static readonly PI_PASSWORD_TESTING2_EMAIL = 'pi_password_tester2@yopmail.com';
  static readonly PI_PASSWORD_TESTING2_PASSWORD = 'UGFzc3dvcmQwMQ==';
  static readonly PI_PASSWORD_TESTING4_EMAIL = 'pi_password_tester4@yopmail.com';
  static readonly PI_PASSWORD_TESTING4_PASSWORD = 'UGFzc3dvcmQwMQ==';
  static readonly PI_PASSWORD_TESTING5_EMAIL = 'pi_password_tester5@yopmail.com';
  static readonly PI_PASSWORD_TESTING5_PASSWORD = 'UGFzc3dvcmQwMQ==';
  static readonly PI_LEISURE_EMAIL_WITH_PRESELECTED_CONTINENTAL_MEAL = 'preselectedMeals@mailinator.com';
  static readonly PI_LEISURE_PASSWORD_WITH_PRESELECTED_CONTINENTAL_MEAL = 'UGFzc3dvcmQwMQ==';
  static readonly POD3_EMAIL = 'pod3@mailinator.com';
  static readonly POD3_PASSWORD = 'UGFzc3dvcmQx';
  static readonly POD7_TEMPORARY_EMAIL = 'auto.pod7.multibookings@mailinator.com';
  static readonly POD7_TEMPORARY_EMAIL2 = 'onebooking@mailinator.com';
  static readonly POD7_TEMPORARY_EMAIL3 = 'testdonations@mailinator.com';
  static readonly PI_LEISURE_EMAIL_PL_RESIDENCE = 'helena_pl_opted_in@yopmail.com';
  static readonly PI_LEISURE_EMAIL_IT_RESIDENCE = 'permanent_it_opted_in@yopmail.com';
  static readonly PI_LEISURE_EMAIL_AN_RESIDENCE = 'andorra_opted_out@yopmail.com';
  static readonly PI_LEISURE_EMAIL_DZ_RESIDENCE = 'algerianu_opted_out2@yopmail.com';
  static readonly PI_LEISURE_EMAIL_DE_RESIDENCE_PERMANENT_OPTED_IN = 'automation_permanent_de_opted_in@yopmail.com';
  static readonly PI_LEISURE_EMAIL_UK_RESIDENCE_PERMANENT_OPTED_IN = 'permanent_uk_opted_in@yopmail.com';
  static readonly PI_LEISURE_EMAIL_UK_RESIDENCE_OPTED_IN = 'uk_automation_opted_in2@yopmail.com';
  static readonly PI_LEISURE_EMAIL_UK_RESIDENCE_PREVIOUS_OPTED_OUT = 'uk_prev_opted_out2@yopmail.com';
  static readonly PI_LEISURE_EMAIL_DE_RESIDENCE_PREVIOUS_OPTED_OUT = 'prev_de_opted_out@yopmail.com';
  static readonly PI_LEISURE_EMAIL_AD_RESIDENCE_PREVIOUS_OPTED_OUT = 'andorra_la_vella_permanent_opted_out2@yopmail.com';
  static readonly PI_LEIURE_EMAIL_PASSWORD_DE_OPTED_IN_FEATURE = 'UGFzc3dvcmQx';
  static readonly POD7_TEMPORARY_PASSWORD = 'UGFzc3dvcmQwMQ==';
  static readonly POD7_TEMPORARY_PASSWORD2 = 'U3VwZXJTZWNyZXQxMjM=';
  static readonly GWF_BOOK10ROOMS_EMAIL = 'johnwick@mailinator.com';
  static readonly GWF_LESSTHAN1OROOMS_EMAIL = 'tonystark@mailinator.com';
  static readonly GWF_ACCESSIBLEANDCHILDREN_EMAIL = 'steverogers@mailinator.com';
  static readonly GWF_YOUTHORSCHOOLGROUP_EMAIL = 'thorodinson@mailinator.com';
  static readonly POD4_EMAIL = 'pod4@mailinator.com';
  static readonly POD4_PASSWORD = 'UGFzc3dvcmQwMQ==';
  static readonly AUTO_VISA_CARD_EMAIL = 'auto.visa.card@yopmail.com';
  static readonly AUTO_VISA_CARD_PASSWORD = 'UGFzc3dvcmQwMQ==';
  static readonly PI_DE_EMAIL = 'auto_pide@yopmail.com';
  static readonly PI_DE_DOUBLEOPTIN_EMAIL = 'auto_pide_optin@yopmail.com';
  static readonly PI_DE_PASSWORD = 'UGFzc3dvcmQwMQ==';
  static readonly AUTO_PRESELECTED_CONTINENTAL = 'auto.preselected@yopmail.com';

  // Business Booker automation users.
  static readonly BB_AUTO_MANAGER = 'automation.travelmanager@mailinator.com';
  static readonly BB_AUTO_MANAGER_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_BOOKER_EMAIL = 'automation.booker@mailinator.com';
  static readonly BB_BOOKER_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_EMAIL = 'automationbb@mailinator.com';
  static readonly BB_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_BOOKER_NO_BOOKINGS = 'booker_no_bookings@mailinator.com';
  static readonly BB_BOOKER_NO_BOOKINGS_PASSWORD = 'UGFzc3dvcmQxMjM=';
  // Paypal test account.
  static readonly PAYPAL_EMAIL1 = 'WhitbreadPayPalTestUK@whitbread.com';
  static readonly PAYPAL_EMAIL2 = 'WhitbreadPayPalTestDE@whitbread.com';
  static readonly PAYPAL_PASSWORD = 'V2hpdGJyZWFkVGVzdCE=';
  static readonly BB_TRAVEL_MANAGER = 'traveling.asl@mailinator.com';
  static readonly BB_TRAVEL_MANAGER_PASSWORD = 'UGFzc3dvcmQxIQ==';
  static readonly BB_SELF_BOOKER = 'greenselfbooker@mailinator.com';
  static readonly BB_SELF_BOOKER_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_NEGOTIATED_RATES_EMAIL = 'greenpod@mailinator.com';
  static readonly BB_NEGOTIATED_RATES_PASSWORD = 'UGFzc3dvcmQx';
  // TODO: workaround because of bug https://whitbreadis.atlassian.net/browse/DNRQ-74723.
  static readonly BB_AUTO_MANAGER_PIBA_STORED = 'vlad.enache2@whitbread.com';
  static readonly BB_AUTO_MANAGER_PIBA_STORED_PASSWORD = 'UGFzc3dvcmQy';
  static readonly BB_AUTO_MANAGER2_PIBA_STORED = 'auto.company.manager2@mailinator.com';
  static readonly BB_AUTO_MANAGER2_PIBA_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_COMPANY_MANAGER_PIBA_STORED = 'auto.company.manager@mailinator.com';
  static readonly BB_AUTO_COMPANY_MANAGER_PIBA_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_BOOKER_PIBA_STORED = 'booker.auto@mailinator.com';
  static readonly BB_AUTO_BOOKER_PIBA_STORED_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_BOOKER_PIBA_STORED_WITH_ALLOWANCES2 = 'booker.auto2@mailinator.com';
  static readonly BB_AUTO_BOOKER_PIBA_STORED_WITH_ALLOWANCES_PASSWORD2 = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_BOOKER_MASTER_STORED = 'booker.auto.mastcard@mailinator.com';
  static readonly BB_AUTO_BOOKER_MASTER_STORED_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_AUTO_BOOKER_VISA_STORED = 'no.piba.booker@mailinator.com';
  static readonly BB_AUTO_BOOKER_VISA_STORED_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_SELF_BOOKER_PIBA_STORED = 'selfbooker.auto@mailinator.com';
  static readonly BB_AUTO_SELF_BOOKER_PIBA_STORED_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_SELF_BOOKER_PIBA_STORED2 = 'selfbooker.auto2@mailinator.com';
  static readonly BB_AUTO_SELF_BOOKER_PIBA_STORED_PASSWORD2 = 'UGFzc3dvcmQxMjM=';
  static readonly BB_MANAGER_WITH_STORED_AMEX = 'amex.auto@mailinator.com';
  static readonly BB_MANAGER_WITH_STORED_AMEX_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_AUTO_MANAGER_MASTER_STORED = 'manager.auto.mastcard@mailinator.com';
  static readonly BB_AUTO_MANAGER_MASTER_STORED_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_SAVED_MEALS_VISA_STORED = 'savedmeals.auto@mailinator.com';
  static readonly BB_AUTO_SAVED_MEALS_VISA_STORED_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_SAVED_MEALS2 = 'savedmeals.auto2@mailinator.com';
  static readonly BB_AUTO_SAVED_MEALS2_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_AUTO_MANAGER_VISA_CENTRALLY_STORED = 'manager.centrallycnp.auto@mailinator.com';
  static readonly BB_AUTO_MANAGER_VISA_CENTRALLY_STORED_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_CENTRALLY_STORED_CARD_VISA_BOOKER = 'bb_CSC_visa_role_booker@mailinator.com';
  static readonly BB_CENTRALLY_STORED_CARD_VISA_BOOKER_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly BB_MANAGER_MI_REPORT = 'ditmireportuser4@mailinator.com';
  static readonly BB_MANAGER_MI_REPORT_PASSWORD = 'UGFzc3dvcmQx=';
  static readonly BB_MANAGER_EMERGENCY_REPORT = 'ditmireportuser2@mailinator.com';
  static readonly BB_MANAGER_EMERGENCY_REPORT_PASSWORD = 'UGFzc3dvcmQx=';
  static readonly BB_MANAGER_EMERGENCY_REPORT_NO_RECORDS = 'ditmireportuser3@mailinator.com';
  static readonly BB_MANAGER_EMERGENCY_REPORT_PASSWORD_NO_RECORDS = 'UGFzc3dvcmQx=';
  static readonly BB_TRAVEL_MANAGER_2 = 'uat.travelmanager.test@mailinator.com';
  static readonly BB_TRAVEL_MANAGER_2_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_CONTACT_BANK_ERROR = 'testcontactbankerror@yopmail.com';
  static readonly BB_CONTACT_BANK_ERROR_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_INCORRECT_CARD_DETAILS_ERROR = 'testincorrectcarddetails2@yopmail.com';
  static readonly BB_INCORRECT_CARD_DETAILS_ERROR_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BB_TRY_AGAIN_ERROR = 'testtryagain@yopmail.com';
  static readonly BB_TRY_AGAIN_ERROR_PASSWORD = 'UGFzc3dvcmQx';

  // Accounts for accompanying guest details.
  static readonly BB_TRAVEL_MANAGER_USER = 'uat.testingbb.tm@mailinator.com';
  static readonly BB_BOOKER_MANAGER_USER = 'uat.testingbb.tm@mailinator.com';
  static readonly BB_BOOKER_MANAGER_PASSWORD = 'UGFzc3dvcmQx';

  // Do not create reservations with these emails; they are used by Bookings page info-card tests.
  static readonly PI_NO_BOOKINGS_EMAIL = 'autopod7nobookings@mailinator.com';
  static readonly ONE_UPCOMING_BOOKING_EMAIL = 'cancelled.auto.booking@mailinator.com';
  static readonly ONE_PAST_BOOKING_EMAIL = 'autoonebooking@mailinator.com';
  static readonly ONE_CANCELLED_BOOKING_EMAIL = 'auto.upcoming.booking@mailinator.com';

  // Automation credentials for existing BART users.
  static readonly BART_DECOMMISSION_USER = 'bart.test.user@mailinator.com';
  static readonly BART_DECOMMISSION_USER_DE = 'bart.test.user.delete6@mailinator.com';
  static readonly BART_DECOMMISSION_PASSWORD = 'UGFzc3dvcmQx';
  static readonly BART_DECOMMISSION_PASSWORD_DE = 'UGFzc3dvcmQx';

  // Automation newly created CDH users.
  static readonly NEW_CDH_BART_DECOMMISSION_USER = 'bart.test.auto@yopmail.com';
  static readonly NEW_CDH_BART_DECOMMISSION_USER_DE = 'bart.test.user.delete15@mailinator.com';
  static readonly NEW_CDH_BART_DECOMMISSION_PASSWORD = 'UGFzc3dvcmQx';
  static readonly NEW_CDH_BART_DECOMMISSION_PASSWORD_DE = 'UGFzc3dvcmQx';

  // Automation credentials for Opera environment.
  static readonly AUTOMATION_USERNAME = 'automation_user';
  static readonly AUTOMATION_PASSWORD = 'cVIkTj8qdkU1cHMzK3NAdjMhKjM9OVBUbiN4eGs9';
  static readonly AUTOMATION_AGENT_USERNAME_CCUI_MFA = 'ccui.agent1@whitbread.com';
  static readonly AUTOMATION_MANAGER_USERNAME_CCUI_MFA = 'ccui.manager1@whitbread.com';
  static readonly AUTOMATION_PASSWORD_CCUI_MFA = 'V3Nxd1hnY2wyMyE=';
  static readonly AUTOMATION_AGENT_USERNAME_CCUI = 'automatic_tests_agent@fake.domain.fake';
  static readonly AUTOMATION_AGENT_PASSWORD_CCUI = 'aG5LQ2tTIUtWdkU4V1RjTHh0bXFLKVEmWERLUlFlZ2hTOGcjS04oU0UmY3dEQWcjZzdSd0FkckokeDh6V0BIJg==';
  static readonly AUTOMATION_MANAGER_USERNAME_CCUI = 'automatic_tests_manager@fake.domain.fake';
  static readonly AUTOMATION_MANAGER_PASSWORD_CCUI = 'TFV3Q2ZmeHlecEpmZkpCZSpZSEBMSVJHVzYyRlZSUCgmakZYRyhqI3pBV0ZadFR2SnRrcGhlS2RoSSskQHRoZA==';
  static readonly ROLE_MANAGER = 'manager';
  static readonly ROLE_STANDARD_AGENT = 'standard';
  static readonly CCUI_AGENT_ID = 'EBBFAEC45DC1420A9044B1160B2DC7';
  static readonly USER_ROLE_LOGGED_IN_MAP = {};

  // Inn Business users.
  static readonly INN_BUSINESS_USER_GUEST_EMAIL = 'innbusiness_guest@mailinator.com';
  static readonly INN_BUSINESS_TETHERED_TM_EMAIL = 'venacch1@mailinator.com';
  // Do not modify test data for CDH cards.
  static readonly INN_BUSINESS_TETHERED_TM_MULTIPLE_EMAIL = 'ib_piuk_travelaccountcard@yopmail.com';
  static readonly INN_BUSINESS_USER_GUEST_PASSWORD = 'UGFzc3dvcmQx';
  static readonly IB_AUTO_LTD = 'IB Automation Ltd';
  static readonly IB_AUTO_LTD_UNIVERSAL_PASSWORD = 'UGFzc3dvcmQxMjM=';
  static readonly IB_TRAVEL_MANAGER_EMAIL = 'auto.ibtm@yopmail.com';
  static readonly IB_TRAVEL_MANAGER_CARD_HOLDER = 'auto.ibtmcardholder@yopmail.com';
  static readonly IB_TRAVEL_MANAGER_FINANCE_CARD = 'auto.ib.tmfinancecard@yopmail.com';
  static readonly IB_TRAVEL_MANAGER_PROFILE_UPDATE_REQUIRED = 'auto.ib.tmmain@yopmail.com';
  static readonly IB_TRAVEL_MANAGER_PROFILE_UPDATE_REQUIRED2 = 'auto.ib.tmmain2@yopmail.com';
  // Booker with no bookings.
  static readonly IB_BOOKER_EMAIL = 'auto.ibbook@yopmail.com';
  static readonly IB_BOOKER2_EMAIL = 'auto.newbooker@yopmail.com';
  static readonly IB_SELF_BOOKER_EMAIL = 'auto.ibself2@yopmail.com';
  static readonly IB_GUEST_EMAIL = 'auto.ibguest@yopmail.com';
  static readonly IB_NO_TETHER_TRAVEL_MANAGER = 'auto.ibtm-notether2@yopmail.com';
  static readonly IB_RESEND_ACTIVATION_EMAIL = 'statustest@yopmail.com';
  // Travel manager with no bookings.
  static readonly IB_VISA_SAVED_CARD = 'auto.tm-visa@yopmail.com';
  static readonly IB_PIBA_SAVED_CARD = 'auto.tm-piba@yopmail.com';
  static readonly IB_NO_SAVED_CARD = 'auto.tm-nocard@yopmail.com';
  static readonly IB_DELETE_CARD = 'auto.delete-card@yopmail.com';
  static readonly IB_ACCOUNT_HOLDER_BOOKER = 'auto.bookah13@yopmail.com';
  static readonly IB_PASSWORD_CHANGE_EMAIL2 = 'auto.changepasstests@yopmail.com';
  static readonly IB_GUEST_WITH_BOOKINGS = 'ib.auto.guestbookings@yopmail.com';
  static readonly IB_GUEST_CH = 'auto.guest.ch2@yopmail.com';
  static readonly IB_SELF_BOOKER_CH = 'auto.selfbooker-cardholder@yopmail.com';
  static readonly IB_BOOKER_CH = 'auto.booker-cardholder@yopmail.com';
  static readonly IB_SELF_AH = 'auto.self-account@yopmail.com';
  static readonly IB_GUEST_AH = 'auto.guest-account@yopmail.com';
  static readonly TM_NOTETHER_PAYAPP = 'autotmnotthetered@yopmail.com';
  static readonly IB_TM_CARD = 'ib_dit_uk_tm_ch@yopmail.com';
  static readonly IB_TM_ACCOUNT_CARD = 'ib_dit_uk_tm_ahch@yopmail.com';
  static readonly IB_TM_FINANCE_CARD = 'ib_dit_uk_tm_fuch@yopmail.com';
  static readonly IB_BOOKER_CARD = 'ib_dit_uk_b_ch@yopmail.com';
  static readonly IB_BOOKER_FINANCE = 'ib_dit_uk_b_f@yopmail.com';
  static readonly IB_BOOKER_ACCOUNT = 'ib_dit_uk_b_ah@yopmail.com';
  static readonly IB_BOOKER_ACCOUNT_CARD = 'ib_dit_uk_b_ahch@yopmail.com';
  static readonly IB_BOOKER_FINANCE_CARD = 'ib_dit_uk_b_fuch@yopmail.com';
  static readonly IB_SELF_ACCOUNT = 'ib_dit_uk_sb_ah@yopmail.com';
  static readonly IB_SELF_CARD = 'ib_dit_uk_sb_ch@yopmail.com';
  static readonly IB_SELF_FINANCE = 'ib_dit_uk_sb_f@yopmail.com';
  static readonly IB_SELF_ACCOUNT_CARD = 'ib_dit_uk_sb_ahch@yopmail.com';
  static readonly IB_SELF_FINANCE_CARD = 'ib_dit_uk_sb_fuch@yopmail.com';
  static readonly IB_GUEST_ACCOUNT = 'ib_dit_uk_g_ah@yopmail.com';
  static readonly IB_GUEST_CARD = 'ib_dit_uk_g_ch@yopmail.com';
  static readonly IB_GUEST_FINANCE = 'ib_dit_uk_g_f@yopmail.com';
  static readonly IB_GUEST_ACCOUNT_CARD = 'ib_dit_uk_g_ahch@yopmail.com';
  static readonly IB_GUEST_FINANCE_CARD = 'ib_dit_uk_g_fuch@yopmail.com';
  static readonly IB_DE_TM_CARD = 'tmchwlde@yopmail.com';
  static readonly IB_DE_TM_ACCOUNT_CARD = 'ib_dit_de_tm_ahch3@yopmail.com';
  static readonly IB_DE_SELF_CARD = 'sbchwlde@yopmail.com';
  static readonly IB_DE_SELF_ACCOUNT_CARD = 'ib_dit_de_sb_ahch3@yopmail.com';
  static readonly IB_DE_BOOKER_CARD = 'bchwlde@yopmail.com';
  static readonly IB_DE_BOOKER_ACCOUNT_CARD = 'ib_dit_de_b_ahch3@yopmail.com';
  static readonly IB_DE_GUEST_CARD = 'gchwlde@yopmail.com';
  static readonly IB_DE_GUEST_ACCOUNT_CARD = 'ib_dit_de_g_ahch3@yopmail.com';
  static readonly IB_SELF_BOOKER_WITH_BOOKINGS_EMAIL = 'selfache_bookerache@yopmail.com';
  static readonly RANDOM_GENERATED_EMAIL = `auto-${crypto.randomUUID().replace(/-/g, '').slice(0, 8)}@yopmail.com`;
  static readonly IB_SUSPENDED_ACCOUNT_DE = 'suspended_accountholder_de@yopmail.com';
  static readonly IB_SUSPENDED_ACCOUNT_FINANCE = 'ib_financeuser_uk_suspended@yopmail.com';
  static readonly IB_TRAVEL_MANAGER_CHANGE_DETAILS = 'autotmchangedetails@yopmail.com';
  static readonly IB_BOOKER_CHANGE_DETAILS = 'autobookerchangedetails@yopmail.com';
  static readonly IB_SELF_BOOKER_CHANGE_DETAILS = 'autoselfbookerchangedetails@yopmail.com';
  static readonly IB_GUEST_CHANGE_DETAILS = 'autoguestchangedetails@yopmail.com';
  static readonly IB_AUTO_TM_NEW = 'auto.ibtmtest@yopmail.com';
  static readonly IB_ANOTHER_EMPLOYEE = 'invite1@yopmail.com';
  // AH with no statements data.
  static readonly IB_AUTO_TM_AH = 'auto-ah2@yopmail.com';
  // Finance user with no statements data.
  static readonly IB_AUTO_TM_FINANCE = 'auto-finance2@yopmail.com';
  static readonly IB_AUTO_TM_CH = 'autotmcardholder@yopmail.com';
  static readonly IB_AUTO_BOOKER_NEW = 'autoibbokertest@yopmail.com';
  static readonly IB_AUTO_BOOKER_AH_TETHERED = 'auto.bookertestaccountholder@yopmail.com';
  static readonly IB_AUTO_SELF_BOOKER_AH_TETHERED = 'auto.ibselfbookertest@yopmail.com';
  static readonly IB_AUTO_GUEST_AH_TETHERED = 'auto.ibguesttest@yopmail.com';
  static readonly IB_DE_TM_AH_CH = 'ib_pibaeuro@yopmail.com';
  static readonly IB_DE_TM_AH_CH_UAT = 'ib_uatde_travelmanager@yopmail.com';
  static readonly IB_DE_BOOKER_UAT = 'ib_uatde_booker@yopmail.com';
  static readonly IB_SB_UNTETHERED = 'ib_dit_uk_sb@yopmail.com';
  static readonly IB_B_UNTETHERED = 'ib_dit_uk_b@yopmail.com';
  static readonly IB_G_UNTETHERED = 'ib_dit_uk_g@yopmail.com';
  static readonly IB_TM_ACCOUNT_HOLDER = 'ib_piuk_travelaccount@yopmail.com';
  static readonly IB_TM_FINANCE_USER = 'ib_piuk_travelfinance@yopmail.com';
  static readonly IB_TM_CARD_HOLDER = 'ib_piuk_travelcard2@yopmail.com';
  static readonly IB_BOOKER_ACCOUNT_HOLDER = 'ib_piuk_bookeraccount@yopmail.com';
  static readonly IB_TM_ACCOUNT_HOLDER_CARD_HOLDER = 'ib_piuk_travelaccountcard@yopmail.com';
  static readonly IB_EMPLOYEE_QUESTIONS = 'auto.ibtestcompany@yopmail.com';
  static readonly IB_COST_CENTER_OWNER = 'bbsittester7@mailinator.com';
  static readonly IB_COST_CENTER_OWNER_PASSWORD = 'U3VoYW4zMTMyNQ==';
  // Do not modify test data for these users.
  static readonly IB_PIBA_ACCOUNT_SPENDING_DE = 'SITtestertwo@mailinator.com';
  static readonly IB_PIBA_ACCOUNT_SPENDING_UK = 'raman.singh@yopmail.com';
  static readonly PASS_IB_PIBA_ACCOUNT_SPENDING_DE = 'SGVsbG8xMjM=';
  static readonly NO_CARDS_COMPANY = 'automocardscompanytestcristi@yopmail.com';
  // Do not modify test data for this user (UK company, TM Finance user, Spending summary data).
  static readonly IB_UK_TM_FINANCE = 'test.ram@yopmail.com';
  // Card holder with no tethered account used for card management tests.
  static readonly IB_AUTO_TM_UNTETHERED = 'auto.ibtmtestnothetered@yopmail.com';
  // Account with no stored cards used for card management tests and pay app tests.
  static readonly IB_WITHOUT_CARDS = 'ib_withoutcards@yopmail.com';
  // Pay app users from the same company as IB_WITHOUT_CARDS.
  static readonly BOOKER_PAYAPP = 'booker-payapp@yopmail.com';
  static readonly SELF_BOOKER_PAYAPP = 'selfbooker-payapp@yopmail.com';
  static readonly TM_PAYAPP = 'travelmanager-payapp-ib@yopmail.com';
  static readonly GUEST_PAYAPP = 'guest-payapp@yopmail.com';
  // Accounts with pay app already started at specific steps.
  static readonly IB_RESUME_APP_COMPANY_DETAILS = 'resume-companydetails@yopmail.com';
  static readonly IB_RESUME_PAYMENT_DETAILS = 'resume-payment@yopmail.com';
  // DO NOT USE THESE EMAILS IN TESTS; they are used for change password tests.
  static readonly IB_CHANGE_PASSWORD_EMAIL = 'ib-change-password@yopmail.com';
  static readonly IB_CHANGE_PASSWORD_EMAIL_DE = 'ib_dit_de_tm_ahch@yopmail.com';
  // Pay app users for locale-specific runs.
  static readonly TM_DE_PAYAPP = 'travel-payapp@yopmail.com';
  static readonly TM_UK_PAYAPP = 'carddetails@yopmail.com';
  static readonly BOOKER_UK_PAYAPP = 'kim_booker@yopmail.com';
  static readonly SELF_BOOKER_UK_PAYAPP = 'self_booker_ib@yopmail.com';
  static readonly BOOKER_DE_PAYAPP = 'booker-de-payapp@yopmail.com';
  static readonly SELF_DE_BOOKER_PAYAPP = 'selfbooker-de-payapp@yopmail.com';
  static readonly IB_CCO_SECURITY_QUESTION = 'ib-cco-sq@yopmail.com';
  // Account holder with statements data.
  static readonly IB_UK_SIP_AH_WITH_DATA = 'account_holder_test_statement@yopmail.com';
  // AH CH DE with no statements data.
  static readonly IB_AUTO_DE_TM_AH_CH = 'ib_dit_de_tm_ah@yopmail.com';
  static readonly CARD_HOLDER = 'CARD_HOLDER';
  static readonly IB_DE_TRAVEL_MANAGER_EMAIL = 'autode.ibtm@yopmail.com';
  static readonly IB_DE_BOOKER_EMAIL = 'autode.booker@yopmail.com';
  static readonly IB_DE_SELFBOOKER = 'autode.selfbooker@yopmail.com';
  static readonly IB_DE_GUEST = 'autode.guest@yopmail.com';
  static readonly IB_DE_PIBA_EURO_SAVED_CARD = 'autode.pibaeuro@yopmail.com';
  static readonly IB_TM_TRAVEL_ACCOUNT_DE = 'autode.tm-account@yopmail.com';
  static readonly IB_TM_COST_CENTRE = 'autode.tm-costcentre@yopmail.com';
  static readonly IB_TM_COST_CENTRE_CARD_HOLDER = 'autode.costcard-tm@yopmail.com';
  static readonly JOURNEY_TRAVEL_MANAGER_EMAIL = 'journey-manager3@yopmail.com';
  static readonly IB_BILLING_FREQUENCY_WEEKLY = 'register_ib@yopmail.com';
  static readonly IB_BILLING_FREQUENCY_WEEKLY_1 = 'dharmesh.shetty@whitbread.com';
  static readonly IB_BILLING_FREQUENCY_TWICE_MONTHLY = 'travel.manager.1@yopmail.com';
  static readonly IB_BILLING_FREQUENCY_WEEKLY_1_PASSWORD = 'V2gxdGJyZWFk';
  static readonly PROFILE_JOURNEY_TM = 'auto-profile-management2@yopmail.com';
  static readonly PROFILE_JOURNEY_BOOKER = 'pmj-booker2@yopmail.com';
  static readonly PROFILE_JOURNEY_SELF = 'pmj-selfbook2@yopmail.com';
  static readonly PROFILE_JOURNEY_GUEST = 'pmj-guest2@yopmail.com';
  static readonly COMPANY_JOURNEY_COMPANY_EDIT = 'automeditcompany@yopmail.com';
  static readonly PI_UK_TM_AH = 'auto-uat-tmah@yopmail.com';
  static readonly NON_TETHERED_TM = 'auto-uat-tm-notether@yopmail.com';
  static readonly IB_TM_EAD_UNTETHERED = 'ib_modtm@yopmail.com';
  static readonly IB_B_EAD_UNTETHERED = 'ib_testmmasso3@yopmail.com';
  static readonly IB_SB_EAD_UNTETHERED = 'ib_testmmasso1@yopmail.com';
  static readonly IB_G_EAD_UNTETHERED = 'ib_modguest2@yopmail.com';
  static readonly IB_TM_EAD_UNTETHERED_DE = 'ib_ead_de2@yopmail.com';
  static readonly IB_B_EAD_UNTETHERED_DE = 'ib_ead_b_de@yopmail.com';
  static readonly IB_NULL_EMAIL = 'emailnullcomp@yopmail.com';
  static readonly IB_NULL_TITLE = 'titlenullcomp@yopmail.com';
  static readonly IB_NULL_FIRSTNAME = 'firstnamenullcomp@yopmail.com';
  static readonly IB_NULL_LASTNAME = 'lastnamenullcomp@yopmail.com';
  static readonly IB_NULL_MAIN_CONTACT = 'nullmaincontactcomp@yopmail.com';
  static readonly HOTEL_LONDON_EUSTON = 'London Euston';

  // Valid postcodes.
  static readonly VALID_DE_POSTCODE = '12566';
  static readonly VALID_UK_POSTCODE = 'BL0 0BE';
  static readonly VALID_RO_POSTCODE = '430163';
  static readonly DIFFERENT_BILLING_DE_POSTCODE = '60327';

  // Registration numbers used by Inn Business pay-app tests.
  static readonly UK_COMPANY_REGISTRATION_NUMBER = '16359230';
  static readonly DE_COMPANY_REGISTRATION_NUMBER = 'HRB 42243';
  static readonly REGISTERED_CHARITY_NUMBER = '52867296952';
  static readonly UK_LIMITED_COMPANY_REGISTRATION_NUMBER = '8209948';

  // Inn Business direct debit details for PIBA UK.
  static readonly BANK_ACCOUNT_NAME_EN = 'PIBA UK Direct Debit';
  static readonly ACCOUNT_NUMBER = '12345679';
  static readonly SORT_CODE = '542100';

  // Inn Business direct debit details for PIBA Euro.
  static readonly BANK_ACCOUNT_NAME_DE = 'PIBA EURO Direct Debit';
  static readonly ACCOUNT_NUMBER_DE = 'COBADEFFDE89370400440532013000';

  // Booking allowances transaction codes.
  static readonly TRANSACTION_CODE_CARD_PARKING = 'PARK';
  static readonly TRANSACTION_CODE_ULTIMATE_WIFI = 'WIFI';
  static readonly TRANSACTION_CODE_FOOD_AND_BEVERAGE = 'FB';
  static readonly TRANSACTION_CODE_FOOD_AND_BEVERAGE_NO_ALCOHOL = 'FBNA';

  // Routing transaction codes.
  static readonly TRANSACTION_CODE_BREAKFAST = 'BREAK';
  static readonly TRANSACTION_CODE_FOOD_ONLY = 'FOOD';
  static readonly TRANSACTION_CODE_ACCOMMODATION = 'ROOM';
  static readonly TRANSACTION_CODE_CITY_TAX = 'CITY';

  // User defined fields.
  static readonly BOOKING_TYPE_CODE = 'UDFC09';
  static readonly BOOKING_TYPE_ANON_VALUE = 'ANON';

  // Reservation/profile identifier types.
  static readonly RESERVATION_PROFILE_TYPE_RESERVATION_CONTACT = 'ReservationContact';
  static readonly PROFILE_TYPE_PROFILE = 'Profile';
  static readonly RESERVATION_NAME_TYPE_PRIMARY = 'Primary';

  // Negotiated rates company and rate labels.
  static readonly NEGOTIATED_RATES_COMPANY = 'Negotiated Rates Testing';
  static readonly FIXED_CORPORATE_NEGOTIATED_RATE = 'Fixed Corporate Discount';

  // Valid site identifiers.
  static readonly SITE_LEISURE = 'leisure';
  static readonly SITE_BUSINESS_BOOKER = 'business-booker';
  static readonly SITE_INN_BUSINESS = 'inn-business';
  static readonly COMPANY_NUMBER = '1234567';

  // Security check responses.
  static readonly SECURITY_CHECK_PASSED = 'ACCEPT';
  static readonly SECURITY_CHECK_FAILED = 'FAILED';

  // Card type text and payment method constants.
  static readonly CARD_PIBA = 'PIBA';
  static readonly CARD_DEBIT = 'CARD';
  static readonly ACCOUNT_TO_COMPANY_PAYMENT_METHOD = 'AC';

  // Cursor states and destination landing page defaults.
  static readonly CURSOR_NOT_ALLOWED = 'not-allowed';
  static readonly CURSOR_POINTER = 'pointer';
  static readonly DLP_HOTELS_FIRST_PAGE = 12;

  static get RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE(): string { return Constants.BROWSER_RESOLUTIONS.isDesktop() ? 'DesktopVariant' : 'MobileVariant'; }

  static readonly BROWSER_RESOLUTIONS = {
    desktop: Constants.toResolution(viewports.desktop),
    tablet: Constants.toResolution(viewports.tablet),
    mobilePhone: Constants.toResolution(viewports.mobile),
    isDesktop() { return Constants.BROWSER_RESOLUTIONS[Constants.browserResolution as keyof typeof Constants.BROWSER_RESOLUTIONS] === Constants.BROWSER_RESOLUTIONS.desktop; },
    isTablet() { return Constants.BROWSER_RESOLUTIONS[Constants.browserResolution as keyof typeof Constants.BROWSER_RESOLUTIONS] === Constants.BROWSER_RESOLUTIONS.tablet; },
    isMobilePhone() { return Constants.BROWSER_RESOLUTIONS[Constants.browserResolution as keyof typeof Constants.BROWSER_RESOLUTIONS] === Constants.BROWSER_RESOLUTIONS.mobilePhone; },
    hasCurrentResolution(resolution: string) { return Constants.BROWSER_RESOLUTIONS[Constants.browserResolution as keyof typeof Constants.BROWSER_RESOLUTIONS] === resolution; },
    getCurrentResolutionWidth() { return Number.parseInt(String(Constants.BROWSER_RESOLUTIONS[Constants.browserResolution as keyof typeof Constants.BROWSER_RESOLUTIONS]).split(',')[0]); },
    getCurrentResolutionHeight() { return Number.parseInt(String(Constants.BROWSER_RESOLUTIONS[Constants.browserResolution as keyof typeof Constants.BROWSER_RESOLUTIONS]).split(',')[1]); },
  };

  static get COUNTRY_CODE_COUNTRY_NAME_MAP(): Record<string, Promise<string>> {
    return {
      DE: Strings.GERMANY.name,
      ES: Strings.SPAIN.name,
      FR: Strings.FRANCE.name,
      GB: Strings.UNITED_KINGDOM_THE.name,
      IE: Strings.IRELAND.name,
      RO: Strings.ROMANIA.name,
      TT: Strings.TRINIDAD_AND_TOBAGO.name,
    };
  }

  static get IB_ROLE_MAP(): Record<string, Promise<string>> {
    return {
      SUPER: Strings.TRAVEL_MANAGER.name,
      BOOKER: IbStrings.BOOKER.name,
      SELF: IbStrings.SELF_BOOKER.name,
      STAYER: Strings.GUEST.name,
    };
  }

  static get IB_STATUS_MAP(): Record<string, Promise<string>> {
    return {
      ACTIVE: IbStrings.ACTIVE.name,
      INACTIVE: Strings.RESEND_ACTIVATION_EMAIL.name,
      DEACTIVATED: Strings.DEACTIVATED.name,
    };
  }

  static get IB_CARD_LABEL_MAP(): Record<string, string> {
    return {
      AC: 'Mastercard', AM: 'American Express', AT: 'InnBusiness Pay', DI: 'Diners Club', DL: 'Visa Debit', EL: 'Visa Debit', MA: 'Mastercard', VI: 'Visa Debit', MC: 'Mastercard', AX: 'American Express', PI: 'InnBusiness Pay', DN: 'Diners Club', VS: 'Visa Debit', BD: 'InnBusiness Pay', PE: 'InnBusiness Pay',
    };
  }

  static get IB_CARD_HOLDER_BADGE_MAP(): Record<string, Promise<string>> {
    return {
      ACCOUNT_HOLDER: IbStrings.ACCOUNT_HOLDER.name,
      CARD_HOLDER: IbStrings.CARD_HOLDER.name,
      FINANCE_USER: IbStrings.FINANCE_USER.name,
    };
  }

  static get DATE_FORMATTER(): Intl.DateTimeFormatOptions { return { day: 'numeric', month: 'short', year: 'numeric' }; }
  static get DATE_FORMATTER_DATE_PICKER(): Intl.DateTimeFormatOptions { return { day: '2-digit', month: 'short', year: '2-digit' }; }
  static get DATE_FORMATTER_FULL_YEAR(): Intl.DateTimeFormatOptions { return { day: '2-digit', month: 'short', year: 'numeric' }; }

  static get ALERT_FREQUENCY_MAP(): Record<string, Promise<string>> {
    return {
      A: IbStrings.IMMEDIATELY.name,
      D: IbStrings.DAILY.name,
      N: IbStrings.NO_ALERTS.name,
      W: IbStrings.WEEKLY.name,
      M: IbStrings.MONTHLY.name,
    };
  }

  static get CURRENCY_CODE(): Record<string, Promise<string>> {
    return {
      '826': Strings.POUND_CURRENCY_SIGN.name,
      '978': Strings.EURO_CURRENCY_SIGN.name,
    };
  }

  static get TRADING_OPTIONS(): Record<string, string> {
    const isEnglish = Constants.locale === 'gb-en';
    const months = String(isEnglish ? Strings.MONTHS.data.default : Strings.MONTHS.data.de);
    const years = String(isEnglish ? Strings.YEARS.data.default : Strings.YEARS.data.de);
    const over = String(isEnglish ? Strings.OVER.data.default : Strings.OVER.data.de);
    return {
      '0-6MONTHS': `0-6 ${months}`,
      '7-12MONTHS': `7-12 ${months}`,
      '1-2YEARS': `1-2 ${years}`,
      '2-3YEARS': `2-3 ${years}`,
      '3-4YEARS': `3-4 ${years}`,
      '4-5YEARS': `4-5 ${years}`,
      '5-6YEARS': `5-6 ${years}`,
      '6-7YEARS': `6-7 ${years}`,
      '7-8YEARS': `7-8 ${years}`,
      '8-9YEARS': `8-9 ${years}`,
      '9-10YEARS': `9-10 ${years}`,
      '10-11YEARS': `10-11 ${years}`,
      '11-12YEARS': `11-12 ${years}`,
      '12-13YEARS': `12-13 ${years}`,
      '13-14YEARS': `13-14 ${years}`,
      '14-15YEARS': `14-15 ${years}`,
      '15-16YEARS': `15-16 ${years}`,
      '16-17YEARS': `16-17 ${years}`,
      '17-18YEARS': `17-18 ${years}`,
      '18-19YEARS': `18-19 ${years}`,
      '20YEARS': `${over} 20 ${years}`,
    };
  }

  static get IB_SUSPENDED_ACCOUNT(): string { return Constants.environment === 'dit' ? 'ib_testinguk@yopmail.com' : 'auto.suspended@yopmail.com'; }
  static get IB_SUSPENDED_CARD_HOLDER(): string { return Constants.environment === 'dit' ? 'cristisuspendat@yopmail.com' : 'auto.suspended.only.card2@yopmail.com'; }
  static get RANDOM_GENERATED_PASSWORD(): string { return `Password${Constants.randomString(5, '0123456789')}`; }

  static get POST_CODE_REGEX(): Record<string, string> {
    return {
      GB: '/^(GIR 0AA|[A-PR-UWYZ]([0-9]{1,2}|([A-HK-Y][0-9]([0-9ABEHMNPRV-Y])?)|[0-9][A-HJKPS-UW]) [0-9][ABD-HJLNP-UW-Z]{2})$/',
      DE: '^([0123456789][0-9]{4})$',
      RO: '[0-9]{6}',
    };
  }

  static getNumberOfDays(numberOfDays: number): number {
    return numberOfDays >= Constants.MAX_NUMBER_PER_NIGHTS ? Constants.MAX_NUMBER_PER_NIGHTS : numberOfDays;
  }

}
