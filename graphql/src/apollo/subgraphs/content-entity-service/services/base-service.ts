import { createAxios } from '../../../client/axios';

const baseUrl = process.env.CONTENT_ENTITY_SERVICE ?? 'http://content-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  LABELS: {
    endpoint: '/v1/content/labels',
    flowCode: 'DIGITAL_CON_003',
    axiosClient: axiosClient
  },
  GET_CARD_MANAGEMENT_LABELS: {
    endpoint: '/v1/content/innb/cardmgmt',
    flowCode: 'DIGITAL_CON_023',
    axiosClient: axiosClient
  },
  GLOBAL_CONFIG: {
    endpoint: '/v1/content/global-config',
    flowCode: 'DIGITAL_CON_029',
    axiosClient: axiosClient
  },
  GET_PAGE_DATA: {
    endpoint: '/v1/content/innb/pagedata',
    flowCode: 'DIGITAL_CON_025',
    axiosClient: axiosClient
  },
  DLP_INFORMATION: {
    endpoint: '/v1/content/dlp-information',
    flowCode: 'DIGITAL_CON_028',
    axiosClient: axiosClient
  },
  CATEGORY_LABELS: {
    endpoint: '/v1/content/labels/{category}',
    flowCode: 'DIGITAL_CON_003',
    axiosClient: axiosClient
  },
  COUNTRIES: {
    endpoint: '/v1/content/countries',
    flowCode: 'DIGITAL_CON_010',
    axiosClient: axiosClient
  },
  FOOTER: {
    endpoint: '/v1/content/footer',
    flowCode: 'DIGITAL_CON_009',
    axiosClient: axiosClient
  },
  ALL_HOTEL_SHORT_INFORMATION: {
    endpoint: '/v1/content/allhotels',
    flowCode: 'DIGITAL_CON_014',
    axiosClient: axiosClient
  },
  HOTEL_INFORMATION: {
    endpoint: '/v1/content/hotels/{hotelId}/information',
    flowCode: 'DIGITAL_CON_014',
    axiosClient: axiosClient
  },
  RATE_INFORMATION: {
    endpoint: '/v1/content/booking/rateInformation',
    flowCode: 'DIGITAL_CON_015',
    axiosClient: axiosClient
  },
  BOOKING_INFORMATION: {
    endpoint: '/v1/content/booking',
    flowCode: 'DIGITAL_CON_016',
    axiosClient: axiosClient
  },
  COOKIE_CONSENT: {
    endpoint: '/v1/content/cookie-policies',
    flowCode: 'DIGITAL_CON_021',
    axiosClient: axiosClient
  },
  SEO_INFORMATION: {
    endpoint: '/v1/content/seo',
    flowCode: 'DIGITAL_CON_022',
    axiosClient: axiosClient
  },
  GET_HOTELS_INFORMATION: {
    endpoint: '/v1/content/hotels/information',
    flowCode: 'DIGITAL_CON_027',
    axiosClient: axiosClient
  },
  ROOM_CLASS_CONFIG: {
    endpoint: '/v1/content/room-class-config',
    flowCode: 'DIGITAL_CON_026',
    axiosClient: axiosClient
  },
  PRICE_FINDER_GLOBAL_CONFIG: {
    endpoint: '/v1/content/price-finder/global-config',
    flowCode: 'DIGITAL_CON_031',
    axiosClient: axiosClient
  },
  RATES_INFORMATION: {
    endpoint: '/v1/content/booking/rateInformation',
    flowCode: 'DIGITAL_CON_005',
    axiosClient: axiosClient
  },
  ROOM_TYPE_INFORMATION: {
    endpoint: '/v1/content/room-type',
    flowCode: 'DIGITAL_CON_006',
    axiosClient: axiosClient
  },
  BOOKING_FLOW_INFORMATION: {
    endpoint: '/v1/content/booking',
    flowCode: 'DIGITAL_CON_007',
    axiosClient: axiosClient
  },
  MAX_ROOMS_LIMITATION: {
    endpoint: '/v1/content/searchrules',
    flowCode: 'DIGITAL_RUL_002',
    axiosClient: axiosClient
  },
  SEARCH_INFORMATION: {
    endpoint: '/v1/content/searchresults/data',
    flowCode: 'DIGITAL_CON_008',
    axiosClient: axiosClient
  },
  HOTELS_INFORMATION: {
    endpoint: '/v1/content/hotels/{hotel}/information',
    flowCode: 'DIGITAL_CON_001',
    axiosClient: axiosClient
  },
  HOTELS_INFORMATION_BY_SLUG: {
    endpoint: '/v1/content/hotels',
    flowCode: 'DIGITAL_CON_002',
    axiosClient: axiosClient
  },
  HOMEPAGE_APPS_CONTENT: {
    endpoint: '/v1/content/homepage',
    flowCode: 'DIGITAL_CON_030',
    axiosClient: axiosClient
  },
  HOTEL_INFORMATION_FOR_BOOKING: {
    endpoint: '/v1/content/hotels/{hotelId}/information',
    flowCode: 'DIGITAL_CON_017',
    axiosClient: axiosClient
  },
  GET_RATE_INFORMATION: {
    endpoint: '/v1/content/booking/rateInformation',
    flowCode: 'DIGITAL_CON_018',
    axiosClient: axiosClient
  },
  ROOM_TYPE: {
    endpoint: '/v1/content/room-type',
    flowCode: 'DIGITAL_CON_019',
    axiosClient: axiosClient
  },
  BOOKING_INFO_MESSAGES: {
    endpoint: '/v1/content/booking',
    flowCode: 'DIGITAL_CON_020',
    axiosClient: axiosClient
  },
  GET_BOOKING_INFO_AEM: {
    endpoint: '/v1/content/booking',
    flowCode: 'DIGITAL_CON_011',
    axiosClient: axiosClient
  },
  GET_MEALS_AEM: {
    endpoint: '/v1/content/meals',
    flowCode: 'DIGITAL_CON_012',
    axiosClient: axiosClient
  },
  GET_LOGO_AEM: {
    endpoint: '/v1/content/hotels/{hotelId}/information',
    flowCode: 'DIGITAL_CON_013',
    axiosClient: axiosClient
  },
  HEADER_INFORMATION: {
    endpoint: '/v1/content/header/data',
    flowCode: 'DIGITAL_CON_004',
    axiosClient: axiosClient
  },
  IN_BUSINESS_HEADER: {
    endpoint: '/v1/content/innb/header',
    flowCode: 'DIGITAL_CON_004',
    axiosClient: axiosClient
  },
  MAX_NIGHTS_LIMITATIONS: {
    endpoint: '/v1/content/searchrules',
    flowCode: 'DIGITAL_RUL_003',
    axiosClient: axiosClient
  },
  MAX_ARRIVAL_DATE_LIMITATIONS: {
    endpoint: '/v1/content/searchrules',
    flowCode: 'DIGITAL_RUL_004',
    axiosClient: axiosClient
  },
  MAX_ROOM_OCCUPANCY: {
    endpoint: '/v1/content/searchrules',
    flowCode: 'DIGITAL_RUL_001',
    axiosClient: axiosClient
  },
  PROMO_CONFIG: {
    endpoint: '/v1/content/promo-config',
    flowCode: 'DIGITAL_CON_029',
    axiosClient: axiosClient
  }
};
