import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.HOTEL_ACCOUNT_SERVICE_OPERA ?? 'http://hotel-account-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GET_PROFILE_DETAILS: {
    endpoint: '/customers/hotels/{customerId}',
    flowCode: 'DIGITAL_ACC_006',
    axiosClient: axiosClient
  },
  UPDATE_PROFILE_DETAILS: {
    endpoint: '/customers/hotels/{customerId}',
    flowCode: 'DIGITAL_ACC_008',
    axiosClient: axiosClient
  },
  GET_NOTIFICATIONS: {
    endpoint: '/innb/notifications',
    flowCode: 'DIGITAL_ACC_011',
    axiosClient: axiosClient
  },
  RESET_PASSWORD: {
    endpoint: '/auth/hotels/forgot-password',
    method: 'PUT',
    flowCode: 'DIGITAL_ACC_016',
    axiosClient: axiosClient
  },
  FORGOT_PASSWORD: {
    endpoint: '/auth/hotels/forgot-password',
    flowCode: 'DIGITAL_ACC_017',
    axiosClient: axiosClient
  },
  VALIDATE_RESET_KEY: {
    endpoint: '/auth/hotels/validate-reset-key',
    flowCode: 'DIGITAL_ACC_018',
    axiosClient: axiosClient
  },
  GET_ACCOUNT_INFO: {
    endpoint: '/v1/hotel-account/innb/account',
    flowCode: 'DIGITAL_ACC_020',
    axiosClient: axiosClient
  }
};
