import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.ADDRESS_LOOKUP_ENTITY_SERVICE ?? 'http://address-lookup-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  PARTIAL_ADDRESS: {
    endpoint: '/v1/addresses',
    flowCode: 'DIGITAL_ALO_001',
    axiosClient: axiosClient
  },
  FORMAT_ADDRESS: {
    endpoint: '/v1/addresses/{identifier}',
    flowCode: 'DIGITAL_ALO_002',
    axiosClient: axiosClient
  }
};
