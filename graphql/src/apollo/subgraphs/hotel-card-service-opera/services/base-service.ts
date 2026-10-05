import { createAxios } from '../../../client/axios';

const baseUrl = process.env.HOTEL_CARD_SERVICE_OPERA ?? 'http://hotel-card-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GET_PAYMENT_CARDS: {
    endpoint: '/companies/{companyId}/cards',
    flowCode: 'DIGITAL_PAY_011',
    axiosClient: axiosClient
  },
  GET_ALL_PIBA_CARDS: {
    endpoint: '/innb/account/{tetheredUserId}/cards',
    flowCode: 'DIGITAL_ACC_002',
    axiosClient: axiosClient
  },
  GET_PIBA_CARD_DETAILS: {
    endpoint: '/innb/account/{tetheredUserId}/cards/{cardId}',
    flowCode: 'DIGITAL_ACC_002',
    axiosClient: axiosClient
  },
  GET_ACCOUNT_REGISTERED_USERS: {
    endpoint: '/innb/account/{tetheredUserId}/users',
    flowCode: 'DIGITAL_ACC_001',
    axiosClient: axiosClient
  },
  UPDATE_PAYMENT_CARD: {
    endpoint: '/companies/admin/{companyId}/cards/{cardId}',
    flowCode: 'DIGITAL_UPD_001',
    axiosClient: axiosClient
  },
  ACTIVATE_INNB_PIBA_CARD: {
    endpoint: '/innb/account/{tetheredUserId}/cards/{cardId}/activate',
    flowCode: 'DIGITAL_ACC_004',
    axiosClient: axiosClient
  },
  UPDATE_INNB_PIBA_CARD: {
    endpoint: '/innb/account/{tetheredUserGuid}/cards/{cardId}',
    flowCode: 'DIGITAL_ACC_003',
    axiosClient: axiosClient
  },
  SAVE_CARD: {
    endpoint: '/v2/customers/cards/session',
    flowCode: 'DIGITAL_UPD_001',
    axiosClient: axiosClient
  },
  ADD_INNB_PIBA_CARD: {
    endpoint: '/innb/account/{tetheredUserGuid}/card',
    flowCode: 'DIGITAL_ACC_005',
    axiosClient: axiosClient
  },
  AUTHORIZE_CARD: {
    endpoint: '/v2/customers/cards/sca',
    flowCode: 'DIGITAL_UPD_001',
    axiosClient: axiosClient
  },
  DELETE_COMPANY_CARD: {
    endpoint: '/companies/admin/{companyId}/cards/{cardId}',
    flowCode: 'DIGITAL_CMP_018',
    axiosClient: axiosClient
  },
  CANCEL_AND_REPLACE_INNB_PIBA_CARD: {
    endpoint: '/innb/account/{tetheredUserId}/cards/{cardId}/cancel-replace',
    flowCode: 'DIGITAL_ACC_022',
    axiosClient: axiosClient
  },
  INVITE_CARDHOLDER: {
    endpoint: '/innb/account/{tetheredUserGuid}/cards/{cardId}/invite',
    flowCode: 'DIGITAL_ACC_023',
    axiosClient: axiosClient
  },
  COST_CENTRE_DETAILS: {
    endpoint: '/innb/account/{tetheredUserGuid}/costCentreDetails',
    flowCode: 'DIGITAL_ACC_024',
    axiosClient: axiosClient
  },
  REPLACE_CARD: {
    endpoint: '/innb/account/{tetheredUserGuid}/card/{cardId}/replace',
    flowCode: 'DIGITAL_ACC_025',
    axiosClient: axiosClient
  }
};
