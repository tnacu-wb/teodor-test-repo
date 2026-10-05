import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.AVAILABILITY_CACHE_SERVICE_OPERA ??
  'http://availability-cache-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  PRICE_FINDER_BY_LOCATION: {
    endpoint: '/search/price-finder/location',
    flowCode: 'DIGITAL_CON_001',
    axiosClient: axiosClient
  },
  PRICE_FINDER_BY_HOTEL: {
    endpoint: '/search/price-finder/hotels',
    flowCode: 'DIGITAL_CON_001',
    axiosClient: axiosClient
  },
  PRICE_FINDER_BY_LOCATION_FOR_CALENDAR: {
    endpoint: '/search/price-finder/calendar',
    flowCode: 'DIGITAL_AVA_0010',
    axiosClient: axiosClient
  }
};
