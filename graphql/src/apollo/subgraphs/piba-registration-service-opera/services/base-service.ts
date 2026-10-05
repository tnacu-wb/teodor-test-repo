import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.PIBA_REGISTRATION_SERVICE_OPERA ?? 'http://piba-registration-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GET_REGISTRATION_INFO: {
    endpoint: '/piba/registration/info/{registrationCode}',
    flowCode: 'DIGITAL_ACC_013',
    axiosClient: axiosClient
  },
  AUTHENTICATE_REGISTRATION: {
    endpoint: '/piba/registration/authenticate',
    flowCode: 'DIGITAL_ACC_014',
    axiosClient: axiosClient
  },
  SUBMIT_REGISTRATION: {
    endpoint: '/piba/registration/submit',
    flowCode: 'DIGITAL_ACC_015',
    axiosClient: axiosClient
  }
};
