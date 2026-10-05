import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.BUSINESS_TETHER_SERVICE_OPERA ?? 'http://business-tether-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  TETHER_LOGIN: {
    endpoint: '/business/tether/login',
    flowCode: 'DIGITAL_WLT_001',
    axiosClient: axiosClient
  },
  BUSINESS_TETHER: {
    endpoint: '/business/tether',
    flowCode: 'DIGITAL_WLT_002',
    axiosClient: axiosClient
  }
};
