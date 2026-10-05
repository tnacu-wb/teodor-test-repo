import { createAxios } from '../../../client/axios';

const baseUrl = process.env.COMPANY_ENTITY_SERVICE ?? 'http://company-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  SEARCH_COMPANIES: {
    endpoint: '/v1/companies',
    flowCode: 'DIGITAL_CMP_001',
    axiosClient: axiosClient
  },
  SEARCH_COMPANIES_PROFILE: {
    endpoint: '/v1/companies/profile',
    flowCode: 'DIGITAL_CMP_001',
    axiosClient: axiosClient
  },
  COMPANY_PROFILE: {
    endpoint: '/v1/companies/{companyId}',
    flowCode: 'DIGITAL_CMP_002',
    axiosClient: axiosClient
  },
  COMPANY_PROFILE_BY_ID: {
    endpoint: '/v1/companies/id/{id}',
    flowCode: 'DIGITAL_CMP_002',
    axiosClient: axiosClient
  }
};
