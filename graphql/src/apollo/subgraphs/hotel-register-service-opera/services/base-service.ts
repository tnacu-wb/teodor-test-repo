import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.HOTEL_REGISTER_SERVICE_OPERA ?? 'http://hotel-register-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  REGISTRATION_STEP_ONE: {
    endpoint: '/v1/hotel-register/innbusiness/registration/step-one',
    flowCode: 'DIGITAL_ACC_012',
    axiosClient: axiosClient
  },
  REGISTRATION_STEP_TWO: {
    endpoint: '/v1/hotel-register/innbusiness/registration/step-two',
    flowCode: 'DIGITAL_ACC_013',
    axiosClient: axiosClient
  },
  APPS_ACCOUNT_REGISTRATION: {
    endpoint: '/v1/hotel-register/accounts/register',
    flowCode: 'DIGITAL_ACC_020',
    axiosClient: axiosClient
  }
};
