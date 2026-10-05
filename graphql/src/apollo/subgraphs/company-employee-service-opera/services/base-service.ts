import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.COMPANY_EMPLOYEE_SERVICE_OPERA ?? 'http://company-employee-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GET_EMPLOYEES: {
    endpoint: '/companies/{companyId}/employees',
    flowCode: 'DIGITAL_CMP_003',
    axiosClient: axiosClient
  },
  GET_EMPLOYEE_DETAILS: {
    endpoint: '/companies/{companyId}/employees/{employeeId}',
    flowCode: 'DIGITAL_CMP_006',
    axiosClient: axiosClient
  },
  SEND_ACTIVATION_EMAIL: {
    endpoint: '/companies/admin/{companyId}/employees/invite',
    flowCode: 'DIGITAL_CMP_005',
    axiosClient: axiosClient
  },
  ADD_EMPLOYEE: {
    endpoint: '/companies/admin/{companyId}/employees',
    flowCode: 'DIGITAL_CMP_010',
    axiosClient: axiosClient
  },
  UPDATE_EMPLOYEE: {
    endpoint: '/companies/{companyId}/employees/{employeeId}',
    flowCode: 'DIGITAL_CMP_007',
    axiosClient: axiosClient
  },
  RESEND_ACTIVATION_EMAIL: {
    endpoint: '/v1/company-employee-service/innbusiness/travelManager/activationEmail',
    flowCode: 'DIGITAL_CMP_019',
    axiosClient: axiosClient
  },
  APPROVE_REJECT_EMPLOYEE: {
    endpoint: '/v1/company-employee-service/innbusiness/travelManager/approvereject',
    flowCode: 'DIGITAL_CMP_020',
    axiosClient: axiosClient
  },
  GET_ACTIVATION_DETAILS: {
    endpoint: '/companies/employees/activation-details',
    flowCode: 'DIGITAL_CMP_021',
    axiosClient: axiosClient
  },
  GET_INN_BUSINESS_ACTIVATION_DETAILS: {
    endpoint: '/v1/company-employee-service/innbusiness/employees/activation-details',
    flowCode: 'DIGITAL_CMP_022',
    axiosClient: axiosClient
  }
};
