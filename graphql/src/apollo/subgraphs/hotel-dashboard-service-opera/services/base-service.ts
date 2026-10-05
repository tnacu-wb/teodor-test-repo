import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.HOTEL_DASHBOARD_SERVICE_OPERA ?? 'hotel-dashboard-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GET_HOTEL_DASHBOARD: {
    endpoint: '/dashboard',
    flowCode: 'DIGITAL_CON_031',
    axiosClient: axiosClient
  }
};
