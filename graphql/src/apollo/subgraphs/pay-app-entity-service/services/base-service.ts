import { createAxios } from '../../../client/axios';

const baseUrl = process.env.PAY_APP_ENTITY_SERVICE ?? 'http://pay-app-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  PAY_APP: {
    endpoint: '/v1/pay-app/applications',
    flowCode: 'DIGITAL_PYA_001',
    axiosClient: axiosClient
  },
  INITIALIZE_APPLICATION: {
    endpoint: '/v1/pay-app/application/initialize',
    flowCode: 'DIGITAL_PYA_002',
    axiosClient: axiosClient
  },
  UPDATE_APP_CONTACT_DETAILS: {
    endpoint: '/v1/pay-app/application/contactDetails',
    flowCode: 'DIGITAL_PYA_003',
    axiosClient: axiosClient
  },
  DELETE_APPLICATION: {
    endpoint: '/v1/pay-app/application/delete',
    flowCode: 'DIGITAL_PYA_004',
    axiosClient: axiosClient
  },
  GET_WORDLINE_USER_PREFERENCES: {
    endpoint: '/v1/pay-app/user/preferences',
    flowCode: 'DIGITAL_PYA_006',
    axiosClient: axiosClient
  },
  APP_LOOKUP_DATA: {
    endpoint: '/v1/pay-app/application/lookup',
    flowCode: 'DIGITAL_PYA_005',
    axiosClient: axiosClient
  },
  COMPANY_DETAILS_LOOKUP: {
    endpoint: '/v1/pay-app/application/companyDetailsLookup',
    flowCode: 'DIGITAL_PYA_007',
    axiosClient: axiosClient
  },
  APPLICATION_DETAILS: {
    endpoint: '/v1/pay-app/application/details',
    flowCode: 'DIGITAL_PYA_008',
    axiosClient: axiosClient
  },
  UPDATE_APP_COMPANY_DETAILS: {
    endpoint: '/v1/pay-app/application/companyDetails',
    flowCode: 'DIGITAL_PYA_009',
    axiosClient: axiosClient
  },
  SHARE_APPLICATION: {
    endpoint: '/v1/pay-app/application/share',
    flowCode: 'DIGITAL_PYA_010',
    axiosClient: axiosClient
  },
  ADD_APPLICATION_CARD: {
    endpoint: '/v1/pay-app/application/card',
    flowCode: 'DIGITAL_PYA_011',
    axiosClient: axiosClient
  },
  DELETE_APPLICATION_CARD: {
    endpoint: '/v1/pay-app/application/delete-card',
    flowCode: 'DIGITAL_PYA_012',
    axiosClient: axiosClient
  },
  GET_APPLICATION_CARDS: {
    endpoint: '/v1/pay-app/application/cards',
    flowCode: 'DIGITAL_PYA_013',
    axiosClient: axiosClient
  },
  REMOVE_PARTICIPANT: {
    endpoint: '/v1/pay-app/application/removeParticipant',
    flowCode: 'DIGITAL_PYA_014',
    axiosClient: axiosClient
  },
  SUBMIT_APPLICATION: {
    endpoint: '/v1/pay-app/application/submit',
    flowCode: 'DIGITAL_PYA_015',
    axiosClient: axiosClient
  },
  APP_PRE_CHECK: {
    endpoint: '/v1/pay-app/application/pre-check',
    flowCode: 'DIGITAL_PYA_016',
    axiosClient: axiosClient
  },
  DIRECT_DEBIT: {
    endpoint: '/v1/pay-app/application/directDebit',
    flowCode: 'DIGITAL_PYA_017',
    axiosClient: axiosClient
  },
  UPDATE_RESUME_URL: {
    endpoint: '/v1/pay-app/application/updateResumeUrl',
    flowCode: 'DIGITAL_PYA_018',
    axiosClient: axiosClient
  },
  GET_DD_SEPA_FORM_STATUS: {
    endpoint: '/v1/pay-app/application/ddSepaFormStatus',
    flowCode: 'DIGITAL_PYA_019',
    axiosClient: axiosClient
  }
};
