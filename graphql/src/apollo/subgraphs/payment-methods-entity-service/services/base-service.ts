import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.PAYMENT_METHODS_ENTITY_SERVICE ?? 'http://payment-methods-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  PAYMENT_METHODS: {
    endpoint: '/v1/payment-methods',
    flowCode: 'DIGITAL_PAY_001',
    axiosClient: axiosClient
  },
  CCUI_PAYMENT_METHODS: {
    endpoint: '/v1/payment-methods/ccui',
    flowCode: 'DIGITAL_PAY_002',
    axiosClient: axiosClient
  },
  PAYMENT_ACTIONS: {
    endpoint: '/v1/payment-methods/payment-actions/{basketReference}',
    flowCode: 'DIGITAL_PAY_003',
    axiosClient: axiosClient
  }
};
