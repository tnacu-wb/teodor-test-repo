import { createAxios } from '../../../client/axios';

const baseUrl = process.env.SPENDING_ENTITY_SERVICE ?? 'http://spending-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GET_COMPANY_SPENDING: {
    endpoint: '/v1/spending/companySpending',
    flowCode: 'DIGITAL_SPE_001',
    axiosClient: axiosClient
  },
  ACCOUNT_SPENDING: {
    endpoint: '/v1/spending/accountSpending',
    flowCode: 'DIGITAL_SPE_003',
    axiosClient: axiosClient
  },
  ACCOUNT_UPCOMING_SPENDING: {
    endpoint: '/v1/spending/upcomingSpending',
    flowCode: 'DIGITAL_SPE_002',
    axiosClient: axiosClient
  },
  ACCOUNT_PAYMENT_INFO: {
    endpoint: '/v1/spending/paymentInfo',
    flowCode: 'DIGITAL_SPE_004',
    axiosClient: axiosClient
  },
  EMPLOYEE_SPEND: {
    endpoint: '/v1/spending/employeeSpend',
    flowCode: 'DIGITAL_SPE_005',
    axiosClient: axiosClient
  }
};
