import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.RULES_AGENT_ENTITY_SERVICE ?? 'http://rules-agent-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  VAT_RULE: {
    endpoint: '/v1/rules/vat-codes',
    flowCode: 'DIGITAL_RUL_005',
    axiosClient: axiosClient
  },
  ROOM_SUBSTITUTION_LIMITATIONS: {
    endpoint: '/v1/rules/room-substitutions',
    flowCode: 'DIGITAL_RUL_007',
    axiosClient: axiosClient
  },
  OCCUPANCY_SUPPLEMENT: {
    endpoint: '/v1/rules/multi-occupancy-supplement?dictionary=false',
    flowCode: 'DIGITAL_RUL_008',
    axiosClient: axiosClient
  },
  SEARCH_RESOURCE_ID: {
    endpoint: '/v1/rules/ccui/rbac/resourceId',
    flowCode: 'DIGITAL_RUL_006',
    axiosClient: axiosClient
  },
  PAYPAL_RULE: {
    endpoint: '/v1/rules/paypal',
    flowCode: 'DIGITAL_PAY_008',
    axiosClient: axiosClient
  }
};
