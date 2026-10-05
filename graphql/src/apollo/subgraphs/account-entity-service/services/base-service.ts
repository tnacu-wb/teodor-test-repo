import { createAxios } from '../../../client/axios';

const baseUrl = process.env.ACCOUNT_ENTITY_SERVICE ?? 'http://account-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  CREATE_ACCOUNT: {
    endpoint: '/v1/account',
    flowCode: 'DIGITAL_ACC_009',
    axiosClient: axiosClient
  },
  UPDATE_MARKETING_PREFERENCES: {
    endpoint: '/v1/account/preferences',
    flowCode: 'DIGITAL_GDE_004',
    axiosClient: axiosClient
  }
};
