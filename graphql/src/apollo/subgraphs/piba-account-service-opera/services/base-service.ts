import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.PIBA_ACCOUNT_SERVICE_OPERA ?? 'http://piba-account-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GET_ACCOUNT_LIST: {
    endpoint: '/piba/account/customers',
    flowCode: 'DIGITAL_ACC_006',
    axiosClient: axiosClient
  },
  RESET_MEMORABLE_WORD: {
    endpoint: '/piba/account/memorableword',
    flowCode: 'DIGITAL_ACC_009',
    axiosClient: axiosClient
  },
  GET_ACCOUNT_BALANCE_SUMMARY: {
    endpoint: '/piba/account/balance/summary',
    flowCode: 'DIGITAL_ACC_010',
    axiosClient: axiosClient
  },
  GET_ACCOUNT_BALANCE_SUMMARY_V2: {
    endpoint: '/v2/piba/account/balance/summary/{tetheredUserGuid}/{scheme}',
    method: 'POST',
    flowCode: 'DIGITAL_ACC_010',
    axiosClient: axiosClient
  },
  VIEW_INVOICES_V2: {
    endpoint: '/v2/piba/account/invoices',
    method: 'POST',
    flowCode: 'DIGITAL_ACC_019',
    axiosClient: axiosClient
  },
  VIEW_ACCOUNT_TRANSACTIONS: {
    endpoint: '/piba/account/transactions',
    method: 'POST',
    flowCode: 'DIGITAL_ACC_021',
    axiosClient: axiosClient
  }
};
