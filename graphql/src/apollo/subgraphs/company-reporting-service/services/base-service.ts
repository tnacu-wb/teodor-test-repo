import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.COMPANY_REPORTING_SERVICE ?? 'http://company-reporting-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  MANAGEMENT_INFORMATION_REPORT: {
    endpoint: '/v1/company-reports/admin/management-information-report',
    flowCode: 'DIGITAL_PAY_003',
    axiosClient: axiosClient
  },
  EMERGENCY_REPORT: {
    endpoint: '/v1/company-reports/admin/emergency-report',
    flowCode: 'DIGITAL_PAY_003',
    axiosClient: axiosClient
  }
};
